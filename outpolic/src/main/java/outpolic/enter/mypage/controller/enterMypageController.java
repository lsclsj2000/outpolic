package outpolic.enter.mypage.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import outpolic.enter.mypage.dto.EnterInfo;
import outpolic.enter.mypage.service.EnterMypageService;
import outpolic.user.mypage.dto.UserInfoDTO;

@Controller
@RequiredArgsConstructor
public class enterMypageController {
	
	private final EnterMypageService enterMypageService;
	// 기업 마이페이지 
    @GetMapping("/enterMypage")
    public String myPage(HttpSession session, Model model) {
    	String memberId = (String) session.getAttribute("SID");
 	    EnterInfo enterInfo = enterMypageService.getEnterInfoById(memberId);
 	    model.addAttribute("enterInfo", enterInfo);
    	return "enter/mypage/enterMypageView";
    }
    
 // 중복체크
    @PostMapping("/check/enter/{type}")
    public ResponseEntity<Boolean> checkEnterInfo(@PathVariable String type,
    											  HttpSession session,
									              @RequestParam(required = false) String memberNickname,
									              @RequestParam(required = false) String memberEmail,
									              @RequestParam(required = false) String memberTelNo) {
    	String memberId = (String) session.getAttribute("SID");
        boolean duplicated = enterMypageService.isEnterInfoDuple(type, memberId, memberNickname, memberEmail, memberTelNo);
        return ResponseEntity.ok(duplicated);
    }
    
    // 기업 개인정보 수정
    @PostMapping("/enterEditView")
    public String enterProfileEditView(@RequestParam("password") String memberPw, HttpSession session, Model model) {
    	String memberId = (String) session.getAttribute("SID");
    	EnterInfo enterInfo = enterMypageService.getEnterInfoById(memberId);
    	
    	if(memberPw.equals(enterInfo.getMemberPw())) {
    		model.addAttribute("enterInfo", enterInfo);
    		return "enter/mypate/enterProfileEditView";
    	}else {
    		model.addAttribute("msg", "비밀번호가 일치하지 않습니다");
  			model.addAttribute("url", "/enterMypage");
  			return "enter/mypage/alert";
    	}
    }
    
    // 기업 기업소개 페이지
    @GetMapping("/enterEnterpriseInfo")
    public String enterAddData() {
    	return "enter/mypage/enterEnterpriseInfoView";
    }
    // 기업 기업소개 페이지 수정
    @GetMapping("/enterEnterpriseEdit")
    public String enterDataEdit() {
    	return "enter/mypage/enterEnterpriseInfoEditView";
    }
    
    // 살짝 위탁
    @GetMapping("/enterGoodsList")
    public String enterGoodsList() {
    	return "enter/goods/enterGoodsList";
    }

	/*
	 * @GetMapping("/enterPfList") public String enterPortfolio() { return
	 * "enter/portfolio/portfolioListView"; }
	 * 
	 * @GetMapping("/enterPfContract") public String enterPfCntract() { return
	 * "enter/portfolio/portfolioContractListView"; }
	 */
    /*
    @GetMapping("/outsourcingContractListView")
	public String showOutsourcingPage1() {
		return "/enter/outsourcing/outsourcingContractListView"; // 뷰 이름은 그대로 유지하거나 변경
	}    
    @GetMapping("/outsourcingListView") // URL 경로를 더 구체적으로 변경하여 충돌 방지
	public String outsourcingListView() {
		return "enter/outsourcing/outsourcingListView"; // 뷰 이름은 그대로 유지하거나 변경
	}
    @GetMapping("/outsourcingStatusListView") // URL 경로를 더 구체적으로 변경하여 충돌 방지
	public String showOutsourcingPage2() {
		return "/enter/outsourcing/outsourcingStatusListView"; // 뷰 이름은 그대로 유지하거나 변경
	}
	*/

    @GetMapping("/bookMarkListView") // URL 경로를 더 구체적으로 변경하여 충돌 방지
	public String showOutsourcingPage3() {
		return "/enter/bookmark/bookMarkListView"; // 뷰 이름은 그대로 유지하거나 변경
	}
    @GetMapping("/reviewListView") // URL 경로를 더 구체적으로 변경하여 충돌 방지
	public String showOutsourcingPage4() {
		return "/enter/review/reviewListView"; // 뷰 이름은 그대로 유지하거나 변경
	}


    
}
