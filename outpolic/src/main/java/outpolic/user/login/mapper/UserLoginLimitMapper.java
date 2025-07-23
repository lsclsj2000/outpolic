package outpolic.user.login.mapper;

import org.apache.ibatis.annotations.Mapper;

import outpolic.user.login.dto.LoginFailDTO;

@Mapper
public interface UserLoginLimitMapper {
	  LoginFailDTO getLimitInfoByMemberCode(String memberCode);
}
