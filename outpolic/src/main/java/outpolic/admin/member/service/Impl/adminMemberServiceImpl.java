package outpolic.admin.member.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import outpolic.admin.member.mapper.AdminMemberMapper;
import outpolic.admin.member.service.AdminMemberService;
import outpolic.common.domain.Member;

@Service
@RequiredArgsConstructor
public class adminMemberServiceImpl implements AdminMemberService {
	
	private final AdminMemberMapper adminMemberMapper;
	@Override
	public List<Member> getMemberList() {
		List<Member> memberList = adminMemberMapper.getMemberList();
		return memberList;
	}
	@Override
	public List<Member> getActiveMemberList() {
		List<Member> memberList = adminMemberMapper.getActiveMemberList();
		return memberList;
	}
	@Override
	public List<Member> getWithdrawMemberList() {
		List<Member> memberList = adminMemberMapper.getWithdrawMemberList();
		return memberList;
	}
	
	

}
