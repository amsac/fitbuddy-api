package com.fitbuddy.backend.repository;

import com.fitbuddy.backend.entity.WorkoutTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WorkoutTemplateRepository extends JpaRepository<WorkoutTemplate, Long> {
    @Query("""
    SELECT DISTINCT wt FROM WorkoutTemplate wt
    LEFT JOIN FETCH wt.exercises te
    LEFT JOIN FETCH te.exercise
    LEFT JOIN FETCH wt.createdBy
""")
    List<WorkoutTemplate> findAllWithExercises();
    @Query("""
    SELECT wt FROM WorkoutTemplate wt
    LEFT JOIN FETCH wt.exercises te
    LEFT JOIN FETCH te.exercise
    LEFT JOIN FETCH wt.createdBy
    WHERE wt.id = :id
""")
    Optional<WorkoutTemplate> findByIdWithExercises(Long id);
}