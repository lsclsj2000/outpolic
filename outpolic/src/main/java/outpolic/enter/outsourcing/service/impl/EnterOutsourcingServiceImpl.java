package outpolic.enter.outsourcing.service.impl;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class EnterOutsourcingServiceImpl implements EnterOutsourcingService {

    private static final Logger logger = LoggerFactory.getLogger(EnterOutsourcingServiceImpl.class);

    private final OutsourcingMapper outsourcingMapper;
    private final PortfolioMapper portfolioMapper;

    private final String FILE_UPLOAD_DIR = "C:/uploads";

    // ... (조회 및 등록 로직은 기존과 동일) ...

    @Override
    public List<EnterOutsourcing> getOutsourcingListByEntCd(String entCd) {
        return outsourcingMapper.findOutsourcingDetailsByEntCd(entCd);
    }
    
    @Override
    public EnterOutsourcing findOutsourcingDetailsByOsCd(String osCd) {
        return outsourcingMapper.findOutsourcingDetailsByOsCd(osCd);
    }

    @Override
    public List<String> searchTags(String query){
        return outsourcingMapper.searchTagsByName(query);
    }

    @Override
    public EnterOutsourcing getOutsourcingByOsCd(String osCd) {
        return outsourcingMapper.findOutsourcingDetailsByOsCd(osCd);
    }

    @Override
    public List<EnterOutsourcing> getAllOutsourcings() {
        return outsourcingMapper.findAllOutsourcings();
    }
    
    @Override
    public String saveStep1Data(OutsourcingFormDataDto formData, HttpSession session) {
        logger.info("--- saveStep1Data 시작 ---");
        if (formData.getOsCd() == null || formData.getOsCd().isEmpty()) {
            String latestOsCd = outsourcingMapper.findLatestOsCd();
            int nextNum = (latestOsCd == null || !latestOsCd.startsWith("OS_C")) ? 1 : Integer.parseInt(latestOsCd.substring(4)) + 1;
            formData.setOsCd(String.format("OS_C%05d", nextNum));
        }
        session.setAttribute("outsourcingFormData", formData);
        return formData.getOsCd();
    }
    
    @Override
    public void saveStep2Data(String osCd, List<String> categoryCodes, String tags, HttpSession session) {
        logger.info("--- saveStep2Data 시작 ---");
        OutsourcingFormDataDto formData = (OutsourcingFormDataDto) session.getAttribute("outsourcingFormData");
        if (formData == null || !osCd.equals(formData.getOsCd())) {
            throw new IllegalStateException("세션 데이터가 만료되었거나 유효하지 않습니다.");
        }
        formData.setCategoryCodes(categoryCodes);
        formData.setTags(tags);
        if (categoryCodes != null && !categoryCodes.isEmpty()) {
            formData.setCtgryId(categoryCodes.get(0));
        }
        session.setAttribute("outsourcingFormData", formData);
    }
    
    @Override
    public List<String> saveStep3Data(String osCd, MultipartFile[] files, HttpSession session) {
        OutsourcingFormDataDto formData = (OutsourcingFormDataDto) session.getAttribute("outsourcingFormData");
        if (formData == null || !osCd.equals(formData.getOsCd())) {
            throw new IllegalStateException("세션 정보가 유효하지 않습니다.");
        }
        List<String> uploadedFileUrls = new ArrayList<>();
        if (files != null) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    try {
                        File uploadDir = new File(FILE_UPLOAD_DIR);
                        if (!uploadDir.exists()) {
                            uploadDir.mkdirs();
                        }
                        String originalFilename = file.getOriginalFilename();
                        String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;
                        String savePath = uploadDir.getAbsolutePath() + File.separator + uniqueFilename;
                        file.transferTo(new File(savePath));
                        uploadedFileUrls.add("/uploads/" + uniqueFilename);
                        logger.info("파일 저장 성공: {}", savePath);
                    } catch (IOException e) {
                        logger.error("3단계 파일 저장 중 오류", e);
                        throw new RuntimeException("파일 저장 처리 중 오류가 발생했습니다.", e);
                    }
                }
            }
        }
        formData.setReferenceFileUrls(uploadedFileUrls);
        session.setAttribute("outsourcingFormData", formData);
        return uploadedFileUrls;
    }

    @Override
    @Transactional
    public void completeOutsourcingRegistration(String osCd, HttpSession session) {
        logger.info("--- completeOutsourcingRegistration 시작, osCd: {} ---", osCd);
        OutsourcingFormDataDto formData = (OutsourcingFormDataDto) session.getAttribute("outsourcingFormData");
        if (formData == null || !osCd.equals(formData.getOsCd())) {
            throw new IllegalStateException("세션 정보가 유효하지 않거나 만료되었습니다.");
        }

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

        outsourcingMapper.insertOutsourcing(finalOutsourcing);
        
        String clCd = "LIST_" + finalOutsourcing.getOsCd();
        outsourcingMapper.insertContentList(clCd, finalOutsourcing.getOsCd());
        
        updateMappings(clCd, formData.getMbrCd(), formData.getCategoryCodes(), formData.getTags());
        if (formData.getReferenceFileUrls() != null && !formData.getReferenceFileUrls().isEmpty()) {
            // TODO: 파일 정보를 DB의 'outsourcing_file' 같은 테이블에 저장하는 로직
        }

        session.removeAttribute("outsourcingFormData");
    }

    @Override
    @Transactional
    public void updateOutsourcingStep1(EnterOutsourcing outsourcingToUpdate) {
        outsourcingToUpdate.setOsMdfcnYmdt(LocalDateTime.now());
        outsourcingMapper.updateOutsourcingStep1(outsourcingToUpdate); 
        logger.info("수정 1단계 완료: {} 의 기본 정보가 업데이트되었습니다.", outsourcingToUpdate.getOsCd());
    }

    @Override
    @Transactional
    public void updateOutsourcingStep2(String osCd, List<String> categoryCodes, String tags) {
        String clCd = outsourcingMapper.findClCdByOsCd(osCd);
        if (clCd == null) {
            throw new IllegalStateException("콘텐츠 목록 정보가 없어 카테고리/태그를 수정할 수 없습니다.");
        }
        
        // ▼▼▼ 수정된 부분 시작 ▼▼▼
        // 1. DB에서 원본 외주 데이터를 가져와서 원래 작성자의 mbrCd를 확보합니다.
        EnterOutsourcing originalOutsourcing = outsourcingMapper.findOutsourcingDetailsByOsCd(osCd);
        if (originalOutsourcing == null) {
            throw new IllegalStateException("수정할 외주 정보를 찾을 수 없습니다.");
        }
        String originalMbrCd = originalOutsourcing.getMbrCd();

        if (categoryCodes != null && !categoryCodes.isEmpty()) {
            outsourcingMapper.updateOutsourcingRepresentativeCategory(osCd, categoryCodes.get(0));
        }

        outsourcingMapper.deleteCategoryMappingByClCd(clCd);
        outsourcingMapper.deleteTagMappingByClCd(clCd);

        // 2. 하드코딩된 값 대신, 위에서 찾은 원본 작성자의 mbrCd를 사용합니다.
        updateMappings(clCd, originalMbrCd, categoryCodes, tags);
        // ▲▲▲ 수정된 부분 끝 ▲▲▲

        logger.info("수정 2단계 완료: {} 의 카테고리 및 태그가 업데이트되었습니다.", osCd);
    }
    
    // ... (updateOutsourcingStep3, deleteOutsourcing, updateMappings, 포트폴리오 연결 관련 메서드는 기존과 동일) ...

    @Override
    @Transactional
    public void updateOutsourcingStep3(String osCd, MultipartFile[] files) {
        if (files == null || files.length == 0 || files[0].isEmpty()) {
            logger.info("수정 3단계: 새로 첨부된 파일이 없어 파일 업데이트를 건너뜁니다.");
            return;
        }

        // TODO: (선택) 기존 파일 삭제 로직
        
        try {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    File uploadDir = new File(FILE_UPLOAD_DIR);
                    if (!uploadDir.exists()) uploadDir.mkdirs();

                    String uniqueFilename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                    String savePath = uploadDir.getAbsolutePath() + File.separator + uniqueFilename;
                    file.transferTo(new File(savePath));
                    
                    // TODO: 파일 정보를 DB에 새로 INSERT 하는 로직
                }
            }
        } catch (IOException e) {
            logger.error("파일 업데이트 중 오류 발생", e);
            throw new RuntimeException("파일 수정 중 오류가 발생했습니다.", e);
        }
        logger.info("수정 3단계 완료: {} 의 첨부 파일이 업데이트되었습니다.", osCd);
    }

    @Override
    @Transactional
    public void deleteOutsourcing(String osCd) {
        String clCd = outsourcingMapper.findClCdByOsCd(osCd);
        if (clCd != null) {
            // ... (모든 연관 테이블 삭제 로직) ...
            outsourcingMapper.deleteCategoryMappingByClCd(clCd);
            // ...
        }
        outsourcingMapper.deleteOutsourcingByOsCd(osCd);
    }

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
                if (trimmedTagName.isEmpty()) continue;
                String tagCd = outsourcingMapper.findTagCdByName(trimmedTagName);
                if (tagCd == null) {
                    String latestTagCd = outsourcingMapper.findLatestTagCd();
                    int nextNum = (latestTagCd == null) ? 1 : Integer.parseInt(latestTagCd.substring(4)) + 1;
                    tagCd = String.format("T_C%05d", nextNum);
                    outsourcingMapper.insertTag(tagCd, trimmedTagName, mbrCd);
                }
                outsourcingMapper.insertTagMapping(tagCd, clCd, mbrCd);
            }
        }
    }

    @Override
    public List<EnterPortfolio> getLinkedPortfoliosByOsCd(String osCd) {
        return portfolioMapper.findLinkedPortfoliosByOsCd(osCd);
    }

    @Override
    public List<EnterPortfolio> searchUnlinkedPortfolios(String osCd, String entCd, String query) {
        return portfolioMapper.findUnlinkedPortfolios(osCd, entCd, query);
    }

    @Override
    @Transactional
    public void linkPortfolioToOutsourcing(String osCd, String prtfCd, String entCd) {
        String latestOpCd = outsourcingMapper.findLatestOpCd();
        int nextNum = (latestOpCd == null) ? 1 : Integer.parseInt(latestOpCd.substring(4)) + 1;
        String newOpCd = String.format("OP_C%05d", nextNum);
        outsourcingMapper.insertOutsourcingPortfolio(newOpCd, osCd, prtfCd, entCd);
    }

    @Override
    @Transactional
    public void unlinkPortfolioFromOutsourcing(String osCd, String prtfCd) {
        outsourcingMapper.unlinkOutsourcingFromPortfolio(osCd, prtfCd);
    }
    
    // ▼▼▼ 구조 개선: OutsourcingMapper를 사용하도록 변경 ▼▼▼
    @Override
    public String findEntCdByMbrCd(String mbrCd) {
        return outsourcingMapper.findEntCdByMbrCd(mbrCd); 
    }
}