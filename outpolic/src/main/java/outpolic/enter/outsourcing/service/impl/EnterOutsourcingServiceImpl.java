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
import outpolic.enter.portfolio.mapper.PortfolioMapper; // PortfolioMapper가 필요한 경우 (예: countPortfoliosByEntCd)

@Service
@RequiredArgsConstructor
public class EnterOutsourcingServiceImpl implements EnterOutsourcingService {

    private final OutsourcingMapper outsourcingMapper;
    private final PortfolioMapper portfolioMapper; // 포트폴리오 개수 확인을 위해 필요

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
        // 포트폴리오 등록 여부 확인 (비즈니스 규칙)
        int portfolioCount = portfolioMapper.countPortfoliosByEntCd(outsourcing.getEntCd());
        if (portfolioCount == 0) {
            throw new IllegalStateException("외주를 등록하려면 먼저 포트폴리오를 1개 이상 등록해야 합니다.");
        }

        // 새로운 외주 코드 생성 (OS_C00001 형식)
        String latestOsCd = outsourcingMapper.findLatestOsCd();
        int nextNum = (latestOsCd == null) ? 1 : Integer.parseInt(latestOsCd.substring(4)) + 1;
        String newOsCd = String.format("OS_C%05d", nextNum);
        outsourcing.setOsCd(newOsCd);
        
        // 대표 카테고리 ID 설정 (DB의 ctgry_id NOT NULL 제약 조건 충족)
        if (categoryCodes != null && !categoryCodes.isEmpty()) {
            outsourcing.setCtgryId(categoryCodes.get(0));
        } else {
            // 클라이언트 HTML에서 required 속성을 추가했지만, 서버 측 방어 로직으로 유지
            throw new IllegalStateException("최소 하나 이상의 카테고리를 선택해야 합니다.");
        }
        
        // Mybatis XML에서 NOW()로 설정하지만 DTO에 설정할 수도 있습니다.
        // outsourcing.setOsRegYmdt(LocalDateTime.now()); 

        // 외주 기본 정보 삽입
        outsourcingMapper.insertOutsourcing(outsourcing);
        
        // content_list에 외주 정보 등록 (콘텐츠 목록 코드 생성)
        String newClCd = "LIST_" + newOsCd; // 예: LIST_OS_C00001
        outsourcingMapper.insertContentList(newClCd, newOsCd); // cl_cd, cntd_cd (osCd)

        // 카테고리 및 태그 매핑 처리
        updateMappings(newClCd, outsourcing.getMbrCd(), categoryCodes, tags);
    }

    @Override
    @Transactional
    public void updateOutsourcing(EnterOutsourcing outsourcing, List<String> categoryCodes, String tags) throws IOException {
        outsourcing.setOsMdfcnYmdt(LocalDateTime.now());
        // admCd는 컨트롤러에서 설정하거나 세션에서 가져와야 함 (여기서 직접 설정하지 않음)
        // outsourcing.setAdmCd(세션에서 가져온 관리자 코드);

        // 업데이트 시에도 대표 카테고리 ID를 업데이트
        if (categoryCodes != null && !categoryCodes.isEmpty()) {
            outsourcing.setCtgryId(categoryCodes.get(0));
        } else {
            throw new IllegalStateException("업데이트 시 최소 하나 이상의 카테고리를 선택해야 합니다.");
        }

        // 외주 기본 정보 업데이트
        outsourcingMapper.updateOutsourcing(outsourcing);

        // 해당 외주(콘텐츠)의 clCd 조회
        String clCd = outsourcingMapper.findClCdByOsCd(outsourcing.getOsCd());

        // 기존 매핑 정보 삭제 후 새로 추가 (클린 업데이트 전략)
        if (clCd != null) {
            outsourcingMapper.deleteCategoryMappingByClCd(clCd);
            outsourcingMapper.deleteTagMappingByClCd(clCd);
            // 만약 os_rcrmt_end_ymdt 컬럼 추가 시 필요하다면 여기에 추가
            // outsourcingMapper.deleteOsRcrmtMappingByClCd(clCd);
        }
        updateMappings(clCd, outsourcing.getMbrCd(), categoryCodes, tags);
    }

    @Override
    @Transactional // 트랜잭션으로 묶어 데이터 일관성 보장
    public void deleteOutsourcing(String osCd) {
        String clCd = outsourcingMapper.findClCdByOsCd(osCd); // 삭제할 외주의 cl_cd 조회
        
        if (clCd != null) {
            // ✨✨✨ 외래 키 제약 조건으로 인해 삭제 순서가 매우 중요합니다. ✨✨✨
            // 자식 테이블의 데이터를 먼저 삭제해야 부모 테이블의 데이터를 삭제할 수 있습니다.
            
            // 1. bookmark 테이블에서 해당 cl_cd를 참조하는 레코드 삭제 (가장 최우선)
            //    -> 로그에 FK 에러가 발생한 지점
            outsourcingMapper.deleteBookmarkByClCd(clCd); // 이 부분 추가
            
            // 1.1 outsourcing_status 테이블에서 해당 cl_cd를 참조하는 레코드 삭제 
            // -> outsourcingMapper.deleteOutsourcingStatusByClCd(clCd);
            outsourcingMapper.deleteOutsourcingStatusByClCd(clCd);
            
            // 1.1 outsourcing_contract_details 테이블에서 해당 cl_cd를 참조한느 레코드 삭제
            outsourcingMapper.deleteOutsourcingContractDetailsByClCd(clCd);
            
            // 1.2 ranking 테이블에서 해당 cl_cd를 참조하는 레코드 삭제 (새로 추가)
            outsourcingMapper.deleteRankingByClCd(clCd);
            
            // 1.3 today_view 테이블에서 해당 cl_cd를 참조하는 레코드 삭제 
            outsourcingMapper.deleteTodayViewByClCd(clCd);
            
            // 1.4
            outsourcingMapper.deleteTotalViewByClCd(clCd);

            // 2. category_mapping, tag_mapping 테이블에서 관련 레코드 삭제
            outsourcingMapper.deleteCategoryMappingByClCd(clCd);
            outsourcingMapper.deleteTagMappingByClCd(clCd);
            
            // 3. content_list 테이블에서 관련 레코드 삭제 (다른 모든 참조 제거 후)
            outsourcingMapper.deleteContentListByClCd(clCd);
        }
        // 4. outsourcing 테이블에서 외주 자체 삭제 (모든 종속 레코드 제거 후)
        outsourcingMapper.deleteOutsourcingByOsCd(osCd);
    }
    
    // 카테고리 및 태그 매핑을 처리하는 private 헬퍼 메서드
    private void updateMappings(String clCd, String mbrCd, List<String> categoryCodes, String tags) {
        // 카테고리 매핑
        if (categoryCodes != null) {
            for (String ctgryCd : categoryCodes) {
                if(ctgryCd != null && !ctgryCd.trim().isEmpty()) {
                    outsourcingMapper.insertCategoryMapping(ctgryCd, clCd, mbrCd);
                }
            }
        }
        // 태그 매핑
        if (tags != null && !tags.trim().isEmpty()) {
            for (String tagName : tags.split(",")) {
                String trimmedTagName = tagName.trim();
                if (trimmedTagName.isEmpty()) continue;

                // 기존 태그인지 확인
                String tagCd = outsourcingMapper.findTagCdByName(trimmedTagName);
                if (tagCd == null) {
                    // 새 태그인 경우 코드 생성 및 삽입 (T_C00001 형식)
                    String latestTagCd = outsourcingMapper.findLatestTagCd();
                    int nextNum = (latestTagCd == null) ? 1 : Integer.parseInt(latestTagCd.substring(4)) + 1;
                    tagCd = String.format("T_C%05d", nextNum);
                    outsourcingMapper.insertTag(tagCd, trimmedTagName, mbrCd);
                }
                // 태그 매핑 삽입
                outsourcingMapper.insertTagMapping(tagCd, clCd, mbrCd);
            }
        }
    }
}