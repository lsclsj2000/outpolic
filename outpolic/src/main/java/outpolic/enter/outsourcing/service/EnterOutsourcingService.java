package outpolic.enter.outsourcing.service;

import java.io.IOException;
import java.util.List;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;
import outpolic.enter.portfolio.domain.EnterPortfolio; // EnterPortfolio import 추가

public interface EnterOutsourcingService {

    List<EnterOutsourcing> getOutsourcingListByEntCd(String entCd);
    EnterOutsourcing getOutsourcingByOsCd(String osCd);
    List<String> searchTags(String query);
    // addOutsourcing 메서드 시그니처 변경: portfolioCds 파라미터 제거
    void addOutsourcing(EnterOutsourcing outsourcing, List<String> categoryCodes, String tags) throws IOException;
    // updateOutsourcing 메서드 시그니처 변경: portfolioCds 파라미터 제거
    void updateOutsourcing(EnterOutsourcing outsourcing, List<String> categoryCodes, String tags) throws IOException;
    void deleteOutsourcing(String osCd);

    // --- 외주-포트폴리오 연결을 위한 새로운 서비스 메서드들 (추가) ---
    List<EnterPortfolio> getLinkedPortfoliosByOsCd(String osCd);
    List<EnterPortfolio> searchUnlinkedPortfolios(String osCd, String entCd, String query);
    void linkPortfolioToOutsourcing(String osCd, String prtfCd, String entCd);
    void unlinkPortfolioFromOutsourcing(String osCd, String prtfCd);
}