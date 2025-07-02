package outpolic.user.contents.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import outpolic.user.search.domain.ContentsDetailDTO;
import outpolic.user.search.service.SearchService;


@Controller
@RequestMapping(value="/user")
public class UserContentsController {
	
	private final SearchService searchService;
	
	public UserContentsController(SearchService searchService) {
        this.searchService = searchService;
    }

}
