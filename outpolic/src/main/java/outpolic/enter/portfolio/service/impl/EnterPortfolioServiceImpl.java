package outpolic.enter.portfolio.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
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
    private final PortfolioAsyncService portfolioAsyncService; // 1. 비동기 서비스 주입

    // --- 조회 관련 메서드 ---

 
    
    @Override
    public int countPortfoliosByEntCd(String entCd) {
        return portfolioMapper.countPortfoliosByEntCd(entCd);
    }

    @Override
    public List<EnterPortfolio> getPortfolioListByEntCd(String entCd) {
        return portfolioMapper.findPortfolioDetailsByEntCd(entCd);
    }

    @Override
    public EnterPortfolio getPortfolioByPrtfCd(String prtfCd) {
        return portfolioMapper.findPortfolioDetailsByPrtfCd(prtfCd);
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
        return portfolioMapper.searchUnlinkedOutsourcings(prtfCd, entCd, query);
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
        String latestPrtfCd = portfolioMapper.findLatestPrtfCd();
        int nextPrtfNum = (latestPrtfCd == null || !latestPrtfCd.startsWith("PO_C")) ? 1 : Integer.parseInt(latestPrtfCd.substring(4)) + 1;
        return String.format("PO_C%05d", nextPrtfNum);
    }

    @Override
    public FileMetaData uploadThumbnail(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        
        String serviceName = "portfolio";
        String imageTypeDir = (file.getContentType() != null && file.getContentType().startsWith("image")) ? "image" : "files";
        String fullServicePath = serviceName + "/" + imageTypeDir;
        
        FileMetaData uploadedFile = filesUtils.uploadFile(file, fullServicePath);

        // [최종 수정] 썸네일 경로가 '/'로 시작하지 않으면 강제로 붙여주는 로직
        if (uploadedFile != null && uploadedFile.getFilePath() != null && !uploadedFile.getFilePath().startsWith("/")) {
            uploadedFile.setFilePath("/" + uploadedFile.getFilePath());
        }
        
        return uploadedFile;
    }

    @Override
    @Transactional
    public void registerNewPortfolio(PortfolioFormDataDto formData) throws IOException {
        EnterPortfolio portfolio = new EnterPortfolio();
        
        // 컨트롤러에서 생성한 UUID 기반의 prtfCd를 그대로 사용합니다.
        portfolio.setPrtfCd(formData.getPrtfCd()); 
        
        portfolio.setEntCd(formData.getEntCd());
        portfolio.setMbrCd(formData.getMbrCd());
        portfolio.setPrtfTtl(formData.getPrtfTtl());
        portfolio.setPrtfCn(formData.getPrtfCn());
        portfolio.setStcCd("SD_ACTIVE");
        
        // [수정] 이제 addPortfolio는 prtf_cd 값을 처음부터 포함하여 INSERT 합니다.
        portfolioMapper.addPortfolio(portfolio); 

        // prtf_cd가 고유하므로 cl_cd도 고유하게 생성됩니다.
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

        portfolio.setPrtfMdfcnYmdt(LocalDateTime.now());
        portfolioMapper.updatePortfolio(portfolio);

        if (portfolioImage != null && !portfolioImage.isEmpty()) {
            List<FileMetaData> existingFiles = portfolioMapper.findFilesByClCd(clCd);
            for (FileMetaData file : existingFiles) {
                filesUtils.deleteFileByPath(file.getFilePath());
            }
            if (!existingFiles.isEmpty()) {
                portfolioMapper.deleteFilesByClCd(clCd);
            }

            // 수정 시에도 경로 보정 로직이 있는 uploadThumbnail 메서드 사용
            FileMetaData newThumbnailMetaData = this.uploadThumbnail(portfolioImage);
            
            if (newThumbnailMetaData != null) {
                portfolioMapper.insertFileRecord(newThumbnailMetaData, clCd, originalMbrCd);
            }
        }

        portfolioMapper.deleteCategoryMappingByClCd(clCd);
        portfolioMapper.deleteTagMappingByClCd(clCd);
        updateMappings(categoryCodes, clCd, originalMbrCd, tags);
    }

    @Override
    public void deletePortfolio(String prtfCd) {
        logger.info("포트폴리오 삭제 요청 수신. 비동기 처리를 시작합니다. ID: {}", prtfCd);
        // 실제 작업은 아래의 비동기 메서드에 위임
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
                    filesUtils.deleteFileByPath(file.getFilePath());
                }

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

    // --- 내부 헬퍼 메서드 ---
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