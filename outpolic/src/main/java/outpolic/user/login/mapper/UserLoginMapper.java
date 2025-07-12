package outpolic.user.login.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import outpolic.common.domain.Member;

@Mapper
public interface UserLoginMapper {

	//입력된 아이디의 유저 데이터 가져오기

	Member findMemberById(@Param("memberId") String memberId);

	
	// 로그인 유저의 마지막 로그인 날짜 업데이트
	int updateMemberLoginDate(Member member);
	
	// 로그인 기록 테이블에 데이터 insert
	void insertLoginHistory(@Param("loginHistoryCode") String loginHistoryCode,
							@Param("memberCode") String memberCode,
							@Param("loginIp") String loginIp);
	
	// 로그인 실패 이력 테이블에 데이터 insert
	void insertLoginFailHistory(@Param("loginId") String loginId,
			@Param("ip") String ip,
			@Param("reason") String reason);
	
	// 로그인기록테이블 기본키 생성
	String getNextLoginHistoryCode();
	
	// 회원의 가장 마지막 로그인 기록 가져오기
	String getLastLoginCode(@Param("memberCode") String memberCode);
	
	//로그아웃 시간 업데이트
	int updateLogoutHistory(@Param("loginHistoryCode") String loginHistoryCode);

}
