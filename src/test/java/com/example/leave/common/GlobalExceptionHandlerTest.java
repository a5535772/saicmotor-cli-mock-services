package com.example.leave.common;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GlobalExceptionHandlerTest {

    @RestController
    static class ProbeController {
        @GetMapping("/probe/business")
        public String business() {
            throw new BusinessException(2001, "年假余额不足");
        }

        @GetMapping("/probe/boom")
        public String boom() {
            throw new IllegalStateException("unexpected");
        }
    }

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new ProbeController())
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

    @Test
    void businessError_isHttp200WithCode() throws Exception {
        mockMvc.perform(get("/probe/business"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(2001))
                .andExpect(jsonPath("$.msg").value("年假余额不足"))
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.nullValue()));
    }

    @Test
    void unknownError_isHttp500() throws Exception {
        mockMvc.perform(get("/probe/boom"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("服务器内部错误"));
    }
}