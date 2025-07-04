package outpolic.enter.portfolio.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import outpolic.enter.portfolio.domain.EnterPortfolio;
import outpolic.enter.portfolio.mapper.PortfolioMapper;
import outpolic.enter.portfolio.service.EnterPortfolioService;

@Service
@RequiredArgsConstructor
public class EnterPortfolioServiceImpl implements EnterPortfolioService {
    
    private final PortfolioMapper portfolioMapper;

    
    @Override
    public List<EnterPortfolio> getPortfolioListByEntCd(String entCd) {
        List<EnterPortfolio> portfolioList = portfolioMapper.findPortfolioDetailsByEntCd(entCd);
        return portfolioList;
    }
    
    @Override
    public EnterPortfolio getPortfolioByPrtfCd(String prtfCd) {
        EnterPortfolio portfolio = portfolioMapper.findPortfolioDetailsByPrtfCd(prtfCd);
        return portfolio;
    }

    @Override
    @Transactional
    public void addPortfolio(EnterPortfolio portfolio, List<String> categoryCodes, String tags) throws IOException {
        String latestPrtfCd = portfolioMapper.findLatestPrtfCd();
        int nextNum = (latestPrtfCd == null) ? 1 : Integer.parseInt(latestPrtfCd.substring(4)) + 1;
        String newPrtfCd = String.format("PO_C%05d", nextNum);
        portfolio.setPrtfCd(newPrtfCd);
        
        portfolioMapper.insertPortfolio(portfolio);

        String newClCd = "LIST_" + newPrtfCd;
        portfolioMapper.insertContentList(newClCd, newPrtfCd);
        
        
        updateMappings(newClCd, portfolio.getMbrCd(), categoryCodes, tags);
    }
    
    @Override
    @Transactional
    public void updatePortfolio(EnterPortfolio portfolio, List<String> categoryCodes, String tags) throws IOException {
        portfolio.setPrtfMdfcnYmdt(LocalDateTime.now());
        portfolioMapper.updatePortfolio(portfolio);

        String clCd = portfolioMapper.findClCdByPrtfCd(portfolio.getPrtfCd());
        

        portfolioMapper.deleteCategoryMappingByClCd(clCd);
        portfolioMapper.deleteTagMappingByClCd(clCd);
        updateMappings(clCd, portfolio.getMbrCd(), categoryCodes, tags);
    }

    @Override
    @Transactional
    public void deletePortfolio(String prtfCd) {
        String clCd = portfolioMapper.findClCdByPrtfCd(prtfCd);
        if (clCd != null) {
            portfolioMapper.deleteFileByClCd(clCd);
            portfolioMapper.deleteCategoryMappingByClCd(clCd);
            portfolioMapper.deleteTagMappingByClCd(clCd);
            portfolioMapper.deleteContentListByClCd(clCd);
        }
        portfolioMapper.deletePortfolioByPrtfCd(prtfCd);
    }
    
    private void updateMappings(String clCd, String mbrCd, List<String> categoryCodes, String tags) {
        if (categoryCodes != null) {
            for (String ctgryCd : categoryCodes) {
                if(ctgryCd != null && !ctgryCd.trim().isEmpty()) portfolioMapper.insertCategoryMapping(ctgryCd, clCd, mbrCd);
            }
        }
        if (tags != null && !tags.trim().isEmpty()) {
            for (String tagName : tags.split(",")) {
                String trimmedTagName = tagName.trim();
                if (trimmedTagName.isEmpty()) continue;
                String tagCd = portfolioMapper.findTagCdByName(trimmedTagName);
                if (tagCd == null) {
                    String latestTagCd = portfolioMapper.findLatestTagCd();
                    int nextNum = (latestTagCd == null) ? 1 : Integer.parseInt(latestTagCd.substring(4)) + 1;
                    tagCd = String.format("T_C%05d", nextNum);
                    portfolioMapper.insertTag(tagCd, trimmedTagName, mbrCd);
                }
                portfolioMapper.insertTagMapping(tagCd, clCd, mbrCd);
            }
        }
    }
}