package outpolic.user.search.controller;




import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.user.search.domain.UserContents;
import outpolic.user.search.service.UserSearchService;
import outpolic.user.search.service.UserSearchHistoryService;


@Controller
@RequestMapping(value="/user")
@RequiredArgsConstructor
@Slf4j
public class UserSearchController {
	
	@Qualifier("userSearchService")
	private final UserSearchService searchService;
	
	private final UserSearchHistoryService searchHistoryService;
		
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
	public List<UserContents> searchAjax(@RequestParam("keyword") String keyword, HttpSession session) {
		
		// --- 검색어 저장 로직 (여기부터) ---
		// 세션에서 로그인한 사용자의 memberCode (SCD)를 가져옴
		String mbrCd = (String) session.getAttribute("SCD");
		
		// 로그인 상태이고, 검색어가 비어있지 않을 때만 저장
		if (mbrCd != null && keyword != null && !keyword.trim().isEmpty()) {
			log.info("검색어 저장 시도: 사용자 코드 = {}, 검색어 = {}", mbrCd, keyword);
			searchHistoryService.addSearchHistory(mbrCd, keyword);
		}
		// --- 검색어 저장 로직 (여기까지) ---
		
		return searchService.getContentsList(keyword);
	}
}
