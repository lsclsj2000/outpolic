package outpolic.admin.announcement.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import outpolic.admin.announcement.domain.AdminAnn;
import outpolic.admin.announcement.service.AdminAnnService;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(value="/admin")
public class AdminAnnController {
	
	private final AdminAnnService adminAnnService;
	
	@PostMapping("/updateAdminAnn")
	@ResponseBody
	public String updateAdminAnn(@RequestBody AdminAnn adminAnn, HttpSession session) {
		// 공지사항 수정 저장
	    String modifier = (String) session.getAttribute("SACD"); // 관리자 코드
	    adminAnn.setAnnMdfcnAdmCode(modifier);
	    
	    int result = adminAnnService.updateAdminAnn(adminAnn);
	    return result > 0 ? "success" : "fail";
	}
	
	// 공지사항 상세 정보 조회 API 추가
	@GetMapping("/getAdminAnnDetail")
	@ResponseBody
	public AdminAnn getAdminAnnDetail(@RequestParam("code") String annCode) {
	    log.info("📌 공지사항 단건 조회: {}", annCode);
	    
	    // annCode를 사용하여 서비스에서 상세 정보를 조회하는 로직
	    AdminAnn adminAnn = adminAnnService.getAdminAnnDetail(annCode); 

	    // 조회된 공지사항 객체를 반환
	    return adminAnn;
	}
	
	@GetMapping("/adminAnn")
	public String adminAnnView(
	    @RequestParam(required = false) String searchField,
	    @RequestParam(required = false) String searchKeyword,
	    @RequestParam(required = false) String status,
	    @RequestParam(required = false) String dateField,
	    @RequestParam(required = false) String startDate,
	    @RequestParam(required = false) String endDate,
	    Model model
	) {
	    Map<String, Object> paramMap = new HashMap<>();
	    paramMap.put("searchField", searchField);
	    paramMap.put("searchKeyword", searchKeyword);
	    paramMap.put("status", status);
	    paramMap.put("dateField", dateField);
	    paramMap.put("startDate", startDate);
	    paramMap.put("endDate", endDate);

	    log.info("📌 공지사항 필터 파라미터: {}", paramMap);

	    List<AdminAnn> adminAnnList = adminAnnService.getAdminAnnListFiltered(paramMap);
	    if (adminAnnList == null) adminAnnList = List.of();

	    log.info("📌 공지사항 조회 결과: {}건", adminAnnList.size());

	    model.addAttribute("adminAnnList", adminAnnList);
	    model.addAttribute("searchField", searchField);
	    model.addAttribute("searchKeyword", searchKeyword);
	    model.addAttribute("status", status);
	    model.addAttribute("dateField", dateField);
	    model.addAttribute("startDate", startDate);
	    model.addAttribute("endDate", endDate);

	    return "admin/announcement/adminAnnView";
	}


	
	@GetMapping("/adminAnnWrite")
	public String adminAnnWriteView() {
		// 공지사항 작성
		
		return "admin/announcement/adminAnnWriteView";
	}

}
