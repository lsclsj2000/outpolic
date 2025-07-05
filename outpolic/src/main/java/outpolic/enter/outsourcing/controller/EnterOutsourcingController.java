package outpolic.enter.outsourcing.controller;

import java.io.IOException;
import java.util.ArrayList; // 추가
import java.util.HashMap;   // 추가
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; // 추가
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid; // 추가
import lombok.RequiredArgsConstructor;
import outpolic.enter.POAddtional.domain.CategorySearchDto;
import outpolic.enter.POAddtional.service.CategorySearchService;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;
import outpolic.enter.outsourcing.service.EnterOutsourcingService;
import outpolic.enter.portfolio.domain.EnterPortfolio;
import outpolic.enter.portfolio.service.EnterPortfolioService;

@Controller
@RequestMapping("/enter/outsourcing")
@RequiredArgsConstructor
public class EnterOutsourcingController {
    
    private final EnterOutsourcingService outsourcingService;
    private final CategorySearchService categorySearchService;
    private final EnterPortfolioService portfolioService; // 주입 받아야 함
    
    
    @GetMapping("/list")
    public String showPortfolioListView() { 
        return "enter/outsourcing/outsourcingListView";
    }
    
    @GetMapping("/listData")
    @ResponseBody
    public ResponseEntity<List<EnterOutsourcing>> getOutsourcingListData(HttpSession session){
        String currentEntCd = "EI_C00001"; // TODO: 실제 세션에서 기업 코드 가져오도록 변경
        return ResponseEntity.ok(outsourcingService.getOutsourcingListByEntCd(currentEntCd));
    }

    @GetMapping("/add")
    public String showAddOutsourcingForm(Model model) {
        model.addAttribute("entCd","EI_C00001"); // TODO: 실제 세션에서 가져오도록 변경
        model.addAttribute("mbrCd", "MB_C0000036"); // TODO: 실제 세션에서 가져오도록 변경
        return "enter/outsourcing/addOutsourcingListView";
    }

    @PostMapping("/add-ajax")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addOutsourcingAjax(
            @Valid @ModelAttribute EnterOutsourcing outsourcing, // @Valid 어노테이션 추가
            BindingResult bindingResult, // BindingResult 파라미터 추가
            @RequestParam(value="categoryCodes", required=false) List<String> categoryCodes,
            @RequestParam(value="tags", required=false) String tags,
            RedirectAttributes redirectAttributes) {
        
        // 유효성 검사 결과 확인
        if (bindingResult.hasErrors()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            // 사용자에게 보여줄 일반적인 오류 메시지
            errorResponse.put("message", "입력 데이터 유효성 검사에 실패했습니다. 모든 필수 항목을 올바르게 채워주세요.");

            List<String> fieldErrors = new ArrayList<>();
            // 각 필드별 오류 메시지를 수집
            bindingResult.getFieldErrors().forEach(error -> {
                fieldErrors.add(error.getField() + ": " + error.getDefaultMessage());
            });
            errorResponse.put("errors", fieldErrors); // 클라이언트에 상세 오류 목록 전달
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // TODO: 실제 세션에서 수정자(관리자) 정보 설정
        outsourcing.setAdmCd("ADM_C0001"); // 임시 하드코딩

        try {
            outsourcingService.addOutsourcing(outsourcing, categoryCodes, tags);
            // 성공 시 JSON 응답과 함께 리다이렉트 URL 전달
            return ResponseEntity.ok(Map.of("success", true, "message", "외주가 성공적으로 등록되었습니다.", "redirectUrl", "/enter/outsourcing/list"));
        } catch (IllegalStateException e) {
            // 비즈니스 로직에서 발생한 예외 처리 (예: 포트폴리오 미등록 등)
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            // 기타 예상치 못한 서버 오류 처리
            e.printStackTrace(); // 개발 중에는 스택 트레이스를 출력하여 디버깅에 도움
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "등록 중 서버 오류가 발생했습니다. 다시 시도해주세요."));
        }
    }
    
    // 2025-07-05 작성
    @GetMapping("/api/categories/search")
    @ResponseBody
    public ResponseEntity<List<CategorySearchDto>> searchCategories(
            @RequestParam(value ="query", required=false, defaultValue="") String query){
        
        return ResponseEntity.ok(categorySearchService.searchCategoriesByName(query));
    }
    
    
    @DeleteMapping("/delete/{osCd}")
    @ResponseBody
    public ResponseEntity<?> deleteOutsourcing(@PathVariable String osCd){
    	try {
    		outsourcingService.deleteOutsourcing(osCd);
    		return ResponseEntity.ok(Map.of("success",true));
    	} catch (Exception e) {
    		e.printStackTrace();
    		return ResponseEntity.badRequest().body(Map.of("success",false,"message",
    				e.getMessage()));
    	}
    }	
    
    @GetMapping("/edit/{osCd}")
    public String showEditOutsourcingForm(@PathVariable String osCd, Model model) {
    	model.addAttribute("outsourcing",outsourcingService.getOutsourcingByOsCd(osCd));
    	return "enter/outsourcing/editOutsourcing";
    }
    
    @PostMapping("/edit")
    public String editOutsourcing(@Valid @ModelAttribute EnterOutsourcing outsourcing,
    							  @RequestParam(value="categoryCodes",required=false) List<String> categoryCodes,
    							  @RequestParam(value="tags", required =false) String tags,
    							  RedirectAttributes redirectAttributes) {
    	try { 
    		outsourcingService.updateOutsourcing(outsourcing, categoryCodes, tags);
    		redirectAttributes.addFlashAttribute("successMessage","수정되었습니다.");
    	} catch (IOException e) {
    		e.printStackTrace();
    		redirectAttributes.addFlashAttribute("errorMessage","수정 중 오류 발생");
    		
    	}
    	return "redirect:/enter/outsourcing/list";
    }
    
    @GetMapping("/api/portfolio/search")
    @ResponseBody
    public ResponseEntity<List<EnterPortfolio>> searchPortfolios(//또는 List<RelatedPortfolioDto>
    		@RequestParam(value="query", required=false, defaultValue="") String query){
    	// EnterPortfolioService에 searchPortfolioByTitle(query) 같은 메서드가 필요합니다.
    	return ResponseEntity.ok(portfolioService.searchPortfoliosByTitle(query));
    }
    		
    
    
}