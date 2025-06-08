package com.springboot.work.user.dto;

import com.springboot.work.util.WorkMessageDTO;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
@Builder
public class UserResponseDTO {

    private Long userId;
    private List<WorkMessageDTO> msg;
}
