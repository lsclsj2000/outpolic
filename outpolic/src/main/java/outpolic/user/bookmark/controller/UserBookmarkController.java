package outpolic.user.bookmark.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Controller
@RequiredArgsConstructor
@Slf4j
public class UserBookmarkController {

	@GetMapping("/user/bookmark")
	public String bookmarkView(HttpSession session, Model model) {
		String memberCode = (String) session.getAttribute("SCD");
		
		return "/user/bookmark/userBookmarkView";
	}
	
 	 
 	
}
