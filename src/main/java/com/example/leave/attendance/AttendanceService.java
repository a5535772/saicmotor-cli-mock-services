package com.example.leave.attendance;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

import com.example.leave.common.ApiResponse;
import com.example.leave.common.BusinessException;

@Service
public class AttendanceService {

    private final Map<String, List<CorrectionApplication>> corrections = new ConcurrentHashMap<>();
    private final AtomicInteger seq = new AtomicInteger(0);

    public ApiResponse<Map<String, Object>> records(String userId) {
        return ApiResponse.ok(Map.of(
            "work_days", 22,
            "late_days", 1,
            "early_days", 0
        ));
    }

    public ApiResponse<Map<String, Object>> submit(String userId, Map<String, String> body) {
        String date = body.get("date");
        String reason = body.get("reason");
        if (date == null || reason == null || !isDate(date)) {
            throw new BusinessException(1002, "参数不完整或格式错误");
        }

        CorrectionApplication app = new CorrectionApplication(
            "COR-" + seq.incrementAndGet(), date, reason, "PENDING");
        corrections.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(app);
        return ApiResponse.ok(Map.of("correction_id", app.id(), "status", app.status()));
    }

    private static boolean isDate(String s) {
        try {
            LocalDate.parse(s);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}