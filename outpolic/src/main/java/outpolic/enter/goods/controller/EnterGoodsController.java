package outpolic.enter.goods.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import outpolic.enter.goods.dto.EnterGoodsDTO;
import outpolic.enter.goods.service.EnterGoodsService;
import outpolic.enter.points.dto.EnterPointsDTO;
import outpolic.enter.points.service.EnterPointsService;

@Controller
@RequiredArgsConstructor
public class EnterGoodsController {
	
	private final EnterPointsService enterPointsService;
	private final EnterGoodsService enterGoodsService; // [추가] 상품 서비스 주입
	
	@GetMapping("/enterGoodsList")
	public String enterGoodsListView(HttpSession session, Model model) {
		
		// [추가] DB에서 'CORPORATE' 타입의 상품만, 지정된 순서대로 조회
	    List<EnterGoodsDTO> productList = enterGoodsService.getActiveProductsByTarget("CORPORATE");
	    model.addAttribute("productList", productList);
		
		// --- 기존 마일리지 조회 로직은 그대로 유지 ---
		String memberCode = (String) session.getAttribute("SCD"); 
		if (memberCode != null) { 
			EnterPointsDTO latestPoints = enterPointsService.getEnterLatestPointsStatus(memberCode); 
			if (latestPoints != null) {
				model.addAttribute("availableMileage", latestPoints.getPtsCumPoints());
			} else {
				model.addAttribute("availableMileage", BigDecimal.ZERO);
			}
		} else {
			model.addAttribute("availableMileage", BigDecimal.ZERO);
		}
		return "enter/goods/enterGoodsList";
	}
}
