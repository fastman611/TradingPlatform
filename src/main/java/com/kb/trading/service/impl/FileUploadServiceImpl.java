package com.kb.trading.service.impl;
import com.kb.trading.service.FileUploadService;
import com.kb.trading.utils.FileUploadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
@Service
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {
    @Override
    public String uploadImage(MultipartFile file) throws IOException {
        try {
            String fileUrl = FileUploadUtil.uploadFile(file);
            log.info("图片上传成功: {}", fileUrl);
            return fileUrl;
        } catch (IOException e) {
            log.error("图片上传失败", e);
            throw e;
        }
    }

    @Override
    public List<String> uploadImages(MultipartFile[] files) throws IOException {
        try {
            List<String> fileUrls = FileUploadUtil.uploadFiles(files);
            log.info("批量图片上传成功，共{}张", fileUrls.size());
            return fileUrls;
        } catch (IOException e) {
            log.error("批量图片上传失败", e);
            throw e;
        }
    }

    @Override
    public void deleteImage(String imageUrl) {
        try {
            FileUploadUtil.deleteFile(imageUrl);
            log.info("图片删除成功: {}", imageUrl);
        } catch (Exception e) {
            log.error("图片删除失败: {}", imageUrl, e);
        }
    }
}
