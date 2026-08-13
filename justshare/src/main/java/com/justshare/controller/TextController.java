package com.justshare.controller;
import com.justshare.entity.SharedText;
import com.justshare.dto.TextResponse;
import com.justshare.service.EncryptionService;
import com.justshare.service.TextService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms/{roomCode}/texts")
public class TextController {

    private final TextService textService;
    private final EncryptionService encryptionService;

    public TextController(
            TextService textService,
            EncryptionService encryptionService
    ) {
        this.textService = textService;
        this.encryptionService = encryptionService;
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
                        .map(text ->
                                new TextResponse(
                                        text,
                                        encryptionService
                                )
                        )
                        .toList();

        return ResponseEntity.ok(texts);
    }


     // ==================================================
    // POST TEXTS
    // ==================================================
    @PostMapping
public ResponseEntity<TextResponse> saveText(
        @PathVariable String roomCode,
        @RequestBody String content
) {

    SharedText text =
            textService.saveText(
                    roomCode,
                    content
            );

    return ResponseEntity.ok(
            new TextResponse(
                    text,
                    encryptionService
            )
    );
}
}
