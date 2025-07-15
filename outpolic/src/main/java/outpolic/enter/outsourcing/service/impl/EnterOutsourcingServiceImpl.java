package outpolic.enter.outsourcing.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

    // --- 단계별 등록 로직 ---
    @Override
    public String saveStep1Data(OutsourcingFormDataDto formData, HttpSession session) {
        if (formData.getOsCd() == null || formData.getOsCd().isEmpty()) {
            String latestOsCd = outsourcingMapper.findLatestOsCd();
            int nextNum = 1; // 기본값 1
            if (latestOsCd != null && latestOsCd.startsWith("OS_C")) {
                try {
                    nextNum = Integer.parseInt(latestOsCd.substring(5)) + 1;
                } catch (NumberFormatException e) {
                    logger.warn("Failed to parse latestOsCd: {}", latestOsCd, e);
                    // NumberFormatException 발생 시 기본값 유지
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
        if (categoryCodes != null && !categoryCodes.isEmpty()) formData.setCtgryId(categoryCodes.get(0));
        session.setAttribute("outsourcingFormData", formData);
    }
    
    @Override
    public List<String> saveStep3Data(String osCd, MultipartFile[] files, HttpSession session) {
        OutsourcingFormDataDto formData = (OutsourcingFormDataDto) session.getAttribute("outsourcingFormData");
        if (formData == null) throw new IllegalStateException("세션 정보가 없습니다.");
        
        List<FileMetaData> uploadedFiles = new ArrayList<>();
        if (files != null) {
            for (MultipartFile file : files) {
                try {
                    // 파일 저장 시 clCd와 mbrCd를 FileMetaData에 설정하여 전달
                    FileMetaData metaData = storeFile(file, "LIST_" + osCd, formData.getMbrCd(), "outsourcing");
                    if (metaData != null) uploadedFiles.add(metaData);
                } catch (IOException e) {
                   logger.error("파일 저장 중 오류 발생", e);
                }
            }
        }

        if (!uploadedFiles.isEmpty()) {
            formData.setUploadedFiles(uploadedFiles);
            // 첫 번째 파일의 경로를 썸네일 URL로 설정
            formData.setThumbnailUrl(uploadedFiles.get(0).getFilePath());
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
        finalOutsourcing.setCtgryId(formData.getCtgryId());
        finalOutsourcing.setOsRegYmdt(LocalDateTime.now());
        finalOutsourcing.setStcCd("SD_ACTIVE");
        if (formData.getThumbnailUrl() != null) finalOutsourcing.setOsThumbnailUrl(formData.getThumbnailUrl());
        
        outsourcingMapper.insertOutsourcing(finalOutsourcing);
        String clCd = "LIST_" + finalOutsourcing.getOsCd();
        outsourcingMapper.insertContentList(clCd, finalOutsourcing.getOsCd());
        if (formData.getUploadedFiles() != null && !formData.getUploadedFiles().isEmpty()) {
            // insertFiles 메서드에 clCd와 mbrCd가 포함된 FileMetaData 객체들을 전달
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
        
        // 기존 매핑 삭제
        outsourcingMapper.deleteCategoryMappingByClCd(clCd);
        outsourcingMapper.deleteTagMappingByClCd(clCd);
        
        // 새 매핑 추가
        updateMappings(clCd, originalOutsourcing.getMbrCd(), categoryCodes, tags);
    }

    @Override
    @Transactional
    public void updateOutsourcingStep3(String osCd, MultipartFile[] files) {
        EnterOutsourcing outsourcing = outsourcingMapper.findOutsourcingDetailsByOsCd(osCd);
        if (outsourcing == null) throw new IllegalStateException("수정할 외주 정보가 없습니다.");

        String clCd = outsourcingMapper.findClCdByOsCd(osCd);
        // 기존 파일 정보 가져오기 (DB에서)
        List<FileMetaData> oldFiles = outsourcingMapper.findFilesByClCd(clCd);
        if (oldFiles != null) {
            for(FileMetaData oldFile : oldFiles) { 
                deleteFile(oldFile.getFilePath()); // 실제 파일 시스템에서 삭제
            }
        }
        outsourcingMapper.deleteFilesByClCd(clCd); // DB에서 파일 메타데이터 삭제

        List<FileMetaData> newFiles = new ArrayList<>();
        if (files != null && files.length > 0 && !files[0].isEmpty()) { // 새 파일이 있을 경우에만 처리
            for (MultipartFile file : files) {
                try {
                    // 파일 저장 시 clCd와 mbrCd를 FileMetaData에 설정
                    newFiles.add(storeFile(file, clCd, outsourcing.getMbrCd(), "outsourcing"));
                } catch (IOException e) { logger.error("파일 수정 중 오류", e);
                }
            }
        }

        if (!newFiles.isEmpty()) {
            outsourcingMapper.insertFiles(newFiles); // 새 파일 메타데이터 DB에 삽입
            outsourcingMapper.updateOutsourcingThumbnail(osCd, newFiles.get(0).getFilePath()); // 썸네일 업데이트
        } else {
            // 새 파일이 없는 경우, 썸네일 URL을 null 또는 기본 이미지로 업데이트
            outsourcingMapper.updateOutsourcingThumbnail(osCd, null);
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
                for (FileMetaData file : filesToDelete) { deleteFile(file.getFilePath()); }
            }
            outsourcingMapper.deleteFilesByClCd(clCd);
            outsourcingMapper.deleteCategoryMappingByClCd(clCd);
            outsourcingMapper.deleteTagMappingByClCd(clCd);
            outsourcingMapper.deleteOutsourcingPortfolioByOsCd(osCd);
            outsourcingMapper.deleteContentListByClCd(clCd);
            // 외주 관련 다른 테이블의 데이터도 삭제해야 할 경우 여기에 추가
            outsourcingMapper.deleteBookmarkByClCd(clCd); // 예시: 북마크 삭제
            outsourcingMapper.deleteOutsourcingContractDetailsByClCd(clCd); // 외주 신청 상세 삭제
            outsourcingMapper.deleteOutsourcingStatusByClCd(clCd); // 외주 진행 상태 삭제
            outsourcingMapper.deleteRankingByClCd(clCd); // 랭킹 정보 삭제
            outsourcingMapper.deleteTodayViewByClCd(clCd); // 오늘 본 정보 삭제
            outsourcingMapper.deleteTotalViewByClCd(clCd); // 총 조회수 정보 삭제
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
        int nextNum = 1; // 기본값 1
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
    public void unlinkPortfolioFromOutsourcing(String osCd, String prtfCd) { outsourcingMapper.unlinkOutsourcingFromPortfolio(osCd, prtfCd);
    }

    // --- Private Helper Methods ---
    private void updateMappings(String clCd, String mbrCd, List<String> categoryCodes, String tags) {
        // 카테고리 매핑
        if (categoryCodes != null) {
            for (String ctgryId : categoryCodes) {
                if (ctgryId != null && !ctgryId.trim().isEmpty()) {
                    outsourcingMapper.insertCategoryMapping(ctgryId, clCd, mbrCd);
                }
            }
        }
        
        // 태그 매핑
        if (tags != null && !tags.trim().isEmpty()) {
            for (String tagName : tags.split(",")) {
                String trimmedTagName = tagName.trim();
                if (trimmedTagName.isEmpty()) continue; // 빈 태그는 건너_
                
                String tagCd = outsourcingMapper.findTagCdByName(trimmedTagName);
                if (tagCd == null) {
                    // 새 태그인 경우 태그 코드 생성 및 삽_
                    String latestTagCd = outsourcingMapper.findLatestTagCd();
                    int nextNum = 1; // 기본값 1
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

    // storeFile 메서드 수정: FileMetaData에 clCd와 mbrCd를 설정할 수 있도록
    private FileMetaData storeFile(MultipartFile file, String clCd, String mbrCd, String serviceName) throws IOException {
        if (file == null || file.isEmpty()) return null;
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        // Paths.get()을 사용하여 OS에 독립적인 경로 생성
        Path directoryPath = Paths.get(fileRealPath, "attachment", serviceName, datePath);
        if (!Files.exists(directoryPath)) Files.createDirectories(directoryPath);
        
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) extension = originalFilename.substring(dotIndex);
        String serverFileName = UUID.randomUUID().toString() + extension;
        Path filePath = directoryPath.resolve(serverFileName);
        file.transferTo(filePath.toFile());

        String fileCode = "FILE_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss")) + UUID.randomUUID().toString().substring(0, 8);
        return new FileMetaData.Builder()
                .fileIdx(fileCode)
                .fileOriginalName(originalFilename)
                .fileNewName(serverFileName)
                .fileExtension(extension.replace(".", ""))
                // 파일 경로를 웹에서 접근 가능한 형태로 저장 (절대 경로에서 fileRealPath 부분 제거)
                .filePath("/attachment/" + serviceName + "/" + datePath + "/" + serverFileName)
                .fileSize(file.getSize())
                .clCd(clCd) // clCd 설정
                .mbrCd(mbrCd) // mbrCd 설정
                .build();
    }

    // deleteFile 메서드 수정: OS 독립적인 경로 처리
    private void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) return;
        // 웹 경로를 실제 파일 시스템 경로로 변환
        String relativePath = fileUrl.replaceFirst("/attachment", "");
        try {
            Path filePath = Paths.get(fileRealPath, relativePath);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            logger.error("파일 삭제 실패: {}", fileUrl, e);
        }
    }
}