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

    public String getAuthor() {
        return author;
    }

    public String getChatId() {
        return chatId;
    }

    public Integer getThreadId() {
        return threadId;
    }

    public Integer getOriginMessageId() {
        return originMessageId;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public void setThreadId(Integer threadId) {
        this.threadId = threadId;
    }

    public void setOriginMessageId(Integer originMessageId) {
        this.originMessageId = originMessageId;
    }
}
