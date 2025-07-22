package outpolic.enter.contents.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.enter.category.domain.EnterCategory;
import outpolic.enter.category.domain.EnterCategoryGroup;
import outpolic.enter.category.service.EnterCategoryService;
import outpolic.enter.contents.service.EnterContentViewService;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;
import outpolic.enter.outsourcing.service.EnterOutsourcingService;
import outpolic.enter.search.domain.EnterContentItemDTO;
import outpolic.enter.search.domain.EnterContentsDetailDTO;
import outpolic.enter.search.service.EnterSearchService;
import outpolic.user.search.domain.UserContentsDetailDTO;


@Controller
@RequestMapping("/enter")
@RequiredArgsConstructor
@Slf4j
public class EnterContentsController {
	
	// final 필드들
    private final EnterSearchService searchService;
    private final EnterCategoryService categoryService;
    private final EnterOutsourcingService enterOutsourcingService;
    
    private final EnterContentViewService contentViewService;

    
    @GetMapping("/api/contents/{contentsId}")
    @ResponseBody // 이 어노테이션이 핵심!
    public ResponseEntity<EnterContentsDetailDTO> getContentsDetailForModal(
    		@PathVariable("contentsId") String contentsId,
            @SessionAttribute(name = "SCD", required = false) String memberCode) {
        
    	log.info("모달 데이터 API 요청: ID = {}, 사용자 코드 = {}", contentsId, memberCode);

        // 1. URL ID로 상세 정보를 먼저 조회합니다.
        EnterContentsDetailDTO detailData = searchService.getContentsDetailById(contentsId, memberCode);

        // 2. 데이터 존재 여부를 확인합니다.
        if (detailData == null) {
            // 데이터가 없으면 404 Not Found 응답을 보내고 즉시 종료합니다.
            return ResponseEntity.notFound().build();
        }

        // 3. [핵심] DTO에서 조회수 처리에 필요한 실제 ID(cl_cd)를 추출합니다.
        String realContentListCode = detailData.getClCd();
        log.info("조회된 실제 DB ID (cl_cd) for View Count: {}", realContentListCode);

        // 4. 추출한 '올바른' ID로 조회수 및 열람 기록 서비스를 호출합니다.
        contentViewService.processContentView(realContentListCode, memberCode);

        // 5. 이미 조회했던 상세 데이터를 클라이언트에게 반환합니다.
        return ResponseEntity.ok(detailData);
    }
    
    @GetMapping("/contents/{contentsId}")
    public String contentsDetailView(@PathVariable(value = "contentsId") String contentsId,
            @SessionAttribute(name = "SCD", required = false) String memberCode,
            Model model) {
        
        log.info("상세 페이지 요청 (URL ID): ID = {}", contentsId);

        // 1. URL ID로 상세 정보를 조회합니다.
        EnterContentsDetailDTO detailData = searchService.getContentsDetailById(contentsId, memberCode);

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
        return "enter/contentsParticular/enterContentsParticularView";
    }


    

    /**
     * '/enter/contents/{osCd}' 주소를 처리하는 메서드
     */
    @GetMapping("/outsourcing/{osCd}")
    public String showContentsParticularView(@PathVariable String osCd, Model model) {
        log.info("상세 페이지 요청 (outsourcing path): ID = {}", osCd);
        
        EnterOutsourcing outsourcingData = enterOutsourcingService.getOutsourcingByOsCd(osCd);

        if (outsourcingData == null) {
            return "redirect:/enter/outsourcing/list"; 
        }

        // ★ DTO 대신 Map을 생성하여 데이터를 담습니다.
        Map<String, Object> detailMap = new HashMap<>();
        detailMap.put("content_id", outsourcingData.getOsCd());
        detailMap.put("content_type", "Outsourcing");
        detailMap.put("content_title", outsourcingData.getOsTtl());
        detailMap.put("content_body", outsourcingData.getOsExpln());
        detailMap.put("price", outsourcingData.getOsAmt() != null ? outsourcingData.getOsAmt().longValue() : 0L);
        detailMap.put("registration_date", outsourcingData.getOsRegYmdt());
        detailMap.put("registrant_name", outsourcingData.getEntCd()); // 임시로 entCd 사용

        // ★ Map 객체를 모델에 담습니다.
        model.addAttribute("detail", detailMap);

        return "enter/contentsParticular/enterContentsParticularView";
    }
}