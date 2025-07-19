package outpolic.user.outsourcing.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.user.outsourcing.dto.UserOsInfoDTO;
import outpolic.user.outsourcing.mapper.UserOutsourcingMapper;
import outpolic.user.outsourcing.service.UserOutsourcingService;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserOutsourcingServiceImpl implements UserOutsourcingService {

	private final UserOutsourcingMapper userOutsourcingMapper;
	
	@Override
	public List<UserOsInfoDTO> UserOsIngSelectByCode(String memberCode) {
	
		return userOutsourcingMapper.UserOsIngSelectByCode(memberCode);
	}

}
