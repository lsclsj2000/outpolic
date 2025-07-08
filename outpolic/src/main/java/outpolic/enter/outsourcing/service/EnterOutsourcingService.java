package outpolic.enter.outsourcing.service;

import java.io.IOException;
import java.util.List;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;

public interface EnterOutsourcingService {
    
    /**
     * 특정 기업의 외주 목록 조회
     */
    List<EnterOutsourcing> getOutsourcingListByEntCd(String entCd);
    
    /**
     * 특정 외주 상세 정보 조회
     */
    EnterOutsourcing getOutsourcingByOsCd(String osCd);

    /**
     * 태그 이름으로 태그 검색 (추천 기능용)
     */
    List<String> searchTags(String query);
    
    /**
     * 새로운 외주 등록 (관련 포트폴리오 포함)
     */
    void addOutsourcing(EnterOutsourcing outsourcing, List<String> categoryCodes, String tags, List<String> portfolioCds) throws IOException;
    
    /**
     * 기존 외주 정보 수정 (관련 포트폴리오 포함)
     */
    void updateOutsourcing(EnterOutsourcing outsourcing, List<String> categoryCodes, String tags, List<String> portfolioCds) throws IOException;

    /**
     * 외주 삭제
     */
    void deleteOutsourcing(String osCd);
    
}