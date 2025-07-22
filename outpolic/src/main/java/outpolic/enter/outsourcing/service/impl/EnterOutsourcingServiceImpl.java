package outpolic.enter.outsourcing.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;
import outpolic.enter.outsourcing.domain.OutsourcingFormDataDto;
import outpolic.enter.outsourcing.mapper.OutsourcingMapper;
import outpolic.enter.outsourcing.service.EnterOutsourcingService;
import outpolic.enter.portfolio.domain.EnterPortfolio;
import outpolic.enter.portfolio.mapper.PortfolioMapper; // 이 부분은 외주 서비스에서 필요없다면 제거해도 됩니다.
import outpolic.systems.file.domain.FileMetaData;
import outpolic.systems.util.FilesUtils;

@Service
@RequiredArgsConstructor
public class EnterOutsourcingServiceImpl implements EnterOutsourcingService {

    private static final Logger logger = LoggerFactory.getLogger(EnterOutsourcingServiceImpl.class);
    private final OutsourcingMapper outsourcingMapper;
    private final PortfolioMapper portfolioMapper; // 외주 기능에서 포트폴리오 매퍼를 직접 사용하지 않으면 제거 가능
    private final FilesUtils filesUtils;

    // DB 저장을 위해 파일 경로를 정리하는 유틸리티 메서드
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

    // 웹 표시 또는 파일 시스템 접근을 위해 경로를 복원하는 유틸리티 메서드
    private String restorePathForWebOrFileSystem(String dbPath) {
        if (dbPath == null) return null;
        String normalizedPath = dbPath.replace("\\", "/");
        // 이미 접두사가 있다면 그대로 반환하여 중복을 방지합니다.
        if (normalizedPath.startsWith("/attachment/") || normalizedPath.startsWith("attachment/")) {
            return normalizedPath.startsWith("/") ? normalizedPath : "/" + normalizedPath;
        }
        // 접두사가 없는 경우에만 붙여줍니다.
        return "/attachment/" + normalizedPath;
    }

    @Override
    public List<EnterOutsourcing> getOutsourcingListByEntCd(String entCd) {
        List<EnterOutsourcing> outsourcings = outsourcingMapper.findOutsourcingDetailsByEntCd(entCd);
        outsourcings.forEach(o -> o.setOsThumbnailUrl(restorePathForWebOrFileSystem(o.getOsThumbnailUrl())));
        return outsourcings;
    }

    @Override
    public EnterOutsourcing findOutsourcingDetailsByOsCd(String osCd) {
        EnterOutsourcing outsourcing = outsourcingMapper.findOutsourcingDetailsByOsCd(osCd);
        if (outsourcing != null) {
            outsourcing.setOsThumbnailUrl(restorePathForWebOrFileSystem(outsourcing.getOsThumbnailUrl()));
            // [!code diff --start]
            // 본문 이미지 경로도 웹 접근 가능하도록 변환
            if (outsourcing.getBodyImages() != null) {
                outsourcing.getBodyImages().forEach(file ->
                    file.setFilePath(restorePathForWebOrFileSystem(file.getFilePath()))
                );
            }
            // [!code diff --end]
        }
        return outsourcing;
    }

    @Override
    public List<String> searchTags(String query) {
        return outsourcingMapper.searchTagsByName(query);
    }

    @Override
    public String findEntCdByMbrCd(String mbrCd) {
        return outsourcingMapper.findEntCdByMbrCd(mbrCd);
    }

    @Override
    public List<String> getFilesByClCd(String clCd) {
        List<FileMetaData> fileMetaDataList = outsourcingMapper.findFilesByClCd(clCd);
        return fileMetaDataList.stream()
                               .map(file -> restorePathForWebOrFileSystem(file.getFilePath()))
                               .collect(Collectors.toList());
    }

    @Override
    public FileMetaData uploadThumbnail(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        String serviceName = "outsourcing";
        String imageTypeDir = (file.getContentType() != null && file.getContentType().startsWith("image")) ? "image" : "files";
        
        // ▼▼▼ 아래와 같이 수정 ▼▼▼
        String fullServicePath = serviceName + "/thumbnail/" + imageTypeDir; // 👈 "outsourcing/thumbnail/image" 경로를 생성합니다.

        FileMetaData uploadedFile = filesUtils.uploadFile(file, fullServicePath);
        if (uploadedFile != null && uploadedFile.getFilePath() != null) {
            String cleanedPath = cleanPathForDb(uploadedFile.getFilePath());
            uploadedFile.setFilePath(cleanedPath);
        }
        return uploadedFile;
    }
    
    // [!code diff --start]
    public FileMetaData uploadBodyImage(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        String serviceName = "outsourcing";
        String imageTypeDir = "body"; // 본문 이미지는 별도 디렉토리
        String fullServicePath = serviceName + "/" + imageTypeDir;

        FileMetaData uploadedFile = filesUtils.uploadFile(file, fullServicePath);
        if (uploadedFile != null && uploadedFile.getFilePath() != null) {
            String cleanedPath = cleanPathForDb(uploadedFile.getFilePath());
            uploadedFile.setFilePath(cleanedPath);
        }
        return uploadedFile;
    }
    // [!code diff --end]

    @Override
    public String saveStep1Data(OutsourcingFormDataDto formData, HttpSession session) {
        if (formData.getOsCd() == null || formData.getOsCd().isEmpty()) {
            String latestOsCd = outsourcingMapper.findLatestOsCd();
            int nextNum = (latestOsCd == null || !latestOsCd.startsWith("OS_C")) ? 1 : Integer.parseInt(latestOsCd.substring(5)) + 1;
            formData.setOsCd(String.format("OS_C%05d", nextNum));
        }
        session.setAttribute("outsourcingFormData", formData);
        return formData.getOsCd();
    }
    
    @Override
    @Transactional
    public void completeOutsourcingRegistration(OutsourcingFormDataDto formData, MultipartFile thumbnailFile, List<MultipartFile> bodyImageFiles, HttpSession session) {
        
        // 세션에서 mbrCd, entCd 가져오기
        String mbrCd = (String) session.getAttribute("SCD");
        String entCd = outsourcingMapper.findEntCdByMbrCd(mbrCd);

        EnterOutsourcing finalOutsourcing = new EnterOutsourcing();

        // 새로운 외주 코드(PK) 생성
        String latestOsCd = outsourcingMapper.findLatestOsCd();
        int nextNum = (latestOsCd == null || !latestOsCd.startsWith("OS_C")) ? 1 : Integer.parseInt(latestOsCd.substring(5)) + 1;
        String newOsCd = String.format("OS_C%05d", nextNum);
        finalOutsourcing.setOsCd(newOsCd);

        // DTO에서 받은 텍스트 데이터 설정
        finalOutsourcing.setEntCd(entCd);
        finalOutsourcing.setMbrCd(mbrCd);
        finalOutsourcing.setOsTtl(formData.getOsTtl());
        finalOutsourcing.setOsExpln(formData.getOsExpln());
        finalOutsourcing.setOsStrtYmdt(formData.getOsStrtYmdt());
        finalOutsourcing.setOsEndYmdt(formData.getOsEndYmdt());
        finalOutsourcing.setOsAmt(formData.getOsAmt());
        finalOutsourcing.setOsFlfmtCnt(formData.getOsFlfmtCnt());

        if (formData.getCategoryCodes() != null && !formData.getCategoryCodes().isEmpty()) {
            finalOutsourcing.setCtgryId(formData.getCategoryCodes().get(0));
        } else {
            throw new IllegalArgumentException("카테고리 ID는 필수입니다. 2단계에서 카테고리를 선택해주세요.");
        }
        
        finalOutsourcing.setOsRegYmdt(LocalDateTime.now());
        finalOutsourcing.setStcCd("SD_ACTIVE");
        
        // 2. 썸네일 처리 로직을 파라미터로 받은 thumbnailFile로 변경하고, 중복 코드 삭제
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            FileMetaData thumbMeta = uploadThumbnail(thumbnailFile);
            if (thumbMeta != null) {
                finalOutsourcing.setOsThumbnailUrl(thumbMeta.getFilePath());
            }
        }

        // 3. outsourcing 테이블에 먼저 INSERT
        outsourcingMapper.insertOutsourcing(finalOutsourcing);

        // 4. 생성된 osCd 기반으로 clCd 생성 후 content_list에 INSERT
        String clCd = "LIST_" + finalOutsourcing.getOsCd();
        outsourcingMapper.insertContentList(clCd, finalOutsourcing.getOsCd());
        
        // 5. 파일들을 DB에 저장
        List<FileMetaData> filesToInsert = new ArrayList<>();

        // 썸네일 파일 메타데이터 추가 (업로드된 경우)
        if (finalOutsourcing.getOsThumbnailUrl() != null) {
            // uploadThumbnail 메소드가 FileMetaData를 반환한다고 가정
            FileMetaData thumbMeta = uploadThumbnail(thumbnailFile);
            if (thumbMeta != null) {
                filesToInsert.add(thumbMeta);
            }
        }

        // 본문 이미지들 메타데이터 추가
        if (bodyImageFiles != null && !bodyImageFiles.isEmpty()) {
            for (MultipartFile bodyFile : bodyImageFiles) {
                FileMetaData bodyMeta = uploadBodyImage(bodyFile);
                if (bodyMeta != null) {
                    filesToInsert.add(bodyMeta);
                }
            }
        }
        
        // DB에 파일 정보 한 번에 INSERT
        if (!filesToInsert.isEmpty()) {
            outsourcingMapper.insertFiles(filesToInsert, clCd, finalOutsourcing.getMbrCd());
        }
        
        // 카테고리 및 태그 매핑 처리
        updateMappings(clCd, finalOutsourcing.getMbrCd(), formData.getCategoryCodes(), formData.getTags());
    }

    @Override
    @Transactional
    public void updateOutsourcingStep1(EnterOutsourcing outsourcingToUpdate) {
        outsourcingToUpdate.setOsMdfcnYmdt(LocalDateTime.now());
        outsourcingMapper.updateOutsourcingStep1(outsourcingToUpdate);
    }

    @Override
    @Transactional
    public void updateOutsourcingStep2(String osCd, List<String> categoryCodes, String tags) {
        String clCd = outsourcingMapper.findClCdByOsCd(osCd);
        if (clCd == null) throw new IllegalStateException("콘텐츠 목록 정보가 없습니다.");
        
        EnterOutsourcing originalOutsourcing = outsourcingMapper.findOutsourcingDetailsByOsCd(osCd);
        if (originalOutsourcing == null) throw new IllegalStateException("수정할 외주 정보를 찾을 수 없습니다.");

        outsourcingMapper.deleteCategoryMappingByClCd(clCd);
        outsourcingMapper.deleteTagMappingByClCd(clCd);

        // 카테고리 매핑 로직은 건드리지 않음
        updateMappings(clCd, originalOutsourcing.getMbrCd(), categoryCodes, tags);
        if (categoryCodes != null && !categoryCodes.isEmpty()) {
            outsourcingMapper.updateOutsourcingRepresentativeCategory(osCd, categoryCodes.get(0));
        } else {
            outsourcingMapper.updateOutsourcingRepresentativeCategory(osCd, null);
        }
    }
    
    @Override
    @Transactional
    public void updateOutsourcingStep3(String osCd, MultipartFile thumbnailFile, List<MultipartFile> newBodyImageFiles, List<String> deletedBodyImageCds) { // [!code modified]
        EnterOutsourcing outsourcing = outsourcingMapper.findOutsourcingDetailsByOsCd(osCd);
        if (outsourcing == null) throw new IllegalStateException("수정할 외주 정보가 없습니다.");

        String clCd = outsourcingMapper.findClCdByOsCd(osCd);
        if (clCd == null) throw new IllegalStateException("콘텐츠 목록 정보를 찾을 수 없습니다.");

        // 기존 썸네일 처리 (새 파일이 업로드되었거나 기존 썸네일이 삭제된 경우)
        boolean hasNewThumbnail = (thumbnailFile != null && !thumbnailFile.isEmpty());
        String currentThumbnailUrl = outsourcing.getOsThumbnailUrl(); // DB에 저장된 현재 썸네일 URL

        if (hasNewThumbnail) {
            // 기존 썸네일 파일 삭제 (물리적 + DB 레코드)
            if (currentThumbnailUrl != null && !currentThumbnailUrl.isEmpty()) {
                // 썸네일의 FileMetaData를 찾아 물리적 파일 및 DB 레코드 삭제
                List<FileMetaData> existingFiles = outsourcingMapper.findFilesByClCd(clCd);
                for (FileMetaData file : existingFiles) {
                    // 썸네일 경로를 DB에 저장된 방식에 맞게 확인
                    String cleanedCurrentThumbnailUrl = cleanPathForDb(currentThumbnailUrl);
                    if (file.getFilePath().equals(cleanedCurrentThumbnailUrl)) {
                        filesUtils.deleteFileByPath(restorePathForWebOrFileSystem(file.getFilePath()));
                        outsourcingMapper.deleteFilesByFileCd(file.getFileIdx()); // file_cd로 삭제
                        break;
                    }
                }
            }
            // 새 썸네일 업로드 및 DB 저장
            FileMetaData newThumbnailMetaData = uploadThumbnail(thumbnailFile);
            if (newThumbnailMetaData != null) {
                outsourcingMapper.updateOutsourcingThumbnail(osCd, newThumbnailMetaData.getFilePath());
                List<FileMetaData> fileList = new ArrayList<>();
                fileList.add(newThumbnailMetaData);
                outsourcingMapper.insertFiles(fileList, clCd, outsourcing.getMbrCd());
            } else {
                // 새 썸네일 업로드 실패 시 기존 썸네일 URL은 유지
            }
        } else { // 새 썸네일이 없는 경우
            // 만약 기존 썸네일이 있었는데, 완전히 삭제를 원할 경우 (프론트엔드에서 null을 넘겨줄 때)
            // 현재 updateOutsourcingThumbnail은 null을 업데이트하지 않습니다.
            // 필요 시 updateOutsourcingThumbnail 메서드를 수정하거나, 별도 로직 추가
            // 여기서는 썸네일 파일만 다루므로, 본문 파일과 별개로 처리합니다.
        }

        // [!code diff --start]
        // 5단계: 본문 이미지 수정
        // 1. 삭제할 기존 본문 이미지 처리
        if (deletedBodyImageCds != null && !deletedBodyImageCds.isEmpty()) {
            for (String fileCd : deletedBodyImageCds) {
                FileMetaData fileToDelete = outsourcingMapper.findFileMetaDataByFileCd(fileCd);
                if (fileToDelete != null) {
                    filesUtils.deleteFileByPath(restorePathForWebOrFileSystem(fileToDelete.getFilePath())); // 물리적 파일 삭제
                    outsourcingMapper.deleteFilesByFileCd(fileCd); // DB 레코드 삭제
                }
            }
        }

        // 2. 새로 추가할 본문 이미지 처리
        if (newBodyImageFiles != null && !newBodyImageFiles.isEmpty()) {
            List<FileMetaData> bodyFilesToInsert = new ArrayList<>();
            for (MultipartFile bodyFile : newBodyImageFiles) {
                FileMetaData bodyMeta = uploadBodyImage(bodyFile); // 본문 이미지 업로드
                if (bodyMeta != null) {
                    bodyFilesToInsert.add(bodyMeta);
                }
            }
            if (!bodyFilesToInsert.isEmpty()) {
                outsourcingMapper.insertFiles(bodyFilesToInsert, clCd, outsourcing.getMbrCd());
            }
        }
        // [!code diff --end]
    }

    @Override
    @Transactional
    public void deleteOutsourcing(String osCd) {
        String clCd = outsourcingMapper.findClCdByOsCd(osCd);
        if (clCd != null) {
            List<FileMetaData> filesToDelete = outsourcingMapper.findFilesByClCd(clCd);
            for (FileMetaData file : filesToDelete) {
                String fullPathToDelete = restorePathForWebOrFileSystem(file.getFilePath());
                filesUtils.deleteFileByPath(fullPathToDelete);
            }
            outsourcingMapper.deletePerusalContentByClCd(clCd);
            outsourcingMapper.deleteOutsourcingStatusByClCd(clCd);
            outsourcingMapper.deleteRankingByClCd(clCd);
            outsourcingMapper.deleteTodayViewByClCd(clCd);
            outsourcingMapper.deleteTotalViewByClCd(clCd);
            outsourcingMapper.deleteBookmarkByClCd(clCd);
            outsourcingMapper.deleteFilesByClCd(clCd);
            outsourcingMapper.deleteTagMappingByClCd(clCd);
            outsourcingMapper.deleteCategoryMappingByClCd(clCd);
            outsourcingMapper.deleteOutsourcingPortfolioByOsCd(osCd);
            outsourcingMapper.deleteOutsourcingContractDetailsByClCd(clCd);
            outsourcingMapper.deleteContentListByClCd(clCd);
        }
        outsourcingMapper.deleteOutsourcingByOsCd(osCd);
    }
    
    @Override
    public List<EnterPortfolio> getLinkedPortfoliosByOsCd(String osCd) {
        return portfolioMapper.findLinkedPortfoliosByOsCd(osCd);
    }

    @Override
    @Transactional
    public void linkPortfolioToOutsourcing(String osCd, String prtfCd, String entCd) {
        String latestOpCd = outsourcingMapper.findLatestOpCd();
        int nextNum = 1;
        if (latestOpCd != null && latestOpCd.startsWith("OP_C")) {
            try {
                nextNum = Integer.parseInt(latestOpCd.substring(4)) + 1;
            } catch (NumberFormatException e) {
                logger.warn("Failed to parse latestOpCd for linking portfolio: {}", latestOpCd, e);
            }
        }
        outsourcingMapper.insertOutsourcingPortfolio(String.format("OP_C%05d", nextNum), osCd, prtfCd, entCd);
    }

    @Override
    @Transactional
    public void unlinkOutsourcingFromPortfolio(String osCd, String prtfCd) {
        outsourcingMapper.unlinkOutsourcingFromPortfolio(osCd, prtfCd);
    }

    @Override
    public List<EnterPortfolio> searchUnlinkedPortfolios(String osCd, String entCd, String query) {
        return portfolioMapper.findUnlinkedPortfolios(osCd, entCd, query);
    }
    
    private void updateMappings(String clCd, String mbrCd, List<String> categoryCodes, String tags) {
        // 카테고리 매핑 로직은 건드리지 않음 (기존 주석 처리된 로직 그대로 유지)
        /*
        if (categoryCodes != null) {
            for (String ctgryId : categoryCodes) {
                if (ctgryId != null && !ctgryId.trim().isEmpty()) {
                    outsourcingMapper.insertCategoryMapping(ctgryId, clCd, mbrCd);
                }
            }
        }
        */

        if (tags != null && !tags.trim().isEmpty()) {
            String[] tagNames = tags.split(",");
            for (String tagName : tagNames) {
                String trimmedTagName = tagName.trim();
                if (trimmedTagName.isEmpty()) continue;
                
                String tagCd = outsourcingMapper.findTagCdByName(trimmedTagName);
                if (tagCd == null) {
                    String latestTagCd = outsourcingMapper.findLatestTagCd();
                    int nextNum = 1;
                    if (latestTagCd != null && latestTagCd.startsWith("T_C")) {
                        try {
                            nextNum = Integer.parseInt(latestTagCd.substring(4)) + 1;
                        } catch (NumberFormatException e) {
                            logger.warn("Failed to parse latestTagCd: {}", latestTagCd, e);
                        }
                    }
                    tagCd = String.format("T_C%05d", nextNum);
                    outsourcingMapper.insertTag(tagCd, trimmedTagName, mbrCd);
                }
                outsourcingMapper.insertTagMapping(tagCd, clCd, mbrCd);
            }
        }
    }
    @Override
    public EnterOutsourcing getOutsourcingByOsCd(String osCd) {
        return this.findOutsourcingDetailsByOsCd(osCd);
    }

    @Override
    @Transactional
    public void unlinkPortfolioFromOutsourcing(String osCd, String prtfCd) {
    	outsourcingMapper.unlinkOutsourcingFromPortfolio(osCd, prtfCd);
    }

}