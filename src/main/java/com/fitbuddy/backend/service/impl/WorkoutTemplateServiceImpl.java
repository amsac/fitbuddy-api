package com.fitbuddy.backend.service.impl;

import com.fitbuddy.backend.dto.CreateWorkoutTemplateRequest;
import com.fitbuddy.backend.dto.WorkoutTemplateResponse;
import com.fitbuddy.backend.entity.*;
import com.fitbuddy.backend.mapper.WorkoutTemplateMapper;
import com.fitbuddy.backend.repository.*;
import com.fitbuddy.backend.service.WorkoutTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkoutTemplateServiceImpl implements WorkoutTemplateService {

    private final WorkoutTemplateRepository templateRepository;
    private final WorkoutTemplateExerciseRepository templateExerciseRepository;
    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;

    @Override
    public WorkoutTemplateResponse createTemplate(CreateWorkoutTemplateRequest request) {
        System.out.println("------Trainer ID: " + request.getCreatedBy());

//        userRepository.findAll().forEach(user -> {
//            System.out.println(
//                    "ID: " + user.getId() +
//                            ", Name: " + user.getName() +
//                            ", Email: " + user.getEmail()
//            );
//        });

        User trainer = userRepository.findById(request.getCreatedBy())
                .orElseThrow(() -> new RuntimeException("Trainer not found"));

        WorkoutTemplate template = WorkoutTemplate.builder()
                .name(request.getName())
                .description(request.getDescription())
                .level(request.getLevel())
                .createdBy(trainer)
                .build();

        template = templateRepository.save(template);

        List<WorkoutTemplateExercise> templateExercises = new ArrayList<>();

        for (CreateWorkoutTemplateRequest.ExerciseItem item : request.getExercises()) {

            Exercise exercise = exerciseRepository.findByExternalId(item.getExternalId())
                    .orElseThrow(() -> new RuntimeException("Exercise not found"));

            WorkoutTemplateExercise te = WorkoutTemplateExercise.builder()
                    .workoutTemplate(template)
                    .exercise(exercise)
                    .targetSets(item.getTargetSets())
                    .targetReps(item.getTargetReps())
                    .build();

            templateExercises.add(te);
        }

        templateExerciseRepository.saveAll(templateExercises);

        template.setExercises(templateExercises);

        return  WorkoutTemplateMapper.toResponse(template);
    }
    @Override
    public List<WorkoutTemplateResponse> getAllTemplates() {

        List<WorkoutTemplate> templates = templateRepository.findAllWithExercises();

        return templates.stream()
                .map(WorkoutTemplateMapper::toResponse)
                .toList();
    }
    @Override
    public WorkoutTemplateResponse getTemplateById(Long id) {

        WorkoutTemplate template = templateRepository.findByIdWithExercises(id)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        return WorkoutTemplateMapper.toResponse(template);
    }
}