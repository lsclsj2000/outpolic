package outpolic.admin.member.service;

import java.util.List;

import outpolic.common.domain.Member;

public interface AdminMemberService {
	//회원 전체 목록 조회
	List<Member> getMemberList();
	
	//활성 회원 목록 조회
	List<Member> getActiveMemberList();
	
	//휴면 회원 목록 조회
	List<Member> getWithdrawMemberList();
}
