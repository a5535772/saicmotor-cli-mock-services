package com.example.leave.leave;

import com.example.leave.common.ApiResponse;
import com.example.leave.common.BusinessException;
import com.example.leave.user.UserDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(LeaveService.class);

    private final UserDirectory userDirectory;
    private final AtomicInteger seq = new AtomicInteger(0);
    private final Map<String, List<LeaveApplication>> applications = new ConcurrentHashMap<>();

    public LeaveService(UserDirectory userDirectory) {
        this.userDirectory = userDirectory;
    }

    public ApiResponse<Map<String, Object>> balance(String userId) {
        UserDirectory.UserInfo u = userDirectory.find(userId);
        if (u == null) {
            log.warn("查询年假余额被拒 — user={} 原因=用户不存在", userId);
            throw new BusinessException(404, "用户不存在");
        }
        int used = applications.getOrDefault(userId, List.of())
            .stream().mapToInt(LeaveApplication::days).sum();
        log.debug("年假余额 — user={}({}) annualBalance={} used={}",
                userId, u.empName(), u.annualBalance(), used);
        return ApiResponse.ok(Map.of("annual_balance", u.annualBalance(), "used", used));
    }

    public ApiResponse<Map<String, Object>> submit(String userId, Map<String, String> body) {
        UserDirectory.UserInfo u = userDirectory.find(userId);
        if (u == null) {
            log.warn("请假申请被拒 — user={} 原因=用户不存在", userId);
            throw new BusinessException(404, "用户不存在");
        }

        String start = body.get("start_date");
        String end = body.get("end_date");
        String reason = body.get("reason");
        if (start == null || end == null || reason == null
                || !isDate(start) || !isDate(end)) {
            log.warn("请假申请被拒 — user={}({}) start={} end={} reason={} 原因=参数不完整或日期格式错误",
                    userId, u.empName(), start, end, reason);
            throw new BusinessException(1002, "参数不完整或格式错误");
        }
        if (end.compareTo(start) < 0) {
            log.warn("请假申请被拒 — user={}({}) start={} end={} 原因=结束日期早于开始日期",
                    userId, u.empName(), start, end);
            throw new BusinessException(1001, "结束日期不能早于开始日期");
        }

        LeaveApplication app = new LeaveApplication(
            "APP-" + seq.incrementAndGet(), start, end, reason, "PENDING");
        applications.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(app);
        log.info("创建请假单 — id={} user={}({}) {}~{} days={} reason={} status={}",
                app.id(), userId, u.empName(), start, end, app.days(), reason, app.status());
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
