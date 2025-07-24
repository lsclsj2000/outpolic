package outpolic.admin.loginHistory.controller;

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

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import outpolic.admin.loginHistory.dto.AdminLoginHistoryDTO;
import outpolic.admin.member.dto.AdminMemberDTO;
import outpolic.admin.member.service.AdminEnterService;
import outpolic.admin.member.service.AdminMemberService;
import outpolic.common.domain.Member;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class adminLoginHistoryController {
	
	/*
	 * @GetMapping("/admin/loginHistory") public String loginHistoryView(Model
	 * model) { List<AdminLoginHistoryDTO> historyList =
	 * loginHistoryService.getAllHistory(); model.addAttribute("historyList",
	 * historyList); return "admin/login/loginHistoryView"; }
	 */
	
	@GetMapping("/loginHistory")
	public String loginHistoryView() {
		return "admin/loginHistory/adminLoginHistoryView";
	}
	
}
