package outpolic.enter.outsourcing.service.impl;

import java.io.IOException; // IOException은 이제 직접 throws하지 않지만, 여전히 다른 곳에서 사용될 수 있으므로 임포트 유지
import java.nio.file.Files; // 이 임포트는 이제 storeFile/deleteFile이 FilesUtils를 사용하므로 불필요할 수 있습니다.
import java.nio.file.Path; // 이 임포트는 이제 storeFile/deleteFile이 FilesUtils를 사용하므로 불필요할 수 있습니다.
import java.nio.file.Paths; // 이 임포트는 이제 storeFile/deleteFile이 FilesUtils를 사용하므로 불필요할 수 있습니다.
import java.time.LocalDate; // 이 임포트는 이제 storeFile/deleteFile이 FilesUtils를 사용하므로 불필요할 수 있습니다.
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID; // 이 임포트는 이제 storeFile/deleteFile이 FilesUtils를 사용하므로 불필요할 수 있습니다.
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
import outpolic.systems.util.FilesUtils; // FilesUtils를 사용하므로 이 임포트는 유지되어야 합니다.

@Service
@RequiredArgsConstructor
public class EnterOutsourcingServiceImpl implements EnterOutsourcingService {

    private static final Logger logger = LoggerFactory.getLogger(EnterOutsourcingServiceImpl.class);
    private final OutsourcingMapper outsourcingMapper;
    private final PortfolioMapper portfolioMapper;
    private final FilesUtils filesUtils; // FilesUtils 주입

    @Value("${file.path}")
    private String fileRealPath; // 이 필드는 FilesUtils가 사용하므로 여기서 직접 사용하지 않습니다.

    @Override
    public List<EnterOutsourcing> getOutsourcingListByEntCd(String entCd) { return outsourcingMapper.findOutsourcingDetailsByEntCd(entCd); }
    @Override
    public EnterOutsourcing findOutsourcingDetailsByOsCd(String osCd) { return outsourcingMapper.findOutsourcingDetailsByOsCd(osCd); }
    @Override
    public List<String> searchTags(String query) { return outsourcingMapper.searchTagsByName(query); }
    @Override
    public EnterOutsourcing getOutsourcingByOsCd(String osCd) { return outsourcingMapper.findOutsourcingDetailsByOsCd(osCd); }
    @Override
    public String findEntCdByMbrCd(String mbrCd) { return outsourcingMapper.findEntCdByMbrCd(mbrCd); }
    @Override
    public List<EnterOutsourcing> getAllOutsourcings() { return outsourcingMapper.findAllOutsourcings(); }

    @Override
    public List<String> getFilesByClCd(String clCd) {
        List<FileMetaData> fileMetaDataList = outsourcingMapper.findFilesByClCd(clCd);
        return fileMetaDataList.stream()
                               .map(FileMetaData::getFilePath)
                               .collect(Collectors.toList());
    }

    @Override
    public String saveStep1Data(OutsourcingFormDataDto formData, HttpSession session) {
        try {
            if (formData.getOsCd() == null || formData.getOsCd().isEmpty()) {
                String latestOsCd = outsourcingMapper.findLatestOsCd();
                int nextNum = (latestOsCd == null || !latestOsCd.startsWith("OS_C")) ? 1 : Integer.parseInt(latestOsCd.substring(5)) + 1;
                formData.setOsCd(String.format("OS_C%05d", nextNum));
            }
            session.setAttribute("outsourcingFormData", formData);
            return formData.getOsCd();
        } catch (Exception e) {
            logger.error("saveStep1Data 처리 중 오류 발생: {}", e.getMessage(), e);
            e.printStackTrace();
            throw new RuntimeException("Error processing saveStep1Data", e);
        }
    }

    @Override
    public void saveStep2Data(String osCd, List<String> categoryCodes, String tags, HttpSession session) {
        OutsourcingFormDataDto formData = (OutsourcingFormDataDto) session.getAttribute("outsourcingFormData");
        if (formData == null) throw new IllegalStateException("세션 정보가 없습니다.");
        formData.setCategoryCodes(categoryCodes);
        formData.setTags(tags);
        if (categoryCodes != null && !categoryCodes.isEmpty()) {
             formData.setCtgryId(categoryCodes.get(0));
        } else {
             formData.setCtgryId(null);
        }
        session.setAttribute("outsourcingFormData", formData);
    }

    @Override
    public List<String> saveStep3Data(String osCd, MultipartFile[] files, HttpSession session) {
        OutsourcingFormDataDto formData = (OutsourcingFormDataDto) session.getAttribute("outsourcingFormData");
        if (formData == null) throw new IllegalStateException("세션 정보가 없습니다.");

        List<FileMetaData> uploadedFiles = new ArrayList<>();
        if (files != null && files.length > 0 && !files[0].isEmpty()) {
            for (MultipartFile file : files) {
                // try { // <-- try 블록 유지 (FilesUtils.uploadFile이 IOException을 던지도록 설정했다면)
                    // FileMetaData metaData = storeFile(file, "outsourcing"); // storeFile 헬퍼 메서드가 이제 IOException을 던지지 않으므로 try-catch 제거
                    FileMetaData metaData = filesUtils.uploadFile(file, "outsourcing"); // FilesUtils에서 던지지 않으면 이대로
                    if (metaData != null) uploadedFiles.add(metaData);
                // } catch (IOException e) { // <-- catch 블록 제거
                //    logger.error("파일 저장 중 오류 발생", e);
                // }
            }
        }

        if (!uploadedFiles.isEmpty()) {
            formData.setUploadedFiles(uploadedFiles);
            formData.setThumbnailUrl(uploadedFiles.get(0).getFilePath());
        } else {
            formData.setUploadedFiles(null);
            formData.setThumbnailUrl(null);
        }
        session.setAttribute("outsourcingFormData", formData);
        return uploadedFiles.stream().map(FileMetaData::getFilePath).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void completeOutsourcingRegistration(String osCd, HttpSession session) {
        OutsourcingFormDataDto formData = (OutsourcingFormDataDto) session.getAttribute("outsourcingFormData");
        if (formData == null) throw new IllegalStateException("세션 정보가 없습니다.");

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

        if (formData.getCtgryId() == null || formData.getCtgryId().isEmpty()) {
             throw new IllegalArgumentException("카테고리 ID는 필수입니다. 2단계에서 카테고리를 선택해주세요.");
        }
        finalOutsourcing.setCtgryId(formData.getCtgryId());

        finalOutsourcing.setOsRegYmdt(LocalDateTime.now());
        finalOutsourcing.setStcCd("SD_ACTIVE");
        finalOutsourcing.setOsThumbnailUrl(formData.getThumbnailUrl());

        outsourcingMapper.insertOutsourcing(finalOutsourcing);
        String clCd = "LIST_" + finalOutsourcing.getOsCd();
        outsourcingMapper.insertContentList(clCd, finalOutsourcing.getOsCd());
        if (formData.getUploadedFiles() != null && !formData.getUploadedFiles().isEmpty()) {
            outsourcingMapper.insertFiles(formData.getUploadedFiles(), clCd, finalOutsourcing.getMbrCd());
        }
        updateMappings(clCd, finalOutsourcing.getMbrCd(), formData.getCategoryCodes(), formData.getTags());
        session.removeAttribute("outsourcingFormData");
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
    public void updateOutsourcingStep3(String osCd, MultipartFile[] files) {
        EnterOutsourcing outsourcing = outsourcingMapper.findOutsourcingDetailsByOsCd(osCd);
        if (outsourcing == null) throw new IllegalStateException("수정할 외주 정보가 없습니다.");

        String clCd = outsourcingMapper.findClCdByOsCd(osCd);

        if (files != null && files.length > 0 && !files[0].isEmpty()) {
            List<FileMetaData> oldFiles = outsourcingMapper.findFilesByClCd(clCd);
            if (oldFiles != null) {
                for(FileMetaData oldFile : oldFiles) {
                    filesUtils.deleteFileByPath(oldFile.getFilePath()); // FilesUtils 사용
                }
            }
            outsourcingMapper.deleteFilesByClCd(clCd);

            List<FileMetaData> newFiles = new ArrayList<>();
            for (MultipartFile file : files) {
                // try { // <-- try 블록 유지 (FilesUtils.uploadFile이 IOException을 던지도록 설정했다면)
                    // FileMetaData newMetaData = storeFile(file, "outsourcing"); // storeFile 헬퍼 메서드가 이제 IOException을 던지지 않으므로 try-catch 제거
                    FileMetaData newMetaData = filesUtils.uploadFile(file, "outsourcing"); // FilesUtils에서 던지지 않으면 이대로
                    if (newMetaData != null) newFiles.add(newMetaData);
                // } catch (IOException e) { // <-- catch 블록 제거
                //    logger.error("파일 수정 중 오류", e);
                // }
            }

            if (!newFiles.isEmpty()) {
                outsourcingMapper.insertFiles(newFiles, clCd, outsourcing.getMbrCd());
                outsourcingMapper.updateOutsourcingThumbnail(osCd, newFiles.get(0).getFilePath());
            } else {
                outsourcingMapper.updateOutsourcingThumbnail(osCd, null);
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
                filesUtils.deleteFileByPath(file.getFilePath()); // FilesUtils 사용
            }
            outsourcingMapper.deletePerusalContentByClCd(clCd); // <-- 이 줄을 추가합니다.

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
    public List<EnterPortfolio> getLinkedPortfoliosByOsCd(String osCd) { return portfolioMapper.findLinkedPortfoliosByOsCd(osCd); }

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

    private void updateMappings(String clCd, String mbrCd, List<String> categoryCodes, String tags) {
        if (categoryCodes != null && !categoryCodes.isEmpty()) {
            for (String ctgryId : categoryCodes) {
                if (ctgryId != null && !ctgryId.trim().isEmpty()) {
                    outsourcingMapper.insertCategoryMapping(ctgryId, clCd, mbrCd);
                }
            }
        }

        if (tags != null && !tags.trim().isEmpty()) {
            for (String tagName : tags.split(",")) {
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

    // private storeFile 메서드는 FilesUtils.uploadFile()이 IOException을 던지지 않는다고 가정하고 제거합니다.
    // private FileMetaData storeFile(MultipartFile file, String serviceName) throws IOException {
    //    return filesUtils.uploadFile(file, serviceName);
    // }

    // private deleteFile 메서드는 FilesUtils.deleteFileByPath()가 boolean을 반환하므로 boolean으로 변경
    private boolean deleteFile(String fileUrl) {
        return filesUtils.deleteFileByPath(fileUrl);
    }

    @Override
    public List<EnterPortfolio> searchUnlinkedPortfolios(String osCd, String entCd, String query) {
        return portfolioMapper.findUnlinkedPortfolios(osCd, entCd, query);
    }
}