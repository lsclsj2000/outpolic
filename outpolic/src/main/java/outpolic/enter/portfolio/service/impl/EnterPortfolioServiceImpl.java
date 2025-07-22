package outpolic.enter.portfolio.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
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
    private final PortfolioAsyncService portfolioAsyncService;
    private final CategorySearchService categorySearchService;

    // 파일 경로 정리 및 복원 유틸리티 메서드
    private String cleanPathForDb(String filePath) {
        if (filePath == null) return null;
        String cleaned = filePath.replace("\\", "/");
        // "attachment/" 또는 "/attachment/" 접두사를 제거합니다.
        if (cleaned.startsWith("/attachment/")) {
            return cleaned.substring("/attachment/".length());
        }
        if (cleaned.startsWith("attachment/")) {
            return cleaned.substring("attachment/".length());
        }
        return cleaned;
    }


    private String restorePathForWebOrFileSystem(String dbPath) {
        if (dbPath == null) return null;
        String normalizedPath = dbPath.replace("\\", "/");
        // 이미 접두사가 있다면 중복해서 붙이지 않고, 올바른 형태로 반환합니다.
        if (normalizedPath.startsWith("/attachment/")) {
            return normalizedPath;
        }
        if (normalizedPath.startsWith("attachment/")) {
            return "/" + normalizedPath;
        }
        // 접두사가 없는 경우에만 붙여줍니다.
        return "/attachment/" + normalizedPath;
    }


    @Override
    public int countPortfoliosByEntCd(String entCd) {
        return portfolioMapper.countPortfoliosByEntCd(entCd);
    }

    @Override
    public List<EnterPortfolio> getPortfolioListByEntCd(String entCd) {
        List<EnterPortfolio> portfolios = portfolioMapper.findPortfolioDetailsByEntCd(entCd);
        portfolios.forEach(p -> {
            // 이 코드가 DB 경로 앞에 "/attachment/"를 붙여줍니다.
            p.setPrtfThumbnailUrl(restorePathForWebOrFileSystem(p.getPrtfThumbnailUrl()));
        });
        return portfolios;
    }
    @Override
    public EnterPortfolio getPortfolioByPrtfCd(String prtfCd) {
        EnterPortfolio portfolio = portfolioMapper.findPortfolioDetailsByPrtfCd(prtfCd);
        if (portfolio != null) {
            // 썸네일 경로를 웹에서 접근 가능한 절대 경로로 변환합니다.
            portfolio.setPrtfThumbnailUrl(restorePathForWebOrFileSystem(portfolio.getPrtfThumbnailUrl()));
            
            // 전체 파일 목록(bodyImages)의 경로도 웹에서 접근 가능하도록 변환
            if (portfolio.getBodyImages() != null) {
                portfolio.getBodyImages().forEach(file ->
                    file.setFilePath(restorePathForWebOrFileSystem(file.getFilePath()))
                );
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
    public List<EnterPortfolio> searchByTitleForLinking(String query, String entCd) {
        return portfolioMapper.findPortfoliosByTitleAndEntCd(query, entCd);
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
      //  String imageTypeDir = "image";
        //String fullServicePath = serviceName + "/" + imageTypeDir; // 결과: "portfolio/image"
        String fullServicePath = serviceName; // "portfolio"만 전달

        FileMetaData uploadedFile = filesUtils.uploadFile(file, fullServicePath); // "portfolio/image"를 전달
        if (uploadedFile != null && uploadedFile.getFilePath() != null) {
            String cleanedPath = cleanPathForDb(uploadedFile.getFilePath());
            uploadedFile.setFilePath(cleanedPath);
        }
        return uploadedFile;
    }

   

 // EnterPortfolioServiceImpl.java

    @Override
    @Transactional
    public void registerNewPortfolio(PortfolioFormDataDto formData, MultipartFile thumbnailFile, List<MultipartFile> bodyImageFiles, HttpSession session) throws IOException {
        String mbrCd = (String) session.getAttribute("SCD");
        String entCd = findEntCdByMbrCd(mbrCd);
        String prtfCd = generateNewPrtfCd();

        EnterPortfolio portfolio = new EnterPortfolio();
        portfolio.setPrtfCd(prtfCd);
        portfolio.setEntCd(entCd);
        portfolio.setMbrCd(mbrCd);
        portfolio.setPrtfTtl(formData.getPrtfTtl());
        portfolio.setPrtfCn(formData.getPrtfCn());
        portfolio.setPrtfPeriodStart(formData.getPrtfPeriodStart());
        portfolio.setPrtfPeriodEnd(formData.getPrtfPeriodEnd());
        portfolio.setPrtfClient(formData.getPrtfClient());
        portfolio.setPrtfIndustry(formData.getPrtfIndustry());
        portfolio.setStcCd("SD_ACTIVE");
        
        if (formData.getCategoryCodes() != null && !formData.getCategoryCodes().isEmpty()) {
            portfolio.setCtgryId(formData.getCategoryCodes().get(0));
        }

        FileMetaData thumbMeta = null;
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            thumbMeta = uploadThumbnail(thumbnailFile);
            portfolio.setPrtfThumbnailUrl(cleanPathForDb(thumbMeta.getFilePath()));
        }
        
        // 1. Save the main portfolio information first
        portfolioMapper.addPortfolio(portfolio);

        // 2. [Key Change] Create the clCd directly from the unique prtfCd
        String clCd = "LIST_" + prtfCd;
        
        // 3. Save to content_list to create the parent row for foreign keys
        portfolioMapper.insertContentList(clCd, prtfCd);

        // 4. Now, safely save files and mappings
        if (thumbMeta != null) {
            portfolioMapper.insertFileRecord(thumbMeta, clCd, mbrCd);
        }
        
        if (bodyImageFiles != null && !bodyImageFiles.isEmpty()) {
            for (MultipartFile bodyFile : bodyImageFiles) {
                FileMetaData bodyMeta = filesUtils.uploadFile(bodyFile, "portfolio"); // Keep path consistent
                if (bodyMeta != null) {
                    bodyMeta.setFilePath(cleanPathForDb(bodyMeta.getFilePath()));
                    portfolioMapper.insertFileRecord(bodyMeta, clCd, mbrCd);
                }
            }
        }
        
        updateMappings(formData.getCategoryCodes(), clCd, mbrCd, formData.getTags());
    }

    @Override
    @Transactional
    public void updatePortfolioAllSteps(PortfolioFormDataDto formData) throws IOException {
        String prtfCd = formData.getPrtfCd();
        String clCd = portfolioMapper.findClCdByPrtfCd(prtfCd);
        if (clCd == null) {
            throw new IllegalStateException("콘텐츠 목록(cl_cd)을 찾을 수 없습니다.");
        }
        String originalMbrCd = portfolioMapper.findMbrCdByClCd(clCd);
        EnterPortfolio existingPortfolio = portfolioMapper.findPortfolioDetailsByPrtfCd(prtfCd);
        if (existingPortfolio == null) {
            throw new IllegalArgumentException("수정할 포트폴리오를 찾을 수 없습니다: " + prtfCd);
        }

        existingPortfolio.setPrtfTtl(formData.getPrtfTtl());
        existingPortfolio.setPrtfCn(formData.getPrtfCn());
        existingPortfolio.setPrtfPeriodStart(formData.getPrtfPeriodStart());
        existingPortfolio.setPrtfPeriodEnd(formData.getPrtfPeriodEnd());
        existingPortfolio.setPrtfClient(formData.getPrtfClient());
        existingPortfolio.setPrtfIndustry(formData.getPrtfIndustry());
        existingPortfolio.setPrtfMdfcnYmdt(LocalDateTime.now());

        // 썸네일 파일 처리 (기존 로직 유지, 필요 시 추가 보완)
        if (formData.getThumbnailFile() != null) {
            // 기존 썸네일이 있다면 물리적으로 삭제
            // (이전에 문제가 되었던 전체 파일 삭제 로직 대신 썸네일만 삭제하도록 수정)
            List<FileMetaData> existingFiles = portfolioMapper.findFilesByClCd(clCd);
            for (FileMetaData file : existingFiles) {
                // 썸네일 경로만 삭제하도록 필터링 (새 썸네일 업로드 시 기존 썸네일만 삭제)
                if (file.getFilePath().contains("portfolio/image/")) { // 혹은 썸네일임을 구분할 수 있는 다른 조건
                    String fullPathToDelete = restorePathForWebOrFileSystem(file.getFilePath());
                    filesUtils.deleteFileByPath(fullPathToDelete);
                    portfolioMapper.deleteFilesByFileCd(file.getFileIdx()); // 특정 file_cd로 삭제하는 메서드가 필요
                }
            }
            // 새 파일 정보로 업데이트하고 DB에 기록
            existingPortfolio.setPrtfThumbnailUrl(formData.getThumbnailFile().getFilePath());
            portfolioMapper.insertFileRecord(formData.getThumbnailFile(), clCd, originalMbrCd);
        } else if (formData.getThumbnailFile() == null && existingPortfolio.getPrtfThumbnailUrl() != null && (formData.getIsThumbnailDeleted() != null && formData.getIsThumbnailDeleted().equals("true"))) {
            // 기존 썸네일이 있었는데, 새 썸네일이 없고, 명시적으로 삭제 요청이 온 경우
            String fullPathToDelete = restorePathForWebOrFileSystem(existingPortfolio.getPrtfThumbnailUrl());
            filesUtils.deleteFileByPath(fullPathToDelete);
            portfolioMapper.deleteFilesByFileCd(getThumbnailFileCd(clCd, existingPortfolio.getPrtfThumbnailUrl())); // 썸네일의 file_cd를 찾아서 삭제
            existingPortfolio.setPrtfThumbnailUrl(null); // DB에서 썸네일 URL 제거
        }

        // 대표 카테고리 ID 업데이트
        if (formData.getCategoryCodes() != null && !formData.getCategoryCodes().isEmpty()) {
            existingPortfolio.setCtgryId(formData.getCategoryCodes().get(0));
        } else {
            existingPortfolio.setCtgryId(null);
        }

        portfolioMapper.updatePortfolio(existingPortfolio);

        // 카테고리 매핑 및 태그 매핑 재처리 (기존 매핑 삭제 후 새로 삽입)
        portfolioMapper.deleteCategoryMappingByClCd(clCd);
        portfolioMapper.deleteTagMappingByClCd(clCd);
        updateMappings(formData.getCategoryCodes(), clCd, originalMbrCd, formData.getTags());

        // [!code diff --start]
        // 삭제된 본문 이미지 처리
        if (formData.getDeletedBodyImageCds() != null && !formData.getDeletedBodyImageCds().isEmpty()) {
            for (String fileCd : formData.getDeletedBodyImageCds()) {
                // Mapper를 통해 FileMetaData를 조회
                FileMetaData fileToDelete = portfolioMapper.findFileMetaDataByFileCd(fileCd);
                if (fileToDelete != null) {
                    filesUtils.deleteFileByPath(restorePathForWebOrFileSystem(fileToDelete.getFilePath()));
                    portfolioMapper.deleteFilesByFileCd(fileCd); // file_cd로 특정 파일만 삭제하는 메서드 필요
                }
            }
        }

        // 새로 추가된 본문 이미지 처리
        if (formData.getNewBodyImageFiles() != null && !formData.getNewBodyImageFiles().isEmpty()) {
            for (MultipartFile bodyFile : formData.getNewBodyImageFiles()) {
                FileMetaData bodyMeta = filesUtils.uploadFile(bodyFile, "portfolio/body");
                if (bodyMeta != null) {
                    portfolioMapper.insertFileRecord(bodyMeta, clCd, originalMbrCd);
                }
            }
        }
        // [!code diff --end]
    }

    @Override
    @Transactional
    public void deletePortfolio(String prtfCd) {
        logger.info("포트폴리오 삭제 요청 수신. 비동기 처리를 시작합니다. ID: {}", prtfCd);
        portfolioAsyncService.deletePortfolio(prtfCd);
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

    @Override
    public List<EnterPortfolio> searchPortfoliosByTitle(String query) {
        return portfolioMapper.findAllPortfoliosByTitle(query);
    }

    // [!code diff --start]
    // 썸네일 파일의 file_cd를 가져오는 헬퍼 메서드 (간단한 로직으로 가정)
    private String getThumbnailFileCd(String clCd, String thumbnailUrl) {
        // 실제로는 clCd와 thumbnailUrl을 기반으로 DB에서 file_cd를 조회해야 합니다.
        // 현재 findFilesByClCd가 List를 반환하므로, 그중에서 썸네일 URL과 일치하는 것을 찾거나
        // 썸네일만 조회하는 전용 쿼리가 필요할 수 있습니다.
        // 여기서는 예시로, 해당 경로를 가진 파일의 첫 번째 file_cd를 반환한다고 가정합니다.
        List<FileMetaData> files = portfolioMapper.findFilesByClCd(clCd);
        for (FileMetaData file : files) {
            if (restorePathForWebOrFileSystem(file.getFilePath()).equals(thumbnailUrl)) {
                return file.getFileIdx();
            }
        }
        return null; // 썸네일을 찾지 못한 경우
    }
    // [!code diff --end]
}