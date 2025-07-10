package outpolic.enter.register.service.Impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import outpolic.enter.mypage.dto.CorpInfo;
import outpolic.enter.register.mapper.EnterRegisterMapper;
import outpolic.enter.register.service.EnterRegisterService;

@Service
@RequiredArgsConstructor
public class EnterRegisterServiceImpl implements EnterRegisterService{
	
	private final EnterRegisterMapper enterRegisterMapper;
	
	@Override
	public String getNextEnterCode() {
		
		return enterRegisterMapper.getNextEnterCode();
	}

	
	@Override
	@Transactional
	public int enterRegister(CorpInfo corpInfo) {
		int registerResult = enterRegisterMapper.enterRegister(corpInfo);
		int updateCdResult = enterRegisterMapper.userEnterCdUpdate(corpInfo);
		System.out.println("등록 요청한 회원코드: " + corpInfo.getMemberCode());
		if(registerResult==0 || updateCdResult==0) {
			throw new RuntimeException("기업 등록 또는 회원 등급 변경 실패");
		}
		return registerResult + updateCdResult;
	}


	@Override
	public int isBrnoDuplicated(String corpBrno) {
		return enterRegisterMapper.isBrnoDuplicated(corpBrno);
	}

}
