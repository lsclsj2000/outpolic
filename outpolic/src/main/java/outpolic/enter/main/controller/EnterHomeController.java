package outpolic.enter.main.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.user.category.service.CategoryService;
import outpolic.user.search.domain.ContentsDetailDTO;
import outpolic.user.search.service.SearchService;

@Controller
@RequestMapping("/enter")
@RequiredArgsConstructor
@Slf4j
public class EnterHomeController {

	// final 필드들
    private final SearchService searchService;
    private final CategoryService categoryService;
    
    @GetMapping("/api/contents/{contentsId}")
    @ResponseBody // 이 어노테이션이 핵심!
    public ResponseEntity<ContentsDetailDTO> getContentsDetailForModal(@PathVariable("contentsId") String contentsId) {
        log.info("모달 데이터 API 요청: ID = {}", contentsId);

        // 기존 서비스 메소드를 그대로 재활용합니다.
        ContentsDetailDTO detailData = searchService.getContentsDetailById(contentsId);

        if (detailData != null) {
            // 데이터가 있으면, DTO와 함께 200 OK 응답을 보냅니다.
            return ResponseEntity.ok(detailData);
        } else {
            // 데이터가 없으면 404 Not Found 응답을 보냅니다.
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/contents/{contentsId}")
    public String contentsDetailView(@PathVariable(value = "contentsId") String contentsId, Model model) {
    	
        
        log.info("상세 페이지 요청: ID = {}", contentsId);


        // 1. 서비스에 contentsId를 넘겨서 상세 정보를 조회합니다.
        ContentsDetailDTO detailData = searchService.getContentsDetailById(contentsId);

        // 2. (중요) 조회된 데이터가 없을 경우에 대한 처리
        if (detailData == null) {
            model.addAttribute("errorMessage", "해당 콘텐츠를 찾을 수 없습니다.");
            return "error/404"; // templates/error/404.html 같은 에러 페이지
        }

        // 3. 조회된 실제 데이터를 "detail" 이라는 이름으로 모델에 담 습니다.
        model.addAttribute("detail", detailData);

        // 4. 상세 페이지 뷰를 리턴합니다.
        return "user/contentsParticular/userContentsParticularView";
    }
}
