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
	
	
	// 콘텐츠 리스트 페이지
	@GetMapping("/userContents")
	public String userContentsView() {
		return "user/contents/userContentsView";
	}

	/**
	 * 콘텐츠 상세 페이지 요청을 처리하는 메서드
	 */
	@GetMapping("/contents/{contentsId}") // URL을 /contents/{id} 형태로 변경
	public String userContentsParticularView(@PathVariable("contentsId") String contentsId, Model model) {

	    // 1. 서비스를 호출하여 DB에서 상세 데이터를 가져옵니다.
		ContentsDetailDTO detail = searchService.getContentsDetailById(contentsId);

	    // 2. (안전장치) 만약 ID에 해당하는 데이터가 없으면, 에러 페이지를 보여줍니다.
	    if (detail == null) {
	        // 간단하게는 이렇게 처리하거나, 전용 에러 페이지로 보낼 수 있습니다.
	        // return "error/404";
	        // 지금은 우선 빈 페이지가 뜨지 않도록 막는 역할만 합니다.
	        // 또는 예외를 던져서 전역 예외 처리기에서 처리할 수도 있습니다.
	        throw new RuntimeException("해당 콘텐츠를 찾을 수 없습니다: " + contentsId);
	    }

	    // 3. 조회된 데이터를 모델에 담습니다.
	    //    HTML에서는 "detail"이라는 이름으로 이 객체에 접근할 수 있습니다.
	    model.addAttribute("detail", detail);

	    // 4. 보여줄 HTML 파일의 경로를 반환합니다.
	    return "user/contentsParticular/userContentsParticularView";
	}

}
