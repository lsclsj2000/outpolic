package outpolic.admin.declaration.controller;

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
import outpolic.admin.declaration.domain.AdminDeclaration;
import outpolic.admin.declaration.service.AdminDeclarationService;
import outpolic.admin.limits.service.AdminLimitsService;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(value="/admin")
public class AdminDeclarationController {
	
	private final AdminDeclarationService adminDeclarationService;
	private final AdminLimitsService adminLimitsService;
	
	@GetMapping("/declarationResults")
    @ResponseBody
    public List<AdminDeclaration> getDeclarationResults() {
        // 신고 처리 결과 목록 조회
        log.info("Fetching declaration results list.");
        return adminDeclarationService.getAdminDeclarationResultList();
    }

	@PostMapping("/processDeclaration")
	@ResponseBody
	public String processDeclaration(@RequestBody AdminDeclaration adminDeclaration, HttpSession session) {
	    String adminCode = (String) session.getAttribute("SACD");

	    if (adminCode == null) {
	        log.warn("Unauthorized attempt to process declaration. Session adminCode is null.");
	        return "FAIL: Unauthorized";
	    }

	    String dcCd = adminDeclaration.getDeclarationCode();
	    String drcCd = adminDeclaration.getDeclarationResultCode(); // 처리 결과 코드
	    String dtCd = adminDeclaration.getDeclarationTypeCode();  // 신고 타입 코드

	    adminDeclaration.setDeclarationMdfcnAdmCode(adminCode); // 처리자 저장

	    try {
	        adminDeclarationService.processDeclaration(adminDeclaration);
	        adminLimitsService.applySanctionAutomatically(drcCd, dtCd, dcCd, adminCode);
	        log.info("Declaration processed successfully for code: {}", dcCd);
	        return "OK";
	    } catch (Exception e) {
	        log.error("Error processing declaration for code: {}", dcCd, e);
	        return "FAIL: " + e.getMessage();
	    }
	}


	
	
    @PostMapping("/updateDeclaration")
	@ResponseBody
	public String updateDeclaration(@RequestBody AdminDeclaration adminDeclaration, HttpSession session) {
		// 신고 내역 수정 업데이트
		String adminCode = (String) session.getAttribute("SACD");
		if (adminCode == null) {
			return "FAIL: Unauthorized";
		}
		
		adminDeclaration.setDeclarationMdfcnAdmCode(adminCode);
		// 이 부분 추가: adm_cd 컬럼에 값을 전달하기 위해 adminCode 필드에도 세션의 adminCode를 설정합니다.
		adminDeclaration.setAdminCode(adminCode); 
		adminDeclarationService.updateDeclaration(adminDeclaration);
		return "OK";
	}
	
	
	@GetMapping("/declarationStatus")
	@ResponseBody
    public List<AdminDeclaration> getDeclarationStatus() {
		// 신고 처리 상태 조회
        return adminDeclarationService.getDeclarationStatusList();
    }
	
	@GetMapping("/adminDeclarationDetail")
	@ResponseBody
	public AdminDeclaration adminDeclarationDetail(@RequestParam("declarationCode") String declarationCode) {
		// 신고 상세 수정 팝업창에 데이터 로드
		AdminDeclaration adminDeclaration = adminDeclarationService.getAdminDeclarationDetail(declarationCode);
		return adminDeclaration;
	}
	
	@GetMapping("/getDeclarationReasonsByTypeCode")
	@ResponseBody
	public List<AdminDeclaration> getDeclarationReasonsByTypeCode(@RequestParam("dtCode") String dtCode) {
	    return adminDeclarationService.getDeclarationReasonsByTypeCode(dtCode);
	}

	
	
	@GetMapping("/getDeclarationReason")
	@ResponseBody
	public AdminDeclaration getDeclarationReason(@RequestParam("code") String code) {
		// 신고 사유 수정팝업창 조회
	    return adminDeclarationService.getDeclarationReasonByCode(code);
	}

	@GetMapping("/getDeclarationResult")
	@ResponseBody
	public AdminDeclaration getDeclarationResult(@RequestParam("code") String code) {
		// 신고처리결과코드 수정팝업창 조회
	    return adminDeclarationService.getDeclarationResultByCode(code);
	}
	
	@PostMapping("/updateDeclarationReason")
	@ResponseBody
	public String updateDeclarationReason(@RequestBody AdminDeclaration adminDeclaration, HttpSession session) {
		// 신고 사유 수정
	    String adminCode = (String) session.getAttribute("SACD");
	    if (adminCode == null) return "FAIL: Unauthorized";
	    adminDeclaration.setDrMdfcnAdmCode(adminCode);
	    adminDeclarationService.updateDeclarationReason(adminDeclaration);
	    return "OK";
	}

	@PostMapping("/updateDeclarationResult")
	@ResponseBody
	public String updateDeclarationResult(@RequestBody AdminDeclaration adminDeclaration, HttpSession session) {
		// 신고처리결과코드 수정
	    String adminCode = (String) session.getAttribute("SACD");
	    if (adminCode == null) return "FAIL: Unauthorized";
	    adminDeclaration.setDrcMdfcnAdmCode(adminCode);
	    adminDeclarationService.updateDeclarationResult(adminDeclaration);
	    return "OK";
	}
	
	
	@PostMapping("/updateDeclarationType")
	@ResponseBody
	public String updateDeclarationType(@RequestBody AdminDeclaration adminDeclaration, HttpSession session) {
		// 신고 타입 수정
	    String adminCode = (String) session.getAttribute("SACD");
	    if (adminCode == null) return "FAIL: Unauthorized";

	    adminDeclaration.setDeclarationTypeMdfcnAdmCode(adminCode);
	    adminDeclarationService.updateDeclarationType(adminDeclaration);
	    return "OK";
	}
	
	@GetMapping("/getDeclarationType")
	@ResponseBody
	public AdminDeclaration getDeclarationType(@RequestParam("code") String code) {
		// 신고 타입 수정팝업창 조회
	    return adminDeclarationService.getDeclarationTypeByCode(code);
	}
	
	@PostMapping("/addDeclarationType")
	@ResponseBody
	public String addDeclarationType(@RequestBody AdminDeclaration adminDeclaration, HttpSession session) {
		// 신고 타입 등록
	    String adminCode = (String) session.getAttribute("SACD");
	    if (adminCode == null) return "FAIL: Unauthorized";
	    adminDeclaration.setDeclarationTypeRegAdmCode(adminCode);
	    adminDeclarationService.insertDeclarationType(adminDeclaration);
	    return "OK";
	}
	
	@GetMapping("/declarationTypes")
	@ResponseBody
	public List<AdminDeclaration> getDeclarationTypes() {
		// 신고 사유_상위 신고 타입
	    return adminDeclarationService.getAdminDeclarationTypeList();
	}

	@PostMapping("/addDeclarationReason")
	@ResponseBody
	public String addDeclarationReason(@RequestBody AdminDeclaration adminDeclaration, HttpSession session) {
		// 신고 사유 등록
	    String adminCode = (String) session.getAttribute("SACD");
	    if (adminCode == null) return "FAIL: Unauthorized";
	    adminDeclaration.setDrRegAdmCode(adminCode);
	    adminDeclarationService.insertDeclarationReason(adminDeclaration);
	    return "OK";
	}

	@PostMapping("/addDeclarationResult")
	@ResponseBody
	public String addDeclarationResult(@RequestBody AdminDeclaration adminDeclaration, HttpSession session) {
		// 신고처리결과 등록
	    String adminCode = (String) session.getAttribute("SACD");
	    if (adminCode == null) return "FAIL: Unauthorized";
	    adminDeclaration.setDrcRegAdmCode(adminCode);
	    adminDeclarationService.insertDeclarationResult(adminDeclaration);
	    return "OK";
	}
	
	@GetMapping("/adminDeclarationResources")
	public String adminDeclarationManageView(Model model, HttpSession session) {
		
		List<String> permissions = (List<String>) session.getAttribute("SPermissions");
		if (!permissions.contains("CS_ADMIN") && !permissions.contains("SYSTEM_ADMIN")) {
			model.addAttribute("msg", "접근 권한이 없습니다.");
			model.addAttribute("url", "/admin"); // 또는 돌아갈 페이지
			return "admin/login/alert"; // alert.html이라는 공용 alert 페이지
		}
		// 신고 자원 등록 페이지
		List<AdminDeclaration> adminDeclarationTypeList = adminDeclarationService.getAdminDeclarationTypeList();
		List<AdminDeclaration> adminDeclarationReasonList  = adminDeclarationService.getAdminDeclarationReasonList();
		List<AdminDeclaration> adminDeclarationResultList  = adminDeclarationService.getAdminDeclarationResultList();
		
		model.addAttribute("title", "신고 자원 등록");
		model.addAttribute("adminDeclarationTypeList", adminDeclarationTypeList);
		model.addAttribute("adminDeclarationReasonList", adminDeclarationReasonList);
		model.addAttribute("adminDeclarationResultList", adminDeclarationResultList);
		
		return "admin/declaration/adminDeclarationResourcesView";
	}
	
	@GetMapping("/adminDeclaration")
	public String adminDeclarationView(	
	    @RequestParam(required = false) String keywordField,
	    @RequestParam(required = false) String keyword,
	    @RequestParam(required = false) String dateField,
	    @RequestParam(required = false) String startDate,
	    @RequestParam(required = false) String endDate,
	    @RequestParam(required = false) String status,
	    Model model, HttpSession session
	) {
		
		List<String> permissions = (List<String>) session.getAttribute("SPermissions");
		if (!permissions.contains("CS_ADMIN") && !permissions.contains("SYSTEM_ADMIN")) {
			model.addAttribute("msg", "접근 권한이 없습니다.");
			model.addAttribute("url", "/admin"); // 또는 돌아갈 페이지
			return "admin/login/alert"; // alert.html이라는 공용 alert 페이지
		}
		// 신고 내역 조회 - 필터
		Map<String, Object> searchParams = new HashMap<>();
	    searchParams.put("keywordField", keywordField);
	    searchParams.put("keyword", keyword);
	    searchParams.put("dateField", dateField);
	    searchParams.put("startDate", startDate);
	    searchParams.put("endDate", endDate);
	    searchParams.put("status", status);

	    List<AdminDeclaration> adminDeclarationList = adminDeclarationService.getAdminDeclarationListFiltered(searchParams);
	    model.addAttribute("adminDeclarationList", adminDeclarationList);

	    // ✅ 검색 조건 유지용 값 전달
	    model.addAttribute("keywordField", keywordField);
	    model.addAttribute("keyword", keyword);
	    model.addAttribute("dateField", dateField);
	    model.addAttribute("startDate", startDate);
	    model.addAttribute("endDate", endDate);
	    model.addAttribute("status", status);

	    return "admin/declaration/adminDeclarationView";
	}
	
	@GetMapping("/adminDeclarationResourcesFilter")
	public String adminDeclarationResourcesView(
		    @RequestParam(required = false, defaultValue = "type") String resourceType,
		    @RequestParam(required = false) String searchField,
		    @RequestParam(required = false) String searchKeyword,
		    @RequestParam(required = false) String status,
		    @RequestParam(required = false) String dateField,
		    @RequestParam(required = false) String startDate,
		    @RequestParam(required = false) String endDate,
		    Model model, HttpSession session
	) {
		List<String> permissions = (List<String>) session.getAttribute("SPermissions");
		if (!permissions.contains("CS_ADMIN") && !permissions.contains("SYSTEM_ADMIN")) {
			model.addAttribute("msg", "접근 권한이 없습니다.");
			model.addAttribute("url", "/admin"); // 또는 돌아갈 페이지
			return "admin/login/alert"; // alert.html이라는 공용 alert 페이지
		}
	    Map<String, Object> paramMap = new HashMap<>();
	    paramMap.put("searchField", searchField);
	    paramMap.put("searchKeyword", searchKeyword);
	    paramMap.put("status", status);
	    paramMap.put("dateField", dateField);
	    paramMap.put("startDate", startDate);
	    paramMap.put("endDate", endDate);

	    // 자원 종류 분기 처리
	    switch (resourceType) {
	        case "type":
	            model.addAttribute("adminDeclarationTypeList", adminDeclarationService.getFilteredDeclarationTypeList(paramMap));
	            break;
	        case "reason":
	            model.addAttribute("adminDeclarationReasonList", adminDeclarationService.getFilteredDeclarationReasonList(paramMap));
	            break;
	        case "result":
	            model.addAttribute("adminDeclarationResultList", adminDeclarationService.getFilteredDeclarationResultList(paramMap));
	            break;
	    }

	    // 검색 조건 다시 넘겨줌
	    model.addAttribute("resourceType", resourceType); // 중요!!
	    model.addAttribute("searchField", searchField);
	    model.addAttribute("searchKeyword", searchKeyword);
	    model.addAttribute("status", status);
	    model.addAttribute("dateField", dateField);
	    model.addAttribute("startDate", startDate);
	    model.addAttribute("endDate", endDate);

	    return "admin/declaration/adminDeclarationResourcesView";
	}


}
