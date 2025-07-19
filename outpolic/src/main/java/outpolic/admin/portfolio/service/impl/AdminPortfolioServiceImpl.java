package outpolic.admin.portfolio.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import outpolic.admin.portfolio.dto.AdminPortfolioDTO;
import outpolic.admin.portfolio.dto.AdminPortfolioSearchDTO; // AdminPortfolioSearchDTO 임포트
import outpolic.admin.portfolio.mapper.AdminPortfolioMapper;
import outpolic.admin.portfolio.service.AdminPortfolioService;
import outpolic.systems.file.domain.FileMetaData;
import outpolic.enter.portfolio.service.impl.PortfolioAsyncService;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminPortfolioServiceImpl implements AdminPortfolioService {
    
    private final AdminPortfolioMapper adminPortfolioMapper;
    private final PortfolioAsyncService portfolioAsyncService;

    private String restorePathForWebOrFileSystem(String dbPath) {
        if (dbPath == null) return null;
        if (!dbPath.startsWith("/attachment/") && !dbPath.startsWith("http")) {
            return "/attachment/" + dbPath;
        }
        return dbPath;
    }

    // 이전에 제공해주신 'getAllPortfolios()' 메서드 (파라미터 없음)를 아래 메서드로 교체해야 합니다.
    // @Override
    // public List<EnterPortfolio> getAllPortfolios() {
    //     return null;
    // }

    @Override // <-- @Override 어노테이션이 올바르게 붙어 있는지 확인
    public List<AdminPortfolioDTO> getAllPortfolios(AdminPortfolioSearchDTO searchDTO) { // <--- 인터페이스와 일치하도록 파라미터 추가
        // AdminPortfolioMapper를 사용하여 데이터 조회
        List<AdminPortfolioDTO> portfolioDTOs = adminPortfolioMapper.findAllPortfoliosForAdmin(searchDTO); // searchDTO 전달

        if (portfolioDTOs != null) {
            return portfolioDTOs.stream()
                                .filter(Objects::nonNull) // null 객체 필터링
                                .map(dto -> {
                                    // 썸네일 URL 경로 복원
                                    dto.setPrtfThumbnailUrl(restorePathForWebOrFileSystem(dto.getPrtfThumbnailUrl()));
                                    return dto;
                                })
                                .collect(Collectors.toList());
        }
        return List.of(); // 빈 리스트 반환
    }

    @Override
    @Transactional
    public void deletePortfolio(String prtfCd) {
        portfolioAsyncService.deletePortfolio(prtfCd);
    }

    @Override
    public List<FileMetaData> getPortfolioFiles(String prtfCd) {
        String clCd = adminPortfolioMapper.findClCdByPrtfCd(prtfCd);
        if (clCd == null) {
            return List.of();
        }
        List<FileMetaData> files = adminPortfolioMapper.findFilesByClCd(clCd);
        if (files != null) {
            files.forEach(file -> file.setFilePath(restorePathForWebOrFileSystem(file.getFilePath())));
        }
        return files;
    }
}