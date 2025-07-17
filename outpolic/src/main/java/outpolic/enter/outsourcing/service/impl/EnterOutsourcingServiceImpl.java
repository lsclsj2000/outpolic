package outpolic.enter.outsourcing.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
import outpolic.systems.util.FilesUtils;

@Service
@RequiredArgsConstructor
public class EnterOutsourcingServiceImpl implements EnterOutsourcingService {

    private static final Logger logger = LoggerFactory.getLogger(EnterOutsourcingServiceImpl.class);
    private final OutsourcingMapper outsourcingMapper;
    private final PortfolioMapper portfolioMapper;
    private final FilesUtils filesUtils;

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

    // 수정: referenceFiles 파라미터 제거 및 관련 로직 제거
    @Override
    @Transactional
    public void saveStep3Data(String osCd, MultipartFile thumbnailFile, HttpSession session) {
        OutsourcingFormDataDto formData = (OutsourcingFormDataDto) session.getAttribute("outsourcingFormData");
        if (formData == null) throw new IllegalStateException("세션 정보가 없습니다.");

        List<FileMetaData> uploadedFiles = new ArrayList<>(); // 업로드된 파일 목록 (썸네일만 포함될 것임)
        String thumbnailUrl = null;

        // 썸네일 파일 처리
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            FileMetaData thumbnailMetaData = filesUtils.uploadFile(thumbnailFile, "outsourcing/thumbnail");
            if (thumbnailMetaData != null) {
                uploadedFiles.add(thumbnailMetaData);
                thumbnailUrl = thumbnailMetaData.getFilePath();
            }
        }
        formData.setThumbnailUrl(thumbnailUrl);
        formData.setUploadedFiles(uploadedFiles); // 썸네일 파일만 포함

        session.setAttribute("outsourcingFormData", formData);
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
    
    // 수정: referenceFiles 파라미터 제거 및 관련 로직 제거
    @Override
    @Transactional
    public void updateOutsourcingStep3(String osCd, MultipartFile thumbnailFile) {
        EnterOutsourcing outsourcing = outsourcingMapper.findOutsourcingDetailsByOsCd(osCd);
        if (outsourcing == null) throw new IllegalStateException("수정할 외주 정보가 없습니다.");

        String clCd = outsourcingMapper.findClCdByOsCd(osCd);
        if (clCd == null) throw new IllegalStateException("콘텐츠 목록 정보를 찾을 수 없습니다. (clCd is null)");

        // 1. 기존 파일 (DB 기록 및 물리적 파일) 삭제 (썸네일만 남음)
        List<FileMetaData> oldFiles = outsourcingMapper.findFilesByClCd(clCd);
        if (oldFiles != null && !oldFiles.isEmpty()) {
            for(FileMetaData oldFile : oldFiles) {
                filesUtils.deleteFileByPath(oldFile.getFilePath());
            }
            outsourcingMapper.deleteFilesByClCd(clCd);
        }

        List<FileMetaData> newUploadedFiles = new ArrayList<>();
        String newThumbnailUrl = null;

        // 2. 새 썸네일 파일 업로드 및 정보 저장
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            FileMetaData newThumbnailMetaData = filesUtils.uploadFile(thumbnailFile, "outsourcing/thumbnail");
            if (newThumbnailMetaData != null) {
                newUploadedFiles.add(newThumbnailMetaData);
                newThumbnailUrl = newThumbnailMetaData.getFilePath();
            }
        }
        outsourcingMapper.updateOutsourcingThumbnail(osCd, newThumbnailUrl);

        // 3. 모든 새 파일 정보 (이제 썸네일만) DB에 일괄 삽입
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
                filesUtils.deleteFileByPath(file.getFilePath());
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

    @Override
    public List<EnterPortfolio> searchUnlinkedPortfolios(String osCd, String entCd, String query) {
        return portfolioMapper.searchUnlinkedPortfolios(osCd, entCd, query);
    }
}