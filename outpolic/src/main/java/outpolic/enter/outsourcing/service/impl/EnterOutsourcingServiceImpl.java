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
import outpolic.enter.portfolio.mapper.PortfolioMapper;
import outpolic.systems.file.domain.FileMetaData;
import outpolic.systems.util.FilesUtils;

@Service
@RequiredArgsConstructor
public class EnterOutsourcingServiceImpl implements EnterOutsourcingService {

    private static final Logger logger = LoggerFactory.getLogger(EnterOutsourcingServiceImpl.class);
    private final OutsourcingMapper outsourcingMapper;
    private final PortfolioMapper portfolioMapper;
    private final FilesUtils filesUtils;

    // DB 저장을 위해 파일 경로를 정리하는 유틸리티 메서드
    private String cleanPathForDb(String filePath) {
        if (filePath == null) return null;
        String cleaned = filePath.startsWith("/") ? filePath.substring(1) : filePath;
        return cleaned.startsWith("attachment/") ? cleaned.substring("attachment/".length()) : cleaned;
    }

    // 웹 표시 또는 파일 시스템 접근을 위해 경로를 복원하는 유틸리티 메서드
    private String restorePathForWebOrFileSystem(String dbPath) {
        if (dbPath == null) return null;
        return "/attachment/" + dbPath;
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
        String imageTypeDir = (file.getContentType() != null && file.getContentType().startsWith("image")) ? "thumbnail" : "files";
        String fullServicePath = serviceName + "/" + imageTypeDir;
        
        FileMetaData uploadedFile = filesUtils.uploadFile(file, fullServicePath);
        if (uploadedFile != null && uploadedFile.getFilePath() != null) {
            String cleanedPath = cleanPathForDb(uploadedFile.getFilePath());
            uploadedFile.setFilePath(cleanedPath);
        }
        return uploadedFile;
    }

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
    public void completeOutsourcingRegistration(OutsourcingFormDataDto formData, HttpSession session) {
        EnterOutsourcing finalOutsourcing = new EnterOutsourcing();
        finalOutsourcing.setOsCd(formData.getOsCd());
        finalOutsourcing.setEntCd(formData.getEntCd());
        finalOutsourcing.setMbrCd(formData.getMbrCd());
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
        
        if (formData.getThumbnailFile() != null) {
            finalOutsourcing.setOsThumbnailUrl(formData.getThumbnailFile().getFilePath());
        }

        outsourcingMapper.insertOutsourcing(finalOutsourcing);

        String clCd = "LIST_" + finalOutsourcing.getOsCd();
        outsourcingMapper.insertContentList(clCd, finalOutsourcing.getOsCd());
        
        if (formData.getThumbnailFile() != null) {
            List<FileMetaData> fileList = new ArrayList<>();
            fileList.add(formData.getThumbnailFile());
            outsourcingMapper.insertFiles(fileList, clCd, finalOutsourcing.getMbrCd());
        }
        
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

        updateMappings(clCd, originalOutsourcing.getMbrCd(), categoryCodes, tags);
        
        if (categoryCodes != null && !categoryCodes.isEmpty()) {
            outsourcingMapper.updateOutsourcingRepresentativeCategory(osCd, categoryCodes.get(0));
        } else {
            outsourcingMapper.updateOutsourcingRepresentativeCategory(osCd, null);
        }
    }
    
    @Override
    @Transactional
    public void updateOutsourcingStep3(String osCd, MultipartFile thumbnailFile) {
        EnterOutsourcing outsourcing = outsourcingMapper.findOutsourcingDetailsByOsCd(osCd);
        if (outsourcing == null) throw new IllegalStateException("수정할 외주 정보가 없습니다.");

        String clCd = outsourcingMapper.findClCdByOsCd(osCd);
        if (clCd == null) throw new IllegalStateException("콘텐츠 목록 정보를 찾을 수 없습니다.");

        List<FileMetaData> oldFiles = outsourcingMapper.findFilesByClCd(clCd);
        if (oldFiles != null && !oldFiles.isEmpty()) {
            for(FileMetaData oldFile : oldFiles) {
                String fullPathToDelete = restorePathForWebOrFileSystem(oldFile.getFilePath());
                filesUtils.deleteFileByPath(fullPathToDelete);
            }
            outsourcingMapper.deleteFilesByClCd(clCd);
        }

        List<FileMetaData> newUploadedFiles = new ArrayList<>();
        String newThumbnailUrlForDb = null;
        
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            FileMetaData newThumbnailMetaData = uploadThumbnail(thumbnailFile);
            if (newThumbnailMetaData != null) {
                newUploadedFiles.add(newThumbnailMetaData);
                newThumbnailUrlForDb = newThumbnailMetaData.getFilePath();
            }
        }
        
        outsourcingMapper.updateOutsourcingThumbnail(osCd, newThumbnailUrlForDb);
        
        if (!newUploadedFiles.isEmpty()) {
            outsourcingMapper.insertFiles(newUploadedFiles, clCd, outsourcing.getMbrCd());
        }
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
    public void unlinkPortfolioFromOutsourcing(String osCd, String prtfCd) {
        outsourcingMapper.unlinkOutsourcingFromPortfolio(osCd, prtfCd);
    }

    @Override
    public List<EnterPortfolio> searchUnlinkedPortfolios(String osCd, String entCd, String query) {
        return portfolioMapper.findUnlinkedPortfolios(osCd, entCd, query);
    }
    
    private void updateMappings(String clCd, String mbrCd, List<String> categoryCodes, String tags) {
        if (categoryCodes != null) {
            for (String ctgryId : categoryCodes) {
                if (ctgryId != null && !ctgryId.trim().isEmpty()) {
                    outsourcingMapper.insertCategoryMapping(ctgryId, clCd, mbrCd);
                }
            }
        }

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
    
}