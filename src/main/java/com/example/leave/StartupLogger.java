package com.example.leave;

import com.example.leave.user.UserDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupLogger {

    private static final Logger log = LoggerFactory.getLogger(StartupLogger.class);

    private final int port;
    private final UserDirectory userDirectory;

    public StartupLogger(@Value("${server.port:8080}") int port, UserDirectory userDirectory) {
        this.port = port;
        this.userDirectory = userDirectory;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        int userCount = userDirectory.getUsers() == null ? 0 : userDirectory.getUsers().size();
        log.info("mock-services 启动完成 — port={} 用户数={}", port, userCount);
    }
}
