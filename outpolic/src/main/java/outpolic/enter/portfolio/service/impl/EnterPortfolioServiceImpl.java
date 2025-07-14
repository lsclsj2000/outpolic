package outpolic.enter.portfolio.service.impl;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;
// OutsourcingMapper 계속 필요 (latestOpCd 등)
import outpolic.enter.outsourcing.mapper.OutsourcingMapper;
import outpolic.enter.portfolio.domain.EnterPortfolio;
import outpolic.enter.portfolio.mapper.PortfolioMapper;
import outpolic.enter.portfolio.service.EnterPortfolioService;
@Service
@RequiredArgsConstructor
public class EnterPortfolioServiceImpl implements EnterPortfolioService {
    
    private final PortfolioMapper portfolioMapper;
    private final OutsourcingMapper outsourcingMapper; // 최신 op_cd 조회를 위해 유지
    private final String FILE_UPLOAD_DIR = "C:/uploads";

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
    public void addPortfolio(EnterPortfolio portfolio, List<String> categoryCodes, String tags,MultipartFile portfolioImage) throws IOException {
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
    public void updatePortfolio(EnterPortfolio portfolio, List<String> categoryCodes, String tags, MultipartFile portfolioImage) throws IOException {
        
        // 1. 새로 업로드된 이미지 파일이 있는지 확인하고, 있다면 저장합니다.
        if (portfolioImage != null && !portfolioImage.isEmpty()) {
            // (선택) 기존에 있던 썸네일 이미지를 서버에서 삭제하는 로직을 여기에 추가할 수 있습니다.

            File uploadDir = new File(FILE_UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            String originalFilename = portfolioImage.getOriginalFilename();
            String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;
            String savePath = uploadDir.getAbsolutePath() + File.separator + uniqueFilename;
            
            portfolioImage.transferTo(new File(savePath));

            // 저장된 파일의 URL 경로를 portfolio 객체에 설정합니다.
            portfolio.setPrtfThumbnailUrl("/uploads/" + uniqueFilename);
        }

        // 2. 나머지 수정 로직을 수행합니다.
        portfolio.setPrtfMdfcnYmdt(LocalDateTime.now());
        portfolioMapper.updatePortfolio(portfolio); // DB에 썸네일 URL도 함께 업데이트됩니다.

        String clCd = portfolioMapper.findClCdByPrtfCd(portfolio.getPrtfCd());
        String originalMbrCd = portfolioMapper.findMbrCdByClCd(clCd);
        
        if (originalMbrCd == null) {
            throw new IllegalStateException("포트폴리오의 원본 등록자 정보를 찾을 수 없습니다. (카테고리 매핑 부재)");
        }

        portfolioMapper.deleteCategoryMappingByClCd(clCd);
        portfolioMapper.deleteTagMappingByClCd(clCd);

        updateMappings(clCd, originalMbrCd, categoryCodes, tags);
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
            portfolioMapper.deleteOutsourcingPortfolioByPrtfCd(prtfCd); // 수정: 올바른 매퍼 메서드 호출
            portfolioMapper.deleteFilesByClCd(clCd);
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
    
    // --- 외주 연결 기능을 위한 로직들은 이제 EnterOutsourcingService에서 관리합니다.
    // ---
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
    @Override
    @Transactional
    public void updateOutsourcingStep1(EnterOutsourcing outsourcingToUpdate) {
        outsourcingToUpdate.setOsMdfcnYmdt(LocalDateTime.now());
        // ▼▼▼ 호출하는 매퍼 메서드 이름을 바꿉니다. ▼▼▼
        outsourcingMapper.updateOutsourcing(outsourcingToUpdate);
    }
    
    @Override
    public String findEntCdByMbrCd(String mbrCd) {
        // portfolioMapper를 통해 직접 호출
        return portfolioMapper.findEntCdByMbrCd(mbrCd); 
    }
}