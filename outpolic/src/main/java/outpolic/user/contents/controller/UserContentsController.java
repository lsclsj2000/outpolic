package outpolic.user.contents.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.user.category.service.UserCategoryService;
import outpolic.user.contents.service.UserContentViewService;
import outpolic.user.search.domain.UserContentsDetailDTO;
import outpolic.user.search.service.UserSearchService;


@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserContentsController {
	
	// final 필드들
    private final UserSearchService searchService;
    private final UserCategoryService categoryService;
    
    // 조회수를 위한 의존성 주입
    private final UserContentViewService contentViewService;
    
    @GetMapping("/api/contents/{contentsId}")
    @ResponseBody
    public ResponseEntity<UserContentsDetailDTO> getContentsDetailForModal(@PathVariable("contentsId") String contentsId) {
        log.info("모달 데이터 API 요청: ID = {}", contentsId);

        // 기존 서비스 메소드를 그대로 재활용합니다.
        UserContentsDetailDTO detailData = searchService.getContentsDetailById(contentsId);

        if (detailData != null) {
            // 데이터가 있으면, DTO와 함께 200 OK 응답을 보냅니다.
            return ResponseEntity.ok(detailData);
        } else {
            // 데이터가 없으면 404 Not Found 응답을 보냅니다.
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/contents/{contentsId}")
    public String contentsDetailView(@PathVariable(value = "contentsId") String contentsId,
            @SessionAttribute(name = "SCD", required = false) String memberCode,
            Model model) {
        
        log.info("상세 페이지 요청 (URL ID): ID = {}", contentsId);

        // 1. URL ID로 상세 정보를 조회합니다.
        UserContentsDetailDTO detailData = searchService.getContentsDetailById(contentsId);

        if (detailData == null) {
            model.addAttribute("errorMessage", "해당 콘텐츠를 찾을 수 없습니다.");
            return "error/404";
        }

        // ===== 2. [핵심 수정] 조회된 detailData 객체에서 실제 DB ID (cl_cd)를 가져옵니다. =====
        String realContentListCode = detailData.getClCd();
        
        log.info("조회된 실제 DB ID (cl_cd): {}", realContentListCode);

        // 3. 이제 '올바른' ID로 조회수 및 열람 기록 서비스를 호출합니다.
        contentViewService.processContentView(realContentListCode, memberCode);
        
        // 4. 모델에 데이터를 담고 뷰를 리턴합니다.
        model.addAttribute("detail", detailData);
        return "user/contentsParticular/userContentsParticularView";
    }
}
