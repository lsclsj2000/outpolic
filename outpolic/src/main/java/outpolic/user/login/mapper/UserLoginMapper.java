package outpolic.user.login.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import outpolic.common.domain.Member;

@Mapper
public interface UserLoginMapper {
	//로그인 회원 찾아오기
	Member findMemberById(@Param("memberId") String memberId);
	//로그인시 기록테이블에 데이터 insert
	void insertLoginHistory(@Param("memberId") String memberId);
}
