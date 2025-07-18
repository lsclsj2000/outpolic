package outpolic.user.mypage.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.common.domain.Member;
import outpolic.user.inquiry.domain.UserInquiry;
import outpolic.user.inquiry.service.UserInquiryService;
import outpolic.user.login.mapper.UserLoginMapper;
import outpolic.common.dto.OutsourcingReviewDTO;
import outpolic.user.mypage.dto.UserInfoDTO;
import outpolic.user.mypage.service.UserMypageEditService;
import outpolic.user.review.dto.ReviewDTO;
import outpolic.user.settlement.dto.UserSettlement;
import outpolic.user.settlement.service.UserSettlementService;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserMypageController {

	private final PasswordEncoder passwordEncoder;
	private final UserInquiryService userInquiryService;
	
	// 결제내역 데이터를 가져오기 위함
	@Autowired
    private UserSettlementService userSettlementService;

	
 // 유저 마이페이지
 	@GetMapping("/user/mypage")
 	public String myPage(HttpSession session, Model model) {

 		String memberCode = (String) session.getAttribute("SCD");
 		String gradeCode = (String) session.getAttribute("SGrd");
 		if(gradeCode == null ||!"USER".equals(gradeCode)) {
 			model.addAttribute("msg", "접근 권한이 없습니다.");
    		model.addAttribute("url", "/");
    		System.out.println("❌ 접근 권한 없음 → 메인으로 리다이렉트");
    		return "user/mypage/alert";

 		}
 		//회원정보
 	    UserInfoDTO userInfo = userMypageEditService.getUserInfoByCode(memberCode);
 	    model.addAttribute("userInfo", userInfo);
 	    
 	    // 리뷰
 	    List<OutsourcingReviewDTO> reviewList = userMypageEditService.getOutsourcingReviewList(memberCode);
 	    model.addAttribute("reviewList", reviewList);
 	    
 	    // 문의
		List <UserInquiry> inquiryList = userInquiryService.getUserInquiryListByCode(memberCode);
		
		model.addAttribute("title", "문의 내역");
		model.addAttribute("inquiryList", inquiryList);
		
		// 완료 외주 수
		int endedOs = userMypageEditService.selectUserEndedOsByCode(memberCode);
		model.addAttribute("endedOs", endedOs);
		
 	    
 	   List<UserSettlement> settlementList = new ArrayList<>(); // 기본적으로 빈 리스트로 초기화
       if (memberCode != null) {
           try {
               settlementList = userSettlementService.userSettlementInfo(memberCode);
               log.info("조회된 결제 내역 수 (UserMypageController): {}", settlementList != null ? settlementList.size() : 0);
           } catch (Exception e) {
               log.error("결제 내역 조회 중 오류 발생: {}", e.getMessage());
               // 오류 발생 시 빈 리스트를 전달하여 템플릿 오류 방지
               settlementList = new ArrayList<>();
           }
       } else {
           log.warn("세션에 회원 코드(SCD)가 없어 결제 내역을 불러올 수 없습니다.");
       }
       model.addAttribute("settlementList", settlementList); // 뷰에 리스트 전달
 	    
 	    
 		return "user/mypage/userMypageView";
 	}

 	// 유저 개인정보
 	@Autowired
 	private UserMypageEditService userMypageEditService;
 	// 중복체크
    @PostMapping("/check/{type}")
    public ResponseEntity<Boolean> checkUserInfo(@PathVariable String type,
    											HttpSession session,
									            @RequestParam(required = false) String memberNickname,
									            @RequestParam(required = false) String memberEmail,
									            @RequestParam(required = false) String memberTelNo) {
    	String memberCode = (String) session.getAttribute("SCD");
        boolean duplicated = userMypageEditService.isUserInfoDuple(type, memberCode, memberNickname, memberEmail, memberTelNo);
        return ResponseEntity.ok(duplicated);
    }
 	// userEditView 이동
 	@PostMapping("/user/userEditView")
 	public String usreProfileEditView(@RequestParam("password") String memberPw, HttpSession session, Model model) {
 		
 		String memberCode = (String) session.getAttribute("SCD");
 	    UserInfoDTO userInfo = userMypageEditService.getUserInfoByCode(memberCode);
 		
 		if(passwordEncoder.matches(memberPw, userInfo.getMemberPw())) {
 		model.addAttribute("userInfo", userInfo);
 		return "user/mypage/userProfileEditView";
 		}else {
 			model.addAttribute("msg", "비밀번호가 일치하지 않습니다");
 			model.addAttribute("url", "/user/mypage");
 			return "user/mypage/alert";
 		}
 	}

 	 @PostMapping("/userEdit") 
 	 public String userProfileEdit(UserInfoDTO userInfo, 
 			 						Model model ) { 
 	 userMypageEditService.editUserInfo(userInfo);
 	 model.addAttribute("title", "개인정보 수정");
 	 model.addAttribute("userInfo", userInfo);
 	 
 	 return "redirect:/mypage"; 
 	 }
 	 
 	 @GetMapping("/userEdit/info")
 	 @ResponseBody
 	 public UserInfoDTO getUserInfoAjax(HttpSession session) {
  		String memberCode = (String) session.getAttribute("SCD");

 		 
 		 return userMypageEditService.getUserInfoByCode(memberCode); 
 	 }
 	 @PostMapping("/userEdit/update")
 	 @ResponseBody
     public String updateUserInfo(@RequestBody UserInfoDTO userInfo) {
         userMypageEditService.editUserInfo(userInfo);
         return "success";
     }
 	 
 	
}
