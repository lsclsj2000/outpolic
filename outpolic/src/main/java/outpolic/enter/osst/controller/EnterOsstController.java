package outpolic.enter.osst.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/enter")
public class EnterOsstController {
	
	@GetMapping("/enterOsstList")
	public String enterOutsourcingStatusList() {
		
		return "enter/osst/enterOutsourcingStatusList";
	}

	@GetMapping("/enterOsst")
	public String enterOutsourcingStatus() {
		
		return "enter/osst/enterOutsourcingStatus";
	}
}
