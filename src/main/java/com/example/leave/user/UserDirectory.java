package com.example.leave.user;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "leave.mock")
public class UserDirectory {

    private List<UserInfo> users;

    public record UserInfo(String username, String empName, int annualBalance) {}

    public List<UserInfo> getUsers() { return users; }
    public void setUsers(List<UserInfo> users) { this.users = users; }

    public UserInfo find(String userId) {
        if (users == null) return null;
        return users.stream()
            .filter(u -> userId.equals(u.username()))
            .findFirst()
            .orElse(null);
    }
}