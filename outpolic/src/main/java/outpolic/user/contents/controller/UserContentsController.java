package outpolic.user.contents.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping(value="/user")
public class UserContentsController {
	
	// 콘텐츠 리스트 페이지
	@GetMapping("/userContents")
	public String userContentsView() {
		return "user/contents/userContentsView";
	}

	// 콘테츠 상세 페이지
	@GetMapping("/userContentsParticular")
	public String userContentsParticularView() {
		return "user/contentsParticular/userContentsParticularView";
	}

}
