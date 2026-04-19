package com.fitbuddy.backend.mapper;

import com.fitbuddy.backend.dto.WorkoutTemplateResponse;
import com.fitbuddy.backend.entity.WorkoutTemplate;
import com.fitbuddy.backend.entity.WorkoutTemplateExercise;

import java.util.stream.Collectors;

public class WorkoutTemplateMapper {

    public static WorkoutTemplateResponse toResponse(WorkoutTemplate template) {

        return WorkoutTemplateResponse.builder()
                .id(template.getId())
                .name(template.getName())
                .description(template.getDescription())
                .level(template.getLevel())
                .createdByName(template.getCreatedBy().getName())
                .exercises(
                        template.getExercises().stream()
                                .map(WorkoutTemplateMapper::mapExercise)
                                .collect(Collectors.toList())
                )
                .build();
    }

    private static WorkoutTemplateResponse.ExerciseItem mapExercise(WorkoutTemplateExercise te) {

        return WorkoutTemplateResponse.ExerciseItem.builder()
                .exerciseId(te.getExercise().getId())
                .exerciseName(te.getExercise().getName())
                .overview(te.getExercise().getOverview())
                .videoUrl(te.getExercise().getVideoUrl())
                .targetSets(te.getTargetSets())
                .targetReps(te.getTargetReps())
                .build();
    }
}