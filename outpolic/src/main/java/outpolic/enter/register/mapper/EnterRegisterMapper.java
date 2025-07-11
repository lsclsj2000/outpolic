package outpolic.enter.register.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import outpolic.enter.mypage.dto.CorpInfo;

@Mapper
public interface EnterRegisterMapper {
	
	//기업 사업자등록번호 중복확인
	int isBrnoDuplicated(String corpBrno);
	// 기업 기본키 만들기
	String getNextEnterCode();
	
	// 기업회원정보 입력
	int enterRegister(CorpInfo corpInfo);
	
	// 회원 등급 업데이트
	int userEnterCdUpdate(CorpInfo corpInfo);
	
}
