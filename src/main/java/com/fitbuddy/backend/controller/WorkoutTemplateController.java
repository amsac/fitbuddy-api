package com.fitbuddy.backend.controller;

import com.fitbuddy.backend.dto.CreateWorkoutTemplateRequest;
import com.fitbuddy.backend.dto.WorkoutTemplateResponse;
import com.fitbuddy.backend.entity.WorkoutTemplate;
import com.fitbuddy.backend.service.WorkoutTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/templates")
@RequiredArgsConstructor
public class WorkoutTemplateController {

    private final WorkoutTemplateService templateService;

    @PostMapping
    public WorkoutTemplateResponse createTemplate(@RequestBody CreateWorkoutTemplateRequest request) {
        return templateService.createTemplate(request);
    }

    @GetMapping
    public List<WorkoutTemplateResponse> getAllTemplates() {
        return templateService.getAllTemplates();
    }

    @GetMapping("/{id}")
    public WorkoutTemplateResponse getTemplateById(@PathVariable Long id) {
        return templateService.getTemplateById(id);
    }
}