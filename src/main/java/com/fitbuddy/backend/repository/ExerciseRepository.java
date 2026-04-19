package com.fitbuddy.backend.repository;

import com.fitbuddy.backend.entity.Exercise;
import com.fitbuddy.backend.entity.WorkoutSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    Optional<Exercise> findByExternalId(String externalId);
}
