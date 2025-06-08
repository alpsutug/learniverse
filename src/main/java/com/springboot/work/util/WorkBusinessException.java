package com.springboot.work.util;

import org.springframework.http.HttpStatus;

import java.util.List;

public class WorkBusinessException extends WorkException {

    public WorkBusinessException(WorkMessageDTO message) {
        super(HttpStatus.CONFLICT, message);
    }

    public WorkBusinessException(WorkMessageDTO message, Throwable cause) {
        super(HttpStatus.CONFLICT, message);
    }

    public WorkBusinessException(List<WorkMessageDTO> messages) {
        super(HttpStatus.CONFLICT, messages);
    }

    public WorkBusinessException(List<WorkMessageDTO> messages, Throwable cause) {
        super(HttpStatus.CONFLICT, messages);}

}
