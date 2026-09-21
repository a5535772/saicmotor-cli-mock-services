package com.example.leave.attendance;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.leave.common.ApiResponse;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    private final AttendanceService service;

    public AttendanceController(AttendanceService service) {
        this.service = service;
    }

    @GetMapping("/records")
    public ResponseEntity<ApiResponse<Map<String, Object>>> records(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.<Map<String, Object>>fail(401, "未登录或会话已过期"));
        }
        return ResponseEntity.ok(service.records(userId));
    }

    @PostMapping("/corrections")
    public ResponseEntity<ApiResponse<Map<String, Object>>> submit(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestBody Map<String, String> body) {
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.<Map<String, Object>>fail(401, "未登录或会话已过期"));
        }
        return ResponseEntity.ok(service.submit(userId, body));
    }
}