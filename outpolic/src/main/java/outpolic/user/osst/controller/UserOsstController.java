package outpolic.user.osst.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/user")
public class UserOsstController {
	
	@GetMapping("/userOsstList")
	public String userOutsourcingStatusList() {
		
		return "user/osst/userOutsourcingStatusList";
	}

	@GetMapping("/userOsst")
	public String userOutsourcingStatus() {
		
		return "user/osst/userOutsourcingStatus";
	}
}
