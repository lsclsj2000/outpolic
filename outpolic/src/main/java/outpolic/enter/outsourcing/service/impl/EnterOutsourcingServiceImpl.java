package outpolic.enter.outsourcing.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;
import outpolic.enter.outsourcing.mapper.OutsourcingMapper;
import outpolic.enter.outsourcing.service.EnterOutsourcingService;
import outpolic.enter.portfolio.mapper.PortfolioMapper;

@Service
@RequiredArgsConstructor
public class EnterOutsourcingServiceImpl implements EnterOutsourcingService {

    private final OutsourcingMapper outsourcingMapper;
    private final PortfolioMapper portfolioMapper;

    @Override
    public List<EnterOutsourcing> getOutsourcingListByEntCd(String entCd) {
        return outsourcingMapper.findOutsourcingDetailsByEntCd(entCd);
    }
    
    @Override
    public EnterOutsourcing getOutsourcingByOsCd(String osCd) {
        return outsourcingMapper.findOutsourcingDetailsByOsCd(osCd);
    }

    @Override
    @Transactional
    public void addOutsourcing(EnterOutsourcing outsourcing, List<String> categoryCodes, String tags, List<String> portfolioCds) throws IOException {
        int portfolioCount = portfolioMapper.countPortfoliosByEntCd(outsourcing.getEntCd());
        if (portfolioCount == 0) {
            throw new IllegalStateException("외주를 등록하려면 먼저 포트폴리오를 1개 이상 등록해야 합니다.");
        }

        String latestOsCd = outsourcingMapper.findLatestOsCd();
        int nextNum = (latestOsCd == null) ? 1 : Integer.parseInt(latestOsCd.substring(4)) + 1;
        String newOsCd = String.format("OS_C%05d", nextNum);
        outsourcing.setOsCd(newOsCd);
        
        if (categoryCodes != null && !categoryCodes.isEmpty()) {
            outsourcing.setCtgryId(categoryCodes.get(0));
        } else {
            throw new IllegalStateException("최소 하나 이상의 카테고리를 선택해야 합니다.");
        }
        
        outsourcingMapper.insertOutsourcing(outsourcing);
        
        String newClCd = "LIST_" + newOsCd;
        outsourcingMapper.insertContentList(newClCd, newOsCd);

        updateMappings(newClCd, outsourcing.getMbrCd(), categoryCodes, tags);
        
        if (portfolioCds != null && !portfolioCds.isEmpty()) {
            for (String prtfCd : portfolioCds) {
                String latestOpCd = outsourcingMapper.findLatestOpCd();
                // ✅ 'int'를 제거하여 변수 중복 선언 오류 해결
                nextNum = (latestOpCd == null) ? 1 : Integer.parseInt(latestOpCd.substring(4)) + 1;
                String newOpCd = String.format("OP_C%05d", nextNum);
                
                outsourcingMapper.insertOutsourcingPortfolio(newOpCd, outsourcing.getOsCd(), prtfCd, outsourcing.getEntCd());
            }
        }
    }

    @Override
    @Transactional
    public void updateOutsourcing(EnterOutsourcing outsourcing, List<String> categoryCodes, String tags, List<String> portfolioCds) throws IOException {
        outsourcing.setOsMdfcnYmdt(LocalDateTime.now());

        if (categoryCodes != null && !categoryCodes.isEmpty()) {
            outsourcing.setCtgryId(categoryCodes.get(0));
        } else {
            throw new IllegalStateException("업데이트 시 최소 하나 이상의 카테고리를 선택해야 합니다.");
        }

        outsourcingMapper.updateOutsourcing(outsourcing);

        String clCd = outsourcingMapper.findClCdByOsCd(outsourcing.getOsCd());

        if (clCd != null) {
            outsourcingMapper.deleteCategoryMappingByClCd(clCd);
            outsourcingMapper.deleteTagMappingByClCd(clCd);
        }
        updateMappings(clCd, outsourcing.getMbrCd(), categoryCodes, tags);
        
        outsourcingMapper.deleteOutsourcingPortfolioByOsCd(outsourcing.getOsCd());
        if (portfolioCds != null && !portfolioCds.isEmpty()) {
            for (String prtfCd : portfolioCds) {
                String latestOpCd = outsourcingMapper.findLatestOpCd();
                int nextNum = (latestOpCd == null) ? 1 : Integer.parseInt(latestOpCd.substring(4)) + 1;
                String newOpCd = String.format("OP_C%05d", nextNum);
                
                // ✅ outsourcing 객체가 아닌 outsourcingMapper 객체로 메서드를 호출하도록 수정
                outsourcingMapper.insertOutsourcingPortfolio(newOpCd, outsourcing.getOsCd(), prtfCd, outsourcing.getEntCd());
            }
        }
    }

    @Override
    @Transactional
    public void deleteOutsourcing(String osCd) {
        String clCd = outsourcingMapper.findClCdByOsCd(osCd);
        
        if (clCd != null) {
            outsourcingMapper.deleteBookmarkByClCd(clCd);
            outsourcingMapper.deleteOutsourcingStatusByClCd(clCd);
            outsourcingMapper.deleteOutsourcingContractDetailsByClCd(clCd);
            outsourcingMapper.deleteRankingByClCd(clCd);
            outsourcingMapper.deleteTodayViewByClCd(clCd);
            outsourcingMapper.deleteTotalViewByClCd(clCd);
            outsourcingMapper.deleteCategoryMappingByClCd(clCd);
            outsourcingMapper.deleteTagMappingByClCd(clCd);
            outsourcingMapper.deleteOutsourcingPortfolioByOsCd(osCd); // ✅ 외주-포폴 연결 정보도 삭제
            outsourcingMapper.deleteContentListByClCd(clCd);
        }
        
        outsourcingMapper.deleteOutsourcingByOsCd(osCd);
    }
    
    private void updateMappings(String clCd, String mbrCd, List<String> categoryCodes, String tags) {
        if (categoryCodes != null) {
            for (String ctgryCd : categoryCodes) {
                if(ctgryCd != null && !ctgryCd.trim().isEmpty()) {
                    outsourcingMapper.insertCategoryMapping(ctgryCd, clCd, mbrCd);
                }
            }
        }
        
        if (tags != null && !tags.trim().isEmpty()) {
            for (String tagName : tags.split(",")) {
                String trimmedTagName = tagName.trim();
                if (trimmedTagName.isEmpty()) continue;

                String tagCd = outsourcingMapper.findTagCdByName(trimmedTagName);
                if (tagCd == null) {
                    String latestTagCd = outsourcingMapper.findLatestTagCd();
                    int nextNum = (latestTagCd == null) ? 1 : Integer.parseInt(latestTagCd.substring(4)) + 1;
                    tagCd = String.format("T_C%05d", nextNum);
                    outsourcingMapper.insertTag(tagCd, trimmedTagName, mbrCd);
                }
                outsourcingMapper.insertTagMapping(tagCd, clCd, mbrCd);
            }
        }
    }
    
    @Override
    public List<String> searchTags(String query){
        return outsourcingMapper.searchTagsByName(query);
    }
}