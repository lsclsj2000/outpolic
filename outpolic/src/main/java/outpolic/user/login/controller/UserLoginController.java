package outpolic.user.login.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.common.domain.Member;
import outpolic.systems.util.IpUtils;
import outpolic.user.login.dto.LoginFailDTO;
import outpolic.user.login.mapper.UserLoginMapper;
import outpolic.user.login.service.LoginLimitService;
import outpolic.user.login.service.UserLoginService;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserLoginController {
	
	private final UserLoginService userLoginService;
	private final UserLoginMapper userLoginMapper;
	private final LoginLimitService loginLimitService;

	
    // 로그인 페이지 GET 요청
    @GetMapping("/login")
    public String showLoginPage() {
        return "user/login/userLoginView"; 
    }

    // 로그인 요청 처리 (POST)
    @PostMapping("/login")
    public String login(@RequestParam String memberId,
                        @RequestParam String memberPw,
                        Member member,
                        HttpSession session,
                        RedirectAttributes redirectAttributes,
                        HttpServletRequest request,
                        Model model) {
   	  
    	  Map<String, Object> loginResult = userLoginService.loginUser(memberId, memberPw);
    	  boolean isMatched = (boolean) loginResult.get("isMatched");
    	  String clientIp = IpUtils.getClientIp(request);
    	  String redirectUri = "redirect:/login";
        
        if (isMatched) {
        	Member memberInfo = (Member) loginResult.get("memberInfo");
        	String status = memberInfo.getStatusCode();
        	String grade = memberInfo.getGradeCode();
        	
        	userLoginMapper.insertLoginHistory(userLoginMapper.getNextLoginHistoryCode(), 
        										memberInfo.getMemberCode(), 
        										clientIp);
        	
        	log.info("로그인 등급: {}", grade);
        	
        	// 세션등록
            if("SD_ACTIVE".equals(status)) {
            	
            	session.invalidate();
            	
            	session = request.getSession(true);
            	
            	session.setAttribute("SID", memberInfo.getMemberId());
            	session.setAttribute("SName", memberInfo.getMemberName());
            	session.setAttribute("SGrd", memberInfo.getGradeCode());
            	session.setAttribute("SCD", memberInfo.getMemberCode());
                //날짜 업데이트
                userLoginService.updateLoginDate(memberInfo);    
                log.info("로그인 날짜 업데이트 호출: {}", member.getMemberId());
                // 회원 프로필 이미지 
                String profileImg = memberInfo.getMemberImg();
                session.setAttribute("SProfilePath", 
                	    (profileImg != null) ?  profileImg : "/user/assets/imgs/outpolic/cutecat.jpg");
                
	            if("USER".equals(grade)) {
	            	
	            	String prevPage = (String) session.getAttribute("prevPage");
	                session.removeAttribute("prevPage");
	                String redirectUrl = (prevPage != null) ? prevPage : "/";
	            	
	            	redirectAttributes.addFlashAttribute("success", "로그인에 성공하였습니다");
	            	log.info("로그인성공");    	
	                model.addAttribute("msg", "로그인에 성공하였습니다.");
	                model.addAttribute("url", redirectUrl);
	                log.info("prevPage = {}", prevPage);
                    log.info("최종 redirectUrl = {}", redirectUrl);
	            	return "user/mypage/alert";
	            	
	        	}else if("ENTER".equals(grade)) {
	        		
	        		String prevPage = (String) session.getAttribute("prevPage");
	                session.removeAttribute("prevPage");
	                String redirectUrl = "/enter";
	                
                    if (prevPage != null) {
                        if (prevPage.startsWith("/user/userSearch")) {
                            // 특별히 대응해야 하는 케이스
                        	String[] parts = prevPage.split("\\?", 2); // [0]=uri, [1]=query
                            redirectUrl = "/enter/enterSearch";

                            if (parts.length == 2) {
                                redirectUrl += "?" + parts[1]; // 쿼리 붙여줌
                            }

                        } else if (prevPage.startsWith("/user/products")) {
                            // 일반적인 경우: /user/ → /enter/
                            redirectUrl = prevPage.replaceFirst("/user/", "/enter/");
                        } else if (prevPage.startsWith("/user/contents")) {
                        	log.info("✅ prevPage matched /user/contents: {}", prevPage);
                            // 일반적인 경우: /user/ → /enter/
                            redirectUrl = prevPage.replaceFirst("^/user/", "/enter/");
                            log.info("➡️ redirectUrl = {}", redirectUrl);
                        } else if(prevPage.startsWith("/userGoodsList")) {
                        	redirectUrl = prevPage.replaceFirst("/userGoodsList", "/enterGoodsList");
                        }else {
                            // prevPage는 있지만 매칭 안되면 기본 경로
                            redirectUrl = "/enter";
                        }
                    }    
                    log.info("prevPage = {}", prevPage);
                    log.info("최종 redirectUrl = {}", redirectUrl);
	            	redirectAttributes.addFlashAttribute("success", "로그인에 성공하였습니다");
	            	log.info("로그인성공");
	                model.addAttribute("msg", "기업회원 로그인에 성공하였습니다.");
	                model.addAttribute("url", redirectUrl);
	                
	            	return "user/mypage/alert";
	        	}else {
	        		model.addAttribute("msg", "올바르지못한 로그인 경로입니다");
	        		model.addAttribute("url", "/login");
	        		return "user/mypage/alert";
	        	}
            }else if("SD_DORMANT".equals(status)) {
            	model.addAttribute("msg", "휴면을 해제 후 로그인해주세요");
        		model.addAttribute("url", "/login");
        		return "user/mypage/alert";
            }else if("SD_LIMIT".equals(status)) {
            	
            	LoginFailDTO limitInfo = loginLimitService.getLimitInfo(memberInfo.getMemberCode());
            	
            	if (limitInfo != null) {
            		
                    model.addAttribute("msg", "회원님의 계정은 제재 상태입니다.");
                    model.addAttribute("startbanDate", limitInfo.getLmtStartYmdt());  // 예: "2025-08-01"
                    model.addAttribute("unbanDate", limitInfo.getLmtEndYmdt());  // 예: "2025-08-01"
                    model.addAttribute("url", "/");
                    return "LimitInfo";  // 별도 페이지 or 기존 alert 재활용
                } else {
                    // 혹시 제재 기록 없음 → 로그인 차단
                    model.addAttribute("msg", "접속이 제한된 계정입니다. 고객센터에 문의하세요.");
                    model.addAttribute("url", "/");
                    return "user/mypage/alert";
                }
            }else {
            	model.addAttribute("msg", "로그인하실 수 없습니다.\n문의사항이 있을 경우 페이지 하단의 고객센터 연락처를 이용해주세요.");
        		model.addAttribute("url", "/login");
        		return "user/mypage/alert";
            }
        } else {
        	model.addAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
        	redirectAttributes.addFlashAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
        	return "user/login/userLoginView";
        }
    }
    
    //로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session, Model model) {
    	String memberCode = (String) session.getAttribute("SCD");
    	if(memberCode != null) {
    	    String loginHistoryCode = userLoginService.getLastLoginCode(memberCode);
    	    userLoginService.updateLogoutHistory(loginHistoryCode);
    		session.invalidate();  // 세션 전체 제거
    		model.addAttribute("msg", "로그아웃되었습니다.");
    		model.addAttribute("url", "/");
    		
    	}else {
    		model.addAttribute("msg", "로그인 상태가 아닙니다.");
    		model.addAttribute("url", "/");
    	}
		return "user/mypage/alert";
    }
    
    
}
