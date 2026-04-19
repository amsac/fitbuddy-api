package com.fitbuddy.backend.service;

import com.fitbuddy.backend.dto.ExerciseResponse;
import java.util.List;

public interface ExerciseService {
    List<ExerciseResponse> getAllExercises();
}