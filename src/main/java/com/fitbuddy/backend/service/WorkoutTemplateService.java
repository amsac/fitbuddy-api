package com.fitbuddy.backend.service;

import com.fitbuddy.backend.entity.WorkoutTemplate;
import com.fitbuddy.backend.dto.CreateWorkoutTemplateRequest;
import com.fitbuddy.backend.dto.WorkoutTemplateResponse;

import java.util.List;

public interface WorkoutTemplateService {
    WorkoutTemplateResponse createTemplate(CreateWorkoutTemplateRequest request);
    List<WorkoutTemplateResponse> getAllTemplates();
    WorkoutTemplateResponse getTemplateById(Long id);
}