package outpolic.enter.portfolio.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// @Async // EnterPortfolioServiceImpl에서는 더 이상 필요 없음
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // @Transactional은 deletePortfolioAsync에서만 필요할 수 있음
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import outpolic.enter.POAddtional.domain.CategorySearchDto;
import outpolic.enter.POAddtional.service.CategorySearchService;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;
import outpolic.enter.portfolio.domain.EnterPortfolio;
import outpolic.enter.portfolio.domain.PortfolioFormDataDto;
import outpolic.enter.portfolio.mapper.PortfolioMapper;
import outpolic.enter.portfolio.service.EnterPortfolioService;
import outpolic.systems.file.domain.FileMetaData;
import outpolic.systems.util.FilesUtils;

@Service
@RequiredArgsConstructor
public class EnterPortfolioServiceImpl implements EnterPortfolioService {

    private static final Logger logger = LoggerFactory.getLogger(EnterPortfolioServiceImpl.class);
    private final PortfolioMapper portfolioMapper;
    private final FilesUtils filesUtils; 
    private final PortfolioAsyncService portfolioAsyncService; // PortfolioAsyncService 주입
    private final CategorySearchService categorySearchService;

    // 파일 경로 정리 및 복원 유틸리티 메서드
    private String cleanPathForDb(String filePath) {
        if (filePath == null) return null;
        String cleaned = filePath.startsWith("/") ? filePath.substring(1) : filePath;
        return cleaned.startsWith("attachment/") ? cleaned.substring("attachment/".length()) : cleaned;
    }

    private String restorePathForWebOrFileSystem(String dbPath) {
        if (dbPath == null) return null;
        return "/attachment/" + dbPath;
    }

    @Override
    public int countPortfoliosByEntCd(String entCd) {
        return portfolioMapper.countPortfoliosByEntCd(entCd);
    }

    @Override
    public List<EnterPortfolio> getPortfolioListByEntCd(String entCd) {
        List<EnterPortfolio> portfolios = portfolioMapper.findPortfolioDetailsByEntCd(entCd);
        portfolios.forEach(p -> {
            p.setPrtfThumbnailUrl(restorePathForWebOrFileSystem(p.getPrtfThumbnailUrl()));
        });
        return portfolios;
    }

    @Override
    public EnterPortfolio getPortfolioByPrtfCd(String prtfCd) {
        EnterPortfolio portfolio = portfolioMapper.findPortfolioDetailsByPrtfCd(prtfCd);
        if (portfolio != null) {
            portfolio.setPrtfThumbnailUrl(restorePathForWebOrFileSystem(portfolio.getPrtfThumbnailUrl()));
            if (portfolio.getCtgryId() != null && !portfolio.getCtgryId().isEmpty()) {
                List<CategorySearchDto> categoryPath = categorySearchService.getCategoryPath(portfolio.getCtgryId());
                portfolio.setCategories(categoryPath);
            } else {
                portfolio.setCategories(Collections.emptyList());
            }
        }
        return portfolio;
    }

    @Override
    public List<String> searchTags(String query) {
        return portfolioMapper.searchTagsByName(query);
    }

    @Override
    public String findEntCdByMbrCd(String mbrCd) {
        return portfolioMapper.findEntCdByMbrCd(mbrCd);
    }

    @Override
    public List<EnterOutsourcing> getLinkedOutsourcings(String prtfCd) {
        return portfolioMapper.findLinkedOutsourcingsByPrtfCd(prtfCd);
    }

    @Override
    public List<EnterOutsourcing> searchUnlinkedOutsourcings(String prtfCd, String entCd, String query) {
        return portfolioMapper.findUnlinkedOutsourcings(prtfCd, entCd, query);
    }

    @Override
    public List<EnterPortfolio> searchPortfoliosByTitle(String query) {
        return portfolioMapper.searchPortfoliosByTitle(query);
    }

    @Override
    public List<EnterOutsourcing> getLinkedOutsourcingsByOsCd(String osCd) {
        return portfolioMapper.findLinkedOutsourcingsByOsCd(osCd);
    }

    @Override
    public List<EnterPortfolio> searchUnlinkedPortfolios(String osCd, String entCd, String query) {
        return portfolioMapper.findUnlinkedPortfolios(osCd, entCd, query);
    }
    
    @Override
    public String generateNewPrtfCd() {
        String maxCode = portfolioMapper.selectMaxPortfolioCode();
        int nextNumber = 1;
        if (maxCode != null && maxCode.startsWith("PO_C")) {
            nextNumber = Integer.parseInt(maxCode.substring(5)) + 1;
        }
        return String.format("PO_C%05d", nextNumber);
    }

    @Override
    public FileMetaData uploadThumbnail(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        String serviceName = "portfolio";
        String imageTypeDir = (file.getContentType() != null && file.getContentType().startsWith("image")) ? "image" : "files";
        String fullServicePath = serviceName + "/" + imageTypeDir;
        
        FileMetaData uploadedFile = filesUtils.uploadFile(file, fullServicePath);
        if (uploadedFile != null && uploadedFile.getFilePath() != null) {
            String cleanedPath = cleanPathForDb(uploadedFile.getFilePath());
            uploadedFile.setFilePath(cleanedPath);
        }
        return uploadedFile;
    }

    @Override
    @Transactional
    public void registerNewPortfolio(PortfolioFormDataDto formData) throws IOException {
        EnterPortfolio portfolio = new EnterPortfolio();
        portfolio.setPrtfCd(formData.getPrtfCd()); 
        
        portfolio.setEntCd(formData.getEntCd());
        portfolio.setMbrCd(formData.getMbrCd());
        portfolio.setPrtfTtl(formData.getPrtfTtl());
        portfolio.setPrtfCn(formData.getPrtfCn());
        portfolio.setStcCd("SD_ACTIVE");
        
        if (formData.getThumbnailFile() != null) {
            portfolio.setPrtfThumbnailUrl(formData.getThumbnailFile().getFilePath());
        }

        // 대표 카테고리 ID를 설정하는 로직 추가
        if (formData.getCategoryCodes() != null && !formData.getCategoryCodes().isEmpty()) {
            portfolio.setCtgryId(formData.getCategoryCodes().get(0)); // 첫 번째 카테고리를 대표 카테고리로 설정
        }

        portfolioMapper.addPortfolio(portfolio); 

        String newClCd = "LIST_" + portfolio.getPrtfCd();
        portfolioMapper.insertContentList(newClCd, portfolio.getPrtfCd());
        
        if (formData.getThumbnailFile() != null) {
            portfolioMapper.insertFileRecord(formData.getThumbnailFile(), newClCd, portfolio.getMbrCd());
        }

        updateMappings(formData.getCategoryCodes(), newClCd, portfolio.getMbrCd(), formData.getTags());
    }

    @Override
    @Transactional
    public void updatePortfolio(EnterPortfolio portfolio, List<String> categoryCodes, String tags, MultipartFile portfolioImage) throws IOException {
        String prtfCd = portfolio.getPrtfCd();
        String clCd = portfolioMapper.findClCdByPrtfCd(prtfCd);
        if (clCd == null) {
            throw new IllegalStateException("콘텐츠 목록(cl_cd)을 찾을 수 없습니다.");
        }
        String originalMbrCd = portfolioMapper.findMbrCdByClCd(clCd);
        EnterPortfolio currentDbPortfolio = portfolioMapper.findPortfolioDetailsByPrtfCd(prtfCd);
        String currentDbThumbnailUrl = currentDbPortfolio != null ? currentDbPortfolio.getPrtfThumbnailUrl() : null;

        portfolio.setPrtfMdfcnYmdt(LocalDateTime.now());
        
        String newThumbnailUrlForDb = currentDbThumbnailUrl;

        if (portfolioImage != null && !portfolioImage.isEmpty()) {
            List<FileMetaData> existingFiles = portfolioMapper.findFilesByClCd(clCd);
            for (FileMetaData file : existingFiles) {
                String fullPathToDelete = restorePathForWebOrFileSystem(file.getFilePath());
                filesUtils.deleteFileByPath(fullPathToDelete);
            }
            if (!existingFiles.isEmpty()) {
                portfolioMapper.deleteFilesByClCd(clCd);
            }

            FileMetaData newThumbnailMetaData = this.uploadThumbnail(portfolioImage);
            if (newThumbnailMetaData != null) {
                newThumbnailUrlForDb = newThumbnailMetaData.getFilePath();
                portfolioMapper.insertFileRecord(newThumbnailMetaData, clCd, originalMbrCd);
            } else {
                newThumbnailUrlForDb = null;
            }
        } 
        else if (portfolioImage != null && portfolioImage.isEmpty() && currentDbThumbnailUrl != null) { 
            List<FileMetaData> existingFiles = portfolioMapper.findFilesByClCd(clCd);
            for (FileMetaData file : existingFiles) {
                String fullPathToDelete = restorePathForWebOrFileSystem(file.getFilePath());
                filesUtils.deleteFileByPath(fullPathToDelete);
            }
            if (!existingFiles.isEmpty()) {
                portfolioMapper.deleteFilesByClCd(clCd);
            }
            newThumbnailUrlForDb = null;
        }
        
        portfolio.setPrtfThumbnailUrl(newThumbnailUrlForDb);
        portfolioMapper.updatePortfolio(portfolio);
        
        portfolioMapper.deleteCategoryMappingByClCd(clCd);
        portfolioMapper.deleteTagMappingByClCd(clCd);
        updateMappings(categoryCodes, clCd, originalMbrCd, tags);
    }

    @Override
    public void deletePortfolio(String prtfCd) {
        logger.info("포트폴리오 삭제 요청 수신. 비동기 처리를 시작합니다. ID: {}", prtfCd);
        // PortfolioAsyncService의 deletePortfolio 메서드를 호출하여 비동기 삭제를 위임
        portfolioAsyncService.deletePortfolio(prtfCd); 
    }
    
    // deletePortfolioAsync 메서드는 이제 PortfolioAsyncService로 이동했으므로, 여기서는 제거됩니다.
    // @Async
    // @Transactional
    // public void deletePortfolioAsync(String prtfCd) {
    //     logger.info("포트폴리오 비동기 삭제 작업을 시작합니다. ID: {}", prtfCd);
    //     try {
    //         String clCd = portfolioMapper.findClCdByPrtfCd(prtfCd);
    //         if (clCd != null) {
    //             List<FileMetaData> filesToDelete = portfolioMapper.findFilesByClCd(clCd);
    //             for (FileMetaData file : filesToDelete) {
    //                 String fullPathToDelete = restorePathForWebOrFileSystem(file.getFilePath());
    //                 filesUtils.deleteFileByPath(fullPathToDelete);
    //             }
    //
    //             portfolioMapper.deletePerusalContentByClCd(clCd);
    //             portfolioMapper.deleteCategoryMappingByClCd(clCd);
    //             portfolioMapper.deleteTagMappingByClCd(clCd);
    //             portfolioMapper.deleteBookmarkByClCd(clCd);
    //             portfolioMapper.deleteFilesByClCd(clCd);
    //             portfolioMapper.deleteOutsourcingContractDetailsByClCd(clCd);
    //             portfolioMapper.deleteRankingByClCd(clCd);
    //             portfolioMapper.deleteTodayViewByClCd(clCd);
    //             portfolioMapper.deleteTotalViewByClCd(clCd);
    //             portfolioMapper.deleteOutsourcingPortfolioByPrtfCd(prtfCd);
    //             portfolioMapper.deleteContentListByClCd(clCd);
    //         }
    //         portfolioMapper.deletePortfolioByPrtfCd(prtfCd);
    //         logger.info("포트폴리오 비동기 삭제 작업을 완료했습니다. ID: {}", prtfCd);
    //     } catch (Exception e) {
    //         logger.error("포트폴리오 비동기 삭제 중 오류 발생. ID: {}", prtfCd, e);
    //     }
    // }

    private void updateMappings(List<String> categoryCodes, String clCd, String mbrCd, String tags) {
        if (categoryCodes != null && !categoryCodes.isEmpty()) {
            for (String ctgryId : categoryCodes) {
                if (ctgryId != null && !ctgryId.trim().isEmpty()) {
                    portfolioMapper.insertCategoryMapping(ctgryId, clCd, mbrCd);
                }
            }
        }

        if (tags != null && !tags.trim().isEmpty()) {
            for (String tagName : tags.split(",")) {
                String trimmedTagName = tagName.trim();
                if (trimmedTagName.isEmpty()) continue;
                String tagCd = portfolioMapper.findTagCdByName(trimmedTagName);
                if (tagCd == null) {
                    String latestTagCd = portfolioMapper.findLatestTagCd();
                    int nextNum = 1;
                    if (latestTagCd != null && latestTagCd.startsWith("T_C")) {
                        try {
                            nextNum = Integer.parseInt(latestTagCd.substring(4)) + 1;
                        } catch (NumberFormatException e) {
                            logger.warn("태그 코드 파싱 오류: {}", latestTagCd, e);
                        }
                    }
                    tagCd = String.format("T_C%05d", nextNum);
                    portfolioMapper.insertTag(tagCd, trimmedTagName, mbrCd);
                }
                portfolioMapper.insertTagMapping(tagCd, clCd, mbrCd);
            }
        }
    }
}