package com.fitbuddy.backend.controller;

import com.fitbuddy.backend.dto.LogSetRequest;
import com.fitbuddy.backend.dto.SetLogResponse;
import com.fitbuddy.backend.service.SetLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/sets")
@RequiredArgsConstructor
public class SetLogController {

    private final SetLogService setLogService;

    @PostMapping
    public SetLogResponse logSet(@RequestBody LogSetRequest request) {
        return setLogService.logSet(request);
    }
}