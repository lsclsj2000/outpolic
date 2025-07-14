package outpolic.admin.member.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import outpolic.common.domain.Member;

@Mapper
public interface AdminMemberMapper {
	
	//회원 목록 조회
	List<Member> getMemberList();
	
	//활성 회원 목록 조회
	List<Member> getActiveMemberList();
	
	//휴면 회원 목록 조회
	List<Member> getWithdrawMemberList();
	
	//특정 회원 정보 조회
	Member selectMemberByCode(String memberCode);
}
