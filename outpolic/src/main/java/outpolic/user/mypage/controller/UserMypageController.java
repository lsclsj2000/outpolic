package outpolic.user.mypage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import outpolic.user.mypage.dto.UserInfoDTO;
import outpolic.user.mypage.service.UserMypageEditService;

@Controller
public class UserMypageController {


 // 유저 마이페이지
 	@GetMapping("/mypage")
 	public String myPage(Model model) {
 		UserInfoDTO userInfo = userMypageEditService.getUserInfoById("user002");
 		model.addAttribute("userInfo", userInfo);
 		return "user/mypage/userMypageView";
 	}

 	// 유저 개인정보
 	@Autowired
 	private UserMypageEditService userMypageEditService;
 	// 중복체크
    @PostMapping("/check/{type}")
    public ResponseEntity<Boolean> checkUserInfo(@PathVariable String type,
									            @RequestParam String memberId,
									            @RequestParam(required = false) String memberNickName,
									            @RequestParam(required = false) String memberEmail,
									            @RequestParam(required = false) String memberTelNo) {
        boolean duplicated = userMypageEditService.isUserInfoDuple(type, memberId, memberNickName, memberEmail, memberTelNo);
        return ResponseEntity.ok(duplicated);
    }
 	// userEditView 이동
 	@PostMapping("/userEditView")
 	public String usreProfileEditView(@RequestParam("password") String memberPw, Model model) {
 		
 		String memberId = "user002";
 		UserInfoDTO userInfo = userMypageEditService.getUserInfoById(memberId);
 		
 		if(memberPw.equals(userInfo.getMemberPw())) {
 		model.addAttribute("userInfo", userInfo);
 		return "user/mypage/userProfileEditView";
 		}else {
 			model.addAttribute("msg", "비밀번호가 일치하지 않습니다");
 			model.addAttribute("url", "/mypage");
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
 	 public UserInfoDTO getUserInfoAjax() {
 		UserInfoDTO userInfo = userMypageEditService.getUserInfoById("user002");
 		 
 		 return userInfo; 
 	 }
 	 @PostMapping("/userEdit/update")
 	 @ResponseBody
     public String updateUserInfo(@RequestBody UserInfoDTO userInfo) {
         userMypageEditService.editUserInfo(userInfo);
         return "success";
     }
 	 
 	
}
