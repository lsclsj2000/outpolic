package outpolic.enter.portfolio.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;
import outpolic.enter.outsourcing.mapper.OutsourcingMapper; // OutsourcingMapper 계속 필요 (latestOpCd 등)
import outpolic.enter.portfolio.domain.EnterPortfolio;
import outpolic.enter.portfolio.mapper.PortfolioMapper;
import outpolic.enter.portfolio.service.EnterPortfolioService;

@Service
@RequiredArgsConstructor
public class EnterPortfolioServiceImpl implements EnterPortfolioService {
    
    private final PortfolioMapper portfolioMapper;
    private final OutsourcingMapper outsourcingMapper; // 최신 op_cd 조회를 위해 유지

    @Override
    public int countPortfoliosByEntCd(String entCd) {
    	return portfolioMapper.countPortfoliosByEntCd(entCd);
    }
    
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
        
        // 기존 매핑 삭제
        portfolioMapper.deleteCategoryMappingByClCd(clCd);
        portfolioMapper.deleteTagMappingByClCd(clCd);
        // 새로운 매핑 저장
        updateMappings(clCd, portfolio.getMbrCd(), categoryCodes, tags);
    }
 
    @Override
    @Transactional
    public void deletePortfolio(String prtfCd) {
        String clCd = portfolioMapper.findClCdByPrtfCd(prtfCd);
        
        // 외래 키 제약 조건 위반을 막기 위해 참조하는 모든 레코드를 먼저 삭제해야 합니다.
        if (clCd != null) {
            // 1. 매핑 테이블 레코드 삭제
            portfolioMapper.deleteCategoryMappingByClCd(clCd);
            portfolioMapper.deleteTagMappingByClCd(clCd);
            
            // 2. 다른 주요 테이블에서 이 콘텐츠(cl_cd)를 참조하는 레코드 삭제(예: 북마크 등)
            portfolioMapper.deleteBookmarkByClCd(clCd); 
            
            // 3. 이 포트폴리오를 참조하는 외주-포폴 연결 레코드 삭제(중요!)
            // OutsourcingMapper로 이동했지만, 여기서는 Portfolio를 지울 때 관련된 연결을 지워야 합니다.
            // PortfolioMapper에 이 메서드를 유지하거나, OutsourcingMapper에서 prtfCd로 삭제하는 메서드를 호출해야 합니다.
            outsourcingMapper.deleteOutsourcingPortfolioByOsCd(prtfCd); // osCd 대신 prtfCd로 삭제 (새로운 Mapper 메서드 필요)
            
            // 4. 콘텐츠 리스트 레코드 삭제
            portfolioMapper.deleteContentListByClCd(clCd);
        }
        
        // 5. 마지막으로 포트폴리오 자체를 삭제
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
    
    @Override
    public List<EnterPortfolio> searchPortfoliosByTitle(String query){
    	return portfolioMapper.findPortfoliosByTitle(query);
    }
    
    @Override
    public List<String> searchTags(String query){
	    return portfolioMapper.searchTagsByName(query);
    }
    
    // --- 외주 연결 기능을 위한 로직들은 이제 EnterOutsourcingService에서 관리합니다. ---
    // 따라서 이 클래스에서는 해당 메서드들을 제거합니다.
    @Override
    public List<EnterOutsourcing> getLinkedOutsourcings(String prtfCd){
        return portfolioMapper.findLinkedOutsourcingsByPrtfCd(prtfCd);
    }
    
    @Override
    public List<EnterOutsourcing> searchUnlinkedOutsourcings(String prtfCd,String entCd,String query){
        return portfolioMapper.findUnlinkedOutsourcings(prtfCd,entCd,query);
    }
    
    @Override
    @Transactional
    public void linkOutsourcing(String prtfCd, String osCd, String entCd) {
        // 이 로직은 EnterOutsourcingService의 linkPortfolioToOutsourcing으로 이동되었습니다.
        // 여기서는 더 이상 처리하지 않습니다.
        throw new UnsupportedOperationException("This method is now handled by EnterOutsourcingService.");
    }
    
    @Override
    @Transactional
    public void unlinkOutsourcing(String prtfCd, String osCd) {
        // 이 로직은 EnterOutsourcingService의 unlinkPortfolioFromOutsourcing으로 이동되었습니다.
        // 여기서는 더 이상 처리하지 않습니다.
        throw new UnsupportedOperationException("This method is now handled by EnterOutsourcingService.");
    }
}