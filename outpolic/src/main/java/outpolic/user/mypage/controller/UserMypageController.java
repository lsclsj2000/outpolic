package outpolic.user.mypage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import outpolic.user.mypage.dto.UserInfoDTO;
import outpolic.user.mypage.service.UserMypageEditService;

@Controller
public class UserMypageController {

	// 홈 페이지 요청 처리
    @GetMapping("/")
    public String index() {
        return "main"; // templates/index.html
    }
    
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

 	@PostMapping("/userEditView")
 	public String usreProfileEditView(Model model) {

 		UserInfoDTO userInfo = userMypageEditService.getUserInfoById("user002");
 		model.addAttribute("userInfo", userInfo);

 		return "user/mypage/userProfileEditView";
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
