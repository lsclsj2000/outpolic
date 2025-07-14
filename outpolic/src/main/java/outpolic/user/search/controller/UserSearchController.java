package outpolic.user.search.controller;




import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.user.search.domain.UserContents;
import outpolic.user.search.service.UserSearchService;


@Controller
@RequestMapping(value="/user")
@RequiredArgsConstructor
@Slf4j
public class UserSearchController {
	
	@Qualifier("userSearchService")
	private final UserSearchService searchService;
		
	@GetMapping("/userSearch")
	public String SearchControllerView(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
						               @RequestParam(value = "filter", required = false) String filter,
						               Model model) {
		
		model.addAttribute("title", "콘텐츠목록조회");
	    model.addAttribute("initialKeyword", keyword);
	    model.addAttribute("initialFilter", filter);
	    
		return "user/search/userSearchView";
	}
	
	@GetMapping("/search/api")
	@ResponseBody
	public List<UserContents> searchAjax(@RequestParam("keyword") String keyword) {
		return searchService.getContentsList(keyword);
	}
}
