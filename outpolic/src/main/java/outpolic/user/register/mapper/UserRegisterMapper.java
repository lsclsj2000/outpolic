package outpolic.user.register.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import outpolic.common.domain.Member;

@Mapper
public interface UserRegisterMapper {
	// 중복검사
	boolean isIdDuplicated(@Param("memberId") String memberId);
	boolean isNickNameDuplicated(@Param("memberNickName") String memberNickName);
    boolean isEmailDuplicated(@Param("memberEmail") String memberEmail);
    boolean isTelDuplicated(@Param("memberTelNo") String memberTelNo);
    
    // 기본키 생성
    String getNextMemberCode();
	 
    // 회원 정보 데이터베이스에 저장되었는가
    int UserRegister(Member member);
}
