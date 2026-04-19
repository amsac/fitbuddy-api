package com.fitbuddy.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SetLogResponse {
    private Long setId;
    private Integer setNumber;
    private Integer reps;
    private Double weight;
}