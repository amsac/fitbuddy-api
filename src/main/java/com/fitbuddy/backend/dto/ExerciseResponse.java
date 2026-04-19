package com.fitbuddy.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExerciseResponse {

    private Long id;
    private String name;
    private String muscleGroup;
    private String imageUrl;
    private String videoUrl;
    private String overview;
    private String instructions;
    private String externalId;
}