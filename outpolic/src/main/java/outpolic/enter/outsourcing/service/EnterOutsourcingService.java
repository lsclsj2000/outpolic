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
    // 이 메서드들의 시그니처를 정확히 확인하세요.
    void saveStep3Data(String osCd, MultipartFile[] referenceFiles, HttpSession session);
    void completeOutsourcingRegistration(String osCd, HttpSession session);
    void updateOutsourcingStep1(EnterOutsourcing outsourcingToUpdate);
    void updateOutsourcingStep2(String osCd, List<String> categoryCodes, String tags);
    // 이 메서드들의 시그니처를 정확히 확인하세요.
    void updateOutsourcingStep3(String osCd, MultipartFile[] referenceFiles);
    void deleteOutsourcing(String osCd);

    List<EnterPortfolio> getLinkedPortfoliosByOsCd(String osCd);
    void linkPortfolioToOutsCd(String osCd, String prtfCd, String entCd); // 오타 수정: linkPortfolioToOutsourcing
    void unlinkPortfolioFromOutsourcing(String osCd, String prtfCd);

    List<EnterPortfolio> searchUnlinkedPortfolios(String osCd, String entCd, String query);
	void linkPortfolioToOutsourcing(String osCd, String prtfCd, String entCd);
	void updateOutsourcingStep3(String osCd, MultipartFile thumbnailFile, MultipartFile[] referenceFiles);
	void saveStep3Data(String osCd, MultipartFile thumbnailFile, MultipartFile[] referenceFiles, HttpSession session);
}