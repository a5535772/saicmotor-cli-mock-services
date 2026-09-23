package com.example.leave.attendance;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.leave.common.ApiResponse;
import com.example.leave.user.UserDirectory;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    private static final Logger log = LoggerFactory.getLogger(AttendanceController.class);

    private final AttendanceService service;
    private final UserDirectory userDirectory;

    public AttendanceController(AttendanceService service, UserDirectory userDirectory) {
        this.service = service;
        this.userDirectory = userDirectory;
    }

    @GetMapping("/records")
    public ResponseEntity<ApiResponse<Map<String, Object>>> records(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        if (userId == null) {
            log.warn("GET /attendance/records — 拒绝访问 原因=缺少 X-User-Id");
            return ResponseEntity.status(401).body(ApiResponse.<Map<String, Object>>fail(401, "未登录或会话已过期"));
        }
        log.info("查询考勤记录 — user={}", describe(userId));
        return ResponseEntity.ok(service.records(userId));
    }

    @PostMapping("/corrections")
    public ResponseEntity<ApiResponse<Map<String, Object>>> submit(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestBody Map<String, String> body) {
        if (userId == null) {
            log.warn("POST /attendance/corrections — 拒绝访问 原因=缺少 X-User-Id");
            return ResponseEntity.status(401).body(ApiResponse.<Map<String, Object>>fail(401, "未登录或会话已过期"));
        }
        log.info("提交补卡申请 — user={} date={} reason={}",
                describe(userId), body.get("date"), body.get("reason"));
        return ResponseEntity.ok(service.submit(userId, body));
    }

    private String describe(String userId) {
        UserDirectory.UserInfo u = userDirectory.find(userId);
        return u == null ? userId : userId + "(" + u.empName() + ")";
    }
}
