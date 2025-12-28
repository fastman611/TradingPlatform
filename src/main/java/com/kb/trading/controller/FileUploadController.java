package com.kb.trading.controller;
import com.kb.trading.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequiredArgsConstructor
@RequestMapping("/upload")
public class FileUploadController {
    private final FileUploadService fileUploadService;

    /**
     * 上传单个图片
     */
    @PostMapping("/single")
    public ResponseEntity<Map<String, Object>> uploadSingleImage(
            @RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();

        try {
            String imageUrl = fileUploadService.uploadImage(file);
            response.put("success", true);
            response.put("message", "图片上传成功");
            response.put("url", imageUrl);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "图片上传失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 上传多个图片
     */
    @PostMapping("/multiple")
    public ResponseEntity<Map<String, Object>> uploadMultipleImages(
            @RequestParam("files") MultipartFile[] files) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<String> imageUrls = fileUploadService.uploadImages(files);
            response.put("success", true);
            response.put("message", "图片上传成功，共" + imageUrls.size() + "张");
            response.put("urls", imageUrls);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "图片上传失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 删除图片
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteImage(
            @RequestParam("imageUrl") String imageUrl) {
        Map<String, Object> response = new HashMap<>();

        try {
            fileUploadService.deleteImage(imageUrl);
            response.put("success", true);
            response.put("message", "图片删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "图片删除失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取允许的图片类型
     */
    @GetMapping("/allowed-types")
    public ResponseEntity<Map<String, Object>> getAllowedTypes() {
        Map<String, Object> response = new HashMap<>();
        String[] allowedTypes = {"jpg", "jpeg", "png", "gif", "bmp", "webp"};
        response.put("success", true);
        response.put("allowedTypes", allowedTypes);
        response.put("maxSize", "5MB");
        return ResponseEntity.ok(response);
    }
}
