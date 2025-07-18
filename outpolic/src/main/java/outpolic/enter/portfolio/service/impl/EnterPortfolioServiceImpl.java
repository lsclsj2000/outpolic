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

            // 카테고리를 덮어쓰던 if-else 블록이 완전히 삭제된 상태여야 합니다.
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
        // 매퍼를 호출하여 DB에서 데이터를 검색합니다.
        // 매퍼 인터페이스와 XML에도 해당 쿼리를 작성해야 합니다.
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
        if (formData.getThumbnailFile() != null) { // 새로운 파일이 업로드된 경우에만 썸네일 관련 작업을 수행합니다. 
            // 기존 파일이 있다면 물리적으로 삭제
            List<FileMetaData> existingFiles = portfolioMapper.findFilesByClCd(clCd);
            for (FileMetaData file : existingFiles) {
                String fullPathToDelete = restorePathForWebOrFileSystem(file.getFilePath());
                filesUtils.deleteFileByPath(fullPathToDelete); 
            }
            if (!existingFiles.isEmpty()) {
                portfolioMapper.deleteFilesByClCd(clCd); 
            }
            
            // 새 파일 정보로 업데이트하고 DB에 기록
            existingPortfolio.setPrtfThumbnailUrl(formData.getThumbnailFile().getFilePath()); 
            portfolioMapper.insertFileRecord(formData.getThumbnailFile(), clCd, originalMbrCd); 
        }
        // 새 파일이 없을 때 기존 이미지를 삭제하던 else 블록을 완전히 제거했습니다.
        // 이렇게 하면 새 파일이 없을 경우 기존 썸네일 정보가 그대로 유지됩니다.

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
                    // ★★★ 이 부분이 카테고리를 DB에 저장하는 핵심입니다. ★★★
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
        // 이 메서드에 맞는 매퍼 호출 로직을 추가해야 합니다.
        // 예를 들어, 모든 포트폴리오 중에서 제목으로 검색하는 쿼리를 호출합니다.
        return portfolioMapper.findAllPortfoliosByTitle(query); 
    }

}