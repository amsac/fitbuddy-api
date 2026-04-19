package com.fitbuddy.backend.service.impl;

import com.fitbuddy.backend.dto.LogSetRequest;
import com.fitbuddy.backend.dto.SetLogResponse;
import com.fitbuddy.backend.entity.ExerciseLog;
import com.fitbuddy.backend.entity.SetLog;
import com.fitbuddy.backend.repository.ExerciseLogRepository;
import com.fitbuddy.backend.repository.SetLogRepository;
import com.fitbuddy.backend.service.SetLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SetLogServiceImpl implements SetLogService {

    private final SetLogRepository setLogRepository;
    private final ExerciseLogRepository exerciseLogRepository;

    @Override
    public SetLogResponse logSet(LogSetRequest request) {

        // 1. Fetch ExerciseLog
        ExerciseLog exerciseLog = exerciseLogRepository.findById(request.getExerciseLogId())
                .orElseThrow(() -> new RuntimeException("ExerciseLog not found"));

        // 2. Get existing sets
        List<SetLog> existingSets = setLogRepository.findByExerciseLogId(request.getExerciseLogId());

        // 3. Calculate setNumber
        int nextSetNumber = existingSets.size() + 1;

        // 4. Create SetLog
        SetLog setLog = SetLog.builder()
                .exerciseLog(exerciseLog)
                .setNumber(nextSetNumber)
                .reps(request.getReps())
                .weight(request.getWeight())
                .build();

        setLog = setLogRepository.save(setLog);

        // 5. Return response
        return SetLogResponse.builder()
                .setId(setLog.getId())
                .setNumber(setLog.getSetNumber())
                .reps(setLog.getReps())
                .weight(setLog.getWeight())
                .build();
    }
}