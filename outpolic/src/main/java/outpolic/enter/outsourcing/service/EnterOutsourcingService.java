package outpolic.enter.outsourcing.service;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;
import outpolic.enter.outsourcing.domain.OutsourcingFormDataDto;
import outpolic.enter.portfolio.domain.EnterPortfolio;
import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpSession;

public interface EnterOutsourcingService {

    // --- 조회 관련 ---
    List<EnterOutsourcing> getOutsourcingListByEntCd(String entCd);
    EnterOutsourcing findOutsourcingDetailsByOsCd(String osCd);
    List<String> searchTags(String query);

    // ★ 추가: 모든 외주 목록을 가져오는 메서드 (사용자 외주 목록에 필요)
    List<EnterOutsourcing> getAllOutsourcings();

    // --- 수정/삭제 관련 ---
    void deleteOutsourcing(String osCd);
    
    // --- 포트폴리오 연결 관련 ---
    List<EnterPortfolio> getLinkedPortfoliosByOsCd(String osCd);
    List<EnterPortfolio> searchUnlinkedPortfolios(String osCd, String entCd, String query);
    void linkPortfolioToOutsourcing(String osCd, String prtfCd, String entCd);
    void unlinkPortfolioFromOutsourcing(String osCd, String prtfCd);

    // --- 단계별 등록 API ---
    String saveStep1Data(OutsourcingFormDataDto formData, HttpSession session);
    void saveStep2Data(String osCd, List<String> categoryCodes, String tags, HttpSession session);
    List<String> saveStep3Data(String osCd, MultipartFile[] files, HttpSession session);
    void completeOutsourcingRegistration(String osCd, HttpSession session);

    // --- 단계별 수정 API ---
    void updateOutsourcingStep1(EnterOutsourcing outsourcing);
    void updateOutsourcingStep2(String osCd, List<String> categoryCodes, String tags);
    void updateOutsourcingStep3(String osCd, MultipartFile[] files);
    EnterOutsourcing getOutsourcingByOsCd(String osCd);
    
    String findEntCdByMbrCd(String mbrCd);
    
}