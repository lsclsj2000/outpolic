package outpolic.enter.portfolio.service;

import org.springframework.web.multipart.MultipartFile;
import outpolic.enter.portfolio.domain.FileMetaData;
import java.io.IOException;

public interface FileService {
    void addFiles(MultipartFile[] files, String featureName, String clCd, String mbrCd) throws IOException;
    void deleteFile(FileMetaData fileMetaData);
}