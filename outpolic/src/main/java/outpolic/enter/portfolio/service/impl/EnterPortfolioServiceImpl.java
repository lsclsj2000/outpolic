package outpolic.enter.portfolio.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import outpolic.enter.POAddtional.domain.CategorySearchDto;
import outpolic.enter.POAddtional.mapper.CategorySearchMapper;
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
    private final PortfolioAsyncService portfolioAsyncService;
    private final CategorySearchMapper categorySearchMapper;

    // Utility method to clean path for DB storage: Removes leading '/' and "attachment/" prefix.
    // Assumes FilesUtils.uploadFile returns a path like "/attachment/sub/path/file.jpg"
    // Stores "sub/path/file.jpg" in DB.
    private String cleanPathForDb(String filePath) {
        if (filePath == null) return null;
        // Remove leading slash if present (FilesUtils might return /attachment/...)
        String cleaned = filePath.startsWith("/") ? filePath.substring(1) : filePath;
        // Remove "attachment/" prefix
        return cleaned.startsWith("attachment/") ? cleaned.substring("attachment/".length()) : cleaned;
    }

    // Utility method to restore path for web display or file system access: Adds "/attachment/" prefix.
    // Assumes DB stores "sub/path/file.jpg".
    // Returns "/attachment/sub/path/file.jpg" for web and FilesUtils.deleteFileByPath.
    private String restorePathForWebOrFileSystem(String dbPath) {
        if (dbPath == null) return null;
        // Ensure it starts with /attachment/
        return "/attachment/" + dbPath; 
    }

    // --- 조회 관련 메서드 ---
    
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
                List<CategorySearchDto> categoryPath = categorySearchMapper.findCategoryPath(portfolio.getCtgryId());
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

    // --- 다단계 등록 관련 신규 메서드들 ---

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
        // Clean path for DB storage (remove leading / and "attachment/" prefix)
        if (uploadedFile != null && uploadedFile.getFilePath() != null) {
            String cleanedPath = cleanPathForDb(uploadedFile.getFilePath());
            uploadedFile.setFilePath(cleanedPath); // DB에는 "portfolio/image/..." 형태로 저장
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
        portfolio.setPrtfTtl(formData.getOsTtl()); // Assuming osTtl is prtfTtl in form data DTO
        portfolio.setPrtfCn(formData.getOsExpln()); // Assuming osExpln is prtfCn in form data DTO
        portfolio.setStcCd("SD_ACTIVE");
        if (formData.getThumbnailFile() != null) {
            portfolio.setPrtfThumbnailUrl(formData.getThumbnailFile().getFilePath());
        }
        portfolioMapper.addPortfolio(portfolio); 

        String newClCd = "LIST_" + portfolio.getPrtfCd();
        portfolioMapper.insertContentList(newClCd, portfolio.getPrtfCd());

        if (formData.getThumbnailFile() != null) {
            portfolioMapper.insertFileRecord(formData.getThumbnailFile(), newClCd, portfolio.getMbrCd());
        }

        updateMappings(formData.getCategoryCodes(), newClCd, portfolio.getMbrCd(), formData.getTags());
    }
    // --- 수정 및 삭제 메서드 ---

    @Override
    @Transactional
    public void updatePortfolio(EnterPortfolio portfolio, List<String> categoryCodes, String tags, MultipartFile portfolioImage) throws IOException {
        String prtfCd = portfolio.getPrtfCd();
        String clCd = portfolioMapper.findClCdByPrtfCd(prtfCd);
        if (clCd == null) {
            throw new IllegalStateException("콘텐츠 목록(cl_cd)을 찾을 수 없습니다.");
        }
        String originalMbrCd = portfolioMapper.findMbrCdByClCd(clCd);

        // Fetch current portfolio state from DB to compare thumbnail URL
        EnterPortfolio currentDbPortfolio = portfolioMapper.findPortfolioDetailsByPrtfCd(prtfCd);
        String currentDbThumbnailUrl = currentDbPortfolio != null ? currentDbPortfolio.getPrtfThumbnailUrl() : null;

        portfolio.setPrtfMdfcnYmdt(LocalDateTime.now()); // Set modification timestamp
        
        String newThumbnailUrlForDb = currentDbThumbnailUrl; // Assume current DB value unless changed

        // 1. 새 파일이 업로드된 경우 (이미지 교체)
        if (portfolioImage != null && !portfolioImage.isEmpty()) {
            // Delete existing files
            List<FileMetaData> existingFiles = portfolioMapper.findFilesByClCd(clCd);
            for (FileMetaData file : existingFiles) {
                String fullPathToDelete = restorePathForWebOrFileSystem(file.getFilePath()); 
                filesUtils.deleteFileByPath(fullPathToDelete);
            }
            if (!existingFiles.isEmpty()) {
                portfolioMapper.deleteFilesByClCd(clCd); // Delete DB records
            }

            // Upload and save new file
            FileMetaData newThumbnailMetaData = this.uploadThumbnail(portfolioImage); 
            if (newThumbnailMetaData != null) {
                newThumbnailUrlForDb = newThumbnailMetaData.getFilePath(); // Cleaned path for DB
                portfolioMapper.insertFileRecord(newThumbnailMetaData, clCd, originalMbrCd); 
            } else {
                newThumbnailUrlForDb = null; // Should not happen if file is not empty, but for safety
            }
        } 
        // 2. 새 파일이 업로드되지 않았고, 기존 이미지가 DB에 있었는데, 클라이언트에서 삭제 의도를 보낸 경우
        //    (HTML에서 initialThumbnailUrl이 null로 설정되고, 빈 MultipartFile이 전송된 경우)
        //    We assume portfolioImage.isEmpty() is true for explicit deletion via HTML JS.
        else if (portfolioImage != null && portfolioImage.isEmpty() && currentDbThumbnailUrl != null) { // currentDbThumbnailUrl != null is crucial
            List<FileMetaData> existingFiles = portfolioMapper.findFilesByClCd(clCd);
            for (FileMetaData file : existingFiles) {
                String fullPathToDelete = restorePathForWebOrFileSystem(file.getFilePath()); 
                filesUtils.deleteFileByPath(fullPathToDelete);
            }
            if (!existingFiles.isEmpty()) {
                portfolioMapper.deleteFilesByClCd(clCd);
            }
            newThumbnailUrlForDb = null; // Set DB thumbnail to null
        }
        // 3. Neither new file upload nor explicit delete: Keep existing thumbnail (newThumbnailUrlForDb already holds currentDbThumbnailUrl)
        //    This means newThumbnailUrlForDb remains `currentDbThumbnailUrl` (the original value from DB).
        //    If currentDbThumbnailUrl was null, it stays null. If it had a path, it keeps that path.
        
        portfolio.setPrtfThumbnailUrl(newThumbnailUrlForDb); // Set the final thumbnail URL (cleaned path)
        portfolioMapper.updatePortfolio(portfolio); 
        
        // 카테고리 및 태그 업데이트 (기존 삭제 후 재삽입)
        portfolioMapper.deleteCategoryMappingByClCd(clCd);
        portfolioMapper.deleteTagMappingByClCd(clCd);
        updateMappings(categoryCodes, clCd, originalMbrCd, tags);
    }

    @Override
    public void deletePortfolio(String prtfCd) {
        logger.info("포트폴리오 삭제 요청 수신. 비동기 처리를 시작합니다. ID: {}", prtfCd);
        deletePortfolioAsync(prtfCd);
    }
    
    @Async
    @Transactional
    public void deletePortfolioAsync(String prtfCd) {
        logger.info("포트폴리오 비동기 삭제 작업을 시작합니다. ID: {}", prtfCd);
        try {
            String clCd = portfolioMapper.findClCdByPrtfCd(prtfCd);
            if (clCd != null) {
                List<FileMetaData> filesToDelete = portfolioMapper.findFilesByClCd(clCd);
                for (FileMetaData file : filesToDelete) {
                    String fullPathToDelete = restorePathForWebOrFileSystem(file.getFilePath()); 
                    filesUtils.deleteFileByPath(fullPathToDelete);
                }

                // ... (rest of deletion logic)
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
            logger.info("포트폴리오 비동기 삭제 작업을 완료했습니다. ID: {}", prtfCd);
        } catch (Exception e) {
            logger.error("포트폴리오 비동기 삭제 중 오류 발생. ID: {}", prtfCd, e);
        }
    }

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