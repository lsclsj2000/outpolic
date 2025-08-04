package outpolic.admin.member.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import outpolic.admin.member.dto.AdminMemberDTO;
import outpolic.admin.member.service.AdminMemberService;
import outpolic.common.domain.Member;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class adminMemberController {
	
	private final AdminMemberService adminMemberService;
	
	//전체 회원목록
	@GetMapping("/memberList")
	public String adminMemberList(Model model, HttpSession httpSession, @RequestParam(defaultValue = "1") Integer currentPage, HttpServletResponse response) {
		List<String> permissions = (List<String>) httpSession.getAttribute("SPermissions");
		if (!permissions.contains("MEMBER_ADMIN") && !permissions.contains("SYSTEM_ADMIN")) {
			model.addAttribute("msg", "접근 권한이 없습니다.");
			model.addAttribute("url", "/admin"); // 또는 돌아갈 페이지
			return "admin/login/alert"; // alert.html이라는 공용 alert 페이지
		}
		if (currentPage < 1) currentPage = 1;
		 int rowPerPage = 30;
		    int startRow = (currentPage - 1) * rowPerPage;

		    List<AdminMemberDTO> memberList = adminMemberService.getMemberListForPg(startRow, rowPerPage);
		    int totalCount = adminMemberService.getMemberCount();
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

		    model.addAttribute("memberList", memberList);
		    model.addAttribute("totalCount", totalCount);
		    model.addAttribute("currentPage", currentPage);
		    model.addAttribute("lastPage", lastPage);
		    model.addAttribute("startPageNum", startPageNum);
		    model.addAttribute("startRow", startRow);
		    model.addAttribute("endPageNum", endPageNum);
		    model.addAttribute("pageSize", rowPerPage);
		    model.addAttribute("path", "/admin/memberList");
		/*
		 * var memberList = adminMemberService.getMemberList();
		 * model.addAttribute("title", "회원목록"); model.addAttribute("memberList",
		 * memberList);
		 */
		return "admin/member/adminMemberListView";
	}
	
	// 회원 목록 필터링
	@GetMapping("/memberList/filter")
	@ResponseBody
	public List<Member> filterMembers(@RequestParam(required = false) String statusCode,
	                                  @RequestParam(required = false) String gradeCode,
	                                  @RequestParam(required = false) String keyword,
	                                  @RequestParam(required = false) String orderBy) {
		System.out.println("🔥 필터 요청 진입");
	    System.out.println("📦 statusCode: " + statusCode);
	    System.out.println("📦 gradeCode: " + gradeCode);
	    System.out.println("📦 orderBy: " + orderBy);

	    List<Member> filtered = adminMemberService.filterMembers(statusCode, gradeCode, orderBy);

	    System.out.println("✅ 필터링 결과 개수: " + filtered.size());

	    return filtered;
	}
	
	// 회원 검색
	@GetMapping("/memberList/search")
	@ResponseBody
	public List<Member> searchMembers(@RequestParam("keyword") String keyword) {
		if(keyword == null) {
			return adminMemberService.getMemberList() ;
		}else {
	    return adminMemberService.searchMembers(keyword);
		}
	}
	
	//회원정보 수정 디테일
	@GetMapping("/memberList/detail")
	@ResponseBody
	public Member adminMemberDetailView(@RequestParam String memberCode) {
		return adminMemberService.getMemberByCode(memberCode);
	}
	
	@GetMapping("/check/nickname")
	@ResponseBody
	public boolean checkNickname(@RequestParam String nickname,
	                             @RequestParam String memberCode) {
	    return !adminMemberService.isNicknameDuplicated(nickname, memberCode);
	}
	
	@PostMapping("/memberList/detail/update")
	@ResponseBody
	public String adminMemberDetailEdit(@RequestBody Member member) {
		
		adminMemberService.editAdminMemberInfo(member);
	 	 
		return "success";
	}

}
