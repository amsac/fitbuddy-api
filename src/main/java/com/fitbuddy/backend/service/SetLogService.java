package com.fitbuddy.backend.service;

import com.fitbuddy.backend.dto.LogSetRequest;
import com.fitbuddy.backend.dto.SetLogResponse;

public interface SetLogService {
    SetLogResponse logSet(LogSetRequest request);
}