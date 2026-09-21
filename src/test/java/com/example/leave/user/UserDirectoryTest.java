package com.example.leave.user;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserDirectoryTest {

    @Test
    void findReturnsUserByUsername() {
        UserDirectory d = new UserDirectory();
        d.setUsers(java.util.List.of(
            new UserDirectory.UserInfo("zhangsan", "张三", 5),
            new UserDirectory.UserInfo("lisi", "李四", 0)));
        UserDirectory.UserInfo u = d.find("zhangsan");
        assertEquals("zhangsan", u.username());
        assertEquals("张三", u.empName());
        assertEquals(5, u.annualBalance());
    }

    @Test
    void findReturnsNullWhenMissing() {
        UserDirectory d = new UserDirectory();
        d.setUsers(java.util.List.of());
        assertNull(d.find("nobody"));
    }

    @Test
    void findReturnsNullWhenUsersNull() {
        UserDirectory d = new UserDirectory();
        assertNull(d.find("zhangsan"));
    }
}