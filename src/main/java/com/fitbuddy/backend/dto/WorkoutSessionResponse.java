package com.fitbuddy.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class WorkoutSessionResponse {

    private Long sessionId;
    private LocalDate date;
    private String templateName;

    private List<ExerciseItem> exercises;

    @Data
    @Builder
    public static class ExerciseItem {
        private Long exerciseLogId;
        private String exerciseName;
        private String videoUrl;
        private String overview;
        private Integer targetSets;
        private Integer targetReps;
    }
}