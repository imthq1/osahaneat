package com.example.demo.Controller;






import com.example.demo.config.Genemi.AIService;


import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;




@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ChatBot {
    private final AIService aiService;
    @PostMapping("/generate")
    public String generateContent(@RequestBody String prompt) {
        try {
            return aiService.generateContent(prompt);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }


}
