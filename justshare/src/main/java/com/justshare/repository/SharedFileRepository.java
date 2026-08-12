package com.justshare.repository;

import com.justshare.entity.Room;
import com.justshare.entity.SharedFile;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SharedFileRepository
        extends JpaRepository<SharedFile, Long> {

    List<SharedFile> findByRoom(Room room);
}
