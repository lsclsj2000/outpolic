package outpolic.user.bookmark.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.user.bookmark.dto.UserBookmarkViewDTO;
import outpolic.user.bookmark.service.UserBookmarkService;


@Controller
@RequiredArgsConstructor
@Slf4j
public class UserBookmarkController {
	
	private final UserBookmarkService userBookmarkService;

	@GetMapping("/bookmark")
	public String bookmarkView(HttpSession session, Model model) {
		String memberCode = (String) session.getAttribute("SCD");
		if (memberCode == null) {
            model.addAttribute("msg", "로그인이 필요합니다.");
            model.addAttribute("url", "/login");
            return "user/mypage/alert";
        }
		
		List<UserBookmarkViewDTO> osBookmarks = userBookmarkService.getBookmarkOsByCode(memberCode);
        List<UserBookmarkViewDTO> poBookmarks = userBookmarkService.getBookmarkPoByCode(memberCode);
        List<UserBookmarkViewDTO> eiBookmarks = userBookmarkService.getBookmarkEiByCode(memberCode);
		
        model.addAttribute("osBookmarks", osBookmarks);
        model.addAttribute("poBookmarks", poBookmarks);
        model.addAttribute("eiBookmarks", eiBookmarks);
        
        return "user/bookmark/userBookmarkView";
	}
	
 	 
 	
}
