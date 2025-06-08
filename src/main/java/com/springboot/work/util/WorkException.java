package com.springboot.work.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public abstract class WorkException extends RuntimeException {
    private List<WorkMessageDTO> messages;
    private HttpStatus httpStatus;

    public WorkException(HttpStatus httpStatus, WorkMessageDTO message) {
        this(httpStatus, List.of(message), null);
    }

    public WorkException(HttpStatus httpStatus, WorkMessageDTO message, Throwable cause) {
        this(httpStatus, List.of(message), cause);
    }

    public WorkException(HttpStatus httpStatus, List<WorkMessageDTO> messages) {
        this(httpStatus, messages, null);
    }

    public WorkException(HttpStatus httpStatus, List<WorkMessageDTO> messages, Throwable cause) {
        super(cause);
        this.messages = messages;
        this.httpStatus = httpStatus;
        translateMessages();
    }

    public void translateMessages() {
        if (this.messages == null) {
            return;
        }


        TranslatorService translatorService =  WorkSpringBeanUtil.getBean(TranslatorService.class);

        for (WorkMessageDTO message : messages) {
            String code = message.getCode();
            // kod varsa ve mesaj yoksa translate edilir:

            if (StringUtils.hasText(code)) {
                String messageText = message.getText();
                if (!StringUtils.hasText(messageText)) {
                    String translatedMessage = translatorService.toLocale(code, message.getArgs());
                    message.setText(translatedMessage);
                }
            }
        }
    }

    @Override
    public String getMessage() {
        return this.messages.stream().map(WorkMessageDTO::getText).collect(Collectors.joining(". "));
    }
}



