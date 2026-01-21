package ru.daru_jo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;




@NoArgsConstructor

@Data
@Entity
@Table(name = "message_send")
public class MessageSend {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "author")
    private String author;

    @Column(name = "chat_id")
    private String chatId;
    @Column(name = "thread_id")
    private Integer threadId;
    @Column(name = "text", length = 4000)
    private String text;

    public MessageSend(ChatInfo chatInfo, String text) {
        this.author = chatInfo.getAuthor();
        this.chatId = chatInfo.getChatId();
        this.threadId = chatInfo.getThreadId();
        this.text = text;
    }
}
