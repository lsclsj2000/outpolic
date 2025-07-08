package outpolic.enter.outsourcingRequest.mapper;

import org.apache.ibatis.annotations.Mapper;

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
}
