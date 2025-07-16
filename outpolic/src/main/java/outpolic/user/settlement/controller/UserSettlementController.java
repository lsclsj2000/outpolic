package outpolic.user.settlement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import outpolic.user.settlement.dto.UserSettlement;
import outpolic.user.settlement.service.UserSettlementService;

@Controller
@Slf4j
public class UserSettlementController {
	@Autowired
	private UserSettlementService userSettlementService;
	
	/*
	 * @GetMapping("/list") public String getUserSettlements(HttpSession session,
	 * Model model) { String mbrCd = (String) session.getAttribute("SCD");
	 * log.info("회원코드 : {}", mbrCd);
	 * 
	 * List<UserSettlement> settlementList =
	 * userSettlementService.userSettlementInfo(mbrCd);
	 * model.addAttribute("settlementList", settlementList); // 뷰에 리스트 전달
	 * 
	 * return "user/mypage/userMypageView"; }
	 */
	
	@GetMapping("#mySettList")
	public String showMypage(HttpSession session, Model model) {
	    String mbrCd = (String) session.getAttribute("SCD");
	    List<UserSettlement> settlements = userSettlementService.userSettlementInfo(mbrCd);
	    model.addAttribute("settlementList", settlements);
	    return "user/mypage/userMypageView";
	}
}
