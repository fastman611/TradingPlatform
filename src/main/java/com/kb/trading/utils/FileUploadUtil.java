package com.kb.trading.utils;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
public class FileUploadUtil {
    // 上传目录（相对于项目根目录）
    private static final String UPLOAD_DIR = "uploads/";

    // 允许的图片类型
    private static final String[] ALLOWED_EXTENSIONS = {
            "jpg", "jpeg", "png", "gif", "bmp", "webp"
    };

    /**
     * 上传单个文件
     * @param file 上传的文件
     * @return 文件的访问URL
     */
    public static String uploadFile(MultipartFile file) throws IOException {
        // 1. 检查文件是否为空
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 2. 检查文件类型
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        if (!isAllowedExtension(fileExtension)) {
            throw new IllegalArgumentException("不支持的文件类型: " + fileExtension);
        }

        // 3. 检查文件大小（5MB限制）
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("文件大小不能超过5MB");
        }

        // 4. 生成唯一文件名（防止重名）
        String uniqueFilename = UUID.randomUUID().toString() + "." + fileExtension;

        // 5. 创建上传目录（如果不存在）
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 6. 保存文件
        Path filePath = Paths.get(UPLOAD_DIR + uniqueFilename);
        Files.copy(file.getInputStream(), filePath);

        // 7. 返回文件的访问URL（相对路径）
        return "/" + UPLOAD_DIR + uniqueFilename;
    }

    /**
     * 上传多个文件
     * @param files 文件数组
     * @return 文件URL列表
     */
    public static List<String> uploadFiles(MultipartFile[] files) throws IOException {
        List<String> fileUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String fileUrl = uploadFile(file);
                fileUrls.add(fileUrl);
            }
        }
        return fileUrls;
    }

    /**
     * 删除文件
     * @param fileUrl 文件的URL
     */
    public static void deleteFile(String fileUrl) {
        if (fileUrl != null && fileUrl.startsWith("/" + UPLOAD_DIR)) {
            String filename = fileUrl.substring(1); // 去掉开头的"/"
            File file = new File(filename);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * 获取文件扩展名
     */
    private static String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 检查文件扩展名是否允许
     */
    private static boolean isAllowedExtension(String extension) {
        for (String allowed : ALLOWED_EXTENSIONS) {
            if (allowed.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }
}
