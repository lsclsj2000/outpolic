package outpolic.user.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/")
public class UserHomeController {
	
	@GetMapping("")
	public String MainView() {
		
		return "main";
	}
	
	@GetMapping("/userListpage")
	public String userListpageView() {
		
		return "user/main/userListpageView";
	}
}
