package outpolic.enter.outsourcing.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile; // MultipartFile 임포트 추가
import org.springframework.util.StringUtils; // StringUtils 임포트 추가 (파일 경로 추출 등)

import jakarta.servlet.http.HttpSession; // HttpSession 임포트 추가

import lombok.RequiredArgsConstructor;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;
import outpolic.enter.outsourcing.domain.OutsourcingFormDataDto; // 새로 정의한 DTO 임포트
import outpolic.enter.outsourcing.mapper.OutsourcingMapper;
import outpolic.enter.outsourcing.service.EnterOutsourcingService;
import outpolic.enter.portfolio.mapper.PortfolioMapper;
import outpolic.enter.portfolio.domain.EnterPortfolio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// 파일 저장을 위한 유틸리티 (실제 경로 설정 필요)
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID; // 고유 ID 생성

@Service
@RequiredArgsConstructor
public class EnterOutsourcingServiceImpl implements EnterOutsourcingService {

    private static final Logger logger = LoggerFactory.getLogger(EnterOutsourcingServiceImpl.class);

    private final OutsourcingMapper outsourcingMapper;
    private final PortfolioMapper portfolioMapper;

    // TODO: 실제 파일 저장 경로 설정 (application.properties 등에서 가져오는 것이 좋음)
    private final String FILE_UPLOAD_DIR = "/home/teamproject/outsourcing_files"; 

    // --- 기존 API 유지 ---
    @Override
    public List<EnterOutsourcing> getOutsourcingListByEntCd(String entCd) {
        return outsourcingMapper.findOutsourcingDetailsByEntCd(entCd);
    }

    @Override
    public EnterOutsourcing getOutsourcingByOsCd(String osCd) {
        return outsourcingMapper.findOutsourcingDetailsByOsCd(osCd);
    }

    @Override
    @Transactional
    public void updateOutsourcing(EnterOutsourcing outsourcing, List<String> categoryCodes, String tags) throws IOException {
        outsourcing.setOsMdfcnYmdt(LocalDateTime.now());
        if (categoryCodes != null && !categoryCodes.isEmpty()) {
            outsourcing.setCtgryId(categoryCodes.get(0));
        } else {
            throw new IllegalStateException("업데이트 시 최소 하나 이상의 카테고리를 선택해야 합니다.");
        }
        outsourcingMapper.updateOutsourcing(outsourcing);

        String clCd = outsourcingMapper.findClCdByOsCd(outsourcing.getOsCd());
        if (clCd != null) {
            outsourcingMapper.deleteCategoryMappingByClCd(clCd);
            outsourcingMapper.deleteTagMappingByClCd(clCd);
        }
        updateMappings(clCd, outsourcing.getMbrCd(), categoryCodes, tags);
    }

    @Override
    @Transactional
    public void deleteOutsourcing(String osCd) {
        String clCd = outsourcingMapper.findClCdByOsCd(osCd);

        if (clCd != null) {
            outsourcingMapper.deleteBookmarkByClCd(clCd);
            outsourcingMapper.deleteOutsourcingStatusByClCd(clCd);
            outsourcingMapper.deleteOutsourcingContractDetailsByClCd(clCd);
            outsourcingMapper.deleteRankingByClCd(clCd);
            outsourcingMapper.deleteTodayViewByClCd(clCd);
            outsourcingMapper.deleteTotalViewByClCd(clCd);
            outsourcingMapper.deleteCategoryMappingByClCd(clCd);
            outsourcingMapper.deleteTagMappingByClCd(clCd);
            outsourcingMapper.deleteOutsourcingPortfolioByOsCd(osCd);
            outsourcingMapper.deleteContentListByClCd(clCd);
        }
        outsourcingMapper.deleteOutsourcingByOsCd(osCd);
    }

    private void updateMappings(String clCd, String mbrCd, List<String> categoryCodes, String tags) {
        logger.info("updateMappings 시작. clCd: {}, mbrCd: {}, categoryCodes: {}, tags: {}", clCd, mbrCd, categoryCodes, tags);
        if (categoryCodes != null) {
            for (String ctgryIdOrName : categoryCodes) {
                if(ctgryIdOrName != null && !ctgryIdOrName.trim().isEmpty()) {
                    try {
                        // TODO: 프론트에서 이름으로 보냈다면 여기서 ID를 찾아야 함. 현재는 ID로 가정.
                        int insertedCatMapRows = outsourcingMapper.insertCategoryMapping(ctgryIdOrName, clCd, mbrCd);
                        if (insertedCatMapRows == 0) {
                             logger.warn("카테고리 매핑 삽입 실패 (0행 삽입됨): ctgryIdOrName={}, clCd={}", ctgryIdOrName, clCd);
                        } else {
                             logger.info("카테고리 매핑 삽입 성공: ctgryIdOrName={}, clCd={}", ctgryIdOrName, clCd);
                        }
                    } catch (Exception e) {
                        logger.error("카테고리 매핑 삽입 중 오류 발생: {}. ctgryIdOrName={}", e.getMessage(), ctgryIdOrName, e);
                        throw e;
                    }
                }
            }
        }
        if (tags != null && !tags.trim().isEmpty()) {
            for (String tagName : tags.split(",")) {
                String trimmedTagName = tagName.trim();
                if (trimmedTagName.isEmpty()) continue;
                try {
                    String tagCd = outsourcingMapper.findTagCdByName(trimmedTagName);
                    if (tagCd == null) {
                        String latestTagCd = outsourcingMapper.findLatestTagCd();
                        int nextNum = (latestTagCd == null) ? 1 : Integer.parseInt(latestTagCd.substring(4)) + 1;
                        tagCd = String.format("T_C%05d", nextNum);
                        int insertedTagRows = outsourcingMapper.insertTag(tagCd, trimmedTagName, mbrCd);
                        if (insertedTagRows == 0) {
                            logger.warn("태그 삽입 실패 (0행 삽입됨): tagName={}", trimmedTagName);
                        } else {
                            logger.info("새 태그 삽입 성공: tagName={}, tagCd={}", trimmedTagName, tagCd);
                        }
                    }
                    int insertedTagMapRows = outsourcingMapper.insertTagMapping(tagCd, clCd, mbrCd);
                    if (insertedTagMapRows == 0) {
                         logger.warn("태그 매핑 삽입 실패 (0행 삽입됨): tagCd={}, clCd={}", tagCd, clCd);
                    } else {
                         logger.info("태그 매핑 삽입 성공: tagCd={}, clCd={}", tagCd, clCd);
                    }
                } catch (Exception e) {
                    logger.error("태그 매핑 삽입 중 오류 발생: {}. tagName={}", e.getMessage(), trimmedTagName, e);
                    throw e;
                }
            }
        }
        logger.info("updateMappings 종료.");
    }

    @Override
    public List<String> searchTags(String query){
        return outsourcingMapper.searchTagsByName(query);
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

    // --- 새로운 단계별 등록 API 구현 ---

    // 1단계: 기본 정보 저장 (세션에 임시 저장 및 osCd 생성)
    @Override
    public String saveStep1Data(OutsourcingFormDataDto formData, HttpSession session) throws IOException {
        logger.info("--- saveStep1Data 메서드 시작 ---");
        logger.info("1단계 폼 데이터 수신: {}", formData);

        // osCd 생성 (최초 진입 시)
        if (formData.getOsCd() == null || formData.getOsCd().isEmpty()) {
            String latestOsCd = outsourcingMapper.findLatestOsCd();
            int nextNum = (latestOsCd == null || latestOsCd.isEmpty() || !latestOsCd.startsWith("OS_C")) ? 1 : Integer.parseInt(latestOsCd.substring(4)) + 1;
            String newOsCd = String.format("OS_C%05d", nextNum);
            formData.setOsCd(newOsCd);
            logger.info("새로운 osCd 생성 및 설정: {}", newOsCd);
        } else {
            logger.info("기존 osCd 재사용: {}", formData.getOsCd());
        }

        // 세션에 데이터 임시 저장
        session.setAttribute("outsourcingFormData", formData);
        logger.info("1단계 데이터 세션에 저장 완료. osCd: {}", formData.getOsCd());
        logger.info("--- saveStep1Data 메서드 종료 ---");
        return formData.getOsCd();
    }
    
    // 2단계: 카테고리 및 태그 저장 (세션에 임시 저장)
    @Override
    public void saveStep2Data(String osCd, List<String> categoryCodes, String tags, HttpSession session) throws IOException {
        logger.info("--- saveStep2Data 메서드 시작 ---");
        logger.info("2단계 데이터 수신 - osCd: {}, categoryCodes: {}, tags: {}", osCd, categoryCodes, tags);

        OutsourcingFormDataDto formData = (OutsourcingFormDataDto) session.getAttribute("outsourcingFormData");
        if (formData == null || !osCd.equals(formData.getOsCd())) {
            logger.error("세션 데이터 불일치 또는 없음. osCd: {}", osCd);
            throw new IllegalStateException("세션 데이터가 만료되었거나 유효하지 않습니다.");
        }

        formData.setCategoryCodes(categoryCodes);
        formData.setTags(tags);
        
        // 대표 카테고리 ID 설정 (categoryCodes 목록에서 첫 번째 것을 사용)
        if (categoryCodes != null && !categoryCodes.isEmpty()) {
            formData.setCtgryId(categoryCodes.get(0));
        } else {
            // 카테고리 필수이므로, 없으면 에러 발생
            logger.error("2단계 저장 실패: 카테고리가 선택되지 않음.");
            throw new IllegalStateException("최소 하나 이상의 카테고리를 선택해야 합니다.");
        }

        session.setAttribute("outsourcingFormData", formData); // 세션 업데이트
        logger.info("2단계 데이터 세션에 저장 완료. osCd: {}, 대표 카테고리: {}", formData.getOsCd(), formData.getCtgryId());
        logger.info("--- saveStep2Data 메서드 종료 ---");
    }

    // 3단계: 첨부 파일 업로드 (세션에 URL 저장, 실제 파일 저장)
    @Override
    public List<String> saveStep3Data(String osCd, MultipartFile[] files, HttpSession session) throws IOException {
        logger.info("--- saveStep3Data 메서드 시작 ---");
        logger.info("3단계 파일 업로드 요청 - osCd: {}, 파일 개수: {}", osCd, files != null ? files.length : 0);

        OutsourcingFormDataDto formData = (OutsourcingFormDataDto) session.getAttribute("outsourcingFormData");
        if (formData == null || !osCd.equals(formData.getOsCd())) {
            logger.error("세션 데이터 불일치 또는 없음 (파일 업로드). osCd: {}", osCd);
            throw new IllegalStateException("세션 데이터가 만료되었거나 유효하지 않습니다.");
        }

        List<String> uploadedFileUrls = new ArrayList<>();
        List<String> currentReferenceFileUrls = new ArrayList<>();
        if (formData.getReferenceFileUrls() != null) {
            currentReferenceFileUrls.addAll(formData.getReferenceFileUrls());
        }

        // 파일 저장 디렉토리 생성 (없을 경우)
        Path uploadPath = Paths.get(FILE_UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            logger.info("파일 업로드 디렉토리 생성: {}", FILE_UPLOAD_DIR);
        }

        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
                    // 파일명 중복 방지를 위한 UUID 추가
                    String newFileName = UUID.randomUUID().toString() + "_" + originalFileName;
                    Path filePath = uploadPath.resolve(newFileName);
                    
                    // 파일 저장
                    Files.copy(file.getInputStream(), filePath);
                    String fileUrl = "/files/outsourcing/" + newFileName; // TODO: 실제 서비스 URL 경로로 변경
                    uploadedFileUrls.add(fileUrl);
                    currentReferenceFileUrls.add(fileUrl); // 세션에 저장할 목록에 추가
                    logger.info("파일 저장 완료: {}", fileUrl);
                }
            }
        }
        formData.setReferenceFileUrls(currentReferenceFileUrls); // 세션 DTO에 파일 URL 목록 업데이트

        session.setAttribute("outsourcingFormData", formData); // 세션 업데이트
        logger.info("3단계 파일 업로드 데이터 세션에 저장 완료. osCd: {}", formData.getOsCd());
        logger.info("--- saveStep3Data 메서드 종료 ---");
        return uploadedFileUrls; // 업로드된 새 파일 URL만 반환
    }

    // 4단계: 최종 등록 완료 (세션 데이터 DB 저장)
    @Override
    @Transactional
    public void completeOutsourcingRegistration(HttpSession session) throws IOException {
        logger.info("--- completeOutsourcingRegistration 메서드 시작 ---");
        OutsourcingFormDataDto formData = (OutsourcingFormDataDto) session.getAttribute("outsourcingFormData");

        if (formData == null || formData.getOsCd() == null || formData.getOsCd().isEmpty()) {
            logger.error("최종 등록 실패: 세션 데이터가 없거나 osCd가 없음.");
            throw new IllegalStateException("등록할 외주 데이터가 유효하지 않습니다. 다시 시도해주세요.");
        }

        // EnterOutsourcing 객체 생성 및 데이터 채우기
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
        finalOutsourcing.setCtgryId(formData.getCtgryId()); // 2단계에서 설정된 대표 카테고리
        finalOutsourcing.setOsRegYmdt(LocalDateTime.now()); // 등록일시
        finalOutsourcing.setStcCd("SD_ACTIVE"); // 초기 상태 활성

        // 1. 외주 기본 정보 삽입 (addOutsourcing에서 복사해옴. useGeneratedKeys 없음)
        try {
            int insertedRows = outsourcingMapper.insertOutsourcing(finalOutsourcing);
            if (insertedRows == 0) {
                logger.error("최종 등록 실패: outsourcing 기본 정보 삽입 실패 (0행). osCd: {}", finalOutsourcing.getOsCd());
                throw new IllegalStateException("외주 기본 정보 저장 실패.");
            }
            logger.info("최종 등록: outsourcing 기본 정보 삽입 성공. osCd: {}", finalOutsourcing.getOsCd());
        } catch (Exception e) {
            logger.error("최종 등록: outsourcing 기본 정보 삽입 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
        
        // 2. content_list 등록
        String clCd = "LIST_" + finalOutsourcing.getOsCd();
        try {
            int insertedClRows = outsourcingMapper.insertContentList(clCd, finalOutsourcing.getOsCd());
             if (insertedClRows == 0) {
                logger.error("최종 등록 실패: content_list 삽입 실패 (0행). clCd: {}", clCd);
                throw new IllegalStateException("콘텐츠 목록 저장 실패.");
            }
            logger.info("최종 등록: content_list 삽입 성공. clCd: {}", clCd);
        } catch (Exception e) {
            logger.error("최종 등록: content_list 삽입 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }

        // 3. 카테고리 및 태그 매핑
        // updateMappings 메서드는 clCd와 mbrCd가 필요. mbrCd는 formData에서 가져옴.
        try {
            updateMappings(clCd, formData.getMbrCd(), formData.getCategoryCodes(), formData.getTags());
            logger.info("최종 등록: 카테고리/태그 매핑 완료. clCd: {}", clCd);
        } catch (Exception e) {
            logger.error("최종 등록: 카테고리/태그 매핑 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }

        // 4. 첨부 파일 정보 DB 저장 (OutsourcingFile 테이블이 있다면 여기에 삽입)
        if (formData.getReferenceFileUrls() != null && !formData.getReferenceFileUrls().isEmpty()) {
            // TODO: OutsourcingFile 테이블에 파일 URL/경로를 저장하는 로직 필요
            // 예시: outsourcingMapper.insertOutsourcingFile(osCd, fileUrl);
            logger.info("최종 등록: {}개의 첨부 파일 URL 확인. DB 저장 로직 필요.", formData.getReferenceFileUrls().size());
            // 실제 파일 관리: 임시 경로에서 영구 경로로 이동/복사 로직도 여기에서 처리될 수 있음.
        }

        // 5. 세션 데이터 제거 (최종 등록 성공 시)
        session.removeAttribute("outsourcingFormData");
        logger.info("--- completeOutsourcingRegistration 메서드 종료 (최종 등록 완료) ---");
    }

    // --- 기존 addOutsourcing 메서드는 이제 completeOutsourcingRegistration에서 활용되므로 제거합니다. ---
    /*
    @Override
    @Transactional
    public void addOutsourcing(EnterOutsourcing outsourcing, List<String> categoryCodes, String tags) throws IOException {
        // ... (기존 addOutsourcing 로직) ...
    }
    */
}