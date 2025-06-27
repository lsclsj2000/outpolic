package outpolic.admin.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class adminMemberController {
	
	@GetMapping("")
	public String adminMain() {
		return "admin/main/adminMainView";
	}

	@GetMapping("/memberList")
	public String adminMemberList() {
		return "admin/member/adminMemberListView";
	}
}
