package com.satyam.apiforge.endpoint;

import com.satyam.apiforge.requestlog.RequestLog;
import com.satyam.apiforge.requestlog.RequestLogRepository;
import com.satyam.apiforge.requestlog.SseService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/mock")
@RequiredArgsConstructor
@Slf4j
public class DynamicMockHandler {

    private final MockEndpointRepository endpointRepository;
    private final RequestLogRepository logRepository;
    private final SseService sseService;

    @RequestMapping("/**")
    public ResponseEntity<String> handleMockRequest(HttpServletRequest request)
            throws Exception {

        String fullPath = request.getRequestURI();
        String afterMock = fullPath.substring("/mock/".length());
        int slashIndex = afterMock.indexOf("/");

        if (slashIndex == -1) {
            return ResponseEntity.badRequest()
                    .body("{\"error\": \"Invalid mock URL format\"}");
        }

        String slug = afterMock.substring(0, slashIndex);
        String path = "/" + afterMock.substring(slashIndex + 1);
        String method = request.getMethod();
        long startTime = System.currentTimeMillis();

        // Capture request headers
        String headers = Collections.list(request.getHeaderNames())
                .stream()
                .map(h -> h + ": " + request.getHeader(h))
                .collect(Collectors.joining("\n"));

        // Capture request body
        String body = "";
        try {
            body = request.getReader().lines()
                    .collect(Collectors.joining("\n"));
        } catch (Exception ignored) {}

        final String requestBody = body;

        return endpointRepository
                .findByWorkspaceSlugAndMethodAndPath(slug, method, path)
//                .filter(MockEndpoint::isActive)
                .map(endpoint -> {
                    try {
                        if (endpoint.getDelayMs() > 0) {
                            Thread.sleep(endpoint.getDelayMs());
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    long responseTime = System.currentTimeMillis() - startTime;

                    // Save log
                    RequestLog log = RequestLog.builder()
                            .endpointId(endpoint.getId())
                            .workspaceSlug(slug)
                            .method(method)
                            .path(path)
                            .requestHeaders(headers)
                            .requestBody(requestBody)
                            .responseStatus(endpoint.getStatusCode())
                            .responseTimeMs(responseTime)
                            .build();

                    logRepository.save(log);

                    // Push to SSE
                    sseService.pushLog(slug, log);

                    return ResponseEntity
                            .status(endpoint.getStatusCode())
                            .header("Content-Type", "application/json")
                            .header("X-Powered-By", "APIForge")
                            .body(endpoint.getResponseBody());
                })
                .orElse(ResponseEntity.status(404)
                        .body("{\"error\": \"Mock endpoint not found\"}"));
    }
}