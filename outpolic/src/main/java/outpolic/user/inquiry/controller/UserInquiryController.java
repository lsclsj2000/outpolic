package outpolic.user.inquiry.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import outpolic.user.inquiry.domain.UserInquiry;
import outpolic.user.inquiry.domain.UserInquiryType; // ✅ UserInquiryType DTO 임포트 추가
import outpolic.user.inquiry.service.UserInquiryService;

@Controller
@RequestMapping(value="/user")
@RequiredArgsConstructor
public class UserInquiryController {
	
	private final UserInquiryService userInquiryService;
	
	@PostMapping("/userInquiryWrite")
	public String adduserInquiryWrite(UserInquiry inquiry, RedirectAttributes redirect) {
	    
	    inquiry.setMemberCode("MB_C0000041");
	    inquiry.setInquiryCode("INQ" + System.currentTimeMillis()); 
	    
	    userInquiryService.adduserInquiryWrite(inquiry);
	    redirect.addFlashAttribute("msg", "문의가 등록되었습니다.");
	    
	    return "redirect:/user/userInquiryList";
	}
	
	@GetMapping("/userInquiryDetail")
	public String userInquiryDetailView(@RequestParam("iq_cd") String inquiryCode, Model model) {
		// 문의 상세내용 조회
		UserInquiry detail = userInquiryService.getUserInquiryByCode(inquiryCode);
	    model.addAttribute("inquiry", detail);
	    return "user/inquiry/userInquiryDetailView";
	}

	
	@GetMapping("/userInquiryList")
	public String userInquiryListView(Model model) {
		// 문의 목록 조회
		var inquiryList = userInquiryService.getUserInquiryList();
		
		model.addAttribute("title", "문의");
		model.addAttribute("inquiryList", inquiryList);
		
		return "user/inquiry/userInquiryListView";
	}
	
	@GetMapping("/userInquiryNotice")
	public String userInquiryNoticeView() {
		// 공지사항 게시판 조회
		return "user/inquiry/userInquiryNoticeView";
	}
	
	@GetMapping("/userInquiryTotal")
	public String userInquiryTotalView() {
		// 전체 게시판 조회
		return "user/inquiry/userInquiryTotalView";
	}
	
	@GetMapping("/userInquiryFaq")
	public String userInquiryFaqView() {
		// 자주 묻는 질문
		return "user/inquiry/userInquiryFqaView";
	}
	
	@GetMapping("/userInquiryWrite") // 문의 작성 페이지 (GET 요청)
	public String userInquiryWriteView(Model model) {
		// 문의 작성 페이지에 문의 유형 목록을 전달
		// ✅ userInquiryService.getUserInquiryTypeByCode("") 대신 getAllInquiryTypes() 사용
		List<UserInquiryType> inquiryTypeList = userInquiryService.getAllInquiryTypes();
		model.addAttribute("inquiryTypeList", inquiryTypeList); // 모델에 "inquiryTypeList"로 추가
		model.addAttribute("inquiry", new UserInquiry()); // 폼 바인딩을 위한 빈 UserInquiry 객체 추가
		
		return "user/inquiry/userInquiryWriteView";
	}
}