package outpolic.enter.outsourcing.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;
import outpolic.enter.outsourcing.domain.OutsourcingFormDataDto;
import outpolic.enter.portfolio.domain.EnterPortfolio;
import outpolic.systems.file.domain.FileMetaData;

public interface EnterOutsourcingService {
    
    List<EnterOutsourcing> getOutsourcingListByEntCd(String entCd);
    EnterOutsourcing findOutsourcingDetailsByOsCd(String osCd);
    List<String> searchTags(String query);
    String findEntCdByMbrCd(String mbrCd);
    List<String> getFilesByClCd(String clCd);

    // 등록 프로세스
    String saveStep1Data(OutsourcingFormDataDto formData, HttpSession session);
    FileMetaData uploadThumbnail(MultipartFile file);
    void completeOutsourcingRegistration(OutsourcingFormDataDto formData, MultipartFile thumbnailFile, List<MultipartFile> bodyImageFiles, HttpSession session);
    
    // 수정 프로세스
    void updateOutsourcingStep1(EnterOutsourcing outsourcingToUpdate);
    void updateOutsourcingStep2(String osCd, List<String> categoryCodes, String tags);
    void updateOutsourcingStep3(String osCd, MultipartFile thumbnailFile, List<MultipartFile> newBodyImageFiles, List<String> deletedBodyImageCds); // [!code modified]
    
    // 삭제
    void deleteOutsourcing(String osCd);
    // 연결/해제 관련
    List<EnterPortfolio> getLinkedPortfoliosByOsCd(String osCd);
    void linkPortfolioToOutsourcing(String osCd, String prtfCd, String entCd);
    void unlinkOutsourcingFromPortfolio(String osCd, String prtfCd);
    List<EnterPortfolio> searchUnlinkedPortfolios(String osCd, String entCd, String query);
    /**
     * [추가] EnterContentsController와의 호환성을 위해 추가
     * @param osCd
     * @return EnterOutsourcing
     */
    EnterOutsourcing getOutsourcingByOsCd(String osCd);
	void unlinkPortfolioFromOutsourcing(String string, String string2);
}