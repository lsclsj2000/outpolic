package outpolic.enter.portfolio.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;
import outpolic.enter.portfolio.domain.FileMetaData;
import outpolic.enter.portfolio.mapper.FileMapper;
import outpolic.enter.portfolio.service.FileService;
import outpolic.enter.portfolio.util.FilesUtils;
import java.io.IOException;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FilesUtils filesUtils;
    private final FileMapper fileMapper;

    @Override
    public void addFiles(MultipartFile[] files, String featureName, String clCd, String mbrCd) throws IOException {
        List<FileMetaData> fileList = filesUtils.uploadFiles(files, featureName, clCd, mbrCd);
        if (fileList != null && !fileList.isEmpty()) {
            fileMapper.addFiles(fileList);
        }
    }
    
    @Override
    public void deleteFile(FileMetaData fileMetaData) {
        if (fileMetaData == null) return;
        boolean isDeleted = filesUtils.deleteFileByPath(fileMetaData.getFilePath());
        if (isDeleted) {
            fileMapper.deleteFileByIdx(fileMetaData.getFileIdx());
        }
    }
}