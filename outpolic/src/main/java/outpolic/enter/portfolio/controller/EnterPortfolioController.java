package outpolic.enter.portfolio.controller;

import outpolic.enter.POAddtional.domain.CategorySearchDto;
import outpolic.enter.POAddtional.service.CategorySearchService;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;
import outpolic.enter.portfolio.domain.EnterPortfolio;
import outpolic.enter.portfolio.service.EnterPortfolioService;
import outpolic.systems.util.FilesUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/enter/portfolio")
@RequiredArgsConstructor
public class EnterPortfolioController {
	


    private final EnterPortfolioService portfolioService;
    private final CategorySearchService categorySearchService;
   
    /**
     * 특정 기업의 포트폴리오 개수를 조회하는 API (UX개선용)
     * @Param session 현재 세션 (기업 코드 가져오기 위함)
     * @return 포트폴리오 개수(정수)
     */
    @GetMapping("/api/countByEntCd") 
    @ResponseBody
    public ResponseEntity<Integer> countPortfolioForEnterprise(HttpSession session) {
    	// TODO: 실제 세션에서 로그인한 기업의 entCd를 가져와야 합니다.
    	String entCd = "EI_C00001"; 
    	
    	int count = portfolioService.countPortfoliosByEntCd(entCd);
    	return ResponseEntity.ok(count);
    }
    

    @GetMapping("/list")
    public String showPortfolioListView() { return "enter/portfolio/portfolioListView"; }

    @GetMapping("/listData")
    @ResponseBody
    public ResponseEntity<List<EnterPortfolio>> getPortfolioListData(HttpSession session) {
        String currentEntCd = "EI_C00001"; // TODO: 실제 세션에서 기업 코드 가져오기
        return ResponseEntity.ok(portfolioService.getPortfolioListByEntCd(currentEntCd));
    }

    @GetMapping("/add")
    public String showAddPortfolioForm(Model model, HttpSession session) {
        model.addAttribute("entCd", "EI_C00001"); // TODO: 실제 세션에서 기업 코드 가져오기
        model.addAttribute("mbrCd", "MB_C0000036"); // TODO: 실제 세션에서 회원 코드 가져오기
        return "enter/portfolio/addPortfolio";
    }
    
    @GetMapping("/ContractList")
    public String showPortfolioContract() {
    	return "enter/portfolio/portfolioContractListView";
    }
    
    @PostMapping("/add-ajax")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addPortfolioAjax(
            @Valid @ModelAttribute EnterPortfolio portfolio,
            BindingResult bindingResult,
            // 1. List<String> 대신 String으로 받도록 변경
            @RequestParam(value="categoryCodes", required=false) String categoryCodesStr, 
            @RequestParam(value="tags", required=false) String tags) {
        
        if (bindingResult.hasErrors()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "입력 데이터 유효성 검사에 실패했습니다.");
            // ... 오류 처리 로직 ...
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // 2. 쉼표로 구분된 문자열을 다시 List<String>으로 변환
        List<String> categoryCodes = new ArrayList<>();
        if (categoryCodesStr != null && !categoryCodesStr.isEmpty()) {
            categoryCodes = Arrays.asList(categoryCodesStr.split(","));
        }
        
        portfolio.setAdmCd("ADM_C001"); // TODO: 세션에서 관리자 정보 가져오기
        try {
            // 3. 서비스에는 변환된 리스트를 전달
            portfolioService.addPortfolio(portfolio, categoryCodes, tags);
            return ResponseEntity.ok(Map.of("success", true, "message", "등록되었습니다.", "redirectUrl", "/enter/portfolio/list"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/api/categories/search")
    @ResponseBody
    public ResponseEntity<List<CategorySearchDto>> searchCategories(@RequestParam(value = "query", required = false, defaultValue = "") String query) {
        return ResponseEntity.ok(categorySearchService.searchCategoriesByName(query));
    }


    @DeleteMapping("/delete/{prtfCd}")
    @ResponseBody
    public ResponseEntity<?> deletePortfolio(@PathVariable String prtfCd) {
        try {
            portfolioService.deletePortfolio(prtfCd);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/edit/{prtfCd}")
    public String showEditPortfolioForm(@PathVariable String prtfCd, Model model) {
        model.addAttribute("portfolio", portfolioService.getPortfolioByPrtfCd(prtfCd));
        return "enter/portfolio/editPortfolio";
    }

    @PostMapping("/edit-ajax") // URL 변경 및 AJAX용으로 수정 
    @ResponseBody
    public ResponseEntity<Map<String, Object>> editPortfolioAjax(
    						    @Valid @ModelAttribute EnterPortfolio portfolio,
    						    BindingResult bindingResult,
                                @RequestParam(value="categoryCodes", required=false) List<String> categoryCodes, 
                                @RequestParam(value="tags", required=false) String tags) {
    	
    	if(bindingResult.hasErrors()) {
        	Map<String, Object> errorResponse = new HashMap<>();
        	errorResponse.put("success", false);
        	errorResponse.put("message", "입력 데이터 유효성 검사에 실패했습니다.");
        	List<String> fieldErrors = new ArrayList<>();
        	bindingResult.getFieldErrors().forEach(error -> {
        		fieldErrors.add(error.getField()+": "+error.getDefaultMessage());
        	});        	
        	errorResponse.put("errors",fieldErrors);
        	return ResponseEntity.badRequest().body(errorResponse);
        }
        try {
            portfolio.setAdmCd("ADM_C001"); // 수정자 정보
            portfolioService.updatePortfolio(portfolio, categoryCodes, tags);
            return ResponseEntity.ok(Map.of("success",true,"message","수정되었습니다.","redirectUrl","/enter/portfolio/list"));
        } catch (Exception e) {
            e.printStackTrace();
           return ResponseEntity.status(500).body(Map.of("success",false,"message","수정 중 오류 발생"+e.getMessage()));
        }
    
    }
    
    @GetMapping("/api/tags/search")
    @ResponseBody
    public ResponseEntity<List<String>> searchTags(@RequestParam(value="query", required=false,defaultValue="")String query){
    	return ResponseEntity.ok(portfolioService.searchTags(query));
    }
    
    // -- 외주 연결 API 추가 --
    @GetMapping("/{prtfCd}/linked-outsourcings")
    @ResponseBody
    public ResponseEntity<List<EnterOutsourcing>> getLinkedOutsourcings(@PathVariable String prtfCd){
    	return ResponseEntity.ok(portfolioService.getLinkedOutsourcings(prtfCd));
    }
    
    @GetMapping("/{prtfCd}/unlinked-outsourcings")
    @ResponseBody
    public ResponseEntity<List<EnterOutsourcing>> searchUnlinkedOutsourcings(
    		@PathVariable String prtfCd,
    		@RequestParam String query,
    		HttpSession session) {
    	String entCd = "EI_C00001"; // TODO: 실제 세션에서 기업 코드 가져오기
    	return ResponseEntity.ok(portfolioService.searchUnlinkedOutsourcings(prtfCd, entCd, query));
    	
    }
    
    @PostMapping("/link-outsourcing")
    @ResponseBody
    public ResponseEntity<?> linkOutsourcing(@RequestBody Map<String, String> payload,HttpSession session){
    	String prtfCd = payload.get("prtfCd");
    	String osCd = payload.get("osCd");
    	String entCd = "EI_C00001"; // TODO: 실제 세션에서 기업 코드 가져오기
    	portfolioService.linkOutsourcing(prtfCd, osCd, entCd);
    	return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/unlink-outsourcing")
    @ResponseBody
    public ResponseEntity<?> unlinkOutsourcing(@RequestBody Map<String, String> payload){
    	String prtfCd = payload.get("prtfCd");
    	String osCd = payload.get("OsCd");
    	portfolioService.unlinkOutsourcing(prtfCd, osCd);
    	return ResponseEntity.ok().build();
    }
    		
}