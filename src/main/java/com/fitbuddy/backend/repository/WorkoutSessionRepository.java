package com.fitbuddy.backend.repository;

import com.fitbuddy.backend.entity.WorkoutSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {
    @Query("""
    SELECT ws FROM WorkoutSession ws
    LEFT JOIN FETCH ws.exerciseLogs el
    LEFT JOIN FETCH el.setLogs
    LEFT JOIN FETCH el.exercise
    LEFT JOIN FETCH ws.workoutTemplate wt
    WHERE ws.id = :sessionId
""")
    Optional<WorkoutSession> findSessionWithDetails(Long sessionId);

    @Query("""
    SELECT DISTINCT ws FROM WorkoutSession ws
    LEFT JOIN FETCH ws.exerciseLogs el
    LEFT JOIN FETCH el.setLogs
    LEFT JOIN FETCH el.exercise
    LEFT JOIN FETCH ws.workoutTemplate
    WHERE ws.user.id = :userId
    AND ws.completed = true
""")
    List<WorkoutSession> findCompletedSessionsWithDetails(Long userId);
    @Query("""
    SELECT ws FROM WorkoutSession ws
    LEFT JOIN FETCH ws.workoutTemplate
    WHERE ws.user.id = :userId
    AND ws.completed = true
""")
    List<WorkoutSession> findCompletedSessionsBasic(Long userId);
}