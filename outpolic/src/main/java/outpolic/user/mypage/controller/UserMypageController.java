package outpolic.user.mypage.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.common.dto.OutsourcingReviewDTO;
import outpolic.systems.file.domain.FileMetaData;
import outpolic.systems.util.FilesUtils;
import outpolic.user.inquiry.domain.UserInquiry;
import outpolic.user.inquiry.service.UserInquiryService;
import outpolic.user.mypage.dto.UserInfoDTO;
import outpolic.user.mypage.service.UserMypageEditService;
import outpolic.user.outsourcing.dto.UserOsInfoDTO;
import outpolic.user.outsourcing.service.UserOutsourcingService;
import outpolic.user.settlement.dto.UserSettlement;
import outpolic.user.settlement.service.UserSettlementService;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserMypageController {

	private final PasswordEncoder passwordEncoder;
	private final UserInquiryService userInquiryService;
	private final UserOutsourcingService userOutsourcingService;
	 private final FilesUtils filesUtils;
	
	// 결제내역 데이터를 가져오기 위함
	@Autowired
    private UserSettlementService userSettlementService;

	
 // 유저 마이페이지
 	@GetMapping("/user/mypage")
 	public String myPage(HttpSession session, Model model, UserInfoDTO userInfoDTO) {

 		String memberCode = (String) session.getAttribute("SCD");
 		String gradeCode = (String) session.getAttribute("SGrd");
 		if(gradeCode == null ||!"USER".equals(gradeCode)) {
 			model.addAttribute("msg", "접근 권한이 없습니다.");
    		model.addAttribute("url", "/");
    		System.out.println("❌ 접근 권한 없음 → 메인으로 리다이렉트");
    		
		if (userInfoDTO.getMemberImg() != null && !userInfoDTO.getMemberImg().isEmpty()) {
	        model.addAttribute("memberImg", "/"+ userInfoDTO.getMemberImg()); // 예: /attachment/mypageProfile/uuid.jpg
	    } else {
	        model.addAttribute("memberImg", null); // 기본 이미지 출력하도록
	    }	
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
		
		//진행 외주 
		List<UserOsInfoDTO> ingOs = userOutsourcingService.UserOsIngSelectByCode(memberCode);
		model.addAttribute("title", "진행중 외주");
		model.addAttribute("ingOs", ingOs);
		
 	    
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
 	 
 	 //회원 프로필사진 변경
 	@Value("${file.path}")
 	private String uploadPath;
 	 
 	@PostMapping("/user/profile/upload")
    public String uploadProfileImage(@RequestParam("profileImage") MultipartFile file,
    								UserInfoDTO userInfoDTO,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
 		System.out.println("🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥");
        String memberCode = (String) session.getAttribute("SCD");

        if (memberCode == null) {
            redirectAttributes.addFlashAttribute("msg", "세션이 만료되었습니다. 다시 로그인해주세요.");
            return "redirect:/login";
        }

        try {
            // ✅ service 구분값으로 "user" 또는 "profile" 등 넣어주기
            FileMetaData fileMetaData = filesUtils.uploadFile(file, "mypageProfile");
            log.info("📦 업로드된 파일 경로: {}", fileMetaData.getFilePath());

            System.out.println("🔥🔥🔥 업로드 파일: " + fileMetaData.getFilePath());
            System.out.println("🔥🔥🔥 프로필 이미지: " + userInfoDTO.getMemberImg());
            // 실제 경로: /attachment/user/날짜/image/uuid.확장자
            // DB에는 이 경로를 저장
            String imagePath =fileMetaData.getFilePath();

            // DB 업데이트
            userMypageEditService.updateProfileImg(memberCode, imagePath);
            log.info("🖼️ 마이페이지 프로필 이미지 경로: {}", userInfoDTO.getMemberImg());

            redirectAttributes.addFlashAttribute("msg", "프로필 사진이 저장되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("msg", "업로드 중 오류가 발생했습니다.");
        }

        return "redirect:/user/mypage";
    }
 	 
 	
}

















