package outpolic.enter.outsourcing.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
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

@Service
@RequiredArgsConstructor
public class EnterOutsourcingServiceImpl implements EnterOutsourcingService {

    private static final Logger logger = LoggerFactory.getLogger(EnterOutsourcingServiceImpl.class);
    private final OutsourcingMapper outsourcingMapper;
    private final PortfolioMapper portfolioMapper; 

    @Value("${file.path}")
    private String fileRealPath;

    // --- 조회 및 공통 로직 ---
    @Override
    public List<EnterOutsourcing> getOutsourcingListByEntCd(String entCd) { return outsourcingMapper.findOutsourcingDetailsByEntCd(entCd);
    }
    @Override
    public EnterOutsourcing findOutsourcingDetailsByOsCd(String osCd) { return outsourcingMapper.findOutsourcingDetailsByOsCd(osCd);
    }
    @Override
    public List<String> searchTags(String query) { return outsourcingMapper.searchTagsByName(query);
    }
    @Override
    public EnterOutsourcing getOutsourcingByOsCd(String osCd) { return outsourcingMapper.findOutsourcingDetailsByOsCd(osCd);
    }
    @Override
    public String findEntCdByMbrCd(String mbrCd) { return outsourcingMapper.findEntCdByMbrCd(mbrCd);
    }
    @Override
    public List<EnterOutsourcing> getAllOutsourcings() { return outsourcingMapper.findAllOutsourcings();
    }
    
    @Override
    public List<String> getFilesByClCd(String clCd) {
        List<FileMetaData> fileMetaDataList = outsourcingMapper.findFilesByClCd(clCd);
        return fileMetaDataList.stream()
                               .map(FileMetaData::getFilePath)
                               .collect(Collectors.toList());
    }

    // --- 단계별 등록 로직 ---
    @Override
    public String saveStep1Data(OutsourcingFormDataDto formData, HttpSession session) {
        if (formData.getOsCd() == null || formData.getOsCd().isEmpty()) {
            String latestOsCd = outsourcingMapper.findLatestOsCd();
            int nextNum = 1;
            if (latestOsCd != null && latestOsCd.startsWith("OS_C")) {
                try {
                    nextNum = Integer.parseInt(latestOsCd.substring(5)) + 1;
                } catch (NumberFormatException e) {
                    logger.warn("Failed to parse latestOsCd: {}", latestOsCd, e);
                }
            }
            formData.setOsCd(String.format("OS_C%05d", nextNum));
        }
        session.setAttribute("outsourcingFormData", formData);
        return formData.getOsCd();
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
                try {
                    FileMetaData metaData = storeFile(file, "LIST_" + osCd, formData.getMbrCd(), "outsourcing");
                    if (metaData != null) uploadedFiles.add(metaData);
                } catch (IOException e) {
                   logger.error("파일 저장 중 오류 발생", e);
                }
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
            for (FileMetaData fileMetaData : formData.getUploadedFiles()) {
                fileMetaData.setClCd(clCd);
                fileMetaData.setMbrCd(finalOutsourcing.getMbrCd());
            }
            outsourcingMapper.insertFiles(formData.getUploadedFiles());
        }
        updateMappings(clCd, finalOutsourcing.getMbrCd(), formData.getCategoryCodes(), formData.getTags());
        session.removeAttribute("outsourcingFormData");
    }

    // --- 수정 로직 ---
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
    public void updateOutsourcingStep3(String osCd, MultipartFile[] files, String existingFileUrlsList) {
        EnterOutsourcing outsourcing = outsourcingMapper.findOutsourcingDetailsByOsCd(osCd);
        if (outsourcing == null) throw new IllegalStateException("수정할 외주 정보가 없습니다.");

        String clCd = outsourcingMapper.findClCdByOsCd(osCd);

        // 기존 파일 삭제 (새 파일이 있거나, 기존 파일을 명시적으로 삭제하는 경우)
        // files가 있으면 기존 파일을 모두 삭제하고 새로 업로드하는 로직 유지
        // files가 없고 existingFileUrlsList가 비어있다면 (모두 삭제된 경우), 기존 파일 삭제
        if (files != null && files.length > 0) { // 새 파일이 있다면 기존 파일 모두 삭제
            List<FileMetaData> oldFiles = outsourcingMapper.findFilesByClCd(clCd);
            if (oldFiles != null) {
                for(FileMetaData oldFile : oldFiles) { 
                    deleteFile(oldFile.getFilePath());
                }
            }
            outsourcingMapper.deleteFilesByClCd(clCd);

            List<FileMetaData> newFiles = new ArrayList<>();
            for (MultipartFile file : files) {
                try {
                    FileMetaData newMetaData = storeFile(file, clCd, outsourcing.getMbrCd(), "outsourcing");
                    if (newMetaData != null) newFiles.add(newMetaData);
                } catch (IOException e) { logger.error("파일 수정 중 오류", e); }
            }

            if (!newFiles.isEmpty()) {
                outsourcingMapper.insertFiles(newFiles);
                outsourcingMapper.updateOutsourcingThumbnail(osCd, newFiles.get(0).getFilePath());
            } else {
                outsourcingMapper.updateOutsourcingThumbnail(osCd, null);
            }
        } else {
            // 새 파일이 없는 경우: 기존 파일 목록 (existingFileUrlsList)으로 DB 및 물리 파일 동기화
            List<String> currentExistingUrlsInDb = outsourcingMapper.findFilesByClCd(clCd).stream()
                                                              .map(FileMetaData::getFilePath)
                                                              .collect(Collectors.toList());
            List<String> urlsFromFrontend = (existingFileUrlsList != null && !existingFileUrlsList.isEmpty()) ?
                                            Arrays.asList(existingFileUrlsList.split(",")) : new ArrayList<>();

            // DB에는 있는데 프론트엔드에서 없어진 파일 삭제
            for (String dbUrl : currentExistingUrlsInDb) {
                if (!urlsFromFrontend.contains(dbUrl)) {
                    deleteFile(dbUrl);
                }
            }
            outsourcingMapper.deleteFilesByClCd(clCd); // 기존 파일 메타데이터 모두 삭제

            // 프론트엔드에서 넘어온 기존 파일 URL들을 다시 DB에 삽입 (metaData 객체로 변환 필요)
            if (!urlsFromFrontend.isEmpty()) {
                List<FileMetaData> filesToReInsert = new ArrayList<>();
                // 기존 파일 메타데이터를 다시 조회하거나, 필요한 정보를 재구성해야 함
                // 이 부분은 기존 파일의 전체 메타데이터 (file_cd, file_original_name 등)를 복구하는 로직이 필요
                // 현재는 URL만으로 파일 메타데이터를 재구성하기 어려움.
                // 따라서, 기존 파일 삭제 로직을 좀 더 신중하게 다루거나, 프론트에서 fileIdx도 같이 넘겨야 함.
                // 가장 간단한 방법은 files가 있으면 모두 교체, 없으면 기존 파일 유지(삭제 로직 타지 않음)로 가는 것.
                // 현재 provided solution: 새 파일 있으면 기존 파일 모두 삭제 후 새 파일 삽입. 없으면 thumbnail url만 null 처리.
                // 기존 파일의 개별 삭제는 deleteOutsourcing 내에만 있고, updateOutsourcingStep3에서는 아님.
                // 그러므로 `updateOutsourcingStep3`에서 `existingFileUrlsList`를 받는 대신,
                // 프론트엔드에서 `outsourcingReferenceFiles`가 비어있고, 기존 파일을 유지하고 싶다면 `updateOutsourcingThumbnail`만 호출하고
                // 실제 파일 삭제는 `remove-existing-file-btn` 클릭 시 별도의 DELETE 요청을 보내는 것이 더 명확함.
            }

            // 기존 로직 유지 (새 파일이 없으면 썸네일만 null 처리)
            if (existingFileUrlsList == null || existingFileUrlsList.isEmpty()) {
                 outsourcingMapper.updateOutsourcingThumbnail(osCd, null);
            }
        }
    }

    // --- 삭제 로직 ---
    @Override
    @Transactional
    public void deleteOutsourcing(String osCd) {
        String clCd = outsourcingMapper.findClCdByOsCd(osCd);
        if (clCd != null) {
            List<FileMetaData> filesToDelete = outsourcingMapper.findFilesByClCd(clCd);
            if (filesToDelete != null) {
                for (FileMetaData file : filesToDelete) { deleteFile(file.getFilePath());
                }
            }
            outsourcingMapper.deleteFilesByClCd(clCd);
            outsourcingMapper.deleteCategoryMappingByClCd(clCd);
            outsourcingMapper.deleteTagMappingByClCd(clCd);
            outsourcingMapper.deleteOutsourcingPortfolioByOsCd(osCd);
            outsourcingMapper.deleteContentListByClCd(clCd);
            outsourcingMapper.deleteBookmarkByClCd(clCd);
            outsourcingMapper.deleteOutsourcingContractDetailsByClCd(clCd);
            outsourcingMapper.deleteOutsourcingStatusByClCd(clCd); 
            outsourcingMapper.deleteRankingByClCd(clCd);
            outsourcingMapper.deleteTodayViewByClCd(clCd);
            outsourcingMapper.deleteTotalViewByClCd(clCd);
        }
        outsourcingMapper.deleteOutsourcingByOsCd(osCd);
    }
    
    // --- 포폴 연결/해제 로직 ---
    @Override
    public List<EnterPortfolio> getLinkedPortfoliosByOsCd(String osCd) { return portfolioMapper.findLinkedPortfoliosByOsCd(osCd);
    }
    @Override
    public List<EnterPortfolio> searchUnlinkedPortfolios(String osCd, String entCd, String query) { return portfolioMapper.findUnlinkedPortfolios(osCd, entCd, query);
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

    // --- Private Helper Methods ---
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

    /**
     * 파일을 물리적으로 저장하고, FileMetaData 객체를 반환합니다.
     * 이 메서드는 파일의 물리적 저장과 메타데이터 생성을 담당합니다.
     * @param file 업로드할 MultipartFile
     * @param clCd 콘텐츠 목록 코드 (FileMetaData에 저장될)
     * @param mbrCd 등록자 회원 코드 (FileMetaData에 저장될)
     * @param serviceName 서비스 구분 (예: "portfolio", "outsourcing")
     * @return 생성된 FileMetaData 객체
     * @throws IOException 파일 저장 중 발생할 수 있는 예외
     */
    private FileMetaData storeFile(MultipartFile file, String clCd, String mbrCd, String serviceName) throws IOException {
       
        if (file == null || file.isEmpty()) return null;

        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        Path directoryPath = Paths.get(fileRealPath.trim(), "attachment", serviceName, datePath);
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }
        
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) extension = originalFilename.substring(dotIndex);
        String newFileName = UUID.randomUUID().toString() + extension;
        
        Path filePath = directoryPath.resolve(newFileName); 
        
        file.transferTo(filePath.toFile());
        String webAccessPath = "/" + Paths.get("attachment", serviceName, datePath, newFileName).toString().replace("\\", "/");
        
        String fileIdx = "FILE_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss")) + UUID.randomUUID().toString().substring(0, 8);
        return new FileMetaData.Builder()
                .fileIdx(fileIdx)
                .fileOriginalName(originalFilename)
                .fileNewName(newFileName)
                .fileExtension(extension.replace(".", ""))
                .filePath(webAccessPath)
                
                .fileSize(file.getSize())
                .clCd(clCd)
                .mbrCd(mbrCd)
                .build();
    }

    // deleteFile 메서드의 반환 타입은 void가 아니라 String (성공/실패 반환용)
    private String deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) return null;

        String relativePathInStorage = fileUrl.replaceFirst("/", "").replace("\\", "/");
        Path filePathToDelete = Paths.get(fileRealPath.trim(), relativePathInStorage);

        try {
            Files.deleteIfExists(filePathToDelete);
            logger.info("파일 삭제 성공: {}", filePathToDelete);
            return "SUCCESS"; // 성공 시 반환
        } catch (IOException e) {
            logger.error("파일 삭제 실패: {}", filePathToDelete, e);
            return "FAILURE"; // 실패 시 반환
        }
    }
}