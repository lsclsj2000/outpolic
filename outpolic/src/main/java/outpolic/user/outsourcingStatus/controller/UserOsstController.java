package outpolic.user.outsourcingStatus.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/user")
public class UserOsstController {
	
	@GetMapping("/userOutsourcingStatusList")
	public String userOutsourcingStatusList() {
		
		return "user/osst/userOutsourcingStatusList";
	}

	@GetMapping("/userOutsourcingStatus")
	public String userOutsourcingStatus() {
		
		return "user/osst/userOutsourcingStatus";
	}
}
