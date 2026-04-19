package com.fitbuddy.backend.service.impl;

import com.fitbuddy.backend.dto.CompletedSessionDetailsResponse;
import com.fitbuddy.backend.dto.SessionDetailsResponse;
import com.fitbuddy.backend.dto.StartSessionRequest;
import com.fitbuddy.backend.dto.WorkoutSessionResponse;
import com.fitbuddy.backend.entity.*;
import com.fitbuddy.backend.repository.*;
import com.fitbuddy.backend.service.WorkoutSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkoutSessionServiceImpl implements WorkoutSessionService {

    private final UserRepository userRepository;
    private final WorkoutTemplateRepository templateRepository;
    private final WorkoutSessionRepository sessionRepository;
    private final ExerciseLogRepository exerciseLogRepository;
    private final SetLogRepository setLogRepository;

    @Override
    public WorkoutSessionResponse startSession(StartSessionRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        WorkoutTemplate template = templateRepository.findById(request.getTemplateId())
                .orElseThrow(() -> new RuntimeException("Template not found"));

        // 1. Create session
        WorkoutSession session = WorkoutSession.builder()
                .user(user)
                .workoutTemplate(template)
                .date(LocalDate.now())
                .build();

        session = sessionRepository.save(session);

        // 2. Create ExerciseLogs
        List<ExerciseLog> logs = new ArrayList<>();

        for (WorkoutTemplateExercise te : template.getExercises()) {

            ExerciseLog log = ExerciseLog.builder()
                    .workoutSession(session)
                    .exercise(te.getExercise())
                    .build();

            logs.add(log);
        }

        logs = exerciseLogRepository.saveAll(logs);

        // 3. Build response
        List<WorkoutSessionResponse.ExerciseItem> responseExercises = new ArrayList<>();

        for (int i = 0; i < logs.size(); i++) {

            ExerciseLog log = logs.get(i);
            WorkoutTemplateExercise te = template.getExercises().get(i);

            responseExercises.add(
                    WorkoutSessionResponse.ExerciseItem.builder()
                            .exerciseLogId(log.getId())
                            .exerciseName(log.getExercise().getName())
                            .videoUrl(log.getExercise().getVideoUrl())
                            .overview((log.getExercise().getOverview()))
                            .targetSets(te.getTargetSets())
                            .targetReps(te.getTargetReps())
                            .build()
            );
        }

        return WorkoutSessionResponse.builder()
                .sessionId(session.getId())
                .date(session.getDate())
                .templateName(template.getName())
                .exercises(responseExercises)
                .build();
    }

    @Override
    public SessionDetailsResponse getSessionDetails(Long sessionId) {

        WorkoutSession session = sessionRepository.findSessionWithDetails(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        List<SessionDetailsResponse.ExerciseItem> exerciseItems = new ArrayList<>();

        for (ExerciseLog log : session.getExerciseLogs()) {

            List<SessionDetailsResponse.SetItem> setItems = new ArrayList<>();

            for (SetLog set : log.getSetLogs()) {
                setItems.add(
                        SessionDetailsResponse.SetItem.builder()
                                .setNumber(set.getSetNumber())
                                .reps(set.getReps())
                                .weight(set.getWeight())
                                .build()
                );
            }

            exerciseItems.add(
                    SessionDetailsResponse.ExerciseItem.builder()
                            .exerciseLogId(log.getId())
                            .exerciseName(log.getExercise().getName())
                            .videoUrl(log.getExercise().getVideoUrl())
                            .overview((log.getExercise().getOverview()))
                            .sets(setItems)
                            .build()
            );
        }

        return SessionDetailsResponse.builder()
                .sessionId(session.getId())
                .date(session.getDate())
                .templateName(session.getWorkoutTemplate().getName())
                .exercises(exerciseItems)
                .build();
    }

    public void completeSession(Long sessionId) {

        WorkoutSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        session.setCompleted(true);
        session.setCompletedAt(LocalDateTime.now());

        sessionRepository.save(session);
    }
    @Override
    public List<CompletedSessionDetailsResponse> getCompletedSessions(Long userId) {

        List<WorkoutSession> sessions =
                sessionRepository.findCompletedSessionsBasic(userId);

        return sessions.stream().map(session -> {

            // 🔥 fetch exerciseLogs separately
            List<ExerciseLog> logs =
                    exerciseLogRepository.findByWorkoutSessionId(session.getId());

            List<CompletedSessionDetailsResponse.ExerciseItem> exerciseItems = new ArrayList<>();

            for (ExerciseLog log : logs) {

                // fetch sets
                List<SetLog> sets =
                        setLogRepository.findByExerciseLogIdOrderBySetNumberAsc(log.getId());

                List<CompletedSessionDetailsResponse.SetItem> setItems = sets.stream()
                        .map(set -> CompletedSessionDetailsResponse.SetItem.builder()
                                .setNumber(set.getSetNumber())
                                .reps(set.getReps())
                                .weight(set.getWeight())
                                .build())
                        .toList();

                exerciseItems.add(
                        CompletedSessionDetailsResponse.ExerciseItem.builder()
                                .exerciseName(log.getExercise().getName())
                                .sets(setItems)
                                .build()
                );
            }

            return CompletedSessionDetailsResponse.builder()
                    .sessionId(session.getId())
                    .templateName(session.getWorkoutTemplate().getName())
                    .date(session.getDate())
                    .completedAt(session.getCompletedAt())
                    .exercises(exerciseItems)
                    .build();

        }).toList();
    }
}