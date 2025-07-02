package outpolic.enter.portfolio.service;
import outpolic.enter.portfolio.domain.EnterPortfolio;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
public interface EnterPortfolioService {
    List<EnterPortfolio> getPortfolioListByEntCd(String entCd);
    void addPortfolio(EnterPortfolio portfolio, List<MultipartFile> portfolioFiles, List<String> categoryCodes, String tags) throws IOException;
    void deletePortfolio(String prtfCd);
    EnterPortfolio getPortfolioByPrtfCd(String prtfCd);
    void updatePortfolio(EnterPortfolio portfolio, List<MultipartFile> portfolioFiles, List<String> categoryCodes, String tags) throws IOException;
}