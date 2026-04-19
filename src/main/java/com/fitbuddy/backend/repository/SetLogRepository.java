package com.fitbuddy.backend.repository;

import com.fitbuddy.backend.entity.SetLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SetLogRepository extends JpaRepository<SetLog, Long> {

    List<SetLog> findByExerciseLogId(Long exerciseLogId);

    List<SetLog> findByExerciseLogIdOrderBySetNumberAsc(Long id);
}