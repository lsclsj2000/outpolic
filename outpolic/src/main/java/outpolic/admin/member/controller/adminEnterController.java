package outpolic.admin.member.controller;

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
import outpolic.admin.member.dto.AdminMemberDTO;
import outpolic.admin.member.service.AdminEnterService;
import outpolic.admin.member.service.AdminMemberService;
import outpolic.common.domain.Member;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class adminEnterController {
	
	private final AdminMemberService adminMemberService;
	private final AdminEnterService adminEnterService;
	
	//전체 회원목록
	@GetMapping("/enterList")
	public String adminEnterList(Model model, HttpSession session, @RequestParam(defaultValue = "1") Integer currentPage) {
		String adminCode = (String)session.getAttribute("SCD");
		model.addAttribute("adminCode",adminCode);
		List<String> permissions = (List<String>) session.getAttribute("SPermissions");
		if (!permissions.contains("MEMBER_ADMIN") && !permissions.contains("SYSTEM_ADMIN")) {
			model.addAttribute("msg", "접근 권한이 없습니다.");
			model.addAttribute("url", "/admin"); // 또는 돌아갈 페이지
			return "admin/login/alert"; // alert.html이라는 공용 alert 페이지
		}
		
		if (currentPage < 1) currentPage = 1;
		 int rowPerPage = 30;
		    int startRow = (currentPage - 1) * rowPerPage;

		    List<AdminMemberDTO> enterList = adminEnterService.getEnterListForPg(startRow, rowPerPage);
		    int totalCount = adminEnterService.getEnterCount();
		    int lastPage = (int) Math.ceil((double) totalCount / rowPerPage);

		    int startPageNum, endPageNum;
		    if (lastPage <= 5) {
		        startPageNum = 1;
		        endPageNum = lastPage;
		    } else if (currentPage <= 3) {
		        startPageNum = 1;
		        endPageNum = 5;
		    } else if (currentPage + 2 >= lastPage) {
		        startPageNum = lastPage - 4;
		        endPageNum = lastPage;
		    } else {
		        startPageNum = currentPage - 2;
		        endPageNum = currentPage + 2;
		    }

		    model.addAttribute("enterList", enterList);
		    model.addAttribute("totalCount", totalCount);
		    model.addAttribute("currentPage", currentPage);
		    model.addAttribute("lastPage", lastPage);
		    model.addAttribute("startPageNum", startPageNum);
		    model.addAttribute("startRow", startRow);
		    model.addAttribute("endPageNum", endPageNum);
		    model.addAttribute("pageSize", rowPerPage);
		    model.addAttribute("path", "/admin/enterList");
		
		
		return "admin/member/adminEnterListView";
	}
	
	// 회원 목록 필터링
	@GetMapping("/enterList/filter")
	@ResponseBody
	public List<AdminMemberDTO> filterEnters(@RequestParam(required = false) String statusCode,
	                                  @RequestParam(required = false) String keyword,
	                                  @RequestParam(required = false) String orderBy) {
		
		System.out.println("🔥 필터 요청 진입");
	    System.out.println("📦 statusCode: " + statusCode);
	    System.out.println("📦 orderBy: " + orderBy);
	    
	    List<AdminMemberDTO> filtered = adminEnterService.selectFilteredEnterpriseMembers(statusCode, orderBy);

	    System.out.println("✅ 필터링 결과 개수: " + filtered.size());

	    return filtered;
	}
	
	// 기업 검색
	@GetMapping("/enterList/search")
	@ResponseBody
	public List<AdminMemberDTO> searchEnters(@RequestParam("keyword") String keyword) {
		if(keyword == null) {
			return adminEnterService.getEnterList();
		}else {
	    return adminEnterService.searchEnterpriseMembers(keyword);
		}
	}
	
	//기업정보 수정 디테일
	@GetMapping("/enterList/detail")
	@ResponseBody
	public AdminMemberDTO adminEnterDetailView(@RequestParam String memberCode) {
		return adminEnterService.selectEnterByCode(memberCode);
	}
	
	
	@PostMapping("/enterList/detail/update")
	@ResponseBody
	public String adminEnterDetailEdit(@RequestBody AdminMemberDTO adminMemberDTO, HttpSession session) {
		System.out.println("받은 값: " + adminMemberDTO);
		String adminCode = (String)session.getAttribute("SACD");
		adminMemberDTO.setAdminCode(adminCode);
		System.out.println("DTO에 들어간 adminCode = " + adminMemberDTO.getAdminCode());
		int result = adminEnterService.updateAdminEnterEditInfo(adminMemberDTO);
	    return result > 0 ? "success" : "fail";
	}
	
}
