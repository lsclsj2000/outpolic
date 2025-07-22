package outpolic.admin.outsourcing.service.impl;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import outpolic.admin.outsourcing.dto.AdminOutsourcingDTO;
import outpolic.admin.outsourcing.dto.AdminOutsourcingSearchDTO;
import outpolic.admin.outsourcing.mapper.AdminOutsourcingMapper;
import outpolic.admin.outsourcing.service.AdminOutsourcingService;
import outpolic.systems.file.domain.FileMetaData;
import outpolic.enter.outsourcing.service.EnterOutsourcingService;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminOutsourcingServiceImpl implements AdminOutsourcingService {

    private final AdminOutsourcingMapper adminOutsourcingMapper;
    private final EnterOutsourcingService enterOutsourcingService;
    private String restorePathForWebOrFileSystem(String dbPath) {
        if (dbPath == null) return null;
        if (!dbPath.startsWith("/attachment/") && !dbPath.startsWith("http")) {
            return "/attachment/" + dbPath;
        }
        return dbPath;
    }

    @Override
    public List<AdminOutsourcingDTO> getAllOutsourcings(AdminOutsourcingSearchDTO searchDTO) {
        List<AdminOutsourcingDTO> outsourcingDTOs = adminOutsourcingMapper.findAllOutsourcingsForAdmin(searchDTO);

        if (outsourcingDTOs != null) {
            return outsourcingDTOs.stream()
                                  .filter(Objects::nonNull)
                                  .map(dto -> {
                                     dto.setOsThumbnailUrl(restorePathForWebOrFileSystem(dto.getOsThumbnailUrl()));
                                      return dto;
                                  })
                                  .collect(Collectors.toList());
        }
        return List.of();
    }

    @Override
    @Transactional
    public void deleteOutsourcing(String osCd) {
        enterOutsourcingService.deleteOutsourcing(osCd);
    }

    @Override
    public List<FileMetaData> getOutsourcingFiles(String osCd) {
        String clCd = adminOutsourcingMapper.findClCdByOsCd(osCd);
        if (clCd == null) {
            return List.of();
        }
        List<FileMetaData> files = adminOutsourcingMapper.findFilesByClCd(clCd);
        if (files != null) {
            files.forEach(file -> file.setFilePath(restorePathForWebOrFileSystem(file.getFilePath())));
        }
        return files;
    }
}