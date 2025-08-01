package outpolic.admin.loginHistory.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import outpolic.admin.loginHistory.dto.AdminLoginHistoryDTO;
import outpolic.admin.loginHistory.service.AdminLoginHistoryService;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class adminLoginHistoryController {
	
	private final AdminLoginHistoryService adminLoginHistoryService;
	
	@GetMapping("/loginHistory")
	public String loginHistoryView(@RequestParam(defaultValue = "1") int currentPage, Model model, HttpSession httpSession) {
		
		List<String> permissions = (List<String>) httpSession.getAttribute("SPermissions");
		if (!permissions.contains("MEMBER_ADMIN") && !permissions.contains("SYSTEM_ADMIN")) {
			model.addAttribute("msg", "접근 권한이 없습니다.");
			model.addAttribute("url", "/admin"); // 또는 돌아갈 페이지
			return "admin/login/alert"; // alert.html이라는 공용 alert 페이지
		}
		
	    if (currentPage < 1) currentPage = 1;
	    
	    int rowPerPage = 30;
	    int startRow = (currentPage - 1) * rowPerPage;

	    List<AdminLoginHistoryDTO> loginList = adminLoginHistoryService.getAllHistory(startRow, rowPerPage);
	    int totalCount = adminLoginHistoryService.getAllHistoryCount();
	    int lastPage = (int) Math.ceil((double) totalCount / rowPerPage);

	    // ✅ 페이지네이션 범위 계산 (항상 5개 표시)
	    int startPageNum;
	    int endPageNum;

	    if (lastPage <= 5) {
	        // 전체 페이지 수가 5 이하인 경우
	        startPageNum = 1;
	        endPageNum = lastPage;
	    } else if (currentPage <= 3) {
	        // 앞 구간 고정
	        startPageNum = 1;
	        endPageNum = 5;
	    } else if (currentPage + 2 >= lastPage) {
	        // 뒤 구간 고정
	        startPageNum = lastPage - 4;
	        endPageNum = lastPage;
	    } else {
	        // 가운데 기준으로
	        startPageNum = currentPage - 2;
	        endPageNum = currentPage + 2;
	    }

	    model.addAttribute("loginList", loginList);
	    model.addAttribute("currentPage", currentPage);
	    model.addAttribute("lastPage", lastPage);
	    model.addAttribute("startPageNum", startPageNum);
	    model.addAttribute("endPageNum", endPageNum);
	    model.addAttribute("path", "/admin/loginHistory");
	    model.addAttribute("pageSize", rowPerPage);
	    model.addAttribute("totalCount", totalCount);

	    return "admin/loginHistory/adminLoginHistoryView";
	}
	
	@GetMapping("/loginHistory/chartData")
	@ResponseBody
	public List<Map<String, Object>> getLoginChartData() {
	    return adminLoginHistoryService.getLoginCountForLast7Days();
	}
	
	
	@GetMapping("/loginHistory/search")
	@ResponseBody
	public List<AdminLoginHistoryDTO> searchLoginHistory(
	        @RequestParam(required = false) String keyword,
	        @RequestParam(required = false) String startDate,
	        @RequestParam(required = false) String endDate) {
		if (startDate != null && !startDate.isBlank()) {
		    startDate += " 00:00:00";  // 하루 시작
		}
		if (endDate != null && !endDate.isBlank()) {
		    endDate += " 23:59:59";    // 하루 끝
		}

	    return adminLoginHistoryService.searchLoginHistory(keyword, startDate, endDate);
	}
	
	@GetMapping("/loginHistory/init")
	@ResponseBody
	public List<AdminLoginHistoryDTO> getAllLoginHistory(@RequestParam(defaultValue = "1") int page) {
	    int rowPerPage = 30;
	    int startRow = (page - 1) * rowPerPage;

	    return adminLoginHistoryService.getAllHistory(startRow, rowPerPage);
	}
}
