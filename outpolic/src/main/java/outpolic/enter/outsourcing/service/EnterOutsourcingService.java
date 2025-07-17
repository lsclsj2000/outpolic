package outpolic.enter.outsourcing.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;
import outpolic.enter.outsourcing.domain.OutsourcingFormDataDto;
import outpolic.enter.portfolio.domain.EnterPortfolio;

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
    // 수정: referenceFiles 파라미터 제거
    void saveStep3Data(String osCd, MultipartFile thumbnailFile, HttpSession session);
    void completeOutsourcingRegistration(String osCd, HttpSession session);
    void updateOutsourcingStep1(EnterOutsourcing outsourcingToUpdate);
    void updateOutsourcingStep2(String osCd, List<String> categoryCodes, String tags);
    // 수정: referenceFiles 파라미터 제거
    void updateOutsourcingStep3(String osCd, MultipartFile thumbnailFile);
    void deleteOutsourcing(String osCd);

    List<EnterPortfolio> getLinkedPortfoliosByOsCd(String osCd);
    void linkPortfolioToOutsourcing(String osCd, String prtfCd, String entCd);
    void unlinkPortfolioFromOutsourcing(String osCd, String prtfCd);

    List<EnterPortfolio> searchUnlinkedPortfolios(String osCd, String entCd, String query);
	void unlinkOutsourcingFromPortfolio(String osCd, String prtfCd);
	List<EnterPortfolio> getLinkedOutsourcingsByOsCd(String osCd);
}