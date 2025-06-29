package outpolic.user.mypage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import outpolic.user.mypage.domain.UserInfoDTO;
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

	/*
	 * // 로그인 페이지 GET 요청
	 * 
	 * @GetMapping("/pageLogin") public String showLoginPage() { return
	 * "user/login/userLoginView"; // templates/page-login.html }
	 * 
	 * // 로그인 요청 처리 (POST)
	 * 
	 * @PostMapping("/pageLogin") public String login(@RequestParam("email") String
	 * email,
	 * 
	 * @RequestParam("password") String password, HttpSession session, Model model)
	 * {
	 * 
	 * // TODO: 실제 로그인 검증 로직 구현 (DB 연동 등) if (email.equals("test@example.com") &&
	 * password.equals("1234")) { session.setAttribute("loginUser", email); return
	 * "redirect:/"; // 로그인 성공 시 홈으로 } else { model.addAttribute("error",
	 * "이메일 또는 비밀번호가 올바르지 않습니다."); return "pagelogin"; // 로그인 실패 시 다시 로그인 페이지로 } }
	 */

	/*
	 * // 비밀번호 재설정 페이지
	 * 
	 * @GetMapping("/forgotPswd") public String forgotPasswordPage() { return
	 * "user/login/userForgotPswdView"; // templates/page-forgot-password.html }
	 */

	/*
	 * // 회원가입 페이지 시작
	 * 
	 * @GetMapping("/pageRegister") public String mainRegPage() { return
	 * "user/register/userRegisterMainView"; // templates/page-register.html }
	 * 
	 * @GetMapping("/choiceRegister") public String choiceReg() { return
	 * "user/register/userRegisterChoiceView"; }
	 * 
	 * @GetMapping("/userRegister") public String userReg() { return
	 * "user/register/userRegisterView"; }
	 * 
	 * @GetMapping("/enterRegAdd") public String enterRegAdd() { return
	 * "enter/register/enterRegisterAddView"; }
	 * 
	 * @GetMapping("/enterRegister") public String enterReg() { return
	 * "enter/register/enterRegisterView"; }
	 */
    
	/*
	 * @PostMapping("/pageRegister") public String register(
	 * 
	 * @RequestParam("email") String email,
	 * 
	 * @RequestParam("password") String password, Model model) {
	 * 
	 * // TODO: 실제 DB에 회원 저장 로직 System.out.println("회원가입: " + email + ", " +
	 * password);
	 * 
	 * // 가입 성공 시 로그인 페이지로 리디렉션 return "redirect:/pageLogin"; }
	 */
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
