package com.example.leave.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserDirectoryBindingTest {

    @Autowired
    private UserDirectory userDirectory;

    @Test
    void bindsUsersFromYaml() {
        var users = userDirectory.getUsers();
        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("zhangsan", users.get(0).username());
        assertEquals("张三", users.get(0).empName());
        assertEquals(5, users.get(0).annualBalance());
    }
}