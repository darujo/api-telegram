package ru.daru_jo.telegram_bot;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.ReplyParameters;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeAllGroupChats;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeAllPrivateChats;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.daru_jo.model.ChatInfo;
import ru.daru_jo.model.MessageSend;
import ru.daru_jo.service.MessageSendService;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component


public class TelegramBotSend {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TelegramBotSend.class);
    private final OkHttpTelegramClient tgClient;

    private MessageSendService messageSendService;

    @Autowired
    public void setMessageSendService(MessageSendService messageSendService) {
        this.messageSendService = messageSendService;
    }

    public TelegramBotSend(@Value("${telegram-bot.token}") String botToken) {
        tgClient = new OkHttpTelegramClient(botToken);
    }


    public Message sendMessage(ChatInfo chatInfo, String text) throws TelegramApiException {
        return sendMessage(chatInfo, text, null);
    }

    public void sendPhoto(ChatInfo chatInfo, File file, String text) throws TelegramApiException {
        sendPhoto(chatInfo, file, text, null);
    }


    @PostConstruct
    public void setCommand() {
        List<BotCommand> botCommands = new ArrayList<>();
        botCommands.add(new BotCommand("/menu", "Открыть меню"));
        botCommands.add(new BotCommand("/stop", "Отвязать аккаунт от уведомлений"));
        botCommands.add(new BotCommand("/link", "Подписаться на уведомления от сервиса трудо затрат"));
        SetMyCommands setMyCommands = new SetMyCommands(botCommands);
        setMyCommands.setScope(new BotCommandScopeAllPrivateChats());
        try {
            tgClient.execute(setMyCommands);

        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        setMyCommands.setScope(new BotCommandScopeAllGroupChats());
        try {
            tgClient.execute(setMyCommands);

        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public String getName() {
        GetMe getMyName = new GetMe();

        try {
            User user = tgClient.execute(getMyName);
            return user.getUserName();
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }

    public void sendPhoto(ChatInfo chatInfo, File file, String text, InlineKeyboardMarkup menu) throws TelegramApiException {
        SendPhoto message = new SendPhoto(chatInfo.getChatId(), new InputFile(file));

        message.setMessageThreadId(chatInfo.getThreadId());
        if (!text.isEmpty()) {
            message.setCaption(text);
            message.setReplyMarkup(menu);
        }
        try {
            tgClient.execute(message);
        } catch (TelegramApiException e) {
            sendMessage(chatInfo, text);
        }
    }

    public void sendDocument(ChatInfo chatInfo, String fileName, File file, String text) throws TelegramApiException {

        SendDocument message = new SendDocument(chatInfo.getChatId(), new InputFile(file, fileName));
        message.setMessageThreadId(chatInfo.getThreadId());
        message.setReplyToMessageId(chatInfo.getOriginMessageId());
        if (!text.isEmpty()) {
            message.setCaption(text);
        }
        try {
            tgClient.execute(message);
        } catch (TelegramApiException e) {
            sendMessage(chatInfo, text);
        }
    }

    public Message sendMessage(ChatInfo chatInfo, String text, InlineKeyboardMarkup menu) throws TelegramApiException {
        SendMessage message = new SendMessage(chatInfo.getChatId(), text);

        message.setMessageThreadId(chatInfo.getThreadId());
        message.enableHtml(true);
//
        if (chatInfo.getOriginMessageId() != null) {
            message.setReplyParameters(
                    ReplyParameters
                            .builder()
//                        .chatId(chatInfo.getChatId())
                            .messageId(chatInfo.getOriginMessageId())
                            .build());
        }
        message.setReplyMarkup(menu);
        Message messageSend = tgClient.execute(message);
        messageSendService.saveMessageSend(new MessageSend(chatInfo, text));
        return messageSend;
    }

    @Value("${telegram-bot.admin-id}")
    private String adminId;

    public void sendMessageForAdmin(String text) throws TelegramApiException {
        sendMessage(new ChatInfo(null, adminId, null, null), text);
    }

    public void deleteMessage(ChatInfo chatInfo) throws TelegramApiException {
        if (chatInfo.getOriginMessageId() == null) {
            return;
        }
        DeleteMessage delete = new DeleteMessage(chatInfo.getChatId(), chatInfo.getOriginMessageId());
        chatInfo.setOriginMessageId(null);
        tgClient.execute(delete);
    }

    public void editMessage(ChatInfo chatInfo, String newText, InlineKeyboardMarkup menu) throws TelegramApiException {
        EditMessageText edit = new EditMessageText(newText);
        edit.setChatId(chatInfo.getChatId());
        edit.setMessageId(chatInfo.getOriginMessageId());
        edit.setText(newText);
        edit.setReplyMarkup(menu);

        tgClient.execute(edit);
    }

    public void EditPhoto(ChatInfo chatInfo, String newText, InlineKeyboardMarkup menu, File file) throws TelegramApiException {
        EditMessageMedia edit = new EditMessageMedia(new InputMediaPhoto(file, "menu.jpg"));
        edit.setChatId(chatInfo.getChatId());
        edit.setMessageId(chatInfo.getOriginMessageId());
        edit.getMedia().setCaption(newText);
//        edit.(newText);
        edit.setReplyMarkup(menu);
        tgClient.execute(edit);
    }

    public boolean SendAction(ChatInfo chatInfo) {
        return SendAction(chatInfo, ActionType.TYPING);

    }

    public boolean SendAction(ChatInfo chatInfo, ActionType actionType) {
        SendChatAction sendChatAction = new SendChatAction(chatInfo.getChatId(), actionType.toString());
        sendChatAction.setChatId(chatInfo.getChatId());
        sendChatAction.setMessageThreadId(chatInfo.getThreadId());

        try {
            return tgClient.execute(sendChatAction);
        } catch (TelegramApiException e) {
            log.error("{}  {}", e.getMessage(), Arrays.toString(e.getStackTrace()));
        }
        return false;
    }
}