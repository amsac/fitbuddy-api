package com.fitbuddy.backend.controller;

import com.fitbuddy.backend.dto.CompletedSessionDetailsResponse;
import com.fitbuddy.backend.dto.SessionDetailsResponse;
import com.fitbuddy.backend.dto.StartSessionRequest;
import com.fitbuddy.backend.dto.WorkoutSessionResponse;
import com.fitbuddy.backend.entity.WorkoutSession;
import com.fitbuddy.backend.service.WorkoutSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
public class WorkoutSessionController {

    private final WorkoutSessionService sessionService;

    @PostMapping("/start")
    public WorkoutSessionResponse startSession(@RequestBody StartSessionRequest request) {
        return sessionService.startSession(request);
    }
    @GetMapping("/{sessionId}")
    public SessionDetailsResponse getSessionDetails(@PathVariable Long sessionId) {
        return sessionService.getSessionDetails(sessionId);
    }
    @PostMapping("/{id}/complete")
    public void completeSession(@PathVariable Long id) {
        sessionService.completeSession(id);
    }
    @GetMapping("/users/{userId}/completed")
    public List<CompletedSessionDetailsResponse> getCompletedSessions(
            @PathVariable Long userId
    ) {
        return sessionService.getCompletedSessions(userId);
    }
}