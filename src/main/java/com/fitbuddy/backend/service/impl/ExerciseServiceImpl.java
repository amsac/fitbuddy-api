package com.fitbuddy.backend.service.impl;

import com.fitbuddy.backend.dto.ExerciseResponse;
import com.fitbuddy.backend.entity.Exercise;
import com.fitbuddy.backend.repository.ExerciseRepository;
import com.fitbuddy.backend.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciseServiceImpl implements ExerciseService {

    private final ExerciseRepository exerciseRepository;

    @Override
    public List<ExerciseResponse> getAllExercises() {

        List<Exercise> exercises = exerciseRepository.findAll();

        return exercises.stream()
                .map(e -> ExerciseResponse.builder()
                        .id(e.getId())
                        .name(e.getName())
                        .muscleGroup(e.getMuscleGroup())
                        .imageUrl(e.getImageUrl())
                        .videoUrl(e.getVideoUrl())
                        .overview(e.getOverview())
                        .instructions(e.getInstructions())
                        .externalId(e.getExternalId())
                        .build())
                .collect(Collectors.toList());
    }
}