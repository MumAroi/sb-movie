package com.movieflix.controllers;

import com.movieflix.service.FileService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;


@RestController
@RequestMapping("/v1/files")
public class FileController {
    
    private final FileService fileService;

    public FileController(FileService fileService){
        this.fileService = fileService;
    }

    @Value("${project.poster}")
    private String path;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
        @RequestParam("file") MultipartFile file
    ) throws IOException {
        String uploadFileName = fileService.uploadFile(path, file);
        return ResponseEntity.ok("File uploaded: " + uploadFileName);
    }

    @GetMapping("/{fileName}")
    public void serverFileHandler(
        @PathVariable String fileName,
        HttpServletResponse response
    ) throws IOException {
        InputStream responseFile = fileService.getResourceFile(path, fileName);
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        StreamUtils.copy(responseFile, response.getOutputStream());
    }
}