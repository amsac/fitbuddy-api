package com.fitbuddy.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CompletedSessionDetailsResponse {

    private Long sessionId;
    private String templateName;

    private LocalDate date;
    private LocalDateTime completedAt;

    private List<ExerciseItem> exercises;

    @Data
    @Builder
    public static class ExerciseItem {
        private String exerciseName;
        private List<SetItem> sets;
    }

    @Data
    @Builder
    public static class SetItem {
        private Integer setNumber;
        private Integer reps;
        private Double weight;
    }
}