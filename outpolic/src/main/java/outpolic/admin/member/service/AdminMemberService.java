package outpolic.admin.member.service;

import java.util.List;

import outpolic.common.domain.Member;

public interface AdminMemberService {
	//회원 전체 목록 조회
	List<Member> getMemberList();
}
