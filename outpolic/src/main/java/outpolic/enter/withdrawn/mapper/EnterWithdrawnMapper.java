package outpolic.enter.withdrawn.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import outpolic.enter.withdrawn.dto.EnterOsInfoDTO;
import outpolic.enter.withdrawn.dto.EnterWithdrawnDTO;

@Mapper
public interface EnterWithdrawnMapper {
	// 회원조회
	EnterWithdrawnDTO getWithdrawnEnterMemberInfoByCode(String memberCode);
	
	//권한삭제
	void deleteEnterAuthorityByCode(String memberCode);
	//북마크삭제
	void deleteEnterBookmarkByCode(String memberCode);
	//마일리지 내역 날리기
	void deleteEnterPointByCode(String memberCode);
	//구독권 보유 회원 목록에서 날리기
	void deleteEnterSubscriptionByCode(String memberCode);
	//이용권 보유 회원 목록에서 날리기
	void deleteEnterTicketByCode(String memberCode);
	
	//회원 상태 변경하기
	int updateEnterSttToWithdrawn(String memberCode);
	
	//탈퇴회원 테이블 기본키 생성
	String getNextWithdrawnCode();
	//탈퇴회원 활동일수 계산
	int getEnterMemberActiveDays(String memberCode);
	//탈퇴회원 테이블에 데이터 삽입
	int insertWithMember(EnterWithdrawnDTO enterWithdrawnDTO);
	
	// 회원 진행중 외주 목록 가져오기
	List<EnterOsInfoDTO> EnterOsIngSelectByCode(String memberCode);
}
