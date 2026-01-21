package ru.daru_jo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.daru_jo.model.MessageSend;
import ru.daru_jo.repository.MessageSendRepository;

@Service
public class MessageSendService {

    private MessageSendRepository messageSendRepository;

    @Autowired
    public void setMessageSendRepository(MessageSendRepository messageSendRepository) {
        this.messageSendRepository = messageSendRepository;
    }


    public void saveMessageSend(MessageSend messageSend) {
        messageSendRepository.save(messageSend);
    }


}
