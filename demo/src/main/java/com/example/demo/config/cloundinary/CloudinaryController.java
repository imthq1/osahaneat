package com.example.demo.config.cloundinary;

import com.example.demo.Domain.response.FileInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class CloudinaryController {
    private final CloudinaryService cloudinaryService;

    public CloudinaryController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @PostMapping("/upload/image")
    public ResponseEntity<List<FileInfo>> uploadImage(@RequestParam("file") MultipartFile[] files,
                                                      @RequestParam("folder") String folderName) throws IOException {

        List<FileInfo> fileInfoList = new ArrayList<>();

        for (MultipartFile file : files) {
            Map<String, Object> uploadResult = cloudinaryService.uploadFile(file, folderName);

            FileInfo fileInfo = new FileInfo();

            String publicId = (String) uploadResult.get("public_id");
            String fileName = publicId.split("/")[1];


            fileInfo.setName(fileName);
            fileInfo.setUrl(publicId);

            fileInfoList.add(fileInfo);
        }


        return ResponseEntity.ok(fileInfoList);
    }


    @PostMapping("/upload/video")
    public ResponseEntity<?> uploadVideo(@RequestParam("file") MultipartFile file,
                                         @RequestParam("folder") String folderName) throws IOException {
        return ResponseEntity.ok(cloudinaryService.uploadVideo(file, folderName));
    }

}
