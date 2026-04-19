package com.fitbuddy.backend.repository;

import com.fitbuddy.backend.entity.User;
import com.fitbuddy.backend.entity.WorkoutSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}