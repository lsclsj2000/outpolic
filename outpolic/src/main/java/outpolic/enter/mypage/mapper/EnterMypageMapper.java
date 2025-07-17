package outpolic.enter.mypage.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import outpolic.common.dto.OutsourcingReviewDTO;
import outpolic.enter.mypage.dto.EnterInfo;

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
	
	List<OutsourcingReviewDTO> selectEnterReviewList(String memberCode);
	
	
}
