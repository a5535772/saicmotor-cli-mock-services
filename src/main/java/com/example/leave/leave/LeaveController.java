package com.example.leave.leave;

import com.example.leave.common.ApiResponse;
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

    private final LeaveService service;

    public LeaveController(LeaveService service) {
        this.service = service;
    }

    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<Map<String, Object>>> balance(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.<Map<String, Object>>fail(401, "未登录或会话已过期"));
        }
        return ResponseEntity.ok(service.balance(userId));
    }

    @PostMapping("/applications")
    public ResponseEntity<ApiResponse<Map<String, Object>>> submit(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestBody Map<String, String> body) {
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.<Map<String, Object>>fail(401, "未登录或会话已过期"));
        }
        return ResponseEntity.ok(service.submit(userId, body));
    }
}