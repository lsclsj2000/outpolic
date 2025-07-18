package outpolic.enter.mypage.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import outpolic.common.dto.OutsourcingReviewDTO;
import outpolic.enter.mypage.dto.EnterInfo;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;
import outpolic.enter.portfolio.domain.EnterPortfolio;

@Mapper
public interface EnterMypageMapper {
	// 특정 기업회원 정보 가져오기
	EnterInfo getEnterInfoByCode(String memberCode);
	
	//특정 기업회원 정보 수정하기
	int updateEnterInfo(EnterInfo enterInfo);
	
	//중복확인
	boolean isNickNameDuplicated(@Param("memberNickname") String memberNickname, @Param("memberCode") String memberCode);
	boolean isEmailDuplicated(@Param("memberEmail") String memberEmail, @Param("memberCode") String memberCode);
	boolean isTelDuplicated(@Param("memberTelNo") String memberTelNo, @Param("memberCode") String memberCode);
	
	// 회원 리뷰글 불러오기
	List<OutsourcingReviewDTO> selectEnterReviewList(String memberCode);
	
	// 회원코드로 회원 외주글 불러오기
	List<EnterOutsourcing> EnterOsSelectByCode(String mbrCd);
	
	//특정 기업 완료된 외주 수 조회
	int EnterEndedOsSelectByCode(String mbrCd);
	
	// 회원코드로 회원 포폴글 불러오기
	List<EnterPortfolio> EnterPfSelectByCode(String mbrCd);
	
	// 회원코드 기준 받은 외주 요청 수 불러오기
	int EnterIncomingOsByCode(String mbrCd);
	
	// 회원코드 기준 진행중 외주 수 불러오기
	int EnterOsIngSelectByCode(String memberCode);
}
