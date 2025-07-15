package outpolic.admin.points.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminPointsController {
	
	// 마일리지 사용 내역
	@GetMapping("/usedPointsList")
	public String usedPointsListView() {
		
		return "admin/points/adminUsedPointsListView";
	}
	
	// 마일리지 적립 내역
	@GetMapping("/earnPointsList")
	public String earnPointsView() {
		
		return "admin/points/adminEarnPointsView";
	}
	
	// 마일리지 기준 생성
	@GetMapping("/standardPointsAddList")
	public String standardPointsAddView() {
		
		return "admin/points/adminStandardPointsAddView";
	}
}
