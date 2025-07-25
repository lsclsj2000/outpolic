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
	public String adminEnterList(Model model, HttpSession session) {
		String adminCode = (String)session.getAttribute("SCD");
		model.addAttribute("adminCode",adminCode);
		var enterList = adminEnterService.getEnterList();
		model.addAttribute("title", "기업목록");
		model.addAttribute("enterList", enterList);
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
