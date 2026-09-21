package com.example.leave.attendance;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AttendanceSmokeTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void recordsWithoutUserIdReturns401() throws Exception {
        mvc.perform(get("/attendance/records"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void recordsReturnsStats() throws Exception {
        mvc.perform(get("/attendance/records").header("X-User-Id", "zhangsan"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data.work_days").value(22))
            .andExpect(jsonPath("$.data.late_days").value(1));
    }

    @Test
    void submitReturnsPending() throws Exception {
        mvc.perform(post("/attendance/corrections")
                .header("X-User-Id", "zhangsan")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"date\":\"2026-09-21\",\"reason\":\"忘记打卡\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data.status").value("PENDING"))
            .andExpect(jsonPath("$.data.correction_id").value("COR-1"));
    }

    @Test
    void submitRejectsMissingParams() throws Exception {
        mvc.perform(post("/attendance/corrections")
                .header("X-User-Id", "zhangsan")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"date\":\"2026-09-21\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(1002));
    }
}