package outpolic.admin.points.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import outpolic.admin.points.dto.AdminPointsHistoryDTO;
import outpolic.admin.points.dto.AdminPointsStandardDTO;
import outpolic.admin.points.service.AdminPointsService;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminPointsController {
	
	private final AdminPointsService adminPointsService;
	
	// 마일리지 적립 내역 페이지
	@GetMapping("/earnPointsList")
	public String earnPointsView(Model model) {
		// 페이지 첫 로드 시 전체 '적립' 목록 조회
		List<AdminPointsHistoryDTO> earnList = adminPointsService.getEarnHistory(new HashMap<>());
		model.addAttribute("earnList", earnList);
		return "admin/points/adminEarnPointsView";
	}
	
	// 마일리지 적립 내역 조회 API (검색 기능)
	@GetMapping("/api/earnPointsList")
	@ResponseBody
	public List<AdminPointsHistoryDTO> getEarnPointsListApi(@RequestParam Map<String, Object> params) {
		return adminPointsService.getEarnHistory(params);
	}
	
	/**
	 * 마일리지 기준 생성 페이지
	 */
	@GetMapping("/standardPointsAddList")
	public String standardPointsAddView(Model model, HttpSession session) {
	    // 페이지 첫 로드 시 전체 기준 목록 조회
	    List<AdminPointsStandardDTO> standardList = adminPointsService.getStandardList(new HashMap<>());
	    model.addAttribute("standardList", standardList);

	    // 세션에서 관리자 코드를 가져와 뷰로 전달
	    String adminCode = (String) session.getAttribute("SACD");
	    model.addAttribute("adminCode", adminCode);
	    
	    return "admin/points/adminStandardPointsAddView";
	}
	
	
	// 마일리지 기준 목록 조회 (검색 기능)
	@GetMapping("/api/standards")
	@ResponseBody
	public List<AdminPointsStandardDTO> getStandardListApi(
	        @RequestParam(required = false) String searchKeyword,
	        @RequestParam(required = false) String filterStatus,
	        @RequestParam(required = false) Integer minAmount,
	        @RequestParam(required = false) Integer maxAmount) {
	    
	    Map<String, Object> params = new HashMap<>();
	    params.put("searchKeyword", searchKeyword);
	    params.put("filterStatus", filterStatus);
	    params.put("minAmount", minAmount);
	    params.put("maxAmount", maxAmount);
	    
	    return adminPointsService.getStandardList(params);
	}
	
	// 마일리지 기준 상세 조회
	@GetMapping("/api/standardDetail")
	@ResponseBody
	public AdminPointsStandardDTO getStandardDetail(@RequestParam String epCd) {
	    return adminPointsService.getStandardByCode(epCd);
	}
	
	// 마일리지 기준 등록
	@PostMapping("/standards/register")
	@ResponseBody
	public ResponseEntity<String> registerStandard(@RequestBody AdminPointsStandardDTO dto, HttpSession session) {
	    String adminCode = (String) session.getAttribute("SACD");
	    if (adminCode == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요한 기능입니다.");
	    }
	    try {
	        dto.setEpRegAdmCd(adminCode);
	        adminPointsService.registerStandard(dto);
	        return ResponseEntity.ok("새로운 마일리지 기준이 등록되었습니다.");
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body("등록 중 오류가 발생했습니다.");
	    }
	}
	
	// 마일리지 기준 수정
	@PostMapping("/standards/update")
	@ResponseBody
	public ResponseEntity<String> updateStandard(@RequestBody AdminPointsStandardDTO dto, HttpSession session) {
	    String adminCode = (String) session.getAttribute("SACD");
	    if (adminCode == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요한 기능입니다.");
	    }
	    try {
	        // DTO에 수정자 코드를 설정
	        dto.setEpMdfcnAdmCd(adminCode);
	        adminPointsService.updateStandard(dto);
	        return ResponseEntity.ok("마일리지 기준이 수정되었습니다.");
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body("수정 중 오류가 발생했습니다.");
	    }
	}
}
