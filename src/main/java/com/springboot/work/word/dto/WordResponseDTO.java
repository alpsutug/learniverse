package com.springboot.work.word.dto;

import com.springboot.work.util.WorkMessageDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WordResponseDTO {
    private Long wordId;
    private List<WorkMessageDTO> msg;
}
