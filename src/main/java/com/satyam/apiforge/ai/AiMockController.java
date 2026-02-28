package com.satyam.apiforge.ai;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiMockController {

    private final ChatModel chatModel;

    public AiMockController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generate(@RequestBody GenerateRequest request) {
        String prompt = """
                You are a mock data generator for REST APIs.
                Generate realistic JSON mock data based on this schema:
                
                %s
                
                Rules:
                - Return ONLY valid JSON, no explanation, no markdown backticks
                - Generate 3-5 items if it's a list
                - Use realistic values (real names, emails, addresses)
                - Match exact field names from the schema
                """.formatted(request.schema());

        ChatResponse response = chatModel.call(
                new Prompt(
                        prompt,
                        GoogleGenAiChatOptions.builder()
                                .temperature(0.4)
                                .build()
                ));

        // Extract text from response
        String result = response.getResult()
                .getOutput()
                .getText();

        return ResponseEntity.ok(result);
    }

    record GenerateRequest(String schema) {}
}