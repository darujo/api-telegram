package ru.daru_jo.telegram_bot;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.daru_jo.model.ChatInfo;

import java.io.File;

public interface MenuServiceIner {
    void openMainMenu(ChatInfo chatInfo) throws TelegramApiException;

    void getMenu(ChatInfo chatInfo, String command, File picture) throws TelegramApiException;

    void openCancel(ChatInfo chatInfo, String text);
}
