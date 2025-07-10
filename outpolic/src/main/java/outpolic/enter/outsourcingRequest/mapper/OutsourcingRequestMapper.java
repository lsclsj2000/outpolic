package outpolic.enter.outsourcingRequest.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import outpolic.enter.outsourcingRequest.domain.OutsourcingRequest;

@Mapper
public interface OutsourcingRequestMapper {
	/**
	 * 가장 마지막으로 생성된 요청 코드(PK)를 조회 (새로운 코드 생성을 위함)
	 */
	String findLatestOcdCd();
	
	/**
	 * 이 '신청' 건 자체를 하나의 콘텐츠로 관리하기 위해 content_list에 등록 
	 */
	int insertContentListForRequest(String clCd, String ocdCd);
	
	/**
	 * 사용자가 작성한 외주 신청 내역을 DB에 저장 
	 */
	int insertOutsourcingRequest(OutsourcingRequest request);
	
	/**
	 * 특정 기업이 받은 모든 외주 요청 목록을 조회합니다.
	 * @param entCd 현재 로그인한 기업의 코드
	 * @return 받은 외주 요청 목록
	 */
	List<OutsourcingRequest> findReceivedRequestsByEntCd(String entCd);
	
	/**
	 * 특정 회원이 요청한 모든 외주 목록을 조회합니다.
	 * @param mbrCd 현재 로그인한 회원의 코드
	 * @return 보낸 외주 요청 목록
	 */
	List<OutsourcingRequest> findSentRequestsByMbrCd(String mbrCd);
	
	/**
	 * 요청 코드(ocdCd)로 특정 요청의 모든 상세 정보를 조회합니다.
	 */
	OutsourcingRequest findRequestByOcdCd(String ocdCd);
	
	/**
	 * 특정 요청의 상태를 변경합니다.
	 */
	int updateRequestStatus(@Param("ocdCd") String ocdCd,@Param("stcCd") String stcCd);
}
