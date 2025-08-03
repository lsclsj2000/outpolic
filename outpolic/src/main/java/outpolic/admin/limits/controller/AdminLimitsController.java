package outpolic.admin.limits.controller;

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
import outpolic.admin.limits.domain.AdminLimits;
import outpolic.admin.limits.service.AdminLimitsService;

@Controller
@RequiredArgsConstructor
@RequestMapping(value="/admin")
@Slf4j
public class AdminLimitsController {
	
	private final AdminLimitsService adminLimitsService;
	
	// 특정 회원의 권한 정보 조회 (새로 추가)
	@GetMapping("/getAuthority")
	@ResponseBody // JSON 응답을 위해 추가
	public AdminLimits getAuthority(@RequestParam("mbrCd") String memberCode) {
	    log.info("회원 권한 조회 요청: {}", memberCode);
	    return adminLimitsService.getMemberAuthorityByMemberCode(memberCode);
	}
	
	// 회원 권한 정보 검색 
	@GetMapping("/getAuthority/search")
	@ResponseBody
	public List<AdminLimits> searchAuthority(@RequestParam(required = false) String keyword,
											 @RequestParam(required = false) String levelValue){
		
		return adminLimitsService.selectAdminLimitAuthority(keyword, levelValue);
	}
	
	
	@PostMapping("/updateMemberAuthority")
	@ResponseBody
	public String updateMemberAuthority(@RequestBody AdminLimits adminLimits, HttpSession session) {
	    try {
	        String adminCode = (String) session.getAttribute("SACD");
	        if (adminCode == null) {
	            return "FAIL: Unauthorized";
	        }
	        
	        adminLimits.setAuthorityMdfcnYmdt(new java.sql.Timestamp(System.currentTimeMillis()));

	        int result = adminLimitsService.updateMemberAuthority(adminLimits);
	        return (result > 0) ? "OK" : "FAIL";
	    } catch (Exception e) {
	        log.error("회원 권한 수정 실패", e);
	        return "Error: " + e.getMessage();
	    }
	}

	
	// 제재 사유 등록 처리
	@PostMapping("/registerLimitsReason")
	@ResponseBody
	public String registerLimitsReason(@RequestBody AdminLimits adminLimits, HttpSession session) {
	    try {
	        String adminCode = (String) session.getAttribute("SACD");
	        if (adminCode == null) {
	            return "FAIL: Unauthorized";
	        }
	        adminLimits.setLimitsReasonRegAdmCode(adminCode);

	        int result = adminLimitsService.registerLimitsReason(adminLimits);
	        if (result > 0) {
	            return "OK";
	        } else {
	            return "Failed to register limits reason.";
	        }
	    } catch (Exception e) {
	        log.error("Error registering limits reason", e);
	        return "Error: " + e.getMessage();
	    }
	}

    // 제재 사유 수정 처리
    @PostMapping("/updateLimitsReason")
    @ResponseBody
    public String updateLimitsReason(@RequestBody AdminLimits adminLimits, HttpSession session) {
        try {
            String adminCode = (String) session.getAttribute("SACD");
            if (adminCode == null) {
                return "FAIL: Unauthorized";
            }
            adminLimits.setLimitsReasonMdfcnAdmCode(adminCode);

            int result = adminLimitsService.updateLimitsReason(adminLimits);
            if (result > 0) {
                return "OK";
            } else {
                return "Failed to update limits reason.";
            }
        } catch (Exception e) {
            log.error("Error updating limits reason", e);
            return "Error: " + e.getMessage();
        }
    }

    // 특정 제재 사유 데이터 조회 (수정 모달에 채울 데이터)
    @GetMapping("/getLimitsReason")
    @ResponseBody
    public AdminLimits getLimitsReason(@RequestParam("limitsReasonCode") String limitsReasonCode) {
        return adminLimitsService.getLimitsReasonById(limitsReasonCode);
    }

    // 신고 타입 목록 조회 (AJAX용) - 프론트엔드와 경로 일치
    @GetMapping("/api/declarationTypes") // 경로 수정
    @ResponseBody
    public List<AdminLimits> getDeclarationTypeList() {
        List<AdminLimits> list = adminLimitsService.getDeclarationTypeList();
        log.info("Controller - getDeclarationTypeList: {}", list);
        return list;
    }

    // 신고 사유 목록 조회 (AJAX용, 신고 타입에 따라 필터링) - 프론트엔드와 경로 일치
    @GetMapping("/api/declarationReasons") // 경로 수정
    @ResponseBody
    public List<AdminLimits> getDeclarationReasonList(@RequestParam(value = "declarationTypeCode", required = false) String declarationTypeCode) {
        List<AdminLimits> list = adminLimitsService.getDeclarationReasonList(declarationTypeCode);
        log.info("Controller - getDeclarationReasonList (type: {}): {}", declarationTypeCode, list);
        return list;
    }

    // 제재 타입 목록 조회 (AJAX용) - 새로 추가 및 프론트엔드와 경로 일치
    @GetMapping("/api/limitsTypes") // 경로 추가
    @ResponseBody
    public List<AdminLimits> getLimitsTypeList() {
        List<AdminLimits> list = adminLimitsService.getAdminLimitsTypeList(); // 기존 메서드 재활용 또는 새로운 서비스 메서드 호출
        log.info("Controller - getLimitsTypeList: {}", list);
        return list;
    }

    // 제재 기간 목록 조회 (AJAX용) - 새로 추가 및 프론트엔드와 경로 일치
    @GetMapping("/api/limitsPeriods") // 경로 추가
    @ResponseBody
    public List<AdminLimits> getLimitsPeriodList() {
        List<AdminLimits> list = adminLimitsService.getAdminLimitsPeriodList(); // 기존 메서드 재활용 또는 새로운 서비스 메서드 호출
        log.info("Controller - getLimitsPeriodList: {}", list);
        return list;
    }

    @GetMapping("/adminLimitsResources")
    public String adminLimitsResourcesView(Model model, HttpSession session) {
    	
    	List<String> permissions = (List<String>) session.getAttribute("SPermissions");
		if (!permissions.contains("CS_ADMIN") && !permissions.contains("SYSTEM_ADMIN")) {
			model.addAttribute("msg", "접근 권한이 없습니다.");
			model.addAttribute("url", "/admin"); // 또는 돌아갈 페이지
			return "admin/login/alert"; // alert.html이라는 공용 alert 페이지
		}
        // 제재 자원 등록 페이지
        List<AdminLimits> adminLimitsTypeList = adminLimitsService.getAdminLimitsTypeList();
        List<AdminLimits> adminLimitsPeriodList = adminLimitsService.getAdminLimitsPeriodList();
        List<AdminLimits> adminLimitsReasonList = adminLimitsService.getAdminLimitsReasonList();
        List<AdminLimits> declarationTypeList = adminLimitsService.getDeclarationTypeList();
        
        log.info("Controller - adminLimitsTypeList: {}", adminLimitsTypeList);
        log.info("Controller - adminLimitsPeriodList: {}", adminLimitsPeriodList);
        
        model.addAttribute("title", "제재 자원 등록");
        model.addAttribute("adminLimitsTypeList", adminLimitsTypeList);
        model.addAttribute("adminLimitsPeriodList", adminLimitsPeriodList);
        model.addAttribute("adminLimitsReasonList", adminLimitsReasonList);
        model.addAttribute("declarationTypeList", declarationTypeList);
        
        return "admin/limits/adminLimitsResourcesView";
    }
	
	
	// 제재 기간 등록 처리
	@PostMapping("/registerLimitsPeriod")
	@ResponseBody
	public String registerLimitsPeriod(@RequestBody AdminLimits adminLimits, HttpSession session) {
		try {
			String adminCode = (String) session.getAttribute("SACD");
			if (adminCode == null) {
				return "FAIL: Unauthorized";
			}
			adminLimits.setLimitsPeriodRegAdmCode(adminCode);

			int result = adminLimitsService.registerLimitsPeriod(adminLimits);
			if (result > 0) {
				return "OK";
			} else {
				return "Failed to register limits period.";
			}
		} catch (Exception e) {
			log.error("Error registering limits period", e);
			return "Error: " + e.getMessage();
		}
	}

	// 제재 기간 수정 처리
	@PostMapping("/updateLimitsPeriod")
	@ResponseBody
	public String updateLimitsPeriod(@RequestBody AdminLimits adminLimits, HttpSession session) {
		try {
			String adminCode = (String) session.getAttribute("SACD");
			if (adminCode == null) {
				return "FAIL: Unauthorized";
			}
			adminLimits.setLimitsPeriodMdfcnAdmCode(adminCode); // 수정자 코드 설정

			int result = adminLimitsService.updateLimitsPeriod(adminLimits);
			if (result > 0) {
				return "OK";
			} else {
				return "Failed to update limits period.";
			}
		} catch (Exception e) {
			log.error("Error updating limits period", e);
			return "Error: " + e.getMessage();
		}
	}

	// 특정 제재 기간 데이터 조회 (수정 모달에 채울 데이터)
	@GetMapping("/getLimitsPeriod")
	@ResponseBody
	public AdminLimits getLimitsPeriod(@RequestParam("limitsPeriodCode") String limitsPeriodCode) {
		return adminLimitsService.getLimitsPeriodById(limitsPeriodCode);
	}
	
	// 제재 타입 등록 처리
	@PostMapping("/registerLimitsType")
	@ResponseBody // JSON 또는 문자열 응답을 위해 추가
	public String registerLimitsType(@RequestBody AdminLimits adminLimits, HttpSession session) { // HttpSession 파라미터 추가
		try {
			String adminCode = (String) session.getAttribute("SACD"); // 세션에서 SACD 값 가져오기
			if (adminCode == null) {
				return "FAIL: Unauthorized"; // 세션에 관리자 코드가 없으면 실패 반환
			}
			adminLimits.setLimitsTypeRegAdmCode(adminCode); // 세션에서 가져온 관리자 코드로 설정

			int result = adminLimitsService.registerLimitsType(adminLimits);
			if (result > 0) {
				return "OK";
			} else {
				return "Failed to register limits type.";
			}
		} catch (Exception e) {
			log.error("Error registering limits type", e);
			return "Error: " + e.getMessage();
		}
	}

	// 제재 타입 수정 처리
	@PostMapping("/updateLimitsType")
	@ResponseBody // JSON 또는 문자열 응답을 위해 추가
	public String updateLimitsType(@RequestBody AdminLimits adminLimits, HttpSession session) { // HttpSession 파라미터 추가
		try {
			String adminCode = (String) session.getAttribute("SACD"); // 세션에서 SACD 값 가져오기
			if (adminCode == null) {
				return "FAIL: Unauthorized"; // 세션에 관리자 코드가 없으면 실패 반환
			}
			adminLimits.setLimitsTypeMdfcnAdmCode(adminCode); // 세션에서 가져온 관리자 코드로 설정

			int result = adminLimitsService.updateLimitsType(adminLimits);
			if (result > 0) {
				return "OK";
			} else {
				return "Failed to update limits type.";
			}
		} catch (Exception e) {
			log.error("Error updating limits type", e);
			return "Error: " + e.getMessage();
		}
	}

	// 특정 제재 타입 데이터 조회 (수정 모달에 채울 데이터)
	@GetMapping("/getLimitsType")
	@ResponseBody // JSON 응답을 위해 추가
	public AdminLimits getLimitsType(@RequestParam("limitsTypeCode") String limitsTypeCode) {
		return adminLimitsService.getLimitsTypeById(limitsTypeCode);
	}
	
	@GetMapping("/adminLimitsAuthority")
	public String adminLimitsAuthorityView(Model model, HttpSession session) {
		
		List<String> permissions = (List<String>) session.getAttribute("SPermissions");
		if (!permissions.contains("CS_ADMIN") && !permissions.contains("SYSTEM_ADMIN")) {
			model.addAttribute("msg", "접근 권한이 없습니다.");
			model.addAttribute("url", "/admin"); // 또는 돌아갈 페이지
			return "admin/login/alert"; // alert.html이라는 공용 alert 페이지
		}
		// 권한 설정
		List<AdminLimits> adminLimitsAuthorityList = adminLimitsService.getAdminLimitsAuthorityList();
		model.addAttribute("title", "회원 권한 조회");
		model.addAttribute("adminLimitsAuthorityList", adminLimitsAuthorityList);
		
		return "admin/limits/adminLimitsAuthorityView";
	}
	
	@GetMapping("/adminLimitsProcess")
	public String adminLimitsProcessView(Model model, HttpSession session) {
		// 제재 처리 페이지
		
		List<String> permissions = (List<String>) session.getAttribute("SPermissions");
		if (!permissions.contains("MEMBER_ADMIN") && !permissions.contains("SYSTEM_ADMIN")) {
			model.addAttribute("msg", "접근 권한이 없습니다.");
			model.addAttribute("url", "/admin"); // 또는 돌아갈 페이지
			return "admin/login/alert"; // alert.html이라는 공용 alert 페이지
		}
		model.addAttribute("title", "제재 처리");
		return "admin/limits/adminLimitsProcessView";
	}
	
	@GetMapping("/adminLimits")
	public String adminLimitsView(Model model, HttpSession session) {
		
		List<String> permissions = (List<String>) session.getAttribute("SPermissions");
		if (!permissions.contains("CS_ADMIN") && !permissions.contains("SYSTEM_ADMIN")) {
			model.addAttribute("msg", "접근 권한이 없습니다.");
			model.addAttribute("url", "/admin"); // 또는 돌아갈 페이지
			return "admin/login/alert"; // alert.html이라는 공용 alert 페이지
		}
		// 제재 내역 조회 페이지
		List<AdminLimits> adminLimitsList = adminLimitsService.getAdminLimitsList();
		model.addAttribute("title", "제재 내역 목록");
		model.addAttribute("adminLimitsList", adminLimitsList);
		
		return "admin/limits/adminLimitsView";
	}
	
	@GetMapping("/adminLimits/search")
	@ResponseBody
	public List<AdminLimits> adminLimitsSearch(@RequestParam(required = false) String keyword,
											   @RequestParam(required = false) String levelSearch,
											   @RequestParam(required = false) String startDate,
											   @RequestParam(required = false) String endDate,
											   @RequestParam(required = false) String searchType,
											   @RequestParam(required = false) String selectDateType
											   ){
		return adminLimitsService.selectAdminLimitsList(keyword, searchType, selectDateType, levelSearch, startDate, endDate);
	}
	
	@GetMapping("/adminLimitsResourcesFilter")
	public String adminLimitsResourcesFilter(
	    @RequestParam(required = false, defaultValue = "type") String resourceType,
	    @RequestParam(required = false) String searchField,
	    @RequestParam(required = false) String searchKeyword,
	    @RequestParam(required = false) String status,
	    @RequestParam(required = false) String dateField,
	    @RequestParam(required = false) String startDate,
	    @RequestParam(required = false) String endDate,
	    Model model
	) {
		// 제재 자원 조회 - 필터
	    Map<String, Object> paramMap = new HashMap<>();
	    paramMap.put("resourceType", resourceType);
	    paramMap.put("searchField", searchField);
	    paramMap.put("searchKeyword", searchKeyword);
	    paramMap.put("status", status);
	    paramMap.put("dateField", dateField);
	    paramMap.put("startDate", startDate);
	    paramMap.put("endDate", endDate);
	    
	    log.info("필터 파라미터: {}", paramMap);

	    switch (resourceType) {
	        case "type":
	            model.addAttribute("adminLimitsTypeList", adminLimitsService.getFilteredLimitsTypeList(paramMap));
	            break;
	        case "period":
	            model.addAttribute("adminLimitsPeriodList", adminLimitsService.getFilteredLimitsPeriodList(paramMap));
	            break;
	        case "reason":
	            model.addAttribute("adminLimitsReasonList", adminLimitsService.getFilteredLimitsReasonList(paramMap));
	            break;
	    }
	    
	    log.info("필터 파라미터: {}", paramMap);

	    // 검색 필터 상태 유지
	    model.addAttribute("resourceType", resourceType);
	    model.addAttribute("searchField", searchField);
	    model.addAttribute("searchKeyword", searchKeyword);
	    model.addAttribute("status", status);
	    model.addAttribute("dateField", dateField);
	    model.addAttribute("startDate", startDate);
	    model.addAttribute("endDate", endDate);
	    
	    log.info("필터 파라미터: {}", paramMap);

	    return "admin/limits/adminLimitsResourcesView";
	}
	
}