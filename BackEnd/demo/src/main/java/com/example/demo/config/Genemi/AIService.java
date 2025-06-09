package com.example.demo.config.Genemi;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
@Service
public class AIService {
    public String generateContent(String prompt) throws IOException {
        // Thiết lập ProcessBuilder để gọi script Python
        String path = "C:" + File.separator + "Users" + File.separator + "xamth" + File.separator + "OneDrive" + File.separator + "Máy tính" + File.separator + "FOLDER" + File.separator + "Spring" + File.separator + "apiAI.py";
        ProcessBuilder processBuilder = new ProcessBuilder("python", path, prompt);

        Process process = processBuilder.start();

        // Đọc kết quả trả về từ script Python
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line);
        }
        return output.toString();
    }
}
