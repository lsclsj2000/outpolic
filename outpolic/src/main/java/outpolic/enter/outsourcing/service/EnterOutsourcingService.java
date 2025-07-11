package outpolic.enter.outsourcing.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpSession;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;
import outpolic.enter.outsourcing.domain.OutsourcingFormDataDto;
import outpolic.enter.portfolio.domain.EnterPortfolio;

public interface EnterOutsourcingService {

    // --- 조회 관련 ---
    List<EnterOutsourcing> getOutsourcingListByEntCd(String entCd);
    
    // ★ 수정: getOutsourcingByOsCd를 findOutsourcingDetailsByOsCd로 통일.
    // 어차피 상세 정보를 가져오는 것이 주 목적이므로 하나만 사용합니다.
    EnterOutsourcing findOutsourcingDetailsByOsCd(String osCd);

    // --- 수정/삭제 관련 ---
    // ★ 수정: 모든 throws IOException 제거. 구현체에서 RuntimeException으로 처리하도록 규칙 변경.
    void deleteOutsourcing(String osCd);
    
    // --- 포트폴리오 연결 관련 ---
    List<EnterPortfolio> getLinkedPortfoliosByOsCd(String osCd);
    List<EnterPortfolio> searchUnlinkedPortfolios(String osCd, String entCd, String query);
    void linkPortfolioToOutsourcing(String osCd, String prtfCd, String entCd);
    void unlinkPortfolioFromOutsourcing(String osCd, String prtfCd);
    
    // --- 검색 관련 ---
    List<String> searchTags(String query);

    // --- 단계별 등록 API ---
    // ★ 수정: 모든 throws IOException 제거.
    String saveStep1Data(OutsourcingFormDataDto formData, HttpSession session);
    void saveStep2Data(String osCd, List<String> categoryCodes, String tags, HttpSession session);
    List<String> saveStep3Data(String osCd, MultipartFile[] files, HttpSession session);
    void completeOutsourcingRegistration(String osCd, HttpSession session);
    
    
    /**
     * 외주 수정 1단계: 기본 정보를 업데이트합니다.
     * @param outsourcingToUpdate 업데이트할 기본 정보가 담긴 객체
     */
    void updateOutsourcingStep1(EnterOutsourcing outsourcing);

    /**
     * 외주 수정 2단계: 카테고리 및 태그를 업데이트합니다.
     * @param osCd 수정할 외주 코드
     * @param categoryCodes 새로운 카테고리 코드 목록
     * @param tags 새로운 태그 문자열
     */
    void updateOutsourcingStep2(String osCd, List<String> categoryCodes, String tags);

    /**
     * 외주 수정 3단계: 첨부 파일을 업데이트합니다.
     * @param osCd 수정할 외주 코드
     * @param files 새로 첨부된 파일 목록
     */
    void updateOutsourcingStep3(String osCd, MultipartFile[] files);

}