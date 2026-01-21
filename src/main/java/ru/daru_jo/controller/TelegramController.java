package ru.daru_jo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.daru_jo.model.ChatInfo;
import ru.daru_jo.service.FileService;
import ru.daru_jo.telegram_bot.TelegramBotSend;

import java.io.File;

@RestController
@RequestMapping("v1/${app.http.bot}")
@SuppressWarnings("unused")
public class TelegramController {

    private TelegramBotSend telegramBotSend;

    @Autowired
    public void setTelegramBotSend(TelegramBotSend telegramBotSend) {
        this.telegramBotSend = telegramBotSend;
    }

    private FileService fileService;

    @Autowired
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(value = "/{chatId}/notifications", consumes = MediaType.TEXT_PLAIN_VALUE)
    public void sendMessageToTelegram(@RequestHeader String username,
                                      @PathVariable String chatId,
                                      @RequestParam(required = false) Integer threadId,
                                      @RequestParam(required = false) Integer originMessageId,
                                      @RequestBody String text) throws TelegramApiException {
        telegramBotSend.sendMessage(new ChatInfo(username, chatId, threadId, originMessageId), text);
    }

    @PostMapping(value = "/send/admin", consumes = MediaType.TEXT_PLAIN_VALUE)
    public void sendMessageToTelegram(@RequestBody String text) throws TelegramApiException {
        telegramBotSend.sendMessageForAdmin(text);
    }

    @PostMapping(value = "/file", consumes = MediaType.TEXT_PLAIN_VALUE)
    public String addFile(@RequestParam String fileName,
                          @RequestBody String body) {
        return fileService.addFile(fileName, body);
    }

    @PostMapping(value = "/{chatId}/file")
    public void sendFile(@RequestHeader String username,
                         @PathVariable String chatId,
                         @RequestParam(required = false) Integer threadId,
                         @RequestParam(required = false) Integer originMessageId,
                         @RequestParam String fileName,
                         @RequestBody String text) throws TelegramApiException {
        File file = fileService.getFile(fileName);
        telegramBotSend.sendDocument(new ChatInfo(username, chatId, threadId, originMessageId), fileName, file, text);
    }

    @DeleteMapping(value = "/file")
    public void deleteFile(@RequestParam String fileName) {
        fileService.delFile(fileName);
    }
}



