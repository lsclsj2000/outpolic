package outpolic.user.login.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import outpolic.common.domain.Member;

@Mapper
public interface UserLoginMapper {
	Member findMemberById(@Param("memberId") String memberId);
}
