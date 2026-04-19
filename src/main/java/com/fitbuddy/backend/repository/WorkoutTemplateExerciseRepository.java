package com.fitbuddy.backend.repository;

import com.fitbuddy.backend.entity.WorkoutTemplateExercise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutTemplateExerciseRepository extends JpaRepository<WorkoutTemplateExercise, Long> {
}