package outpolic.user.outsourcing.service;

import java.util.List;

import org.springframework.stereotype.Service;

import outpolic.user.outsourcing.dto.UserOsInfoDTO;

@Service
public interface UserOutsourcingService {
	 //진행중인 외주 가져오기
	List<UserOsInfoDTO>  UserOsIngSelectByCode(String memberCode);
}
