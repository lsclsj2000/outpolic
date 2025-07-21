package outpolic.admin.outsourcing.service;

import java.util.List;

import outpolic.admin.outsourcing.dto.AdminOutsourcingDTO;
import outpolic.admin.outsourcing.dto.AdminOutsourcingSearchDTO; // 검색 DTO 임포트
import outpolic.systems.file.domain.FileMetaData;

public interface AdminOutsourcingService {
    // getAllOutsourcings 메서드에 검색 DTO 파라미터 추가
    List<AdminOutsourcingDTO> getAllOutsourcings(AdminOutsourcingSearchDTO searchDTO);
    void deleteOutsourcing(String osCd);
    List<FileMetaData> getOutsourcingFiles(String osCd);
}