package com.fitbuddy.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SetLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer setNumber;

    private Integer reps;

    private Double weight;

    @ManyToOne
    @JoinColumn(name = "exercise_log_id")
    private ExerciseLog exerciseLog;
}
