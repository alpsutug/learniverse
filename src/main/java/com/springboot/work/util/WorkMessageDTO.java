package com.springboot.work.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkMessageDTO implements Serializable {

    private String code;
    private String text;
    private WorkMessageType type;
    @JsonIgnore
    private String[] args;

}
