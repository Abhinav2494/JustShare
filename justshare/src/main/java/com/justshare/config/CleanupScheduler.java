package com.justshare.config;

import com.justshare.service.RoomService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CleanupScheduler {

    private final RoomService roomService;

    public CleanupScheduler(RoomService roomService) {
        this.roomService = roomService;
    }

    // Runs every hour
    @Scheduled(cron = "0 0 * * * *")
    public void cleanupExpiredRooms() {

        System.out.println("Running expired room cleanup...");

        roomService.deleteExpiredRooms();

        System.out.println("Expired room cleanup completed.");
    }
}