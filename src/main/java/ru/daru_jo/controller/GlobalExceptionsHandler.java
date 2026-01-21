package ru.daru_jo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import ru.darujo.exceptions.AppError;

@ControllerAdvice
public class GlobalExceptionsHandler {
    @ExceptionHandler
    public ResponseEntity<AppError> catchTelegramApiRequestException(TelegramApiRequestException e){
        return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value() ,e.getMessage()),HttpStatus.NOT_FOUND);
    }

}
