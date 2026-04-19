package com.fitbuddy.backend.dto;

import lombok.Data;

@Data
public class LogSetRequest {
    private Long exerciseLogId;
    private Integer reps;
    private Double weight;
}