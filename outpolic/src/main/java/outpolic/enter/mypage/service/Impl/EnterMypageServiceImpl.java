package outpolic.enter.mypage.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.common.dto.OutsourcingReviewDTO;
import outpolic.enter.mypage.dto.CorpInfo;
import outpolic.enter.mypage.dto.EnterInfo;
import outpolic.enter.mypage.mapper.EnterMypageMapper;
import outpolic.enter.mypage.mapper.EnterpriseMapper;
import outpolic.enter.mypage.service.EnterMypageService;
import outpolic.enter.register.mapper.EnterRegisterMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnterMypageServiceImpl implements EnterMypageService {
	
	private final EnterMypageMapper enterMypageMapper;
	private final EnterpriseMapper enterpriseMapper;
	private final EnterRegisterMapper enterRegisterMapper;
	
	//기업정보입력
	// 기업 정보 입력위해 기업 기본키 생성
	public String getNextEnterCode() {
		return enterRegisterMapper.getNextEnterCode();
	}
	
	
	
	
	// 개인정보 호출
	@Override
	public EnterInfo getEnterInfoByCode(String memberCode) {

		return enterMypageMapper.getEnterInfoByCode(memberCode);
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
	public boolean isEnterInfoDuple(String type, String memberCode, String memberNickname, String memberEmail,
			String memberTelNo) {	
		switch (type) { 
        case "memberNickname":
            return enterMypageMapper.isNickNameDuplicated(memberNickname, memberCode);
        case "memberEmail":
            return enterMypageMapper.isEmailDuplicated(memberEmail, memberCode);
        case "memberTelNo":
            return enterMypageMapper.isTelDuplicated(memberTelNo, memberCode);
        default:
            return false;
		}
	}
	
	// 기업정보 호출
	@Override
	public CorpInfo getEnterpriseInfoByCode(String memberCode) {
	
		return enterpriseMapper.getEnterpriseInfoByCode(memberCode);
	}

	//기업정보 수정
	@Override
	public void editEnterpriseInfo(CorpInfo corpInfo) {
		log.info("수정요청 들어온 데이터 : {}", corpInfo);
		int result = enterpriseMapper.updateEnterpriseInfo(corpInfo);
		log.info("수정 완료된 데이터는 {}건 있습니다", result);	
		
	}




	@Override
	public List<OutsourcingReviewDTO> getOutsourcingReviewList(String memberCode) {
		
		return enterMypageMapper.selectEnterReviewList(memberCode);
	}

	

}
