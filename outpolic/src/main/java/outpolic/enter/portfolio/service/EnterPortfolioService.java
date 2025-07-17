package outpolic.enter.portfolio.service;

import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;
import outpolic.enter.portfolio.domain.EnterPortfolio;
import outpolic.enter.portfolio.domain.PortfolioFormDataDto;
import outpolic.systems.file.domain.FileMetaData;

public interface EnterPortfolioService {

    // --- 조회 관련 ---
    int countPortfoliosByEntCd(String entCd);
    List<EnterPortfolio> getPortfolioListByEntCd(String entCd);
    EnterPortfolio getPortfolioByPrtfCd(String prtfCd);
    String findEntCdByMbrCd(String mbrCd);
    List<String> searchTags(String query);
    List<EnterOutsourcing> getLinkedOutsourcings(String prtfCd);
    List<EnterOutsourcing> searchUnlinkedOutsourcings(String prtfCd, String entCd, String query);
    List<EnterPortfolio> searchPortfoliosByTitle(String query);
    List<EnterOutsourcing> getLinkedOutsourcingsByOsCd(String osCd);
    List<EnterPortfolio> searchUnlinkedPortfolios(String osCd, String entCd, String query);

    // --- 수정 및 삭제 ---
    void updatePortfolio(EnterPortfolio portfolio, List<String> categoryCodes, String tags, MultipartFile portfolioImage) throws IOException;
    void deletePortfolio(String prtfCd);

    // ▼▼▼ [수정] 다단계 등록을 위한 신규 메서드들 선언 ▼▼▼
    String generateNewPrtfCd();
    FileMetaData uploadThumbnail(MultipartFile file);
    void registerNewPortfolio(PortfolioFormDataDto formData) throws IOException;
}