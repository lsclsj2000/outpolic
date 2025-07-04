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
    public void addOutsourcing(EnterOutsourcing outsourcing, List<String> categoryCodes, String tags) throws IOException {
        int portfolioCount = portfolioMapper.countPortfoliosByEntCd(outsourcing.getEntCd());
        if (portfolioCount == 0) {
            throw new IllegalStateException("외주를 등록하려면 먼저 포트폴리오를 1개 이상 등록해야 합니다.");
        }

        String latestOsCd = outsourcingMapper.findLatestOsCd();
        int nextNum = (latestOsCd == null) ? 1 : Integer.parseInt(latestOsCd.substring(4)) + 1;
        String newOsCd = String.format("OS_C%05d", nextNum);
        outsourcing.setOsCd(newOsCd);
        
        outsourcingMapper.insertOutsourcing(outsourcing);
        
        String newClCd = "LIST_" + newOsCd;
        outsourcingMapper.insertContentList(newClCd, newOsCd);
        
        updateMappings(newClCd, outsourcing.getMbrCd(), categoryCodes, tags);
    }

    @Override
    @Transactional
    public void updateOutsourcing(EnterOutsourcing outsourcing, List<String> categoryCodes, String tags) throws IOException {
        outsourcing.setOsMdfcnYmdt(LocalDateTime.now());
        outsourcingMapper.updateOutsourcing(outsourcing);

        String clCd = outsourcingMapper.findClCdByOsCd(outsourcing.getOsCd());

        // 기존 매핑 정보 삭제 후 새로 추가
        outsourcingMapper.deleteCategoryMappingByClCd(clCd);
        outsourcingMapper.deleteTagMappingByClCd(clCd);
        updateMappings(clCd, outsourcing.getMbrCd(), categoryCodes, tags);
    }

    @Override
    @Transactional
    public void deleteOutsourcing(String osCd) {
        String clCd = outsourcingMapper.findClCdByOsCd(osCd);
        if (clCd != null) {
            outsourcingMapper.deleteCategoryMappingByClCd(clCd);
            outsourcingMapper.deleteTagMappingByClCd(clCd);
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
}