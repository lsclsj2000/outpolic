package outpolic.enter.portfolio.service;

import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;
import outpolic.enter.portfolio.domain.EnterPortfolio;

public interface EnterPortfolioService {

    // --- 조회 관련 ---
    int countPortfoliosByEntCd(String entCd);
    List<EnterPortfolio> getPortfolioListByEntCd(String entCd);
    EnterPortfolio getPortfolioByPrtfCd(String prtfCd);
    String findEntCdByMbrCd(String mbrCd);
    List<String> searchTags(String query);
    List<EnterOutsourcing> getLinkedOutsourcings(String prtfCd);
    List<EnterOutsourcing> searchUnlinkedOutsourcings(String prtfCd, String entCd, String query);

    // ▼▼▼ 이 메서드 선언을 추가합니다. ▼▼▼
    List<EnterPortfolio> searchPortfoliosByTitle(String query);

    // --- CUD 관련 ---
    void addPortfolio(EnterPortfolio portfolio, List<String> categoryCodes, String tags, MultipartFile portfolioImage) throws IOException;
    void updatePortfolio(EnterPortfolio portfolio, List<String> categoryCodes, String tags, MultipartFile portfolioImage) throws IOException;
    void deletePortfolio(String prtfCd);
    List<EnterPortfolio> getLinkedPortfoliosByOsCd(String osCd);
    
}
