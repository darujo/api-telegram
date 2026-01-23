package ru.daru_jo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "message_receive")
public class MessageReceive {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_id")
    private Long chatId;
    @Column(name = "thread_id")
    private Integer threadId;

    @Column(name = "text", length = 4000)
    private String text;

    @Column(name = "user_name")
    private String userName;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "Last_name")
    private String lastName;

    @Column(name = "title")
    private String title;
    @Column(name = "type")
    private String type;

    @Column(name = "is_forum")
    private Boolean isForum;
    @Column(name = "is_channel_chat")
    private Boolean isChannelChat;
    @Column(name = "is_user_char")
    private Boolean isUserChar;
    @Column(name = "is_group_chat")
    private Boolean isGroupChat;
    @Column(name = "is_super_group_chat")
    private Boolean isSuperGroupChat;

    public MessageReceive(Long chatId, Integer threadId, String text, String userName, String firstName, String lastName, String title, String type, Boolean isForum, Boolean isChannelChat, Boolean isUserChar, Boolean isGroupChat, Boolean isSuperGroupChat) {
        this.chatId = chatId;
        this.threadId = threadId;
        this.text = text;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.title = title;
        this.type = type;
        this.isForum = isForum;
        this.isChannelChat = isChannelChat;
        this.isUserChar = isUserChar;
        this.isGroupChat = isGroupChat;
        this.isSuperGroupChat = isSuperGroupChat;
    }

}
