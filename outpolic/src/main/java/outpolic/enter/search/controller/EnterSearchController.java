package outpolic.enter.search.controller;




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
import outpolic.enter.search.domain.EnterContents;
import outpolic.enter.search.service.EnterSearchService;


@Controller
@RequestMapping(value="/enter")
@RequiredArgsConstructor
@Slf4j
public class EnterSearchController {
	
	@Qualifier("enterSearchService")
	private final EnterSearchService searchService;
		
	@GetMapping("/enterSearch")
	public String SearchControllerView(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
						               @RequestParam(value = "filter", required = false) String filter,
						               Model model) {
			
		model.addAttribute("title", "콘텐츠목록조회");
		model.addAttribute("initialFilter", filter);
	    model.addAttribute("initialKeyword", keyword);

		
		return "enter/search/enterSearchView";
	}
	
	@GetMapping("/search/api")
	@ResponseBody
	public List<EnterContents> searchAjax(@RequestParam("keyword") String keyword) {
		return searchService.getContentsList(keyword);
	}
}
