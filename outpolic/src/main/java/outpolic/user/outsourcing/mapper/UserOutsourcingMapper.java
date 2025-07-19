package outpolic.user.outsourcing.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import outpolic.user.outsourcing.dto.UserOsInfoDTO;
@Mapper
public interface UserOutsourcingMapper {
	 // 진행중인 외주 가져오기
	List<UserOsInfoDTO> UserOsIngSelectByCode(String memberCode);
}
