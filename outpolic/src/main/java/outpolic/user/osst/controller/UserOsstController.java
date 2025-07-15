package outpolic.user.osst.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/user")
public class UserOsstController {
	
	@GetMapping("/userOsstList")
	public String userOutsourcingStatusList(Model model) {
		// 진행 외주 목록 조회
		model.addAttribute("title", "진행 외주 목록");
		
		return "user/osst/userOutsourcingStatusList";
	}

	@GetMapping("/userOsst")
	public String userOutsourcingStatus(Model model) {
		// 진행 외주 상세 조회
		model.addAttribute("title", "진행 외주 상세");
		
		return "user/osst/userOutsourcingStatus";
	}
}
