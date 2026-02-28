package com.satyam.apiforge.requestlog;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

@Service
@Slf4j
public class SseService {

    // slug → list of active SSE connections
    private final Map<String, List<SseEmitter>> emitters = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SseEmitter subscribe(String workspaceSlug) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.computeIfAbsent(workspaceSlug, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeEmitter(workspaceSlug, emitter));
        emitter.onTimeout(() -> removeEmitter(workspaceSlug, emitter));
        emitter.onError(e -> removeEmitter(workspaceSlug, emitter));

        log.debug("SSE subscriber added for workspace: {}", workspaceSlug);
        return emitter;
    }

    public void pushLog(String workspaceSlug, RequestLog logEntry) {
        List<SseEmitter> workspaceEmitters = emitters.get(workspaceSlug);
        if (workspaceEmitters == null || workspaceEmitters.isEmpty()) return;

        workspaceEmitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("request-log")
                        .data(objectMapper.writeValueAsString(logEntry)));
            } catch (Exception e) {
                removeEmitter(workspaceSlug, emitter);
            }
        });
    }

    private void removeEmitter(String slug, SseEmitter emitter) {
        List<SseEmitter> list = emitters.get(slug);
        if (list != null) list.remove(emitter);
    }
}