package outpolic.admin.announcement.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
	public String adminAnnWriteView() {
		// 공지사항 작성
		
		return "admin/announcement/adminAnnWriteView";
	}

}
