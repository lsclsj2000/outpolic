package outpolic.user.register.mapper;

import org.apache.ibatis.annotations.Param;

public interface UserRegisterMapper {
	boolean isIdDuplicated(@Param("memberId") String memberId);
	boolean isNickNameDuplicated(@Param("memberNickName") String memberNickName);
    boolean isEmailDuplicated(@Param("memberEmail") String memberEmail);
    boolean isTelDuplicated(@Param("memberTelNo") String memberTelNo);
	 
}
