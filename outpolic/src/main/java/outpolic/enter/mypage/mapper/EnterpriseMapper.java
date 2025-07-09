package outpolic.enter.mypage.mapper;

import org.apache.ibatis.annotations.Mapper;

import outpolic.enter.mypage.dto.CorpInfo;


@Mapper
public interface EnterpriseMapper {
	//특정 기업회원 기업정보 가져오기
	CorpInfo getEnterpriseInfoByCode(String memberCode);
	
	//특정 기업회원 기업정보 수정하기
	int updateEnterpriseInfo(CorpInfo corpInfo);
}
