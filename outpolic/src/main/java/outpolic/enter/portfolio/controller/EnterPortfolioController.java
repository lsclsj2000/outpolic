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
    public ResponseEntity<List<EnterPortfolio>> getPortfolioListData(HttpSession session) { // HttpSession 파라미터 추가
        // 1. 세션에서 현재 로그인한 사용자의 회원 코드(mbrCd)를 가져옵니다.
        String mbrCd = (String) session.getAttribute("SCD");

        // 2. 만약 로그인 상태가 아니라면, 401 Unauthorized 응답을 보냅니다.
     // ▼▼▼ 바로 이 부분이 불필요한 DB 조회를 막아주는 핵심 코드입니다 ▼▼▼
        if (mbrCd == null) {
            // 로그인하지 않았으면 DB 조회 없이 즉시 빈 목록을 반환합니다.
            return ResponseEntity.ok(java.util.Collections.emptyList());
        }

        // 로그인한 사용자일 경우에만 DB 조회를 수행합니다.
        String entCd = portfolioService.findEntCdByMbrCd(mbrCd);
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
            // 1. 파라미터를 String이 아닌 List<String>으로 직접 받도록 수정
            @RequestParam(value="categoryCodes", required=false) List<String> categoryCodes,
            @RequestParam(value="tags", required=false) String tags,
            @RequestParam(value="portfolioImage",required=false) MultipartFile portfolioImage,
            HttpSession session) {

        // 2. List에 대한 유효성 검사로 하나로 통일
        if (categoryCodes == null || categoryCodes.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "카테고리는 최소 하나 이상 선택해야 합니다."));
        }
        
        // 폼 데이터의 다른 필드에 대한 유효성 검사
        if (bindingResult.hasErrors()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "입력 데이터 유효성 검사에 실패했습니다.");
            // 필요하다면 어떤 필드가 문제인지 상세 오류를 추가할 수 있습니다.
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // 세션에서 로그인 정보를 가져와 설정
        String mbrCd = (String) session.getAttribute("SCD");
        String entCd = portfolioService.findEntCdByMbrCd(mbrCd);
        
        portfolio.setEntCd(entCd); 
        portfolio.setMbrCd(mbrCd);
        
        try {
            // 3. 더 이상 문자열을 분리할 필요 없이 List를 그대로 서비스에 전달
            portfolioService.addPortfolio(portfolio, categoryCodes, tags, portfolioImage);
            return ResponseEntity.ok(Map.of("success", true, "message", "포트폴리오가 성공적으로 등록되었습니다.", "redirectUrl", "/enter/portfolio/list"));
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
    						@RequestParam(value="tags", required=false) String tags,
    						@RequestParam(value="portfolioImage",required=false) MultipartFile portfolioImage,
    						HttpSession session) {
    	
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
           
            portfolioService.updatePortfolio(portfolio, categoryCodes, tags,portfolioImage);
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