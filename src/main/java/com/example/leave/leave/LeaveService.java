package com.example.leave.leave;

import com.example.leave.common.ApiResponse;
import com.example.leave.common.BusinessException;
import com.example.leave.user.UserDirectory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LeaveService {

    private final UserDirectory userDirectory;
    private final AtomicInteger seq = new AtomicInteger(0);
    private final Map<String, List<LeaveApplication>> applications = new ConcurrentHashMap<>();

    public LeaveService(UserDirectory userDirectory) {
        this.userDirectory = userDirectory;
    }

    public ApiResponse<Map<String, Object>> balance(String userId) {
        UserDirectory.UserInfo u = userDirectory.find(userId);
        if (u == null) throw new BusinessException(404, "用户不存在");
        int used = applications.getOrDefault(userId, List.of())
            .stream().mapToInt(LeaveApplication::days).sum();
        return ApiResponse.ok(Map.of("annual_balance", u.annualBalance(), "used", used));
    }

    public ApiResponse<Map<String, Object>> submit(String userId, Map<String, String> body) {
        UserDirectory.UserInfo u = userDirectory.find(userId);
        if (u == null) throw new BusinessException(404, "用户不存在");

        String start = body.get("start_date");
        String end = body.get("end_date");
        String reason = body.get("reason");
        if (start == null || end == null || reason == null
                || !isDate(start) || !isDate(end)) {
            throw new BusinessException(1002, "参数不完整或格式错误");
        }
        if (end.compareTo(start) < 0) {
            throw new BusinessException(1001, "结束日期不能早于开始日期");
        }

        LeaveApplication app = new LeaveApplication(
            "APP-" + seq.incrementAndGet(), start, end, reason, "PENDING");
        applications.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(app);
        return ApiResponse.ok(Map.of("application_id", app.id(), "status", app.status()));
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