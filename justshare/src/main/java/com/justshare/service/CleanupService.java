package com.justshare.service;

import com.justshare.entity.Room;
import com.justshare.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CleanupService {

    private final RoomRepository roomRepository;

    public CleanupService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Transactional
    public void deleteExpiredRooms() {

        LocalDateTime now = LocalDateTime.now();

        List<Room> expiredRooms =
                roomRepository.findByExpiresAtBefore(now);

        if (expiredRooms.isEmpty()) {
            return;
        }

        roomRepository.deleteAll(expiredRooms);

        System.out.println(
                "Deleted " +
                        expiredRooms.size() +
                        " expired rooms."
        );
    }
}