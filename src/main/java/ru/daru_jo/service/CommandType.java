package ru.daru_jo.service;

import lombok.Getter;
//import ru.darujo.type.TypeEnum;

@Getter
public enum CommandType
//        implements TypeEnum
{
    LINK("Привязать аккаунт"),
    STOP ("Убрать оповещения"),
    REPORT ("Отчеты",false,true),
    SEND_ME("Мне", true),
    SEND_ALL("Подписанным на уведомления", true),
    CANCEL("Отменить");


    private final String name;
    private final Boolean newParam;
    private final Boolean availParam;
    CommandType(String name) {
        this(name , false);
    }


    CommandType(String name, Boolean availParam) {
        this(name , availParam,false);
    }
    CommandType(String name,  Boolean availParam, Boolean newParam) {
        this.name = name;
        this.newParam = newParam;
        this.availParam = availParam;
    }
}
