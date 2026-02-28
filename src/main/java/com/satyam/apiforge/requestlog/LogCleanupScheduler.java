package com.satyam.apiforge.requestlog;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class LogCleanupScheduler {

    private final RequestLogRepository logRepository;

    // Runs every day at midnight
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cleanOldLogs() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
        logRepository.deleteByCalledAtBefore(cutoff);
        log.info("Cleaned request logs older than 30 days");
    }
}