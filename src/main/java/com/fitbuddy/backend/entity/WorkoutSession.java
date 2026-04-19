package com.fitbuddy.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private String notes;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "template_id")
    private WorkoutTemplate workoutTemplate;

    @OneToMany(mappedBy = "workoutSession", cascade = CascadeType.ALL)
    private Set<ExerciseLog> exerciseLogs;

    private boolean completed;
    private LocalDateTime completedAt;
}
