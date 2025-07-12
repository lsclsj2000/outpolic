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
	
	@GetMapping("/memberList")
	public String adminMemberList(Model model) {
		var memberList = adminMemberService.getMemberList();
		model.addAttribute("title", "회원목록");
		model.addAttribute("memberList", memberList);
		return "admin/member/adminMemberListView";
	}
}
