package outpolic.admin.member.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.admin.member.dto.AdminMemberDTO;
import outpolic.admin.member.mapper.AdminMemberMapper;
import outpolic.admin.member.service.AdminMemberService;
import outpolic.common.domain.Member;

@Service
@RequiredArgsConstructor
@Slf4j
public class adminMemberServiceImpl implements AdminMemberService {
	
	private final AdminMemberMapper adminMemberMapper;
	
	@Override
	public List<Member> getMemberList() {
		List<Member> memberList = adminMemberMapper.getMemberList();
		return memberList;
	}
	
	@Override
	public List<Member> getWithdrawMemberList() {
		List<Member> memberList = adminMemberMapper.getWithdrawMemberList();
		return memberList;
	}
	
	@Override
	public Member getMemberByCode(String memberCode) {
		return adminMemberMapper.selectMemberByCode(memberCode);
	}

	@Override
	public void editAdminMemberInfo(Member member) {
		 log.info("수정 요청 받은 회원 정보: {}", member);
		int result = adminMemberMapper.updateAdminMemberEditInfo(member);
		log.info("업데이트 결과: {}", result);
		
	}

	@Override
	public boolean isNicknameDuplicated(String nickname, String memberCode) {
		return adminMemberMapper.countNicknameDuplicate(nickname, memberCode) > 0;
	}



	@Override
	public List<Member> getMemberListByStatus(String statusCode) {
		String status;
		switch(statusCode.toLowerCase()) {
			case "active":
	            status = "SD_ACTIVE";
	            break;
	        case "withdrawn":
	            status = "SD_WITHDRAWN";
	            break;
	        case "dormant":
	            status = "SD_DORMANT";
	            break;
	        case "limit":
	            status = "SD_LIMIT";
	            break;
	        default:
	            status = null; 
		}
		List<Member> memberList = adminMemberMapper.getMemberListByStatus(status);
		
		return memberList;
	}
	
	@Override
	public List<Member> filterMembers(String statusCode, String gradeCode, String orderBy) {
	    return adminMemberMapper.selectFilteredMembers(statusCode, gradeCode, orderBy);
	}

	@Override
	public List<Member> searchMembers(String keyword) {
		return adminMemberMapper.searchMembers(keyword);
	}

	@Override
	public int getMemberCount() {
		return adminMemberMapper.getMemberCount();
	}

	@Override
	public List<Member> getMemberListForPg(int startRow, int rowPerPage) {
		// TODO Auto-generated method stub
		return adminMemberMapper.getMemberListForPg(startRow, rowPerPage);
	}
	

}
