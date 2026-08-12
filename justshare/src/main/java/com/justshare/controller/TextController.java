package com.justshare.controller;

import com.justshare.dto.TextResponse;
import com.justshare.service.TextService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms/{roomCode}/texts")
public class TextController {

    private final TextService textService;

    public TextController(
            TextService textService
    ) {
        this.textService = textService;
    }

    // ==================================================
    // GET ALL TEXTS
    // ==================================================

    @GetMapping
    public ResponseEntity<List<TextResponse>> getTexts(
            @PathVariable String roomCode
    ) {

        List<TextResponse> texts =
                textService
                        .getTexts(roomCode)
                        .stream()
                        .map(TextResponse::new)
                        .toList();

        return ResponseEntity.ok(texts);
    }
}