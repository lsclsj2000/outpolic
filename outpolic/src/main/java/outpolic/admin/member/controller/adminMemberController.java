package outpolic.admin.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
	//활성회원목록
	@GetMapping("/activeMemberList")
	public String adminActiveMemberList(Model model) {
		var memberList = adminMemberService.getActiveMemberList();
		model.addAttribute("title", "회원목록");
		model.addAttribute("memberList", memberList);				
		return "admin/member/adminActiveMemberListView";		
	}
	
	//휴면회원목록
	@GetMapping("/withdrawMemberList")
	public String adminWithdrawMemberList(Model model) {
		var memberList = adminMemberService.getWithdrawMemberList();
		model.addAttribute("title", "회원목록");
		model.addAttribute("memberList", memberList);				
		return "admin/member/adminWithdrawMemberListView";		
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
