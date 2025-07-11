package outpolic.admin.goods.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminGoodsController {
	
	@GetMapping("/goodsList")
	public String goodsListView() {
		
		return "admin/goods/adminGoodsSelectView";
	}
	
	@GetMapping("/goodsAdd")
	public String addGoodsView() {
		
		return "admin/goods/adminGoodsAddView";
	}
}
