package com.example.leave.leave;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record LeaveApplication(String id, String startDate, String endDate, String reason, String status) {
    public int days() {
        // submit() ISO-validates startDate/endDate before storing, so parse never throws here.
        LocalDate s = LocalDate.parse(startDate);
        LocalDate e = LocalDate.parse(endDate);
        return (int) ChronoUnit.DAYS.between(s, e) + 1;
    }
}