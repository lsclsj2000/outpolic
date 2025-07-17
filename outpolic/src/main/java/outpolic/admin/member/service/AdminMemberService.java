package outpolic.admin.member.service;

import java.util.List;

import outpolic.common.domain.Member;

public interface AdminMemberService {
	//회원 전체 목록 조회
	List<Member> getMemberList();
	
	//활성 회원 목록 조회
	List<Member> getMemberListByStatus(String statusCode);
	
	//휴면 회원 목록 조회
	List<Member> getWithdrawMemberList();
	
	//특정 회원 정보 조회
	Member getMemberByCode(String memberCode);
	
	//특정 회원 정보 수정
	void editAdminMemberInfo(Member member);
	
	boolean isNicknameDuplicated(String nickname, String memberCode);
	
	public List<Member> filterMembers(String statusCode, String gradeCode);

	List<Member> searchMembers(String keyword);
}
