package com.kb.trading.service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
public interface FileUploadService {
    /**
     * 上传单个图片
     */
    String uploadImage(MultipartFile file) throws IOException;

    /**
     * 上传多个图片
     */
    List<String> uploadImages(MultipartFile[] files) throws IOException;

    /**
     * 删除图片
     */
    void deleteImage(String imageUrl);
}
