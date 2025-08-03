package outpolic.admin.announcement.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.admin.announcement.domain.AdminAnn;
import outpolic.admin.announcement.service.AdminAnnService;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(value="/admin")
public class AdminAnnController {
	
	private final AdminAnnService adminAnnService;
	
	@GetMapping("/adminAnn")
	public String adminAnnView(Model model) {
		// 공지사항 내역 조회
		List<AdminAnn> adminAnnList = adminAnnService.getAdminAnnList();
		model.addAttribute("adminAnnList", adminAnnList);
		
		return "admin/announcement/adminAnnView";
	}
	
	@GetMapping("/adminAnnWrite")
	public String adminAnnWriteView(Model model, HttpSession session) {
		
		List<String> permissions = (List<String>) session.getAttribute("SPermissions");
		if (!permissions.contains("SYSTEM_ADMIN") && !permissions.contains("CS_ADMIN")) {
			model.addAttribute("msg", "접근 권한이 없습니다.");
			model.addAttribute("url", "/admin"); // 또는 돌아갈 페이지
			return "admin/login/alert"; // alert.html이라는 공용 alert 페이지
		}
		// 공지사항 작성
		
		return "admin/announcement/adminAnnWriteView";
	}

}
