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

    private static final Logger logger = LoggerFactory.getLogger(EnterOutsourcingServiceImpl.class); // [cite: 181]
    private final OutsourcingMapper outsourcingMapper; // [cite: 182]
    private final PortfolioMapper portfolioMapper; // [cite: 182]

    @Value("${file.path}")
    private String fileRealPath; // [cite: 182]

    @Override
    public List<EnterOutsourcing> getOutsourcingListByEntCd(String entCd) { return outsourcingMapper.findOutsourcingDetailsByEntCd(entCd); // [cite: 183]
}
    @Override
    public EnterOutsourcing findOutsourcingDetailsByOsCd(String osCd) { return outsourcingMapper.findOutsourcingDetailsByOsCd(osCd); // [cite: 184]
}
    @Override
    public List<String> searchTags(String query) { return outsourcingMapper.searchTagsByName(query); // [cite: 185]
}
    @Override
    public EnterOutsourcing getOutsourcingByOsCd(String osCd) { return outsourcingMapper.findOutsourcingDetailsByOsCd(osCd); // [cite: 186]
}
    @Override
    public String findEntCdByMbrCd(String mbrCd) { return outsourcingMapper.findEntCdByMbrCd(mbrCd); // [cite: 187]
}
    @Override
    public List<EnterOutsourcing> getAllOutsourcings() { return outsourcingMapper.findAllOutsourcings(); // [cite: 188]
}
    
    @Override
    public List<String> getFilesByClCd(String clCd) {
        List<FileMetaData> fileMetaDataList = outsourcingMapper.findFilesByClCd(clCd); // [cite: 189]
        return fileMetaDataList.stream()
                               .map(FileMetaData::getFilePath)
                               .collect(Collectors.toList()); // [cite: 190]
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
        } catch (Exception e) { // 모든 예외를 잡습니다
            logger.error("saveStep1Data 처리 중 오류 발생: {}", e.getMessage(), e); // 로거로도 출력
            e.printStackTrace(); // <-- 이 부분이 콘솔에 스택 트레이스를 강제로 출력합니다.
            throw new RuntimeException("Error processing saveStep1Data", e); // 500 에러를 다시 발생시키기 위해 예외를 던집니다.
        }
    }

    @Override
    public void saveStep2Data(String osCd, List<String> categoryCodes, String tags, HttpSession session) {
        OutsourcingFormDataDto formData = (OutsourcingFormDataDto) session.getAttribute("outsourcingFormData"); // [cite: 196]
        if (formData == null) throw new IllegalStateException("세션 정보가 없습니다."); // [cite: 197]
        formData.setCategoryCodes(categoryCodes); // [cite: 197]
        formData.setTags(tags); // [cite: 197]
        if (categoryCodes != null && !categoryCodes.isEmpty()) {
             formData.setCtgryId(categoryCodes.get(0)); // [cite: 198]
        } else {
             formData.setCtgryId(null); // [cite: 199]
        }
        session.setAttribute("outsourcingFormData", formData); // [cite: 200]
    }
    
    @Override
    public List<String> saveStep3Data(String osCd, MultipartFile[] files, HttpSession session) {
        OutsourcingFormDataDto formData = (OutsourcingFormDataDto) session.getAttribute("outsourcingFormData"); // [cite: 201]
        if (formData == null) throw new IllegalStateException("세션 정보가 없습니다."); // [cite: 202]
        
        List<FileMetaData> uploadedFiles = new ArrayList<>(); // [cite: 202]
        if (files != null && files.length > 0 && !files[0].isEmpty()) {
            for (MultipartFile file : files) {
                try {
                    FileMetaData metaData = storeFile(file, "LIST_" + osCd, formData.getMbrCd(), "outsourcing"); // [cite: 203]
                    if (metaData != null) uploadedFiles.add(metaData); // [cite: 204]
                } catch (IOException e) {
                   logger.error("파일 저장 중 오류 발생", e); // [cite: 204]
                }
            }
        }

        if (!uploadedFiles.isEmpty()) {
            formData.setUploadedFiles(uploadedFiles); // [cite: 205]
            formData.setThumbnailUrl(uploadedFiles.get(0).getFilePath()); // [cite: 206]
        } else {
            formData.setUploadedFiles(null); // [cite: 206]
            formData.setThumbnailUrl(null); // [cite: 206]
        }
        session.setAttribute("outsourcingFormData", formData); // [cite: 207]
        return uploadedFiles.stream().map(FileMetaData::getFilePath).collect(Collectors.toList()); // [cite: 207]
    }

    @Override
    @Transactional
    public void completeOutsourcingRegistration(String osCd, HttpSession session) {
        OutsourcingFormDataDto formData = (OutsourcingFormDataDto) session.getAttribute("outsourcingFormData"); // [cite: 208]
        if (formData == null) throw new IllegalStateException("세션 정보가 없습니다."); // [cite: 209]
        
        EnterOutsourcing finalOutsourcing = new EnterOutsourcing(); // [cite: 209]
        finalOutsourcing.setOsCd(formData.getOsCd()); // [cite: 209]
        finalOutsourcing.setEntCd(formData.getEntCd()); // [cite: 209]
        finalOutsourcing.setMbrCd(formData.getMbrCd()); // [cite: 209]
        finalOutsourcing.setOsTtl(formData.getOsTtl()); // [cite: 209]
        finalOutsourcing.setOsExpln(formData.getOsExpln()); // [cite: 209]
        finalOutsourcing.setOsStrtYmdt(formData.getOsStrtYmdt()); // [cite: 209]
        finalOutsourcing.setOsEndYmdt(formData.getOsEndYmdt()); // [cite: 210]
        finalOutsourcing.setOsAmt(formData.getOsAmt()); // [cite: 210]
        finalOutsourcing.setOsFlfmtCnt(formData.getOsFlfmtCnt()); // [cite: 210]
        
        if (formData.getCtgryId() == null || formData.getCtgryId().isEmpty()) {
             throw new IllegalArgumentException("카테고리 ID는 필수입니다. 2단계에서 카테고리를 선택해주세요."); // [cite: 210]
        }
        finalOutsourcing.setCtgryId(formData.getCtgryId()); // [cite: 211]
        
        finalOutsourcing.setOsRegYmdt(LocalDateTime.now()); // [cite: 211]
        finalOutsourcing.setStcCd("SD_ACTIVE"); // [cite: 211]
        finalOutsourcing.setOsThumbnailUrl(formData.getThumbnailUrl()); // [cite: 211]
        
        outsourcingMapper.insertOutsourcing(finalOutsourcing); // [cite: 211]
        String clCd = "LIST_" + finalOutsourcing.getOsCd(); // [cite: 212]
        outsourcingMapper.insertContentList(clCd, finalOutsourcing.getOsCd()); // [cite: 212]
        if (formData.getUploadedFiles() != null && !formData.getUploadedFiles().isEmpty()) {
            for (FileMetaData fileMetaData : formData.getUploadedFiles()) {
                fileMetaData.setClCd(clCd); // [cite: 213]
                fileMetaData.setMbrCd(finalOutsourcing.getMbrCd()); // [cite: 213]
            }
            outsourcingMapper.insertFiles(formData.getUploadedFiles()); // [cite: 214]
        }
        updateMappings(clCd, finalOutsourcing.getMbrCd(), formData.getCategoryCodes(), formData.getTags()); // [cite: 214]
        session.removeAttribute("outsourcingFormData"); // [cite: 215]
    }

    @Override
    @Transactional
    public void updateOutsourcingStep1(EnterOutsourcing outsourcingToUpdate) {
        outsourcingToUpdate.setOsMdfcnYmdt(LocalDateTime.now()); // [cite: 215]
        outsourcingMapper.updateOutsourcingStep1(outsourcingToUpdate); // [cite: 216]
    }

    @Override
    @Transactional
    public void updateOutsourcingStep2(String osCd, List<String> categoryCodes, String tags) {
        String clCd = outsourcingMapper.findClCdByOsCd(osCd); // [cite: 216]
        if (clCd == null) throw new IllegalStateException("콘텐츠 목록 정보가 없습니다."); // [cite: 217]
        EnterOutsourcing originalOutsourcing = outsourcingMapper.findOutsourcingDetailsByOsCd(osCd); // [cite: 217]
        if (originalOutsourcing == null) throw new IllegalStateException("수정할 외주 정보를 찾을 수 없습니다."); // [cite: 218]
        
        outsourcingMapper.deleteCategoryMappingByClCd(clCd); // [cite: 218]
        outsourcingMapper.deleteTagMappingByClCd(clCd); // [cite: 218]
        
        updateMappings(clCd, originalOutsourcing.getMbrCd(), categoryCodes, tags); // [cite: 219]
        if (categoryCodes != null && !categoryCodes.isEmpty()) {
            outsourcingMapper.updateOutsourcingRepresentativeCategory(osCd, categoryCodes.get(0)); // [cite: 219]
        } else {
            outsourcingMapper.updateOutsourcingRepresentativeCategory(osCd, null); // [cite: 220]
        }
    }

    @Override
    @Transactional
    public void updateOutsourcingStep3(String osCd, MultipartFile[] files) { // existingFileUrlsList 매개변수 제거 [cite: 286]
        EnterOutsourcing outsourcing = outsourcingMapper.findOutsourcingDetailsByOsCd(osCd); // [cite: 221]
        if (outsourcing == null) throw new IllegalStateException("수정할 외주 정보가 없습니다."); // [cite: 222]

        String clCd = outsourcingMapper.findClCdByOsCd(osCd); // [cite: 222]

        // 1. 새 파일이 '있으면' 기존 파일 모두 삭제 후 새 파일 업로드
        if (files != null && files.length > 0 && !files[0].isEmpty()) { // [cite: 223]
            // 기존 파일 물리적 삭제 및 DB 메타데이터 삭제
            List<FileMetaData> oldFiles = outsourcingMapper.findFilesByClCd(clCd); // [cite: 224]
            if (oldFiles != null) {
                for(FileMetaData oldFile : oldFiles) { 
                    deleteFile(oldFile.getFilePath()); // [cite: 224]
                }
            }
            outsourcingMapper.deleteFilesByClCd(clCd); // [cite: 225]

            // 새 파일 저장 및 DB 삽입
            List<FileMetaData> newFiles = new ArrayList<>(); // [cite: 226]
            for (MultipartFile file : files) {
                try {
                    FileMetaData newMetaData = storeFile(file, clCd, outsourcing.getMbrCd(), "outsourcing"); // [cite: 226]
                    if (newMetaData != null) newFiles.add(newMetaData); // [cite: 227]
                } catch (IOException e) { 
                   logger.error("파일 수정 중 오류", e); // [cite: 227]
                }
            }

            if (!newFiles.isEmpty()) {
                outsourcingMapper.insertFiles(newFiles); // [cite: 228]
                outsourcingMapper.updateOutsourcingThumbnail(osCd, newFiles.get(0).getFilePath()); // [cite: 229]
            } else {
                // 새로 업로드된 파일이 없으면 썸네일도 제거 (모두 삭제한 경우)
                outsourcingMapper.updateOutsourcingThumbnail(osCd, null); // [cite: 229]
            }
        } else {
            // 새 파일이 없는 경우, 기존 파일을 유지합니다.
            // (명시적으로 파일이 선택되지 않았다면 기존 파일은 그대로 둡니다.)
            // 썸네일 URL을 변경하지 않으므로, updateOutsourcingThumbnail 호출도 필요 없습니다.
            // 이전에 불완전했던 existingFileUrlsList 관련 로직은 제거하여 혼동을 방지합니다.
            // (즉, 이 else 블록에서는 아무 작업도 수행하지 않음)
        }
    }

    @Override
    @Transactional
    public void deleteOutsourcing(String osCd) {
        String clCd = outsourcingMapper.findClCdByOsCd(osCd); // content_list의 cl_cd를 먼저 가져옴
        if (clCd != null) {
            // --- 1. cl_cd를 간접적으로 참조하는 자식 테이블 (outsourcing_contract_details의 자식) ---
            //    (outsourcing_contract_details는 cl_cd를 참조하고, outsourcing_status는 outsourcing_contract_details를 참조)
            outsourcingMapper.deleteOutsourcingStatusByClCd(clCd); //
            
            // --- 2. cl_cd를 직접 참조하는 모든 테이블의 데이터 삭제 ---
            outsourcingMapper.deleteRankingByClCd(clCd);     // cl_cd 참조 [cite: 2]
            outsourcingMapper.deleteTodayViewByClCd(clCd);   // cl_cd 참조 [cite: 2]
            outsourcingMapper.deleteTotalViewByClCd(clCd);   // cl_cd 참조 [cite: 2]
            outsourcingMapper.deleteBookmarkByClCd(clCd);    // cl_cd 참조 [cite: 2]
            outsourcingMapper.deleteFilesByClCd(clCd);       // cl_cd 참조 [cite: 2]
            
            outsourcingMapper.deleteTagMappingByClCd(clCd);      // cl_cd 참조 [cite: 2]
            outsourcingMapper.deleteCategoryMappingByClCd(clCd); // cl_cd 참조 (THIS IS THE CAUSE OF YOUR CURRENT ERROR) [cite: 2]

            // --- 3. os_cd 또는 prtf_cd를 참조하는 매핑 테이블 삭제 ---
            outsourcingMapper.deleteOutsourcingPortfolioByOsCd(osCd); // os_cd 참조 [cite: 2]

            // --- 4. content_list를 참조하는 직접적인 부모 테이블 삭제 ---
            //    (outsourcing_contract_details는 cl_cd를 참조하며, 이제 모든 자식이 삭제됨)
            outsourcingMapper.deleteOutsourcingContractDetailsByClCd(clCd); // cl_cd 참조 [cite: 2]

            // --- 5. 마지막으로 content_list 및 outsourcing 테이블 삭제 ---
            //    (이제 content_list를 참조하는 모든 자식이 삭제되었으므로 삭제 가능)
            outsourcingMapper.deleteContentListByClCd(clCd); // [cite: 2]
        }
        // outsourcing 테이블 자체는 os_cd가 Primary Key이므로 마지막에 삭제 [cite: 2]
        outsourcingMapper.deleteOutsourcingByOsCd(osCd); // [cite: 2]
    }
    
    @Override
    public List<EnterPortfolio> getLinkedPortfoliosByOsCd(String osCd) { return portfolioMapper.findLinkedPortfoliosByOsCd(osCd); // [cite: 248]
}
    @Override
    public List<EnterPortfolio> searchUnlinkedPortfolios(String osCd, String entCd, String query) { return portfolioMapper.findUnlinkedPortfolios(osCd, entCd, query); // [cite: 249]
}
    @Override
    @Transactional
    public void linkPortfolioToOutsourcing(String osCd, String prtfCd, String entCd) {
        String latestOpCd = outsourcingMapper.findLatestOpCd(); // [cite: 250]
        int nextNum = 1;
        if (latestOpCd != null && latestOpCd.startsWith("OP_C")) {
            try {
                nextNum = Integer.parseInt(latestOpCd.substring(4)) + 1; // [cite: 251]
            } catch (NumberFormatException e) {
                logger.warn("Failed to parse latestOpCd for linking portfolio: {}", latestOpCd, e); // [cite: 252]
            }
        }
        outsourcingMapper.insertOutsourcingPortfolio(String.format("OP_C%05d", nextNum), osCd, prtfCd, entCd); // [cite: 253]
    }

    @Override
    @Transactional
    public void unlinkPortfolioFromOutsourcing(String osCd, String prtfCd) {
        outsourcingMapper.unlinkOutsourcingFromPortfolio(osCd, prtfCd); // [cite: 254]
    }

    private void updateMappings(String clCd, String mbrCd, List<String> categoryCodes, String tags) {
        if (categoryCodes != null && !categoryCodes.isEmpty()) { 
            for (String ctgryId : categoryCodes) {
                if (ctgryId != null && !ctgryId.trim().isEmpty()) {
                    outsourcingMapper.insertCategoryMapping(ctgryId, clCd, mbrCd); // [cite: 255]
                }
            }
        }
        
        if (tags != null && !tags.trim().isEmpty()) {
            for (String tagName : tags.split(",")) {
                String trimmedTagName = tagName.trim(); // [cite: 256]
                if (trimmedTagName.isEmpty()) continue; // [cite: 257]
                String tagCd = outsourcingMapper.findTagCdByName(trimmedTagName); // [cite: 257]
                if (tagCd == null) {
                    String latestTagCd = outsourcingMapper.findLatestTagCd(); // [cite: 257]
                    int nextNum = 1; 
                    if (latestTagCd != null && latestTagCd.startsWith("T_C")) {
                        try {
                            nextNum = Integer.parseInt(latestTagCd.substring(4)) + 1; // [cite: 258]
                        } catch (NumberFormatException e) {
                            logger.warn("Failed to parse latestTagCd: {}", latestTagCd, e); // [cite: 259]
                        }
                    }
                    tagCd = String.format("T_C%05d", nextNum); // [cite: 260]
                    outsourcingMapper.insertTag(tagCd, trimmedTagName, mbrCd); // [cite: 261]
                }
                outsourcingMapper.insertTagMapping(tagCd, clCd, mbrCd); // [cite: 261]
            }
        }
    }

    /**
     * 파일을 물리적으로 저장하고, FileMetaData 객체를 반환합니다. [cite: 262]
     * 이 메서드는 파일의 물리적 저장과 메타데이터 생성을 담당합니다.
     * @param file 업로드할 MultipartFile
     * @param clCd 콘텐츠 목록 코드 (FileMetaData에 저장될)
     * @param mbrCd 등록자 회원 코드 (FileMetaData에 저장될)
     * @param serviceName 서비스 구분 (예: "portfolio", "outsourcing")
     * @return 생성된 FileMetaData 객체
     * @throws IOException 파일 저장 중 발생할 수 있는 예외
     */
    private FileMetaData storeFile(MultipartFile file, String clCd, String mbrCd, String serviceName) throws IOException {
       
        if (file == null || file.isEmpty()) return null; // [cite: 263]

        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")); // [cite: 264]
        Path directoryPath = Paths.get(fileRealPath.trim(), "attachment", serviceName, datePath); // [cite: 264]
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath); // [cite: 265]
        }
        
        String originalFilename = file.getOriginalFilename(); // [cite: 266]
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.'); // [cite: 267]
        if (dotIndex > 0) extension = originalFilename.substring(dotIndex); // [cite: 267]
        String newFileName = UUID.randomUUID().toString() + extension; // [cite: 268]
        
        Path filePath = directoryPath.resolve(newFileName); // [cite: 268]
        
        file.transferTo(filePath.toFile()); // [cite: 268]
        String webAccessPath = "/" + Paths.get("attachment", serviceName, datePath, newFileName).toString().replace("\\", "/"); // [cite: 269]
        
        String fileIdx = "FILE_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss")) + UUID.randomUUID().toString().substring(0, 8); // [cite: 269]
        return new FileMetaData.Builder()
                .fileIdx(fileIdx)
                .fileOriginalName(originalFilename)
                .fileNewName(newFileName)
                .fileExtension(extension.replace(".", ""))
                .filePath(webAccessPath)
                
                .fileSize(file.getSize()) // [cite: 270]
                .clCd(clCd) // [cite: 270]
                .mbrCd(mbrCd) // [cite: 270]
                .build(); // [cite: 271]
    }

    // deleteFile 메서드의 반환 타입은 void가 아니라 String (성공/실패 반환용)
    private String deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) return null; // [cite: 272]
        String relativePathInStorage = fileUrl.replaceFirst("/", "").replace("\\", "/"); // [cite: 273]
        Path filePathToDelete = Paths.get(fileRealPath.trim(), relativePathInStorage); // [cite: 273]
        try {
            Files.deleteIfExists(filePathToDelete); // [cite: 274]
            logger.info("파일 삭제 성공: {}", filePathToDelete); // [cite: 274]
            return "SUCCESS"; // 성공 시 반환 [cite: 274]
        } catch (IOException e) {
            logger.error("파일 삭제 실패: {}", filePathToDelete, e); // [cite: 275]
            return "FAILURE"; // 실패 시 반환 [cite: 276]
        }
    }
}