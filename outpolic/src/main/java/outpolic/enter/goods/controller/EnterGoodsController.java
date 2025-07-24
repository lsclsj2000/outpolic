package outpolic.enter.goods.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import outpolic.enter.points.dto.EnterPointsDTO;
import outpolic.enter.points.service.EnterPointsService;

@Controller
public class EnterGoodsController {
	
	@Autowired
	private EnterPointsService enterPointsService; // EnterPointsService 주입
	
	@GetMapping("/enterGoodsList")
	public String userGoodsListView(HttpSession session, Model model) {
		
		// 세션에서 회원 코드를 'memberCode' 변수에 저장합니다.
		String memberCode = (String) session.getAttribute("SCD"); 
		
		// 회원의 마일리지 정보 조회
		// 'memberCode' 변수를 사용하여 마일리지 서비스를 호출합니다.
		if (memberCode != null) { 
			EnterPointsDTO latestPoints = enterPointsService.getEnterLatestPointsStatus(memberCode); 
			if (latestPoints != null) {
				// 조회된 누적 마일리지를 availableMileage라는 이름으로 모델에 추가
				model.addAttribute("availableMileage", latestPoints.getPtsCumPoints());
			} else {
				// 마일리지 내역이 없는 경우 0으로 설정
				model.addAttribute("availableMileage", BigDecimal.ZERO);
			}
		} else {
			// 로그인하지 않은 경우 0으로 설정 (또는 로그인 페이지로 리다이렉트 등 처리)
			model.addAttribute("availableMileage", BigDecimal.ZERO);
		}
		return "enter/goods/enterGoodsList";
	}
}
