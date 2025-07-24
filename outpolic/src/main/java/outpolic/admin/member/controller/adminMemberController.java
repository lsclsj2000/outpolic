package outpolic.admin.member.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import outpolic.admin.member.service.AdminEnterService;
import outpolic.admin.member.service.AdminMemberService;
import outpolic.common.domain.Member;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class adminMemberController {
	
	private final AdminMemberService adminMemberService;
	
	//전체 회원목록
	@GetMapping("/memberList")
	public String adminMemberList(Model model) {
		var memberList = adminMemberService.getMemberList();
		model.addAttribute("title", "회원목록");
		model.addAttribute("memberList", memberList);
		return "admin/member/adminMemberListView";
	}
	
	// 회원 목록 필터링
	@GetMapping("/memberList/filter")
	@ResponseBody
	public List<Member> filterMembers(@RequestParam(required = false) String statusCode,
	                                  @RequestParam(required = false) String gradeCode,
	                                  @RequestParam(required = false) String keyword) {
		System.out.println("🔥 필터 요청 진입");
	    System.out.println("📦 statusCode: " + statusCode);
	    System.out.println("📦 gradeCode: " + gradeCode);

	    List<Member> filtered = adminMemberService.filterMembers(statusCode, gradeCode);

	    System.out.println("✅ 필터링 결과 개수: " + filtered.size());

	    return filtered;
	}
	
	// 회원 검색
	@GetMapping("/memberList/search")
	@ResponseBody
	public List<Member> searchMembers(@RequestParam("keyword") String keyword) {
		if(keyword == null) {
			return adminMemberService.getMemberList() ;
		}else {
	    return adminMemberService.searchMembers(keyword);
		}
	}
	
	//회원정보 수정 디테일
	@GetMapping("/memberList/detail")
	@ResponseBody
	public Member adminMemberDetailView(@RequestParam String memberCode) {
		return adminMemberService.getMemberByCode(memberCode);
	}
	
	@GetMapping("/check/nickname")
	@ResponseBody
	public boolean checkNickname(@RequestParam String nickname,
	                             @RequestParam String memberCode) {
	    return !adminMemberService.isNicknameDuplicated(nickname, memberCode);
	}
	
	@PostMapping("/memberList/detail/update")
	@ResponseBody
	public String adminMemberDetailEdit(@RequestBody Member member) {
		adminMemberService.editAdminMemberInfo(member);
	 	 
		return "success";
	}

}
