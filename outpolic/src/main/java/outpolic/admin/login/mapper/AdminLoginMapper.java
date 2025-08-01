package outpolic.admin.login.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import outpolic.admin.login.dto.AdminLoginDTO;
import outpolic.common.domain.Member;

@Mapper
public interface AdminLoginMapper {
	// 입력한 아이디의 유저 데이터 가져오기
	AdminLoginDTO findAdminMemberById(@Param("memberId") String memberId);
	
	// 로그인한 관리자의 권한 가져오기
	List<String> getAdminPermissions(String adminCode);
	
	//로그인 유저의 마지막 로그인 날짜 업데이트
	int updateAdminMemberLoginDate(Member member);
	
	//로그인 기록 테이블 기본키 생성
	String getNextAdminLoginHistoryCode();
	
	//로그인 기록 테이블에 데이터 삽입
	void insertAdminLoginHistory(@Param("loginHistoryCode") String loginHistoryCode,
			@Param("memberCode") String memberCode,
			@Param("loginIp") String loginIp);
	
	// 회원의 가장 마지막 로그인 기록 가져오기
	String getAdminLastLoginCode(@Param("memberCode") String memberCode);
	
	//로그아웃 시간 업데이트
	int updateAdminLogoutHistory(@Param("loginHistoryCode") String loginHistoryCode);
}
