package com.springboot.work.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WorkBusinessException.class)
    public ResponseEntity<?> handleWorkBusinessException(WorkBusinessException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("messages", ex.getMessages()); // içinde WorkMessageDTO listesi var
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    /* Genel fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

     */
}
