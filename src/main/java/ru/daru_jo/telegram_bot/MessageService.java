package ru.daru_jo.telegram_bot;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.daru_jo.model.ChatInfo;

public interface MessageService {
    void command(ChatInfo chatInfo, String command) throws TelegramApiException;

    void message(ChatInfo chatInfo, String text) throws TelegramApiException;

    void getMenu(ChatInfo chatInfo, String command) throws TelegramApiException;

}
