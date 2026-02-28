package com.satyam.apiforge.requestlog;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/sse")
@RequiredArgsConstructor
public class SseController {

    private final SseService sseService;

    @GetMapping("/workspace/{slug}")
    public SseEmitter subscribe(@PathVariable String slug) {
        return sseService.subscribe(slug);
    }
}