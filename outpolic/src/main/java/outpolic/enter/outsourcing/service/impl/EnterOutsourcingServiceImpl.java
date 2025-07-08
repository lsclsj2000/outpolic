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

/**
 * @Service:이 클래스가 비즈니스 로직을 처리하는 서비스 계층의 컴포넌트임을 Spring에게 알려줍니다. 
 * @RequiredArgsConstructor:final로 선언된 필드들을 위한 생성자를 자동으로 만들어줍니다.(생성자 주입)
 */

@Service
@RequiredArgsConstructor
public class EnterOutsourcingServiceImpl implements EnterOutsourcingService {
	
	// 데이터 베이스와 통신하기 위한 매퍼 인터페이스들 
    private final OutsourcingMapper outsourcingMapper;
    private final PortfolioMapper portfolioMapper;
    
    /**
     * 특정 기업이 등록한 모든 외주 목록을 조회합니다.
     */
    @Override
    public List<EnterOutsourcing> getOutsourcingListByEntCd(String entCd) {
        return outsourcingMapper.findOutsourcingDetailsByEntCd(entCd);
    }
    
    /**
     * 외주 코드(PK)를 이용해 특정 외주 한 건의 상세 정보를 조회합니다. 
     */
    @Override
    public EnterOutsourcing getOutsourcingByOsCd(String osCd) {
        return outsourcingMapper.findOutsourcingDetailsByOsCd(osCd);
    }
    /**
     * 새로운 외주를 등록하고, 관련 카테고리/태그/포트폴리오를 연결합니다.
     * @Transactional: 이 메서드 안의 모든 DB작업이 하나의 묶음으로 처리됩니다.
     * 중간에 하나라도 실패하면 모든 작업이 등록 전 상태로 되돌아갑니다.(데이터 일관성 보장)
     */
    @Override
    @Transactional
    public void addOutsourcing(EnterOutsourcing outsourcing, List<String> categoryCodes, String tags, List<String> portfolioCds) throws IOException {
        // 1. 비즈니스 규칙 검사: 외주를 등록하려면 포트폴리오가 최소 1개 있어야 합니다.
    	int portfolioCount = portfolioMapper.countPortfoliosByEntCd(outsourcing.getEntCd());
        if (portfolioCount == 0) {
            throw new IllegalStateException("외주를 등록하려면 먼저 포트폴리오를 1개 이상 등록해야 합니다.");
        }
        
        // 2. 새로운 외주를 위한 고유 코드(PK)를 생성합니다. (예: OS_C00001)
        String latestOsCd = outsourcingMapper.findLatestOsCd();
        int nextNum = (latestOsCd == null) ? 1 : Integer.parseInt(latestOsCd.substring(4)) + 1;
        String newOsCd = String.format("OS_C%05d", nextNum);
        outsourcing.setOsCd(newOsCd);
        
        // 3. DB 제약조건(NOT NULL)을 위해, 선택된 카테고리 중 첫 번째를 대표 카테고리로 지정합니다.
        if (categoryCodes != null && !categoryCodes.isEmpty()) {
            outsourcing.setCtgryId(categoryCodes.get(0));
        } else {
            throw new IllegalStateException("최소 하나 이상의 카테고리를 선택해야 합니다.");
        }
        
        // 4. 외주 기본 정보를 `outsourcing` 테이블에 저장합니다.
        outsourcingMapper.insertOutsourcing(outsourcing);
        
        // 5. 이 외주를 전체 콘텐츠의 일부로 관리하기 위해 `content_list` 테이블에 등록합니다.
        String newClCd = "LIST_" + newOsCd;
        outsourcingMapper.insertContentList(newClCd, newOsCd);

        // 6. 카테고리 및 태그 연결 정보를 저장합니다. (공통 로직 호출)
        updateMappings(newClCd, outsourcing.getMbrCd(), categoryCodes, tags);
        
        // 7. 함께 등록된 관련 포트폴리오 연결 정보를 저장합니다.
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
    
    /**
     * 기존 외주 정보를 수정하고, 관련 연결 정보들을 모두 새로 업데이트합니다.
     */
    @Override
    @Transactional
    public void updateOutsourcing(EnterOutsourcing outsourcing, List<String> categoryCodes, String tags, List<String> portfolioCds) throws IOException {
        // 1.수정일시를 현재 시간으로 설정합니다.
    	outsourcing.setOsMdfcnYmdt(LocalDateTime.now());
    	
    	// 2. 대표 카테고리를 업데이트합니다.
        if (categoryCodes != null && !categoryCodes.isEmpty()) {
            outsourcing.setCtgryId(categoryCodes.get(0));
        } else {
            throw new IllegalStateException("업데이트 시 최소 하나 이상의 카테고리를 선택해야 합니다.");
        }
        
        // 3. 변경된 외주 기본 정보를 DB에 업데이트합니다.
        outsourcingMapper.updateOutsourcing(outsourcing);
        
        // 4. 이후의 매핑 작업을 위해 외주에 연결된 콘텐츠 코드(cl_cd)를 조회합니다.
        String clCd = outsourcingMapper.findClCdByOsCd(outsourcing.getOsCd());
        
        // 5. "클린 업데이트(Clean Update)" 전략: 기존 연결 정보를 모두 삭제하고 새로 받은 정보로 다시 만듭니다.
        // 5-1. 기존 카테고리/태그 연결 삭제 
        if (clCd != null) {
            outsourcingMapper.deleteCategoryMappingByClCd(clCd);
            outsourcingMapper.deleteTagMappingByClCd(clCd);
        }
        // 5-2. 새로운 카테고리/태그 정보로 다시 저장 
        updateMappings(clCd, outsourcing.getMbrCd(), categoryCodes, tags);
        
        // 5-3. 기존 포트폴리오 연결 삭제 후 새로 저장 
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
    
    /**
     * 외주 정보를 삭제합니다. 관련된 모든 자식 데이터들을 먼저 삭제해야 합니다.
     * (삭제 순서가 매우 중요!)
     */
    @Override
    @Transactional
    public void deleteOutsourcing(String osCd) {
        String clCd = outsourcingMapper.findClCdByOsCd(osCd);
        
        if (clCd != null) {
        	// 다른 테이블에서 이 글을 참조하고 있는 모든 정보들을 순서대로 삭제 
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
        
        // 마지막으로 외주 원본 글을 삭제
        outsourcingMapper.deleteOutsourcingByOsCd(osCd);
    }
    /**
     * 등록/수정 시 중복되는 카테고리 및 태그 저장 로직을 처리하는 내부 헬퍼 메서드
     */
    private void updateMappings(String clCd, String mbrCd, List<String> categoryCodes, String tags) {
    	// 카테고리 연결 정보 저장 
    	if (categoryCodes != null) {
            for (String ctgryCd : categoryCodes) {
                if(ctgryCd != null && !ctgryCd.trim().isEmpty()) {
                    outsourcingMapper.insertCategoryMapping(ctgryCd, clCd, mbrCd);
                }
            }
        }
        // 태그 연결 정보 저장 
        if (tags != null && !tags.trim().isEmpty()) {
            for (String tagName : tags.split(",")) {
                String trimmedTagName = tagName.trim();
                if (trimmedTagName.isEmpty()) continue;
                // 이미 존재하는 태그인지 확인
                String tagCd = outsourcingMapper.findTagCdByName(trimmedTagName);
                if (tagCd == null) {
                	// 없는 태그라면 새로 생성 
                    String latestTagCd = outsourcingMapper.findLatestTagCd();
                    int nextNum = (latestTagCd == null) ? 1 : Integer.parseInt(latestTagCd.substring(4)) + 1;
                    tagCd = String.format("T_C%05d", nextNum);
                    outsourcingMapper.insertTag(tagCd, trimmedTagName, mbrCd);
                }
                // 최종적으로 콘텐츠와 태그를 연결 
                outsourcingMapper.insertTagMapping(tagCd, clCd, mbrCd);
            }
        }
    }
    /**
     * 태그 추천 기능을 위해 이름으로 태그를 검색합니다. 
     */
    @Override
    public List<String> searchTags(String query){
        return outsourcingMapper.searchTagsByName(query);
    }
}