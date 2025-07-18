package outpolic.enter.portfolio.service.impl;

import java.util.List;

import org.mybatis.logging.Logger;
import org.mybatis.logging.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import outpolic.enter.portfolio.mapper.PortfolioMapper;
import outpolic.systems.file.domain.FileMetaData;
import outpolic.systems.util.FilesUtils;

@Service
@RequiredArgsConstructor
public class PortfolioAsyncService {

    private static final Logger logger = LoggerFactory.getLogger(PortfolioAsyncService.class);
    private final PortfolioMapper portfolioMapper;
    private final FilesUtils filesUtils;

    @Async
    @Transactional
    public void deletePortfolio(String prtfCd) {
        try {
            String clCd = portfolioMapper.findClCdByPrtfCd(prtfCd);
            if (clCd != null) {
                // 물리적 파일 삭제
                List<FileMetaData> filesToDelete = portfolioMapper.findFilesByClCd(clCd);
                for (FileMetaData file : filesToDelete) {
                    filesUtils.deleteFileByPath(file.getFilePath());
                }

                // 모든 연관 테이블 데이터 삭제
                portfolioMapper.deletePerusalContentByClCd(clCd);
                portfolioMapper.deleteCategoryMappingByClCd(clCd);
                portfolioMapper.deleteTagMappingByClCd(clCd);
                portfolioMapper.deleteBookmarkByClCd(clCd);
                portfolioMapper.deleteFilesByClCd(clCd);
                portfolioMapper.deleteOutsourcingContractDetailsByClCd(clCd);
                portfolioMapper.deleteRankingByClCd(clCd);
                portfolioMapper.deleteTodayViewByClCd(clCd);
                portfolioMapper.deleteTotalViewByClCd(clCd);
                portfolioMapper.deleteOutsourcingPortfolioByPrtfCd(prtfCd); 
                portfolioMapper.deleteContentListByClCd(clCd);
            }
            portfolioMapper.deletePortfolioByPrtfCd(prtfCd);
        } catch (Exception e) {
        }
    }
}