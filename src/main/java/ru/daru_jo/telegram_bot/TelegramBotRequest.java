package ru.daru_jo.telegram_bot;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.daru_jo.model.ChatInfo;
import ru.daru_jo.model.MessageReceive;
import ru.daru_jo.service.MessageReceiveService;

import java.util.List;

@Slf4j
@Service
public class TelegramBotRequest implements SpringLongPollingBot, LongPollingUpdateConsumer, AutoCloseable {
    private MessageService messageService;

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Value("${telegram-bot.token}")
    private String botToken;

    private String botName;
    @PostConstruct
    public void init() {
        botName = telegramBotSend.getName();
        messageForAdmin("Бот @" + botName + " запущен");
    }

    private MessageReceiveService messageReceiveService;

    @Autowired
    public void setMessageReceiveService(MessageReceiveService messageReceiveService) {
        this.messageReceiveService = messageReceiveService;
    }

    private TelegramBotSend telegramBotSend;

    @Autowired
    public void setTelegramBotSend(TelegramBotSend telegramBotSend) {
        this.telegramBotSend = telegramBotSend;
    }

    /**
     * Этот метод вызывается при получении обновлений через метод GetUpdates.
     *
     * @param updates Получено обновление
     */
    @Override
    @Async // <-- 7
    public void consume(List<Update> updates) {
        updates.forEach(request -> {
            if (request.hasChannelPost()) {
                try {
                    telegramBotSend.sendMessage(new ChatInfo("null", "@" + request.getChannelPost().getChat().getUserName(), null, request.getChannelPost().getMessageId()), "Был крутой пост. @DaruJo85 помните его");
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            } else if (request.hasMessage()) {

                Message requestMessage = request.getMessage();

                log.info(requestMessage.getChat().getUserName());
                log.info(String.valueOf(requestMessage.getChatId()));
                String chatId = Long.toString(requestMessage.getChatId());
                Integer threadId = requestMessage.getMessageThreadId();
                ChatInfo chatInfo = new ChatInfo(requestMessage.getFrom().getUserName(), chatId, threadId, requestMessage.getMessageId());
                if (telegramBotSend.SendAction(chatInfo)) {
                    log.error("Не удалось уведомить пользователя, что я что-то делаю.");
                }


                messageReceiveService.saveMessageReceive(
                        new MessageReceive(
                                requestMessage.getChatId(),
                                requestMessage.getMessageThreadId(),
                                requestMessage.getText(),
                                "@" + requestMessage.getFrom().getUserName(),
                                requestMessage.getFrom().getFirstName(),
                                requestMessage.getFrom().getLastName(),
                                requestMessage.getChat().getTitle(),
                                requestMessage.getChat().getType(),
                                requestMessage.getChat().getIsForum(),
                                requestMessage.getChat().isChannelChat(),
                                requestMessage.getChat().isUserChat(),
                                requestMessage.getChat().isGroupChat(),
                                requestMessage.getChat().isSuperGroupChat()));
                try {

                    if (requestMessage.getText().startsWith("/")) {
                        log.info("Команда: ");
                        String command = requestMessage.getText();
                        int pos = command.indexOf("@");
                        if (pos > 1) {
                            if (command.substring(pos + 1).equals(botName)) {
                                command = command.substring(0, pos - 1);
                            }
                        }
                        messageService.command(chatInfo, command);
                        log.info(requestMessage.getText());
                    } else {
                        log.info("Сообщение: ");
                        messageService.message(chatInfo, requestMessage.getText());
                        log.info(requestMessage.getText());
                    }
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }

            } else if (request.hasCallbackQuery()) {
                CallbackQuery callbackQuery = request.getCallbackQuery();
                MaybeInaccessibleMessage requestMessage = callbackQuery.getMessage();
                Integer threadId = callbackQuery.getMessage() instanceof Message ? ((Message) callbackQuery.getMessage()).getMessageThreadId() : null;
                MessageReceive messageReceive = messageReceiveService.saveMessageReceive(
                        new MessageReceive(
                                requestMessage.getChatId(),
                                threadId,
                                callbackQuery.getData(),
                                "@" + callbackQuery.getFrom().getUserName(),
                                callbackQuery.getFrom().getFirstName(),
                                callbackQuery.getFrom().getLastName(),
                                requestMessage.getChat().getTitle(),
                                requestMessage.getChat().getType(),
                                requestMessage.getChat().getIsForum(),
                                requestMessage.getChat().isChannelChat(),
                                requestMessage.getChat().isUserChat(),
                                requestMessage.getChat().isGroupChat(),
                                requestMessage.getChat().isSuperGroupChat()));
                ChatInfo chatInfo = new ChatInfo(
                        messageReceive.getUserName(),
                        Long.toString(messageReceive.getChatId()),
                        messageReceive.getThreadId(),
                        requestMessage.getMessageId());
                try {
                    messageService.getMenu(chatInfo, callbackQuery.getData());
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void close() { // <-- 8
        messageForAdmin("⚠️ The bot @" + botName + " has stopped");
    }

    private void messageForAdmin(String text) {
        log.error(text);
        try {
            telegramBotSend.sendMessageForAdmin(text);
        } catch (TelegramApiException e) {
            log.error("Failed to send message while stopping the bot", e);
        }
    }

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }


}