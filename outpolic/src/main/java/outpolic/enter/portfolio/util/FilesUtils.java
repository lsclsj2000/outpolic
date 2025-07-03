package outpolic.enter.portfolio.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import outpolic.enter.portfolio.domain.FileMetaData;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FilesUtils {

    private final String fileRealPath = "C:/upload/";

    public String getFileRealPath() {
        return fileRealPath;
    }

    public List<FileMetaData> uploadFiles(MultipartFile[] multipartFiles, String featureName, String clCd, String mbrCd) {
        List<FileMetaData> fileList = new ArrayList<>();
        if (multipartFiles != null) {
            for (MultipartFile multipartFile : multipartFiles) {
                FileMetaData fileInfo = storeFile(multipartFile, featureName, clCd, mbrCd);
                if (fileInfo != null) fileList.add(fileInfo);
            }
        }
        return fileList;
    }

    private FileMetaData storeFile(MultipartFile multipartFile, String featureName, String clCd, String mbrCd) {
        if (multipartFile == null || multipartFile.isEmpty()) return null;

        try {
            LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
            String dateDirectory = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String typeDirectory = (multipartFile.getContentType() != null && multipartFile.getContentType().startsWith("image")) ? "images" : "files";

            Path folderPath = Paths.get(fileRealPath, featureName, dateDirectory, typeDirectory);
            createFolder(folderPath);

            String originalFileName = multipartFile.getOriginalFilename();
            String extension = getExtension(originalFileName);
            String newFileName = UUID.randomUUID().toString() + (extension.isEmpty() ? "" : "." + extension);
            Path uploadPath = folderPath.resolve(newFileName);
            
            multipartFile.transferTo(uploadPath);

            String fileIdx = "file_" + now.format(DateTimeFormatter.ofPattern("yyMMddHHmmss")) + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
            String webAccessPath = "/" + featureName + "/" + dateDirectory + "/" + typeDirectory + "/" + newFileName;

            return FileMetaData.builder()
                    .fileIdx(fileIdx)
                    .fileOriginalName(originalFileName)
                    .fileNewName(newFileName)
                    .filePath(webAccessPath.replace("\\", "/"))
                    .fileExtn(extension)
                    .fileRegYmdt(now)
                    .clCd(clCd)
                    .mbrCd(mbrCd)
                    .build();
        } catch (IOException e) {
            log.error("파일 저장 중 오류 발생: {}", e.getMessage(), e);
            return null;
        }
    }

    public boolean deleteFileByPath(String filePath) {
        if (filePath == null || filePath.isEmpty()) return false;
        String relativePath = filePath;
        if(filePath.startsWith("/file/display")) {
            relativePath = filePath.substring(13);
        } else if (filePath.startsWith("/uploads/")) {
            relativePath = filePath.substring(8);
        }
        
        Path deletePath = Paths.get(fileRealPath, relativePath);
        try {
            return Files.deleteIfExists(deletePath);
        } catch (IOException e) {
            log.error("파일 삭제 중 오류 발생: {}", e.getMessage(), e);
            return false;
        }
    }
    
    private void createFolder(Path path) {
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new RuntimeException("디렉토리 생성 실패: " + path, e);
            }
        }
    }
    
    private String getExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) return "";
        int dotIdx = fileName.lastIndexOf(".");
        return (dotIdx != -1 && dotIdx < fileName.length() - 1) ? fileName.substring(dotIdx + 1) : "";
    }
}