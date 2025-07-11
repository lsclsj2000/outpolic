package outpolic.enter.outsourcing.service;

import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile; // MultipartFile 임포트 추가

import jakarta.servlet.http.HttpSession; // HttpSession 임포트 추가
import outpolic.enter.outsourcing.domain.EnterOutsourcing;
import outpolic.enter.outsourcing.domain.OutsourcingFormDataDto; // 새로 정의한 DTO 임포트
import outpolic.enter.portfolio.domain.EnterPortfolio;

public interface EnterOutsourcingService {

    // --- 기존 API 유지 ---
    List<EnterOutsourcing> getOutsourcingListByEntCd(String entCd);
    EnterOutsourcing getOutsourcingByOsCd(String osCd);
    List<String> searchTags(String query);
    void updateOutsourcing(EnterOutsourcing outsourcing, List<String> categoryCodes, String tags) throws IOException;
    void deleteOutsourcing(String osCd);
    List<EnterPortfolio> getLinkedPortfoliosByOsCd(String osCd);
    List<EnterPortfolio> searchUnlinkedPortfolios(String osCd, String entCd, String query);
    void linkPortfolioToOutsourcing(String osCd, String prtfCd, String entCd);
    void unlinkPortfolioFromOutsourcing(String osCd, String prtfCd);

    // --- 새로운 단계별 등록 API ---
    // 1단계: 기본 정보 저장 (세션에 임시 저장 및 osCd 생성)
    String saveStep1Data(OutsourcingFormDataDto formData, HttpSession session) throws IOException;
    
    // 2단계: 카테고리 및 태그 저장 (세션에 임시 저장)
    void saveStep2Data(String osCd, List<String> categoryCodes, String tags, HttpSession session) throws IOException;

    // 3단계: 첨부 파일 업로드 (세션에 URL 저장, 실제 파일 저장)
    List<String> saveStep3Data(String osCd, MultipartFile[] files, HttpSession session) throws IOException;

    // 4단계: 최종 등록 완료 (세션 데이터 DB 저장)
    void completeOutsourcingRegistration(HttpSession session) throws IOException;
}