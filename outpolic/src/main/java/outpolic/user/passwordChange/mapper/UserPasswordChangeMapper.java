package outpolic.user.passwordChange.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import outpolic.common.domain.Member;

@Mapper
public interface UserPasswordChangeMapper {

	 // 기존 비밀번호 가져오기
    String getEncodedPassword(@Param("memberCode") String memberCode);

    // 비밀번호 업데이트
    
    void updatePassword(@Param("memberCode") String memberCode,
            @Param("newPassword") String newPassword);
    
    Member findByMemberCode(String memberCode);

    void updateMemberMdfcn(@Param("memberCode") String memberCode,
            @Param("newPassword") String newPassword);
}
