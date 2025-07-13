package outpolic.admin.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import outpolic.admin.member.service.AdminMemberService;

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
}
