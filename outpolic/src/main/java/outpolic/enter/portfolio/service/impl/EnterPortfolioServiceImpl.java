package outpolic.enter.portfolio.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;
import outpolic.enter.outsourcing.mapper.OutsourcingMapper;
import outpolic.enter.portfolio.domain.EnterPortfolio;
import outpolic.enter.portfolio.mapper.PortfolioMapper;
import outpolic.enter.portfolio.service.EnterPortfolioService;

@Service
@RequiredArgsConstructor
public class EnterPortfolioServiceImpl implements EnterPortfolioService {
    
    private final PortfolioMapper portfolioMapper;
    private final OutsourcingMapper outsourcingMapper;
    
    

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
        

        portfolioMapper.deleteCategoryMappingByClCd(clCd);
        portfolioMapper.deleteTagMappingByClCd(clCd);
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
            // portfolioMapper.deleteBookMarkByCiCd(clCd);
            
            // 3. 콘텐츠 리스트 레코드 삭제 
            portfolioMapper.deleteContentListByClCd(clCd);
        }
        // 4. 이 포트폴리오를 참조하는 외주-포폴 연결 레코드 삭제(순서 중요!)
       // 아직 추가하지 않음 portfolioMapper.deleteOutsourcingPortfolioByPrtfCd(prtfCd);
        
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
    	// PortfolioMapper에 제목으로 포트폴리오를 검색하는 메서드가 필요합니다.
    	// 예를 들어: portfolioMapper.findPortfoliosByTitle(query);
    	// 또는 간단하게 findPortfoliioDetailsByEntCd를 응용하거나 새로운 Mapper 메서드 정의.
    	return portfolioMapper.findPortfoliosByTitle(query);
    	
    }
    
    
   @Override
   public List<String> searchTags(String query){
	   return portfolioMapper.searchTagsByName(query);
   }
   
   // 외주 연결 기능을 위한 로직 구현
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
	   // 새로운 외주-포폴 연결 코드(PK) 생성
	   String latestOpCd = outsourcingMapper.findLatestOpCd();
	   int nextNum = (latestOpCd == null) ? 1 : Integer.parseInt(latestOpCd.substring(4))+1;
	   String newOpCd = String.format("OP_C%05d", nextNum);
	   	
	   portfolioMapper.linkOutsourcingToPortfolio(newOpCd,osCd,prtfCd,entCd);
   }
   
   @Override
   @Transactional
   public void unlinkOutsourcing(String prtfCd, String osCd) {
	   portfolioMapper.unlinkOutsourcingFromPortfolio(osCd,prtfCd);
   }
}