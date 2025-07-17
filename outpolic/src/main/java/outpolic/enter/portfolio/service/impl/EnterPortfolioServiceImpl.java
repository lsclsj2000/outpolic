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

        // 새로 추가된 필드 설정
        portfolio.setPrtfPeriodStart(formData.getPrtfPeriodStart());
        portfolio.setPrtfPeriodEnd(formData.getPrtfPeriodEnd());
        portfolio.setPrtfClient(formData.getPrtfClient());
        portfolio.setPrtfIndustry(formData.getPrtfIndustry());
        
        if (formData.getThumbnailFile() != null) {
            portfolio.setPrtfThumbnailUrl(formData.getThumbnailFile().getFilePath());
        }

        // 대표 카테고리 ID를 설정하는 로직 추가
        if (formData.getCategoryCodes() != null && !formData.getCategoryCodes().isEmpty()) {
            portfolio.setCtgryId(formData.getCategoryCodes().get(0));
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
    public void updatePortfolioAllSteps(PortfolioFormDataDto formData) throws IOException {
        String prtfCd = formData.getPrtfCd();
        String clCd = portfolioMapper.findClCdByPrtfCd(prtfCd);
        if (clCd == null) {
            throw new IllegalStateException("콘텐츠 목록(cl_cd)을 찾을 수 없습니다.");
        }
        String originalMbrCd = portfolioMapper.findMbrCdByClCd(clCd);
        
        // 기존 포트폴리오 정보를 DB에서 다시 불러옴
        EnterPortfolio existingPortfolio = portfolioMapper.findPortfolioDetailsByPrtfCd(prtfCd);
        if (existingPortfolio == null) {
            throw new IllegalArgumentException("수정할 포트폴리오를 찾을 수 없습니다: " + prtfCd);
        }

        // formData에서 넘어온 값으로 existingPortfolio 업데이트
        existingPortfolio.setPrtfTtl(formData.getPrtfTtl());
        existingPortfolio.setPrtfCn(formData.getPrtfCn());
        existingPortfolio.setPrtfPeriodStart(formData.getPrtfPeriodStart());
        existingPortfolio.setPrtfPeriodEnd(formData.getPrtfPeriodEnd());
        existingPortfolio.setPrtfClient(formData.getPrtfClient());
        existingPortfolio.setPrtfIndustry(formData.getPrtfIndustry());
        existingPortfolio.setPrtfMdfcnYmdt(LocalDateTime.now());

        // 썸네일 파일 처리
        // formData.getThumbnailFile()이 null이면 새 파일이 업로드되지 않았음을 의미
        // 이 때, initialThumbnailUrl이 null이면 썸네일을 아예 삭제한 것으로 간주 (빈 Blob 전송 시)
        // initialThumbnailUrl이 유효하고 formData.getThumbnailFile()이 null이면 기존 썸네일 유지
        if (formData.getThumbnailFile() != null) { // 새로운 파일이 업로드된 경우
            // 기존 파일 삭제
            List<FileMetaData> existingFiles = portfolioMapper.findFilesByClCd(clCd);
            for (FileMetaData file : existingFiles) {
                String fullPathToDelete = restorePathForWebOrFileSystem(file.getFilePath());
                filesUtils.deleteFileByPath(fullPathToDelete);
            }
            if (!existingFiles.isEmpty()) {
                portfolioMapper.deleteFilesByClCd(clCd);
            }
            // 새 파일 저장
            existingPortfolio.setPrtfThumbnailUrl(formData.getThumbnailFile().getFilePath());
            portfolioMapper.insertFileRecord(formData.getThumbnailFile(), clCd, originalMbrCd);
        } else { // formData.getThumbnailFile()이 null인 경우 (새 파일 선택 안함, 또는 삭제 의도)
            // edit.html에서 initialThumbnailUrl을 null로 만들고 빈 Blob을 보냈으므로,
            // 이 경우 기존 썸네일을 DB에서 null로 만들어야 함.
            // 기존 썸네일이 있었으나 이제는 없어야 하는 경우
            if (existingPortfolio.getPrtfThumbnailUrl() != null) {
                 // 기존 파일 삭제 로직 (물리적 + DB 레코드)
                List<FileMetaData> existingFiles = portfolioMapper.findFilesByClCd(clCd);
                for (FileMetaData file : existingFiles) {
                    String fullPathToDelete = restorePathForWebOrFileSystem(file.getFilePath());
                    filesUtils.deleteFileByPath(fullPathToDelete);
                }
                if (!existingFiles.isEmpty()) {
                    portfolioMapper.deleteFilesByClCd(clCd);
                }
                existingPortfolio.setPrtfThumbnailUrl(null); // DB에서 썸네일 URL을 null로 업데이트
            }
            // 만약 existingPortfolio.getPrtfThumbnailUrl()이 이미 null이었고,
            // 새 파일을 선택하지도, 삭제하지도 않았다면 이 else 블록은 아무것도 하지 않음.
            // 즉, null 상태를 유지.
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
}