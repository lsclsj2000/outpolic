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
import outpolic.enter.portfolio.mapper.PortfolioMapper; // PortfolioMapper 계속 필요
import outpolic.enter.portfolio.domain.EnterPortfolio; // EnterPortfolio import 추가

@Service
@RequiredArgsConstructor
public class EnterOutsourcingServiceImpl implements EnterOutsourcingService {

    private final OutsourcingMapper outsourcingMapper;
    private final PortfolioMapper portfolioMapper; // 포트폴리오 관련 DB 작업에 사용

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
    public void addOutsourcing(EnterOutsourcing outsourcing, List<String> categoryCodes, String tags) throws IOException { // portfolioCds 파라미터 제거
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

        // 7. 함께 등록된 관련 포트폴리오 연결 정보를 저장하는 로직 제거 (분리됨)
        // 기존 코드:
        // if (portfolioCds != null && !portfolioCds.isEmpty()) { ... }
    }

    @Override
    @Transactional
    public void updateOutsourcing(EnterOutsourcing outsourcing, List<String> categoryCodes, String tags) throws IOException { // portfolioCds 파라미터 제거
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

        // 5-3. 기존 포트폴리오 연결 삭제 및 새로 저장하는 로직 제거 (분리됨)
        // outsourcingMapper.deleteOutsourcingPortfolioByOsCd(outsourcing.getOsCd());
        // if (portfolioCds != null && !portfolioCds.isEmpty()) { ... }
    }

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
            outsourcingMapper.deleteOutsourcingPortfolioByOsCd(osCd); // 외주-포폴 연결 정보도 삭제
            outsourcingMapper.deleteContentListByClCd(clCd);
        }

        // 마지막으로 외주 원본 글을 삭제
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

    // --- 외주-포트폴리오 연결을 위한 새로운 서비스 메서드 구현 (추가) ---

    @Override
    public List<EnterPortfolio> getLinkedPortfoliosByOsCd(String osCd) {
        // PortfolioMapper에 osCd로 연결된 포트폴리오를 찾는 메서드가 필요
        return portfolioMapper.findLinkedPortfoliosByOsCd(osCd);
    }

    @Override
    public List<EnterPortfolio> searchUnlinkedPortfolios(String osCd, String entCd, String query) {
        // PortfolioMapper에 osCd에 연결되지 않은 포트폴리오를 검색하는 메서드가 필요
        return portfolioMapper.findUnlinkedPortfolios(osCd, entCd, query);
    }

    @Override
    @Transactional
    public void linkPortfolioToOutsourcing(String osCd, String prtfCd, String entCd) {
        // 새로운 외주-포폴 연결 코드(PK) 생성
        String latestOpCd = outsourcingMapper.findLatestOpCd(); // OutsourcingMapper 사용
        int nextNum = (latestOpCd == null) ? 1 : Integer.parseInt(latestOpCd.substring(4)) + 1;
        String newOpCd = String.format("OP_C%05d", nextNum);
        
        // outsourcingMapper를 통해 outsourcing_portfolio에 삽입
        // 이 메서드는 PortfolioMapper에도 있었지만, Outsourcing쪽에서 관리하는게 자연스러움
        outsourcingMapper.insertOutsourcingPortfolio(newOpCd, osCd, prtfCd, entCd);
    }

    @Override
    @Transactional
    public void unlinkPortfolioFromOutsourcing(String osCd, String prtfCd) {
        // outsourcingMapper를 통해 outsourcing_portfolio에서 삭제
        outsourcingMapper.unlinkOutsourcingFromPortfolio(osCd, prtfCd);
    }
}