package outpolic.admin.osst.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.admin.osst.domain.AdminOsst;
import outpolic.admin.osst.service.AdminOsstService;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminOsstController {
	
	private final AdminOsstService adminOsstService; 
	
	
	@GetMapping("/adminOsstSteps")
	@ResponseBody
	public List<AdminOsst> getOsstSteps(@RequestParam String ocdCd, Model model) {
		// 외주 진행 수정 팝업창 데이터 조회
		List<AdminOsst> adminOsstMdfcn = adminOsstService.getOsstStepsByOcdCd(ocdCd);
		model.addAttribute("adminOsstMdfcn", adminOsstMdfcn);
		
	    return adminOsstMdfcn;
	}
	
	@GetMapping("/adminOsst")
	public String adminOutsourcingStatusView(@RequestParam(required = false) String searchField,
	                                         @RequestParam(required = false) String searchKeyword,
	                                         @RequestParam(required = false) String stepStatus,
	                                         Model model, HttpSession session) {
		
		List<String> permissions = (List<String>) session.getAttribute("SPermissions");
		if (!permissions.contains("CONTENT_ADMIN") && !permissions.contains("SYSTEM_ADMIN")) {
			model.addAttribute("msg", "접근 권한이 없습니다.");
			model.addAttribute("url", "/admin"); // 또는 돌아갈 페이지
			return "admin/login/alert"; // alert.html이라는 공용 alert 페이지
		}
		// 외주 진행 목록
	    List<AdminOsst> adminOsst = adminOsstService.getAdminOsstListFiltered(searchField, searchKeyword, stepStatus);

	    model.addAttribute("adminOsst", adminOsst);
	    model.addAttribute("searchField", searchField);
	    model.addAttribute("searchKeyword", searchKeyword);
	    model.addAttribute("stepStatus", stepStatus);

	    return "admin/osst/adminOutsourcingStatusView";
	}
	
	@PostMapping("/adminUpdateStepStatus")
	@ResponseBody
	public String updateStepStatus(@RequestBody AdminOsst step) {
		// 외주 진행 수정 팝업창 업데이트
	    adminOsstService.updateStepStatus(step.getOcdCode(), step.getStcCode(), step.getOspCustYn());
	    return "success";
	}
	
}
