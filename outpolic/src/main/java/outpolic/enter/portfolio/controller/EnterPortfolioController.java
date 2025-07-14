package outpolic.enter.portfolio.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import outpolic.enter.POAddtional.domain.CategorySearchDto;
import outpolic.enter.POAddtional.service.CategorySearchService;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;
import outpolic.enter.portfolio.domain.EnterPortfolio;
import outpolic.enter.portfolio.service.EnterPortfolioService;
import org.springframework.http.HttpStatus;


@Controller
@RequestMapping("/enter/portfolio")
@RequiredArgsConstructor
public class EnterPortfolioController {

    private final EnterPortfolioService portfolioService;
    private final CategorySearchService categorySearchService;
    
    @GetMapping("/list")
    public String showPortfolioListView() {
        return "enter/portfolio/portfolioListView";
    }

    @GetMapping("/listData")
    @ResponseBody
    public ResponseEntity<List<EnterPortfolio>> getPortfolioListData(HttpSession session){
        // 1. 세션에서 현재 로그인한 사용자의 회원 코드(mbrCd)를 가져옵니다.
        String mbrCd = (String) session.getAttribute("SCD");

        // 2. 만약 로그인 상태가 아니라면, 빈 목록을 반환합니다.
        if (mbrCd == null) {
            return ResponseEntity.ok(java.util.Collections.emptyList());
        }

        // 3. mbrCd를 사용해 현재 기업의 고유 코드(entCd)를 조회합니다.
        // (이전에 만든 findEntCdByMbrCd 메서드 활용)
        String entCd = portfolioService.findEntCdByMbrCd(mbrCd);
        
        // 4. 조회된 현재 기업의 entCd로 포트폴리오 목록을 가져옵니다.
        return ResponseEntity.ok(portfolioService.getPortfolioListByEntCd(entCd));
    }

    @GetMapping("/api/countByEntCd")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> countPortfolioForEnterprise(HttpSession session){
        String entCd = "EI_C00001"; 
        int count = portfolioService.countPortfoliosByEntCd(entCd);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @GetMapping("/add")
    public String showAddPortfolioForm(Model model, HttpSession session) {
        String mbrCd = (String) session.getAttribute("SCD");
        String entCd = portfolioService.findEntCdByMbrCd(mbrCd); // 예시: 서비스 메서드 호출

        model.addAttribute("entCd", entCd);
        model.addAttribute("mbrCd", mbrCd);
        model.addAttribute("portfolio", new EnterPortfolio());
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
            @RequestParam(value="categoryCodes", required=false) String categoryCodesStr, 
            @RequestParam(value="tags", required=false) String tags,
            @RequestParam(value="portfolioImage",required=false) MultipartFile portfolioImage,
            HttpSession session) { // HttpSession 파라미터 추가

        // 세션에서 값을 가져와 portfolio 객체에 설정합니다.
        String mbrCd = (String) session.getAttribute("SCD");
        String entCd = portfolioService.findEntCdByMbrCd(mbrCd); // 예시
        
        portfolio.setEntCd(entCd); 
        portfolio.setMbrCd(mbrCd); // <- 이 값 'MB_C0000036'이 admin 테이블에 있어야 합니다.
    	
        if (bindingResult.hasErrors()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "입력 데이터 유효성 검사에 실패했습니다.");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        List<String> categoryCodes = new ArrayList<>();
        if (categoryCodesStr != null && !categoryCodesStr.isEmpty()) {
            categoryCodes = Arrays.asList(categoryCodesStr.split(","));
        }
        
        try {
            portfolioService.addPortfolio(portfolio, categoryCodes, tags, portfolioImage);
            return ResponseEntity.ok(Map.of("success", true, "message", "포트폴리오가 성공적으로 등록되었습니다.", "redirectUrl", "/enter/portfolio/list"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace(); 
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("success", false, "message", "포트폴리오 등록 중 서버 오류가 발생했습니다: " + e.getMessage()));
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

    @PostMapping("/edit-ajax") 
    @ResponseBody
    public ResponseEntity<Map<String, Object>> editPortfolioAjax(
    						@Valid @ModelAttribute EnterPortfolio portfolio,
    						BindingResult bindingResult,
    						@RequestParam(value="categoryCodes", required=false) String categoryCodesStr, 
    						@RequestParam(value="tags", required=false) String tags) {
    	
    	if(bindingResult.hasErrors()) {
        	Map<String, Object> errorResponse = new HashMap<>();
        	errorResponse.put("success", false);
        	errorResponse.put("message", "입력 데이터 유효성 검사에 실패했습니다.");
        	List<String> fieldErrors = new ArrayList<>();
        	bindingResult.getFieldErrors().forEach(error -> {
        		fieldErrors.add(error.getField()+": "+error.getDefaultMessage());
        	});
        	return ResponseEntity.badRequest().body(errorResponse);
        }

        List<String> categoryCodes = new ArrayList<>();
        if (categoryCodesStr != null && !categoryCodesStr.isEmpty()) {
            categoryCodes = Arrays.asList(categoryCodesStr.split(","));
        }

        try {
            portfolio.setAdmCd("ADM_C004"); 
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
    	String entCd = "EI_C00001"; 
    	return ResponseEntity.ok(portfolioService.searchUnlinkedOutsourcings(prtfCd, entCd, query));
    	
    }
    
    @PostMapping("/link-outsourcing")
    @ResponseBody
    public ResponseEntity<?> linkOutsourcing(@RequestBody Map<String, String> payload,HttpSession session){
    	String prtfCd = payload.get("prtfCd");
    	String osCd = payload.get("osCd");
    	String entCd = "EI_C00001"; 
    	portfolioService.linkOutsourcing(prtfCd, osCd, entCd);
    	return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/unlink-outsourcing")
    @ResponseBody
    public ResponseEntity<?> unlinkOutsourcing(@RequestBody Map<String, String> payload){
    	String prtfCd = payload.get("prtfCd");
    	String osCd = payload.get("osCd"); 
    	portfolioService.unlinkOutsourcing(prtfCd, osCd);
    	return ResponseEntity.ok().build();
    }
}