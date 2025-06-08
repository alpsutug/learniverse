package com.springboot.work.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WorkMessageUtil {


    private static WorkMessageDTO createWorkMessage(String code, String text, WorkMessageType type, String... args) {
        return WorkMessageDTO.builder()
                .code(code)
                .text(text)
                .type(type)
                .args(args)
                .build();
    }

    public static WorkMessageDTO createWorkMessageWithCode(String code, WorkMessageType messageType, String... args) {
        return createWorkMessage(code, null, messageType, args);
    }

    public static WorkMessageDTO createWorkMessageWithText(String text, WorkMessageType messageType) {
        return createWorkMessage(null, text, messageType);
    }
}

