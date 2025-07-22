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

    private String cleanPathForDb(FileMetaData fileMetaData) {
        if (fileMetaData == null || fileMetaData.getFilePath() == null) return null;
        
        String fullPath = fileMetaData.getFilePath().replace("\\", "/");
        String serverFileName = fileMetaData.getFileNewName();

        if (fullPath.endsWith("/" + serverFileName)) {
            return "/" + fullPath.substring(0, fullPath.length() - serverFileName.length());
        }
        return "/" + fullPath;
    }

    private String restorePathForWebOrFileSystem(String dbPath) {
        if (dbPath == null) return null;
        String normalizedPath = dbPath.replace("\\", "/");
        if (normalizedPath.startsWith("/")) {
            return normalizedPath;
        }
        return "/" + normalizedPath;
    }
    
    private String restoreFullPathForWeb(FileMetaData fileMetaData) {
        if (fileMetaData == null || fileMetaData.getFilePath() == null || fileMetaData.getFileNewName() == null) return null;
        String dbPath = fileMetaData.getFilePath();
        return dbPath + fileMetaData.getFileNewName();
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
            
            if (outsourcing.getBodyImages() != null) {
                outsourcing.getBodyImages().forEach(file ->
                    file.setFilePath(restoreFullPathForWeb(file))
                );
            }
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
                               .map(file -> restoreFullPathForWeb(file))
                               .collect(Collectors.toList());
    }

    @Override
    public FileMetaData uploadThumbnail(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        FileMetaData uploadedFile = filesUtils.uploadFile(file, "outsourcing");
        if (uploadedFile != null && uploadedFile.getFilePath() != null) {
            uploadedFile.setFilePath("/" + uploadedFile.getFilePath());
        }
        return uploadedFile;
    }
    
    @Override
    public FileMetaData uploadBodyImage(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        FileMetaData uploadedFile = filesUtils.uploadFile(file, "outsourcing");
        if (uploadedFile != null && uploadedFile.getFilePath() != null) {
            uploadedFile.setFilePath(cleanPathForDb(uploadedFile));
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
    public void completeOutsourcingRegistration(OutsourcingFormDataDto formData, MultipartFile thumbnailFile, List<MultipartFile> bodyImageFiles, HttpSession session) {
        
        String mbrCd = (String) session.getAttribute("SCD");
        if (mbrCd == null) {
            throw new IllegalStateException("로그인한 사용자 코드를 찾을 수 없습니다.");
        }
        String entCd = outsourcingMapper.findEntCdByMbrCd(mbrCd);

        EnterOutsourcing finalOutsourcing = new EnterOutsourcing();

        String latestOsCd = outsourcingMapper.findLatestOsCd();
        int nextNum = (latestOsCd == null || !latestOsCd.startsWith("OS_C")) ? 1 : Integer.parseInt(latestOsCd.substring(5)) + 1;
        String newOsCd = String.format("OS_C%05d", nextNum);
        finalOutsourcing.setOsCd(newOsCd);
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

        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            FileMetaData thumbMeta = uploadThumbnail(thumbnailFile);
            if (thumbMeta != null) {
                finalOutsourcing.setOsThumbnailUrl(thumbMeta.getFilePath());
            }
        }

        outsourcingMapper.insertOutsourcing(finalOutsourcing);
        String clCd = "LIST_" + finalOutsourcing.getOsCd();
        outsourcingMapper.insertContentList(clCd, finalOutsourcing.getOsCd());
        
        List<FileMetaData> bodyFilesToInsert = new ArrayList<>();
        if (bodyImageFiles != null && !bodyImageFiles.isEmpty()) {
            for (MultipartFile bodyFile : bodyImageFiles) {
                FileMetaData bodyMeta = uploadBodyImage(bodyFile);
                if (bodyMeta != null) {
                    bodyFilesToInsert.add(bodyMeta);
                }
            }
        }
        
        if (!bodyFilesToInsert.isEmpty()) {
            outsourcingMapper.insertFiles(bodyFilesToInsert, clCd, finalOutsourcing.getMbrCd());
        }
        
        updateMappings(clCd, finalOutsourcing.getMbrCd(), formData.getCategoryCodes(), formData.getTags()); // [!code changed]
    }

    @Override
    @Transactional
    public void updateOutsourcingStep1(EnterOutsourcing outsourcingToUpdate) {
        outsourcingToUpdate.setOsMdfcnYmdt(LocalDateTime.now());
        outsourcingMapper.updateOutsourcingStep1(outsourcingToUpdate);
    }

    @Override
    @Transactional
    public void updateOutsourcingStep2(String osCd, List<String> categoryCodes, String tags) { // [!code changed]
        // mbrCd를 세션에서 가져옵니다.
        String mbrCd = (String) ((HttpSession)org.springframework.web.context.request.RequestContextHolder.currentRequestAttributes().resolveReference("session")).getAttribute("SCD"); // [!code changed]
        if (mbrCd == null) { // [!code changed]
            throw new IllegalStateException("로그인한 사용자 코드를 찾을 수 없습니다."); // [!code changed]
        } // [!code changed]

        String clCd = outsourcingMapper.findClCdByOsCd(osCd);
        if (clCd == null) throw new IllegalStateException("콘텐츠 목록 정보가 없습니다.");
        
        EnterOutsourcing originalOutsourcing = outsourcingMapper.findOutsourcingDetailsByOsCd(osCd);
        if (originalOutsourcing == null) throw new IllegalStateException("수정할 외주 정보를 찾을 수 없습니다.");

        outsourcingMapper.deleteCategoryMappingByClCd(clCd);
        outsourcingMapper.deleteTagMappingByClCd(clCd);
        updateMappings(clCd, mbrCd, categoryCodes, tags); // [!code changed]
    }
    
    @Override
    @Transactional
    public void updateOutsourcingStep3(String osCd, MultipartFile thumbnailFile, List<MultipartFile> newBodyImageFiles, List<String> deletedBodyImageCds) {
        EnterOutsourcing outsourcing = outsourcingMapper.findOutsourcingDetailsByOsCd(osCd);
        if (outsourcing == null) throw new IllegalStateException("수정할 외주 정보가 없습니다.");

        String clCd = outsourcingMapper.findClCdByOsCd(osCd);
        if (clCd == null) throw new IllegalStateException("콘텐츠 목록 정보를 찾을 수 없습니다.");
        
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            FileMetaData newThumbnailMetaData = uploadThumbnail(thumbnailFile);
            if (newThumbnailMetaData != null) {
                outsourcingMapper.updateOutsourcingThumbnail(osCd, newThumbnailMetaData.getFilePath());
            }
        }

        if (deletedBodyImageCds != null && !deletedBodyImageCds.isEmpty()) {
            for (String fileCd : deletedBodyImageCds) {
                FileMetaData fileToDelete = outsourcingMapper.findFileMetaDataByFileCd(fileCd);
                if (fileToDelete != null) {
                    String fullPathToDelete = restoreFullPathForWeb(fileToDelete);
                    filesUtils.deleteFileByPath(fullPathToDelete);
                    outsourcingMapper.deleteFilesByFileCd(fileCd);
                }
            }
        }

        List<FileMetaData> bodyFilesToInsert = new ArrayList<>();
        if (newBodyImageFiles != null && !newBodyImageFiles.isEmpty()) {
            for (MultipartFile bodyFile : newBodyImageFiles) {
                FileMetaData bodyMeta = uploadBodyImage(bodyFile);
                if (bodyMeta != null) {
                    bodyFilesToInsert.add(bodyMeta);
                }
            }
            if (!bodyFilesToInsert.isEmpty()) {
                // mbrCd를 세션에서 가져와야 합니다.
                String mbrCd = (String) ((HttpSession)org.springframework.web.context.request.RequestContextHolder.currentRequestAttributes().resolveReference("session")).getAttribute("SCD"); // [!code changed]
                if (mbrCd == null) { // [!code changed]
                    throw new IllegalStateException("로그인한 사용자 코드를 찾을 수 없습니다."); // [!code changed]
                } // [!code changed]
                outsourcingMapper.insertFiles(bodyFilesToInsert, clCd, mbrCd); // [!code changed]
            }
        }
    }

    @Override
    @Transactional
    public void deleteOutsourcing(String osCd) {
        String clCd = outsourcingMapper.findClCdByOsCd(osCd);
        if (clCd != null) {
            List<FileMetaData> filesToDelete = outsourcingMapper.findFilesByClCd(clCd);
            for (FileMetaData file : filesToDelete) {
                String fullPathToDelete = restoreFullPathForWeb(file);
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