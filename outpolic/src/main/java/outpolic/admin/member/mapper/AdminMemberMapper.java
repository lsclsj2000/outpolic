package outpolic.admin.member.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import outpolic.common.domain.Member;

@Mapper
public interface AdminMemberMapper {
	
	//회원 목록 조회
	List<Member> getMemberList();
	
	//상태에 따른 회원 목록 조회
	List<Member> getMemberListByStatus(String statusCode);
	
	//휴면 회원 목록 조회
	List<Member> getWithdrawMemberList();
	
	//특정 회원 정보 조회
	Member selectMemberByCode(String memberCode);
	
	//특정 회원 정보 업데이트
	int updateAdminMemberEditInfo(Member member);
	
	//닉네임 중복 확인
	int countNicknameDuplicate(@Param("nickname") String nickname, @Param("memberCode") String memberCode);
}
