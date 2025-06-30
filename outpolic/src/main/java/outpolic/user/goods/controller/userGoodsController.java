package outpolic.user.goods.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class userGoodsController {
	
	@GetMapping("/userGoodsList")
	public String userGoodsListView() {
		return "user/goods/userGoodsList";
	}
}
