package outpolic.user.goods.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import outpolic.user.goods.dto.UserGoodsDTO;
import outpolic.user.goods.service.UserGoodsService;
import outpolic.user.points.dto.UserPointsDTO;
import outpolic.user.points.service.UserPointsService;

@Controller
public class UserGoodsController {
	
	@Autowired
	private UserPointsService userPointsService; // UserPointsService 주입
	
	@Autowired
	private UserGoodsService userGoodsService;
	
	@GetMapping("/userGoodsList")
	public String userGoodsListView(HttpSession session, Model model) {
		
		// 'GENERAL' 타입의 상품을 지정된 순서대로 조회
		List<UserGoodsDTO> productList = userGoodsService.getActiveProductsByTarget("GENERAL");
	    model.addAttribute("productList", productList);
	    
	    // --- 기존 마일리지 조회 로직은 그대로 유지 ---
	    String memberCode = (String) session.getAttribute("SCD"); 
	    if (memberCode != null) { 
	        UserPointsDTO latestPoints = userPointsService.getUserLatestPointsStatus(memberCode); 
	        if (latestPoints != null) {
	            model.addAttribute("availableMileage", latestPoints.getPtsCumPoints());
	        } else {
	            model.addAttribute("availableMileage", BigDecimal.ZERO);
	        }
	    } else {
	        model.addAttribute("availableMileage", BigDecimal.ZERO);
	    }
	    return "user/goods/userGoodsList";
	}
}
