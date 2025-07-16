package outpolic.enter.portfolio.service;

import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import outpolic.enter.outsourcing.domain.EnterOutsourcing; // EnterOutsourcing 임포트 필요
import outpolic.enter.portfolio.domain.EnterPortfolio;

public interface EnterPortfolioService {

    // --- 조회 관련 ---
    int countPortfoliosByEntCd(String entCd);
    List<EnterPortfolio> getPortfolioListByEntCd(String entCd);
    EnterPortfolio getPortfolioByPrtfCd(String prtfCd);
    String findEntCdByMbrCd(String mbrCd);
    List<String> searchTags(String query);
    List<EnterOutsourcing> getLinkedOutsourcings(String prtfCd); // Portfolio에 연결된 Outsourcing 조회
    List<EnterOutsourcing> searchUnlinkedOutsourcings(String prtfCd, String entCd, String query); // Portfolio에 연결되지 않은 Outsourcing 검색

    List<EnterPortfolio> searchPortfoliosByTitle(String query);

    // 이 메서드는 "외주(Outsourcing)" 목록을 반환해야 하므로 타입을 명확히 수정합니다.
    List<EnterOutsourcing> getLinkedOutsourcingsByOsCd(String osCd);

    // EnterOutsourcingService에 있어야 할 메서드이므로, 여기서는 제거합니다.
    // List<EnterPortfolio> searchUnlinkedPortfolios(String osCd, String entCd, String query); // <-- 이 줄 제거

    // --- CUD 관련 ---
    void addPortfolio(EnterPortfolio portfolio,List<String> categoryCodes, String tags, MultipartFile portfolioImage) throws IOException;
    void updatePortfolio(EnterPortfolio portfolio,List<String> categoryCodes, String tags, MultipartFile portfolioImage) throws IOException;
    void deletePortfolio(String prtfCd);
	List<EnterPortfolio> searchUnlinkedPortfolios(String osCd, String entCd, String query);
}