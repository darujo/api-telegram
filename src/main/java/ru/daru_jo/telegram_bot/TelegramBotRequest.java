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
import ru.darujo.dto.information.ResultMes;
import ru.darujo.exceptions.ResourceNotFoundRunTime;
//import ru.darujo.integration.UserServiceIntegration;
import ru.daru_jo.model.ChatInfo;
import ru.daru_jo.model.MessageReceive;
import ru.daru_jo.service.CommandType;
import ru.daru_jo.service.FileService;
import ru.daru_jo.service.MessageReceiveService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TelegramBotRequest implements SpringLongPollingBot, LongPollingUpdateConsumer, AutoCloseable {
//    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TelegramBotRequest.class);
    @Override
    public String getBotToken() {
        log.error("botToken " + botToken);
        return botToken;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }


    private FileService fileService;

    private String botName;
    @Value("${telegram-bot.token}")
    private String botToken;

    @PostConstruct
    public void init() {
//        botName = telegramBotSend.getName();
        messageForAdmin("Бот @" + botName + " запущен");
    }

//    @Autowired
//    public void setFileService(FileService fileService) {
//        this.fileService = fileService;
//    }
//
//    private MessageReceiveService messageReceiveService;
//
//    @Autowired
//    public void setMessageReceiveService(MessageReceiveService messageReceiveService) {
//        this.messageReceiveService = messageReceiveService;
//    }
//
//    private TelegramBotSend telegramBotSend;
//
//    @Autowired
//    public void setTelegramBotSend(TelegramBotSend telegramBotSend) {
//        this.telegramBotSend = telegramBotSend;
//    }
//
//    private MenuServiceIner menuService;
//
//    @Autowired
//    public void setMenuService(MenuServiceIner menuService) {
//        this.menuService = menuService;
//    }
//
//    Map<Long, String> userLastCommand = new HashMap<>();
//

    /**
     * Этот метод вызывается при получении обновлений через метод GetUpdates.
     *
     * @param updates Получено обновление
     */
    @Override
    @Async // <-- 7
    public void consume(List<Update> updates) {
//        updates.forEach(request -> {
//            if (request.hasChannelPost()) {
//                try {
//                    telegramBotSend.sendMessage(new ChatInfo("null", "@" + request.getChannelPost().getChat().getUserName(), null, request.getChannelPost().getMessageId()), "Был крутой пост. @DaruJo85 помните его");
//                } catch (TelegramApiException e) {
//                    throw new RuntimeException(e);
//                }
//            } else if (request.hasMessage()) {
//
//                Message requestMessage = request.getMessage();
//
//                log.info(requestMessage.getChat().getUserName());
//                log.info(String.valueOf(requestMessage.getChatId()));
//                String chatId = Long.toString(requestMessage.getChatId());
//                Integer threadId = requestMessage.getMessageThreadId();
//                ChatInfo chatInfo = new ChatInfo(requestMessage.getFrom().getUserName(), chatId, threadId, requestMessage.getMessageId());
//                if (telegramBotSend.SendAction(chatInfo)) {
//                    log.error("Не удалось уведомить пользователя, что я что-то делаю.");
//                }
//
//
//                messageReceiveService.saveMessageReceive(
//                        new MessageReceive(
//                                requestMessage.getChatId(),
//                                requestMessage.getMessageThreadId(),
//                                requestMessage.getText(),
//                                "@" + requestMessage.getFrom().getUserName(),
//                                requestMessage.getFrom().getFirstName(),
//                                requestMessage.getFrom().getLastName(),
//                                requestMessage.getChat().getTitle(),
//                                requestMessage.getChat().getType(),
//                                requestMessage.getChat().getIsForum(),
//                                requestMessage.getChat().isChannelChat(),
//                                requestMessage.getChat().isUserChat(),
//                                requestMessage.getChat().isGroupChat(),
//                                requestMessage.getChat().isSuperGroupChat()));
//                try {
//                    if (request.hasMessage() && requestMessage.hasText()) {
//                        log.info("Working onUpdateReceived, request.message");
//                        log.info(request.getMessage().getText());
//
//                    } else {
//                        if (request.getMessage().getNewChatTitle().isEmpty()) {
//                            defaultMsg(chatInfo, "Извините я пока не умею с этим работать.");
//                        }
//                        return;
//                    }
//
//                    switch (requestMessage.getText()) {
//                        case "/start" ->
//                                telegramBotSend.sendPhoto(new ChatInfo("AutoHi", chatId, threadId, requestMessage.getMessageId()),
//                                        fileService.getFile("hi")
//                                        , """
//                                                Напишите команду для показа списка мыслей:\s
//                                                 /link - подписаться на уведомления от сервиса учета трудозатрат\s
//                                                 /stop - отвязать аккаунт от уведомлений""");
//                        case "/link" -> getLink(chatInfo);
//                        case "/menu" -> {
//                            telegramBotSend.deleteMessage(chatInfo);
//                            chatInfo.setAuthor("Autoresponder");
//                            menuService.openMainMenu(chatInfo);
//                        }
//                        case "/stop" -> getStop(chatInfo);
//                        default -> {
//                            if (requestMessage.getText().equals("/link@" + botName)) {
//                                getLink(chatInfo);
//                            } else if (requestMessage.getText().equals("/menu@" + botName)) {
//                                telegramBotSend.deleteMessage(chatInfo);
//                                menuService.openMainMenu(new ChatInfo("Autoresponder", chatId, threadId, null));
//                            } else {
//                                String lastCommand = userLastCommand.get(requestMessage.getChatId());
//                                if (lastCommand != null
//                                        && !requestMessage.getText().startsWith("/")
//                                        && (lastCommand.startsWith("/link") || lastCommand.equals(CommandType.LINK.toString()))) {
//                                    try {
//
//                                        Integer code = Integer.parseInt(requestMessage.getText());
//                                        ResultMes resultMes = new ResultMes();
//                                                // todo вынести
//                                        // userServiceIntegration.linkCodeTelegram(code, requestMessage.getChatId(), requestMessage.getMessageThreadId());
//                                        if (resultMes.isOk()) {
//                                            telegramBotSend.deleteMessage(chatInfo);
//                                            defaultMsg(chatInfo, "Вы успешно подключены к оповещениям");
//                                            userLastCommand.remove(requestMessage.getChatId());
//                                        } else {
//                                            defaultMsg(chatInfo, resultMes.getMessage());
//                                        }
//                                    } catch (NumberFormatException ex) {
//                                        defaultMsg(chatInfo, "Код должен быть числом");
//                                    } catch (ResourceNotFoundRunTime ex) {
//                                        defaultMsg(chatInfo, ex.getMessage());
//                                    }
//
//                                } else
//                                    defaultMsg(chatInfo, "Я записал вашу мысль, не знаю что с ней делать.) \n ");
//                            }
//                        }
//                    }
//                } catch (TelegramApiException e) {
//                    throw new RuntimeException(e);
//                }
//                log.info(requestMessage.getText());
//
//                if (requestMessage.getText().startsWith("/")) {
//                    log.info("Команда: ");
//                    userLastCommand.put(requestMessage.getChatId(), requestMessage.getText());
//                    log.info(requestMessage.getText());
//                } else {
//                    log.info("Сообщение: ");
//                    log.info(requestMessage.getText());
//                }
//            } else if (request.hasCallbackQuery()) {
//                CallbackQuery callbackQuery = request.getCallbackQuery();
//                MaybeInaccessibleMessage requestMessage = callbackQuery.getMessage();
//                Integer threadId = callbackQuery.getMessage() instanceof Message ? ((Message) callbackQuery.getMessage()).getMessageThreadId() : null;
//                MessageReceive messageReceive = messageReceiveService.saveMessageReceive(
//                        new MessageReceive(
//                                requestMessage.getChatId(),
//                                threadId,
//                                callbackQuery.getData(),
//                                "@" + callbackQuery.getFrom().getUserName(),
//                                callbackQuery.getFrom().getFirstName(),
//                                callbackQuery.getFrom().getLastName(),
//                                requestMessage.getChat().getTitle(),
//                                requestMessage.getChat().getType(),
//                                requestMessage.getChat().getIsForum(),
//                                requestMessage.getChat().isChannelChat(),
//                                requestMessage.getChat().isUserChat(),
//                                requestMessage.getChat().isGroupChat(),
//                                requestMessage.getChat().isSuperGroupChat()));
//                ChatInfo chatInfo = new ChatInfo(
//                        messageReceive.getUserName(),
//                        Long.toString(messageReceive.getChatId()),
//                        messageReceive.getThreadId(),
//                        requestMessage.getMessageId());
//                try {
//                    if (CommandType.STOP.equals(CommandType.valueOf(callbackQuery.getData()))) {
//                        try {
//                            telegramBotSend.deleteMessage(chatInfo);
//                            getStop(chatInfo);
//                        } catch (TelegramApiException e) {
//                            throw new RuntimeException(e);
//                        }
//                    } else if (CommandType.LINK.equals(CommandType.valueOf(callbackQuery.getData()))) {
//                        try {
//                            getLink(chatInfo);
//                        } catch (TelegramApiException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                } catch (IllegalArgumentException ex) {
//                    log.info(String.valueOf(ex));
//                }
//
//                try {
//                    menuService.getMenu(chatInfo, callbackQuery.getData(), fileService.getFile("menu"));
//                } catch (TelegramApiException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        });
    }

//    private void getLink(ChatInfo chatInfo) throws TelegramApiException {
//        telegramBotSend.deleteMessage(chatInfo);
//        menuService.openCancel(chatInfo, "Введите одноразовый код:");
//    }

    private void getStop(ChatInfo chatInfo) throws TelegramApiException {
//        todo Вынести
//        try {
//            if (userServiceIntegration.linkDeleteTelegram(Long.parseLong(chatInfo.getChatId()), chatInfo.getThreadId())) {
//                if (chatInfo.getOriginMessageId() == null) {
//                    defaultMsg(chatInfo, "Что-то пошло не так как хотелось бы.");
//                } else {
//                    telegramBotSend.editMessage(chatInfo, "Вы успешно отключены", null);
//                }
//            } else {
//                defaultMsg(chatInfo, "Что-то пошло не так как хотелось бы.");
//            }
//        } catch (ResourceNotFoundRunTime ex) {
//            defaultMsg(chatInfo, "Сервис авторизации временно не доступен попробуйте позже");
//        }
    }

    /**
     * Шаблонный метод отправки сообщения пользователю
     *
//     * @param chatInfo - индификатор чата
//     * @param msg      - сообщение
     */
//    private void defaultMsg(ChatInfo chatInfo, String msg) throws TelegramApiException {
//        chatInfo.setAuthor("Autoresponder");
//        telegramBotSend.sendMessage(chatInfo, msg);
//    }

    @Override
    public void close() { // <-- 8
        messageForAdmin("⚠️ The bot @" + botName + " has stopped");
    }

    private void messageForAdmin(String text) {
        log.error(text);
//        try {
//            telegramBotSend.sendMessageForAdmin(text);
//        } catch (TelegramApiException e) {
//            log.error("Failed to send message while stopping the bot", e);
//        }
    }
}
