package com.fitbuddy.backend.repository;

import com.fitbuddy.backend.entity.Exercise;
import com.fitbuddy.backend.entity.ExerciseLog;
import com.fitbuddy.backend.entity.SetLog;
import com.fitbuddy.backend.entity.WorkoutSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExerciseLogRepository extends JpaRepository<ExerciseLog, Long> {
    List<ExerciseLog> findByWorkoutSessionId(Long sessionId);
}
