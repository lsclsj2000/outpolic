package outpolic.enter.portfolio.service;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;
import outpolic.enter.portfolio.domain.EnterPortfolio;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

public interface EnterPortfolioService {
	
	/**
	 * 특정 기업 코드(entCd)로 등록된 포트폴리오 개수를 조회합니다.
	 * @param entCd
	 * @return
	 */
	int countPortfoliosByEntCd(String entCd);
	
    List<EnterPortfolio> getPortfolioListByEntCd(String entCd);
    void addPortfolio(EnterPortfolio portfolio, List<String> categoryCodes, String tags) throws IOException;
    void deletePortfolio(String prtfCd);
    EnterPortfolio getPortfolioByPrtfCd(String prtfCd);
    void updatePortfolio(EnterPortfolio portfolio, List<String> categoryCodes, String tags) throws IOException;
    
    /**
     * 포트폴리오 제목으로 포트폴리오를 검색합니다. (외주-포트폴리오 연결 기능용)
     * @Param query 검색어(포트폴리오 제목)
     * @return 검색된 포트폴리오 목록 (간단한 정보만 포함할 수도 있음)
     * */
    List<EnterPortfolio> searchPortfoliosByTitle(String query);
    List<String> searchTags(String query);
    
    // 외주 연결 기능을 위해 아래 4개 메서드 추가
    List<EnterOutsourcing> getLinkedOutsourcings(String prtfCd);
    List<EnterOutsourcing> searchUnlinkedOutsourcings(String prtfCd, String entCd, String query);
    void linkOutsourcing(String prtfCd, String osCd, String entCd);
    void unlinkOutsourcing(String prtfCd, String osCd);
}