package com.example.leave.leave;

import com.example.leave.common.ApiResponse;
import com.example.leave.user.UserDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/leave")
public class LeaveController {

    private static final Logger log = LoggerFactory.getLogger(LeaveController.class);

    private final LeaveService service;
    private final UserDirectory userDirectory;

    public LeaveController(LeaveService service, UserDirectory userDirectory) {
        this.service = service;
        this.userDirectory = userDirectory;
    }

    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<Map<String, Object>>> balance(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        if (userId == null) {
            log.warn("GET /leave/balance — 拒绝访问 原因=缺少 X-User-Id");
            return ResponseEntity.status(401).body(ApiResponse.<Map<String, Object>>fail(401, "未登录或会话已过期"));
        }
        log.info("查询年假余额 — user={}", describe(userId));
        return ResponseEntity.ok(service.balance(userId));
    }

    @PostMapping("/applications")
    public ResponseEntity<ApiResponse<Map<String, Object>>> submit(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestBody Map<String, String> body) {
        if (userId == null) {
            log.warn("POST /leave/applications — 拒绝访问 原因=缺少 X-User-Id");
            return ResponseEntity.status(401).body(ApiResponse.<Map<String, Object>>fail(401, "未登录或会话已过期"));
        }
        log.info("提交请假申请 — user={} start={} end={} reason={}",
                describe(userId), body.get("start_date"), body.get("end_date"), body.get("reason"));
        return ResponseEntity.ok(service.submit(userId, body));
    }

    private String describe(String userId) {
        UserDirectory.UserInfo u = userDirectory.find(userId);
        return u == null ? userId : userId + "(" + u.empName() + ")";
    }
}
