package com.example.leave.attendance;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.leave.common.ApiResponse;
import com.example.leave.common.BusinessException;

@Service
public class AttendanceService {

    private static final Logger log = LoggerFactory.getLogger(AttendanceService.class);

    private final Map<String, List<CorrectionApplication>> corrections = new ConcurrentHashMap<>();
    private final AtomicInteger seq = new AtomicInteger(0);

    public ApiResponse<Map<String, Object>> records(String userId) {
        Map<String, Object> data = Map.of(
            "work_days", 22,
            "late_days", 1,
            "early_days", 0
        );
        log.debug("考勤记录 — user={} data={}", userId, data);
        return ApiResponse.ok(data);
    }

    public ApiResponse<Map<String, Object>> submit(String userId, Map<String, String> body) {
        String date = body.get("date");
        String reason = body.get("reason");
        if (date == null || reason == null || !isDate(date)) {
            log.warn("补卡申请被拒 — user={} date={} reason={} 原因=参数不完整或日期格式错误",
                    userId, date, reason);
            throw new BusinessException(1002, "参数不完整或格式错误");
        }

        CorrectionApplication app = new CorrectionApplication(
            "COR-" + seq.incrementAndGet(), date, reason, "PENDING");
        corrections.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(app);
        log.info("创建补卡单 — id={} user={} date={} reason={} status={}",
                app.id(), userId, date, reason, app.status());
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
