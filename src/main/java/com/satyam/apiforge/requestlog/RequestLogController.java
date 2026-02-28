package com.satyam.apiforge.requestlog;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class RequestLogController {

    private final RequestLogRepository logRepository;

    @GetMapping("/workspace/{slug}")
    public ResponseEntity<List<RequestLog>> getByWorkspace(@PathVariable String slug) {
        return ResponseEntity.ok(
                logRepository.findByWorkspaceSlugOrderByCalledAtDesc(slug));
    }
}