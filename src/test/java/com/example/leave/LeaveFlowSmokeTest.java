package com.example.leave;

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
class LeaveFlowSmokeTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void balanceWithoutUserIdReturns401() throws Exception {
        mvc.perform(get("/leave/balance"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void balanceReturnsAnnualBalanceForZhangsan() throws Exception {
        mvc.perform(get("/leave/balance").header("X-User-Id", "zhangsan"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data.annual_balance").value(5));
    }

    @Test
    void submitThenBalanceReflectsUsed() throws Exception {
        mvc.perform(post("/leave/applications")
                .header("X-User-Id", "zhangsan")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"start_date\":\"2026-09-21\",\"end_date\":\"2026-09-22\",\"reason\":\"年假\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data.status").value("PENDING"));

        mvc.perform(get("/leave/balance").header("X-User-Id", "zhangsan"))
            .andExpect(jsonPath("$.data.used").value(2));
    }

    @Test
    void submitRejectsEndBeforeStart() throws Exception {
        mvc.perform(post("/leave/applications")
                .header("X-User-Id", "zhangsan")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"start_date\":\"2026-09-22\",\"end_date\":\"2026-09-21\",\"reason\":\"x\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(1001));
    }
}