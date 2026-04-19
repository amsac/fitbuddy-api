package com.fitbuddy.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class SessionDetailsResponse {

    private Long sessionId;
    private LocalDate date;
    private String templateName;

    private List<ExerciseItem> exercises;

    @Data
    @Builder
    public static class ExerciseItem {
        private Long exerciseLogId;
        private String videoUrl;
        private String overview;
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