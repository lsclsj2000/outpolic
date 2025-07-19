package outpolic.admin.portfolio.service;

import java.util.List;

import outpolic.admin.portfolio.dto.AdminPortfolioDTO;
import outpolic.admin.portfolio.dto.AdminPortfolioSearchDTO;
import outpolic.enter.portfolio.domain.EnterPortfolio;
import outpolic.systems.file.domain.FileMetaData;

public interface AdminPortfolioService {
    // 검색 DTO를 파라미터로 받아 AdminPortfolioDTO 리스트 반환
    List<AdminPortfolioDTO> getAllPortfolios(AdminPortfolioSearchDTO searchDTO);
    void deletePortfolio(String prtfCd);
    List<FileMetaData> getPortfolioFiles(String prtfCd);
}