package outpolic.enter.mypage.service.Impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.enter.mypage.dto.EnterInfo;
import outpolic.enter.mypage.dto.EnterpriseInfo;
import outpolic.enter.mypage.mapper.EnterMypageMapper;
import outpolic.enter.mypage.mapper.EnterpriseMapper;
import outpolic.enter.mypage.service.EnterMypageService;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnterMypageServiceImpl implements EnterMypageService {
	
	private final EnterMypageMapper enterMypageMapper;
	private final EnterpriseMapper enterpriseMapper;
	
	// 개인정보 호출
	@Override
	public EnterInfo getEnterInfoById(String memberId) {

		return enterMypageMapper.getEnterInfoById(memberId);
	}

	//개인정보 수정
	@Override
	public void editEnterInfo(EnterInfo enterInfo) {
		log.info("수정요청 들어온 데이터 : {}", enterInfo);
		int result = enterMypageMapper.updateEnterInfo(enterInfo);
		log.info("수정 완료된 데이터는 {}건 있습니다", result);
	}
	// 중복 검사
	@Override
	public boolean isEnterInfoDuple(String type, String memberId, String memberNickname, String memberEmail,
			String memberTelNo) {	
		switch (type) { 
        case "memberNickname":
            return enterMypageMapper.isNickNameDuplicated(memberNickname, memberId);
        case "memberEmail":
            return enterMypageMapper.isEmailDuplicated(memberEmail, memberId);
        case "memberTelNo":
            return enterMypageMapper.isTelDuplicated(memberTelNo, memberId);
        default:
            return false;
		}
	}
	
	// 기업정보 호출
	@Override
	public EnterpriseInfo getEnterpriseInfoByCode(String memberCode) {
	
		return enterpriseMapper.getEnterpriseInfoByCode(memberCode);
	}

	//기업정보 수정
	@Override
	public void editEnterpriseInfo(EnterpriseInfo enterpriseInfo) {
		log.info("수정요청 들어온 데이터 : {}", enterpriseInfo);
		int result = enterpriseMapper.updateEnterpriseInfo(enterpriseInfo);
		log.info("수정 완료된 데이터는 {}건 있습니다", result);	
		
	}

	

}
