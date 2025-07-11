package outpolic.admin.search.controller;




import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.admin.search.domain.AdminContents;
import outpolic.admin.search.service.AdminSearchService;


@Controller
@RequestMapping(value="/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminSearchController {
	
	private final AdminSearchService searchService;
		
	@GetMapping("adminSearch")
	public String SearchControllerView(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword, Model model) {
		
		List<AdminContents> contentsList = searchService.getContentsList(keyword);
		
		model.addAttribute("title", "콘텐츠목록조회");
	    model.addAttribute("contentsList", contentsList);
	    model.addAttribute("initialKeyword", keyword);

		
		return "admin/search/adminSearchView";
	}
	
	@GetMapping("/search/api")
	@ResponseBody
	public List<AdminContents> searchAjax(@RequestParam("keyword") String keyword) {
		return searchService.getContentsList(keyword);
	}
}
