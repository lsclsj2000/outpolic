package outpolic.enter.withdrawn.service;

import java.util.List;

import org.springframework.stereotype.Service;

import outpolic.enter.withdrawn.dto.EnterOsInfoDTO;

@Service
public interface EnterWithdrawnService {
	
	// 탈퇴회원 정보
	void withdrawnEnterMember(String memberCode, String reason);
	
	//진행중 외주 가져오기
	List<EnterOsInfoDTO> EnterOsIngSelectByCode(String memberCode);
}
