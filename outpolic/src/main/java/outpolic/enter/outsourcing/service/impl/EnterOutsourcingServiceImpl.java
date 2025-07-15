package outpolic.enter.outsourcing.service.impl;

import java.time.LocalDateTime;
import java.util.List;

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

@Service
@RequiredArgsConstructor
public class EnterOutsourcingServiceImpl implements EnterOutsourcingService {

    private static final Logger logger = LoggerFactory.getLogger(EnterOutsourcingServiceImpl.class);

    // --- 의존성 주입 (파일 관련 의존성 제거) ---
    private final OutsourcingMapper outsourcingMapper;
    private final PortfolioMapper portfolioMapper;

    // ======================================================
    // ▼▼▼ 조회 및 공통 로직 ▼▼▼
    // ======================================================

    @Override
    public List<EnterOutsourcing> getOutsourcingListByEntCd(String entCd) {
        return outsourcingMapper.findOutsourcingDetailsByEntCd(entCd);
    }

    @Override
    public EnterOutsourcing findOutsourcingDetailsByOsCd(String osCd) {
        return outsourcingMapper.findOutsourcingDetailsByOsCd(osCd);
    }

    @Override
    public List<String> searchTags(String query) {
        return outsourcingMapper.searchTagsByName(query);
    }

    @Override
    public EnterOutsourcing getOutsourcingByOsCd(String osCd) {
        return outsourcingMapper.findOutsourcingDetailsByOsCd(osCd);
    }
    
    @Override
    public String findEntCdByMbrCd(String mbrCd) {
        return outsourcingMapper.findEntCdByMbrCd(mbrCd);
    }

    // ======================================================
    // ▼▼▼ 외주 "등록" 관련 로직 (단계별) ▼▼▼
    // ======================================================

    @Override
    public String saveStep1Data(OutsourcingFormDataDto formData, HttpSession session) {
        logger.info("--- saveStep1Data 시작 ---");
        if (formData.getOsCd() == null || formData.getOsCd().isEmpty()) {
            String latestOsCd = outsourcingMapper.findLatestOsCd();
            int nextNum = (latestOsCd == null || !latestOsCd.startsWith("OS_C")) ? 1 : Integer.parseInt(latestOsCd.substring(5)) + 1;
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
    @Transactional
    public void completeOutsourcingRegistration(String osCd, HttpSession session) {
        logger.info("--- completeOutsourcingRegistration 시작, osCd: {} ---", osCd);
        OutsourcingFormDataDto formData = (OutsourcingFormDataDto) session.getAttribute("outsourcingFormData");
        if (formData == null || !osCd.equals(formData.getOsCd())) {
            throw new IllegalStateException("세션 정보가 유효하지 않거나 만료되었습니다.");
        }
        if (formData.getCtgryId() == null || formData.getCtgryId().isBlank()) {
            throw new IllegalStateException("대표 카테고리를 선택해주세요.");
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
        
        // 썸네일 관련 로직이 없는 상태
        outsourcingMapper.insertOutsourcing(finalOutsourcing);
        
        String clCd = "LIST_" + finalOutsourcing.getOsCd();
        outsourcingMapper.insertContentList(clCd, finalOutsourcing.getOsCd());
        
        updateMappings(clCd, formData.getMbrCd(), formData.getCategoryCodes(), formData.getTags());

        session.removeAttribute("outsourcingFormData");
    }

    // ======================================================
    // ▼▼▼ 외주 "수정" 관련 로직 ▼▼▼
    // ======================================================

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

        updateMappings(clCd, originalMbrCd, categoryCodes, tags);
        logger.info("수정 2단계 완료: {} 의 카테고리 및 태그가 업데이트되었습니다.", osCd);
    }
    
    // 파일 관련 로직이었던 Step3는 제거
    @Override
    public void updateOutsourcingStep3(String osCd, MultipartFile[] files) {
        logger.warn("파일 수정 기능은 현재 구현에서 제외되었습니다.");
        // 파일 관련 로직 없음
    }

    // ======================================================
    // ▼▼▼ 공통 및 기타 로직 ▼▼▼
    // ======================================================
    
    private void updateMappings(String clCd, String mbrCd, List<String> categoryCodes, String tags) {
        if (categoryCodes != null) {
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
                    int nextNum = (latestTagCd == null || !latestTagCd.startsWith("T_C")) ? 1 : Integer.parseInt(latestTagCd.substring(4)) + 1;
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
        int nextNum = (latestOpCd == null || !latestOpCd.startsWith("OP_C")) ? 1 : Integer.parseInt(latestOpCd.substring(4)) + 1;
        String newOpCd = String.format("OP_C%05d", nextNum);
        outsourcingMapper.insertOutsourcingPortfolio(newOpCd, osCd, prtfCd, entCd);
    }

    @Override
    @Transactional
    public void unlinkPortfolioFromOutsourcing(String osCd, String prtfCd) {
        outsourcingMapper.unlinkOutsourcingFromPortfolio(osCd, prtfCd);
    }
    
    @Override
    @Transactional
    public void deleteOutsourcing(String osCd) {
        String clCd = outsourcingMapper.findClCdByOsCd(osCd);
        if (clCd != null) {
            // 파일 삭제 관련 로직 제거
            outsourcingMapper.deleteCategoryMappingByClCd(clCd);
            outsourcingMapper.deleteTagMappingByClCd(clCd);
            outsourcingMapper.deleteOutsourcingPortfolioByOsCd(osCd);
            outsourcingMapper.deleteContentListByClCd(clCd);
        }
        outsourcingMapper.deleteOutsourcingByOsCd(osCd);
    }

	@Override
	public List<EnterOutsourcing> getAllOutsourcings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> saveStep3Data(String osCd, MultipartFile[] files, HttpSession session) {
		// TODO Auto-generated method stub
		return null;
	}
}
