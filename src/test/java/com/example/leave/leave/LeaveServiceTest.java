package com.example.leave.leave;

import com.example.leave.common.BusinessException;
import com.example.leave.user.UserDirectory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class LeaveServiceTest {

    private LeaveService service;

    @BeforeEach
    void setUp() {
        UserDirectory d = new UserDirectory();
        d.setUsers(List.of(new UserDirectory.UserInfo("zhangsan", "张三", 5)));
        service = new LeaveService(d);
    }

    @Test
    void balanceReturnsAnnualBalance() {
        var resp = service.balance("zhangsan");
        assertEquals(0, resp.getCode());
        assertEquals(5, ((Map<?, ?>) resp.getData()).get("annual_balance"));
        assertEquals(0, ((Map<?, ?>) resp.getData()).get("used"));
    }

    @Test
    void submitStoresApplicationAndIncrementsUsed() {
        service.submit("zhangsan", Map.of("start_date", "2026-09-21", "end_date", "2026-09-22", "reason", "年假"));
        var resp = service.balance("zhangsan");
        assertEquals(2, ((Map<?, ?>) resp.getData()).get("used"));
    }

    @Test
    void submitRejectsEndBeforeStart() {
        var e = assertThrows(BusinessException.class, () ->
            service.submit("zhangsan", Map.of("start_date", "2026-09-22", "end_date", "2026-09-21", "reason", "x")));
        assertEquals(1001, e.getCode());
    }

    @Test
    void submitRejectsMissingField() {
        var e = assertThrows(BusinessException.class, () ->
            service.submit("zhangsan", Map.of("start_date", "2026-09-21", "end_date", "2026-09-22")));
        assertEquals(1002, e.getCode());
    }
}