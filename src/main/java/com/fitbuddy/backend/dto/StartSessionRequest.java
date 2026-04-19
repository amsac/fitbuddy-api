package com.fitbuddy.backend.dto;

import lombok.Data;

@Data
public class StartSessionRequest {
    private Long userId;
    private Long templateId;
}