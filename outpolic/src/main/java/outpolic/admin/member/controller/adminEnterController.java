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
	public String adminEnterList(Model model) {
		var enterList = adminEnterService.getEnterList();
		model.addAttribute("title", "기업목록");
		model.addAttribute("enterList", enterList);
		return "admin/member/adminEnterListView";
	}
	
	// 회원 목록 필터링
	@GetMapping("/enterList/filter")
	@ResponseBody
	public List<AdminMemberDTO> filterEnters(@RequestParam(required = false) String statusCode,
	                                  @RequestParam(required = false) String keyword) {

	    List<AdminMemberDTO> filtered = adminEnterService.getEnterListByStatus(statusCode);

	    System.out.println("✅ 필터링 결과 개수: " + filtered.size());

	    return filtered;
	}
	
	// 회원 검색
	@GetMapping("/enterList/search")
	@ResponseBody
	public List<AdminMemberDTO> searchEnters(@RequestParam("keyword") String keyword) {
	    return adminEnterService.searchEnterpriseMembers(keyword);
	}
	
	//회원정보 수정 디테일
	@GetMapping("/enterList/detail")
	@ResponseBody
	public AdminMemberDTO adminEnterDetailView(@RequestParam String memberCode) {
		return adminEnterService.selectEnterByCode(memberCode);
	}
	
	
	@PostMapping("/enterList/detail/update")
	@ResponseBody
	public String adminEnterDetailEdit(@RequestBody AdminMemberDTO adminMemberDTO) {
		adminEnterService.updateAdminEnterEditInfo(adminMemberDTO);
		return "success";
	}
	
}
