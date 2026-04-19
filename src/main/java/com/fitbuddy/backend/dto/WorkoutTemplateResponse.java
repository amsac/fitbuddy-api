package com.fitbuddy.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WorkoutTemplateResponse {

    private Long id;
    private String name;
    private String description;
    private String level;

    private String createdByName;

    private List<ExerciseItem> exercises;

    @Data
    @Builder
    public static class ExerciseItem {
        private Long exerciseId;
        private String exerciseName;
        private String overview;
        private String videoUrl;
        private Integer targetSets;
        private Integer targetReps;
    }
}