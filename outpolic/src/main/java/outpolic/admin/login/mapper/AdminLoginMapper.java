package outpolic.admin.login.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import outpolic.common.domain.Member;

@Mapper
public interface AdminLoginMapper {
	// 입력한 아이디의 유저 데이터 가져오기
	Member findAdminMemberById(@Param("memberId") String memberId);
	
	//로그인 유저의 마지막 로그인 날짜 업데이트
	int updateAdminMemberLoginDate(Member member);
}
