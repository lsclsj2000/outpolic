package outpolic.enter.outsourcing.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;
import outpolic.enter.outsourcing.domain.OutsourcingFormDataDto;
import outpolic.enter.portfolio.domain.EnterPortfolio; // EnterPortfolio 임포트 필요

public interface EnterOutsourcingService {
    List<EnterOutsourcing> getOutsourcingListByEntCd(String entCd);
    EnterOutsourcing findOutsourcingDetailsByOsCd(String osCd);
    List<String> searchTags(String query);
    EnterOutsourcing getOutsourcingByOsCd(String osCd);
    String findEntCdByMbrCd(String mbrCd);
    List<EnterOutsourcing> getAllOutsourcings();
    List<String> getFilesByClCd(String clCd);

    String saveStep1Data(OutsourcingFormDataDto formData, HttpSession session);
    void saveStep2Data(String osCd, List<String> categoryCodes, String tags, HttpSession session);
    List<String> saveStep3Data(String osCd, MultipartFile[] files, HttpSession session);
    void completeOutsourcingRegistration(String osCd, HttpSession session);
    void updateOutsourcingStep1(EnterOutsourcing outsourcingToUpdate);
    void updateOutsourcingStep2(String osCd, List<String> categoryCodes, String tags);
    void updateOutsourcingStep3(String osCd, MultipartFile[] files);
    void deleteOutsourcing(String osCd);

    List<EnterPortfolio> getLinkedPortfoliosByOsCd(String osCd); // 외주에 연결된 포트폴리오 조회

    void linkPortfolioToOutsourcing(String osCd, String prtfCd, String entCd);
    void unlinkPortfolioFromOutsourcing(String osCd, String prtfCd);

    // 이 메서드를 EnterOutsourcingService에 추가합니다.
    List<EnterPortfolio> searchUnlinkedPortfolios(String osCd, String entCd, String query);
}