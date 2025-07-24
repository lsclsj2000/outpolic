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
    // DB 저장을 위해 파일 경로를 정리하는 유틸리티 메서드 (본문 이미지의 file_path 컬럼용)
    // FilesUtils가 'attachment/...' 형태로 경로를 반환한다고 가정
    private String cleanPathForDb(FileMetaData fileMetaData) {
        if (fileMetaData == null || fileMetaData.getFilePath() == null) return null;

        String fullPath = fileMetaData.getFilePath().replace("\\", "/");
        String serverFileName = fileMetaData.getFileNewName();
        
        String pathOnly;
        if (fullPath.endsWith("/" + serverFileName)) {
            pathOnly = fullPath.substring(0, fullPath.length() - serverFileName.length());
        } else {
            pathOnly = fullPath;
        }

        // 슬래시가 맨 앞에 없는 경우에만 붙여줍니다.
        if (!pathOnly.startsWith("/")) {
            return "/" + pathOnly;
        }
        return pathOnly;
    }


    private String restorePathForWebOrFileSystem(String dbPath) {
        if (dbPath == null) return null;
        String normalizedPath = dbPath.replace("\\", "/");
        // DB에 저장된 썸네일 경로가 이미 '/'로 시작하면 그대로 반환.
        // FilesUtils가 '/' 없이 'attachment/...'로 반환했으므로, DB에도 그렇게 저장될 가능성이 높음.
        // 따라서, 대부분의 경우 앞에 '/'를 붙여야 함.
        if (normalizedPath.startsWith("/")) {
            return normalizedPath;
        }
        return "/" + normalizedPath;
    }

    // 이 메서드는 FileMetaData 객체의 filePath에 저장된 경로를 웹에서 접근 가능한 전체 URL로 변환할 때 사용됩니다.
    // 즉, DB에 '/attachment/서비스/날짜/image/' 경로만 저장했다면, 여기에 FileMetaData의 fileNewName을 붙여야 합니다.
    // file_path 컬럼에 저장된 경로가 이미 '/'로 시작한다고 가정합니다.
    private String restoreFullPathForWeb(FileMetaData fileMetaData) {
        if (fileMetaData == null || fileMetaData.getFilePath() == null || fileMetaData.getFileNewName() == null) return null;
        String dbPath = fileMetaData.getFilePath(); // DB에 저장된 경로 (예: /attachment/portfolio/20250722/image/)
        // dbPath는 이미 '/'로 시작하는 형태로 저장되어야 함 (cleanPathForDb 결과)
        return dbPath + fileMetaData.getFileNewName();
    }


    @Override
    public int countPortfoliosByEntCd(String entCd) {
        return portfolioMapper.countPortfoliosByEntCd(entCd);
    }

    @Override
    public List<EnterPortfolio> getPortfolioListByEntCd(String entCd) {
        List<EnterPortfolio> portfolios = portfolioMapper.findPortfolioDetailsByEntCd(entCd);
        portfolios.forEach(p -> {
            // prtfThumbnailUrl은 이미 전체 경로이므로, restorePathForWebOrFileSystem을 사용하지 않아도 됩니다.
            // 하지만 일관성을 위해 사용하되, 내부 로직이 이미 /attachment/가 있는 경우 처리하도록 되어 있으므로 문제 없습니다.
            p.setPrtfThumbnailUrl(restorePathForWebOrFileSystem(p.getPrtfThumbnailUrl()));
        });
        return portfolios;
    }
    @Override
    public EnterPortfolio getPortfolioByPrtfCd(String prtfCd) {
        EnterPortfolio portfolio = portfolioMapper.findPortfolioDetailsByPrtfCd(prtfCd);
        if (portfolio != null) {
            // 썸네일 경로를 웹에서 접근 가능한 절대 경로로 변환합니다. (이미 전체 경로이므로 문제 없음)
            portfolio.setPrtfThumbnailUrl(restorePathForWebOrFileSystem(portfolio.getPrtfThumbnailUrl()));
            
            // 본문 이미지 경로도 웹 접근 가능하도록 변환
            if (portfolio.getBodyImages() != null) {
                portfolio.getBodyImages().forEach(file ->
                    file.setFilePath(restoreFullPathForWeb(file))
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
        // FilesUtils에 서비스 이름만 전달 (FilesUtils가 내부적으로 'attachment/서비스명/날짜/image/파일명' 형태로 반환한다고 가정)
        FileMetaData uploadedFile = filesUtils.uploadFile(file, "portfolio");
        // 썸네일은 portfolio 테이블에 파일명까지 포함된 전체 웹 경로를 저장
        // FilesUtils가 반환하는 filePath는 'attachment/...' 형태일 것이므로, 앞에 '/'를 붙여서 저장
        if (uploadedFile != null && uploadedFile.getFilePath() != null) {
            String path = uploadedFile.getFilePath().replace("\\", "/");
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            uploadedFile.setFilePath(path);
        }
        return uploadedFile;
    }

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

        // 썸네일 처리: file 테이블에 저장하지 않고 Portfolio 테이블에 경로만 저장
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            FileMetaData thumbMeta = uploadThumbnail(thumbnailFile);
            if (thumbMeta != null) {
                // DB에는 파일명까지 포함된 전체 웹 경로를 저장
                portfolio.setPrtfThumbnailUrl(thumbMeta.getFilePath());
            }
        }
        
        // 1. Save the main portfolio information first
        portfolioMapper.addPortfolio(portfolio);
        // 2. [Key Change] Create the clCd directly from the unique prtfCd
        String clCd = "LIST_" + prtfCd;
        // 3. Save to content_list to create the parent row for foreign keys
        portfolioMapper.insertContentList(clCd, prtfCd);
        
        // 4. 본문 이미지들을 file 테이블에 저장 (썸네일은 file 테이블에 저장하지 않음)
        if (bodyImageFiles != null && !bodyImageFiles.isEmpty()) {
            for (MultipartFile bodyFile : bodyImageFiles) {
                // FilesUtils에 서비스 이름만 전달 (FilesUtils가 내부적으로 'attachment/서비스명/날짜/image/파일명' 형태로 반환한다고 가정)
                FileMetaData bodyMeta = filesUtils.uploadFile(bodyFile, "portfolio");
                if (bodyMeta != null) {
                    // DB에는 '/attachment/portfolio/날짜/image/' 형태로 저장
                    bodyMeta.setFilePath(cleanPathForDb(bodyMeta));
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
        
        // 썸네일 파일 처리: 새 파일이 업로드된 경우
        if (formData.getNewThumbnailFile() != null && !formData.getNewThumbnailFile().isEmpty()) {
            FileMetaData newThumbnailMetaData = uploadThumbnail(formData.getNewThumbnailFile());
            if (newThumbnailMetaData != null) {
                // Portfolio 테이블에 썸네일 경로 업데이트
                existingPortfolio.setPrtfThumbnailUrl(newThumbnailMetaData.getFilePath());
            }
        } else if (formData.getIsThumbnailDeleted() != null && formData.getIsThumbnailDeleted().equals("true")) {
            // 기존 썸네일이 있었는데, 새 썸네일이 없고, 명시적으로 삭제 요청이 온 경우
            if (existingPortfolio.getPrtfThumbnailUrl() != null) {
                // 물리적 파일 삭제 시에는 prtf_thumbnail_url (전체 경로)를 사용합니다.
                filesUtils.deleteFileByPath(existingPortfolio.getPrtfThumbnailUrl());
            }
            existingPortfolio.setPrtfThumbnailUrl(null);
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

        // 삭제된 본문 이미지 처리
        if (formData.getDeletedBodyImageCds() != null && !formData.getDeletedBodyImageCds().isEmpty()) {
            for (String fileCd : formData.getDeletedBodyImageCds()) {
                FileMetaData fileToDelete = portfolioMapper.findFileMetaDataByFileCd(fileCd);
                if (fileToDelete != null) {
                    // 물리적 파일 삭제 시에는 file_path (폴더 경로)와 file_srvr_nm (파일명)을 조합하여 전체 경로를 만듭니다.
                    String fullPathToDelete = restoreFullPathForWeb(fileToDelete);
                    filesUtils.deleteFileByPath(fullPathToDelete);
                    portfolioMapper.deleteFilesByFileCd(fileCd);
                }
            }
        }

        // 새로 추가된 본문 이미지 처리
        if (formData.getNewBodyImageFiles() != null && !formData.getNewBodyImageFiles().isEmpty()) {
            for (MultipartFile bodyFile : formData.getNewBodyImageFiles()) {
                // FilesUtils에 서비스 이름만 전달 (FilesUtils가 내부적으로 'attachment/서비스명/날짜/image/파일명' 형태로 반환한다고 가정)
                FileMetaData bodyMeta = filesUtils.uploadFile(bodyFile, "portfolio");
                if (bodyMeta != null) {
                    // DB에는 '/attachment/portfolio/날짜/image/' 형태로 저장
                    bodyMeta.setFilePath(cleanPathForDb(bodyMeta));
                    portfolioMapper.insertFileRecord(bodyMeta, clCd,originalMbrCd);
                }
            }
        }
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

    // 썸네일 파일의 file_cd를 가져오는 헬퍼 메서드 (간단한 로직으로 가정)
    private String getThumbnailFileCd(String clCd, String thumbnailUrl) {
        // 실제로는 clCd와 thumbnailUrl을 기반으로 DB에서 file_cd를 조회해야 합니다.
        // 현재 findFilesByClCd가 List를 반환하므로, 그중에서 썸네일 URL과 일치하는 것을 찾거나
        // 썸네일만 조회하는 전용 쿼리가 필요할 수 있습니다.
        // 여기서는 예시로, 해당 경로를 가진 파일의 첫 번째 file_cd를 반환한다고 가정합니다.
        List<FileMetaData> files = portfolioMapper.findFilesByClCd(clCd);
        for (FileMetaData file : files) {
            // 이 비교는 file.getFilePath()가 이미 파일명 없이 경로만 저장되어 있을 때 유효합니다.
            if (restorePathForWebOrFileSystem(file.getFilePath()).equals(thumbnailUrl)) {
                return file.getFileIdx();
            }
        }
        return null;
    }
}