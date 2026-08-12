package com.justshare.repository;

import com.justshare.entity.Room;
import com.justshare.entity.SharedText;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SharedTextRepository
        extends JpaRepository<SharedText, Long> {

    List<SharedText> findByRoom(Room room);
}

