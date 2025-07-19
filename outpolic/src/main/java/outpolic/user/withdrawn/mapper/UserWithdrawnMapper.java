package outpolic.user.withdrawn.mapper;

import org.apache.ibatis.annotations.Mapper;

import outpolic.user.withdrawn.dto.UserWithdrawnDTO;

@Mapper
public interface UserWithdrawnMapper {
	//해당회원 조회
	UserWithdrawnDTO getWithdrawnMemberInfoByCode(String memberCode);
	// 권한 삭제
	void deleteAuthorityByCode(String memberCode);
	// 북마크 삭제
	void deleteBookmarkByCode(String memberCode);
	// 마일리지 내역 삭제
	void deletePointByCode(String memberCode);
	// 구독권 보유 회원 목록에서 삭제
	void deleteSubscriptionByCode(String memberCode);
	// 이용권 보유 회원 목록에서 삭제
	void deleteTicketByCode(String memberCode);
	// 회원 상태 변경
	int updateMemberSttToWithdrawn(String memberCode);
	// 탈퇴회원 테이블 기본키 생성
	String getNextWithdrawnCode();
	// 탈퇴회원 활동일수 계산
	int getMemberActiveDays(String memberCode);
	// 탈퇴회원 테이블에 데이터 삽입
	int insertWithMember(UserWithdrawnDTO userWithdrawnDTO);
}
