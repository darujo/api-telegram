package ru.daru_jo.model;

import lombok.Getter;
import lombok.Setter;

@Getter
public class ChatInfo {

    @Setter
    private String author;
    @Setter
    private String chatId;
    @Setter
    private Integer threadId;
    @Setter
    private Integer originMessageId;

    public ChatInfo(String author, String chatId, Integer threadId, Integer originMessageId) {
        this.author = author;
        this.chatId = chatId;
        this.threadId = threadId;
        this.originMessageId = originMessageId;
    }

}
