package outpolic.admin.inquiry.controller;

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
import outpolic.admin.inquiry.domain.AdminInquiry;
import outpolic.admin.inquiry.domain.AdminInquiryType;
import outpolic.admin.inquiry.service.AdminInquiryService;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(value="/admin")
public class AdminInquiryController {
	
	private final AdminInquiryService adminInquiryService; 
	
	@PostMapping("/updateInquiryType")
	@ResponseBody
	public String updateInquiryType(@RequestBody AdminInquiryType inquiryType, HttpSession session) {
		// 문의 자원 수정
	    String adminCode = (String) session.getAttribute("SACD");
	    if (adminCode == null) return "FAIL: Unauthorized";

	    inquiryType.setInquiryTypeMdfcnAdm(adminCode);
	    adminInquiryService.updateInquiryType(inquiryType);
	    return "OK";
	}
	
	@GetMapping("/getInquiryTypeDetail")
	@ResponseBody
	public AdminInquiryType getInquiryTypeDetail(@RequestParam String code) {
		// 문의 자원 수정_원본 조회
	    return adminInquiryService.getAdminInquiryTypeByCode(code);
	}
	
	
	@PostMapping("/adminAddInquiryType")
    @ResponseBody
    public String adminAddInquiryType(@RequestBody AdminInquiryType inquiryType, HttpSession session) {
		// 문의 자원 등록
        String adminCode = (String) session.getAttribute("SACD");
        if (adminCode == null) return "FAIL: Unauthorized";

        inquiryType.setInquiryTypeRegAdm(adminCode);
        
        adminInquiryService.insertInquiryType(inquiryType);
        return "OK";
    }
	
	
	@GetMapping("/adminInquiryResources") 
	public String adminInquiryResourcesView(
	    @RequestParam(required = false) String searchField,
	    @RequestParam(required = false) String searchKeyword,
	    @RequestParam(required = false) String status,
	    @RequestParam(required = false) String dateField,
	    @RequestParam(required = false) String startDate,
	    @RequestParam(required = false) String endDate,
	    Model model) {

	    Map<String, Object> paramMap = new HashMap<>();
	    paramMap.put("searchField", searchField);
	    paramMap.put("searchKeyword", searchKeyword);
	    paramMap.put("status", status);
	    paramMap.put("dateField", dateField);
	    paramMap.put("startDate", startDate);
	    paramMap.put("endDate", endDate);

	    List<AdminInquiryType> adminInquiryTypeList = adminInquiryService.getFilteredInquiryTypeList(paramMap);

	    model.addAttribute("adminInquiryTypeList", adminInquiryTypeList);

	    // 필터값도 다시 넣어줌
	    model.addAttribute("searchField", searchField);
	    model.addAttribute("searchKeyword", searchKeyword);
	    model.addAttribute("status", status);
	    model.addAttribute("dateField", dateField);
	    model.addAttribute("startDate", startDate);
	    model.addAttribute("endDate", endDate);

	    return "admin/inquiry/adminInquiryResourcesView"; 
	}

	
	@GetMapping("/adminInquiryMdfcn")
	@ResponseBody
	public AdminInquiry adminInquiryMdfcn(@RequestParam("inquiryCode") String inquiryCode) {
		// 문의 상세 수정 팝업창
		AdminInquiry adminInquiryMdfcn = adminInquiryService.getAdminInquiryMdfcnList(inquiryCode);
		
		return adminInquiryMdfcn;
	}
	
	@GetMapping("/adminInquiryList")
	public String adminInquiryList(
	        @RequestParam(required = false) String searchField,
	        @RequestParam(required = false) String searchKeyword,
	        @RequestParam(required = false) String status,
	        @RequestParam(required = false) String dateField,
	        @RequestParam(required = false) String startDate,
	        @RequestParam(required = false) String endDate,
	        Model model) {

	    Map<String, Object> paramMap = new HashMap<>();
	    paramMap.put("searchField", searchField);
	    paramMap.put("searchKeyword", searchKeyword);
	    paramMap.put("status", status);
	    paramMap.put("dateField", dateField);
	    paramMap.put("startDate", startDate);
	    paramMap.put("endDate", endDate);

	    List<AdminInquiry> inquiryList = adminInquiryService.getFilteredInquiryList(paramMap);

	    model.addAttribute("inquiryList", inquiryList);

	    // 검색 조건도 모델에 추가하여 HTML에서 유지되도록 한다
	    model.addAttribute("searchField", searchField);
	    model.addAttribute("searchKeyword", searchKeyword);
	    model.addAttribute("status", status);
	    model.addAttribute("dateField", dateField);
	    model.addAttribute("startDate", startDate);
	    model.addAttribute("endDate", endDate);

	    return "admin/inquiry/adminInquiryView";  // 또는 해당 Thymeleaf 경로
	}


	
	@PostMapping("/updateInquiry")
	@ResponseBody
	public String updateInquiry(@RequestBody AdminInquiry adminInquiry, HttpSession session) {
	    // 문의수정 저장
	    String adminCode = (String) session.getAttribute("SACD");

	    if (adminCode == null) {
	        return "FAIL: Unauthorized";
	    }

	    adminInquiry.setInquiryMdfcnAdmCode(adminCode);

	    adminInquiryService.updateInquiry(adminInquiry);
	    return "OK";
	}
	
	@PostMapping("/updateInquiryAnswer")
	@ResponseBody
	public String updateInquiryAnswer(@RequestBody AdminInquiry adminInquiry, HttpSession session) {
		
		// 문의답변 저장
	    String adminCode = (String) session.getAttribute("SACD");

	    if (adminCode == null) {
	        return "FAIL: Unauthorized";
	    }

	    adminInquiry.setInquiryProcessMdfcnAdmCode(adminCode);
	    adminInquiry.setInquiryProcessRegAdmCode(adminCode);

	    adminInquiryService.updateInquiryAnswer(adminInquiry);
	    return "OK";
	}
	
}
