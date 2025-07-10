package outpolic.enter.outsourcingRequest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/page/outsourcing")
public class OutsourcingRequestViewController {
	
	@GetMapping("/outsourcingRequestListView")
	public String outsourcingRequestListView() {
		return "enter/outsourcingRequest/outsourcingRequestListView";
	}
	
	@GetMapping("/receivedRequestListView")
	public String receivedRequestListView() {
		return "enter/outsourcingRequest/receivedRequestListView";
	}
	
	@GetMapping("/requestDetailView")
	public String requestDetailView() {
		return "enter/outsourcingRequest/requestDetailView";
	}
	
}
