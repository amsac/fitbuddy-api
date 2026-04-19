package com.fitbuddy.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateWorkoutTemplateRequest {

    private String name;
    private String description;
    private String level;
    private Long createdBy; // trainerId

    private List<ExerciseItem> exercises;

    @Data
    public static class ExerciseItem {
        private String externalId;
        private String exerciseId;
        private Integer targetSets;
        private Integer targetReps;
    }
}