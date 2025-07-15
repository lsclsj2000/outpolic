package outpolic.admin.member.controller;

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
	//상태에 따른 회원목록
	@GetMapping("/memberList/{status}")
	public String adminMemberListByStatus(@PathVariable("status") String statusCode ,Model model) {
		var memberList = adminMemberService.getMemberListByStatus(statusCode);
		model.addAttribute("title", "회원목록");
		model.addAttribute("memberList", memberList);				
		model.addAttribute("status", statusCode);				
		return "admin/member/adminMemberListViewByStatus";		
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
