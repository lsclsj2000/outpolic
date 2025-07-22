package outpolic.user.osst.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import outpolic.user.osst.domain.UserOsst;
import outpolic.user.osst.domain.UserOsstRecord;
import outpolic.user.review.dto.UserOutsourcingReviewDTO;


@Mapper
public interface UserOsstMapper {
	
	// 단계 승인
	int updateStepApproval(@Param("ospCd") String ospCd);
	
	// osr_cd 자동생성
	String getNextOsrCode();
	
	// 보고 피드백 제출
	int insertRecord(UserOsstRecord record);
	
	// 진행 외주 상세 조회
	UserOsst getUserOsstDetail(@Param("osstDetailCode") String osstDetailCode, @Param("memberCode") String memberCode);
	
	// 진행 외주 목록 조회
	List<UserOsst> getUserOsstList(String memberCode);
	
	// 진행 외주 단계 조회
	List<UserOsst> getUserOsstStcCode(@Param("ocdCd") String ocdCd);
	
	// 외주 기록 조회 (ocdCd 기준으로 조회하는 메서드 이름 변경)
	List<UserOsstRecord> getOutsourcingRecordsByOcdCd(@Param("ocdCd") String ocdCd);
	
	// 김한별 추가 영역
	
	// 신규 리뷰 저장을 위한 메소드
    int insertReview(UserOutsourcingReviewDTO reviewDTO);

    // 신규 리뷰 코드를 생성하기 위한 메소드
    String getNextReviewCode();
    
    // 외주 계약 코드로 기업 코드를 조회하기 위한 메소드
    String getEnterpriseCodeByOscId(String oscId);
    
    // 외주계약 ID와 작성자 회원 코드로 작성된 리뷰 정보를 조회
    UserOutsourcingReviewDTO findReviewByOscIdAndMbrCd(Map<String, Object> params);
    
    // 기존 리뷰의 내용을 수정
    int updateReview(UserOutsourcingReviewDTO reviewDTO);
}