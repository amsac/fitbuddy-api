package com.fitbuddy.backend.service;

import com.fitbuddy.backend.dto.CompletedSessionDetailsResponse;
import com.fitbuddy.backend.dto.StartSessionRequest;
import com.fitbuddy.backend.dto.WorkoutSessionResponse;
import com.fitbuddy.backend.dto.SessionDetailsResponse;

import java.util.List;


public interface WorkoutSessionService {
    WorkoutSessionResponse startSession(StartSessionRequest request);
    SessionDetailsResponse getSessionDetails(Long sessionId);
    void completeSession(Long sessionId);
    List<CompletedSessionDetailsResponse> getCompletedSessions(Long userId);
}
