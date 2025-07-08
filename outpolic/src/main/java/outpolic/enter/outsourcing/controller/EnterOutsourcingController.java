package outpolic.enter.outsourcing.controller;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import outpolic.enter.POAddtional.domain.CategorySearchDto;
import outpolic.enter.POAddtional.service.CategorySearchService;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;
import outpolic.enter.outsourcing.service.EnterOutsourcingService;
import outpolic.enter.portfolio.domain.EnterPortfolio;
import outpolic.enter.portfolio.service.EnterPortfolioService;

/**
 * @Controller: 이 클래스가 웹 브라우저의 요청을 처리하는 컨트롤러임을 Spring에게 알려줍니다.
 * @RequestMapping("/enter/outsourcing"): 이 컨트롤러의 모든 메서드는 '/enter/outsourcing'으로 시작하는 URL을 처리합니다.
 * @RequiredArgsConstructor: Lombok 어노테이션으로,final로 선언된 필드들을 위한 생성자를 자동으로 만들어 의존성을 주입해줍니다.
 */
@Controller
@RequestMapping("/enter/outsourcing")
@RequiredArgsConstructor
public class EnterOutsourcingController {
    
	// 이 컨트롤러가 사용할 서비스들을 선언(의존성 주입)
    private final EnterOutsourcingService outsourcingService;
    private final CategorySearchService categorySearchService;
    private final EnterPortfolioService portfolioService;

    /**
     * 외주 목록 페이지(View)를 보여주는 메서드
     * URL: GET/enter/outsourcing/list
     */
    @GetMapping("/list")
    public String showPortfolioListView() { 
        // resources/templates/enter/outsourcing/outsourcingListView.html 파일을 찾아서 보여줍니다.

        return "enter/outsourcing/outsourcingListView";
    }
    
    /**
     * 외주 목록 페이지에 필요한 데이터를 JSON 형태로 제공하는 API
     * @ResponseBody: HTML 페이지가 아닌 순수 데이터(JSON)를 반환하도록 만듭니다.
     * @ResponseEntity: HTTP 상태 코드(예: 200 OK)와 함께 데이터를 포장해서 보냅니다.
     * URL: GET /enter/outsourcing/listData
     */
    @GetMapping("/listData")
    @ResponseBody
    public ResponseEntity<List<EnterOutsourcing>> getOutsourcingListData(HttpSession session){
        // TODO: 실제 운영 시에는 세션에서 현재 로그인한 기업의 코드를 가져와야 합니다.
    	String currentEntCd = "EI_C00001"; 
    	// 서비스 로직을 호출하여 데이터를 가져온 후, 성공(OK) 상태와 함께 데이터를 반환합니다.
        return ResponseEntity.ok(outsourcingService.getOutsourcingListByEntCd(currentEntCd));
    }
    
    /**
     * 외주 등록 페이지(View)를 보여주는 메서드
     * URL: GET /enter/outsourcing/add
     */

    @GetMapping("/add")
    public String showAddOutsourcingForm(Model model) {
    	// Model 객체: 컨트롤러에서 HTML로 데이터를 전달할 때 사용하는 통로 
        model.addAttribute("entCd","EI_C00001"); // TODO: 실제 세션에서 기업 코드 가져오기
        model.addAttribute("mbrCd", "MB_C0000036"); // TODO: 실제 세션에서 회원 코드 가져오기
        return "enter/outsourcing/addOutsourcingListView";
    }
    
    @GetMapping("/ContractList")
    public String showOutsourcingContractListView() {
    	return "enter/outsourcing/outsourcingRequestListView";
    }
    /**
     * 외주 등록 요청을 처리하는 API (AJAX로 호출됨)
     * @Valid: outsourcing 객체에 대해 EnterOutsourcing 클래스에 정의된 유효성 검사(@NotBlank 등)를 실행합니다.
     * @ModelAttribute:HTML form으로 전송된 여러 데이터를 EnterOutsourcing 객체 하나에 자동으로 담아줍니다.
     * @RequestParam: form 데이터 중 특정 이름(name)을 가진 파라미터 하나하나를 개별적으로 받습니다.
     * URL: POST /enter/outsourcing/add-ajax
     */
    @PostMapping("/add-ajax")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addOutsourcingAjax(
            @Valid @ModelAttribute EnterOutsourcing outsourcing,
            BindingResult bindingResult, // @Valid의 유효성 검사 결과를 담는 객체 
            @RequestParam(value="categoryCodes", required=false) String categoryCodesStr,
            @RequestParam(value="tags", required=false) String tags,
            @RequestParam(value="portfolioCds", required=false) String portfolioCdsStr) {
        
    	// 프론트엔드에서 쉼표로 합쳐 보낸 카테고리 코드를 다시 리스트로 변환
        List<String> categoryCodes = new ArrayList<>();
        if (categoryCodesStr != null && !categoryCodesStr.isEmpty()) {
            categoryCodes = Arrays.asList(categoryCodesStr.split(","));
        }
        // portfolioCds 변수 선언 및 변환 로직 추가 
        List<String> portfolioCds = new ArrayList<>();
        if(portfolioCdsStr != null && !portfolioCdsStr.isEmpty()) {
        	portfolioCds = Arrays.asList(portfolioCdsStr.split(","));
        }
        
        
        // 유효성 검사에서 오류가 발견되면, 오류 정보를 담아 400 Bad Request 상태로 응답 
        if (bindingResult.hasErrors()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "입력 데이터 유효성 검사에 실패했습니다. 모든 필수 항목을 올바르게 채워주세요.");
            List<String> fieldErrors = new ArrayList<>();
            System.out.println("-----BindingResult Errors-----");
            bindingResult.getFieldErrors().forEach(error -> {
            	System.out.println("Field:"+error.getField());
            	System.out.println("Value:"+error.getRejectedValue());
            	System.out.println("Message:"+error.getDefaultMessage());
                fieldErrors.add(error.getField() + ": " + error.getDefaultMessage());
            });
            System.out.println("------------");
            errorResponse.put("errors", fieldErrors);
            // ... (오류 메세지 가공 로직) ...
            return ResponseEntity.badRequest().body(errorResponse);
        }

        try {
        	// 서비스 로직을 호출하여 외주 등록 처리
            outsourcingService.addOutsourcing(outsourcing, categoryCodes, tags, portfolioCds);
            // 성공 시, 성공 메세지와 리다이렉트할 URL을 담아 200 OK 상태로 응답 
            return ResponseEntity.ok(Map.of("success", true, "message", "외주가 성공적으로 등록되었습니다.", "redirectUrl", "/enter/outsourcing/list"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
        	// 처리 중 예외 발생 시, 실패 메세지를 담아 500 Internal Server Error 상태로 응답 
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "등록 중 서버 오류가 발생했습니다. 다시 시도해주세요."));
        }
    }
    /**
     * 카테고리 검색 API
     * URL: GET /enter/outsourcing/api/categories/search?query=검색어
     */
    @GetMapping("/api/categories/search")
    @ResponseBody
    public ResponseEntity<List<CategorySearchDto>> searchCategories(
            @RequestParam(value ="query", required=false, defaultValue="") String query){
        return ResponseEntity.ok(categorySearchService.searchCategoriesByName(query));
    }
    /**
     * 태그 검색 API
     * URL: GET /enter/outsourcing/api/tags/search?query=검색어
     */
    @GetMapping("/api/tags/search")
    @ResponseBody
    public ResponseEntity<List<String>> searchOutsourcingTags(@RequestParam(value = "query", required = false, defaultValue = "") String query) {
        return ResponseEntity.ok(outsourcingService.searchTags(query));
    }
    
    /**
     * 외주 삭제 API
     * @PathVariable: URL 경로의 일부(여기서는 {osCd}를 변수로 받습니다.
     * URL: DELETE/enter/outsourcing/delete/OS_C00001 
     *
     */
    @DeleteMapping("/delete/{osCd}")
    @ResponseBody
    public ResponseEntity<?> deleteOutsourcing(@PathVariable String osCd){
        try {
            outsourcingService.deleteOutsourcing(osCd);
            return ResponseEntity.ok(Map.of("success",true));
        } catch (Exception e) {
            
            return ResponseEntity.badRequest().body(Map.of("success",false,"message", e.getMessage()));
        }
    }
    
    /**
     * 외주 수정 페이지(View)를 보여주는 메서드
     * URL: GET /enter/outsourcing/edit/OS_C00001
     */
    @GetMapping("/edit/{osCd}")
    public String showEditOutsourcingForm(@PathVariable String osCd, Model model) {
    	// 수정할 외주의 기존 정보를 DB에서 조회
    	EnterOutsourcing outsourcingData = outsourcingService.getOutsourcingByOsCd(osCd);
    	// 조회된 정보를 Model에 담아 HTML로 전달 
        model.addAttribute("outsourcing",outsourcingService.getOutsourcingByOsCd(osCd));
        return "enter/outsourcing/editOutsourcing";
    }
    /**
     * 외주 수정 요청을 처리하는 API (AJAX로 호출됨)
     * URL: POST /enter/outsourcing/edit-ajax
     */
    @PostMapping("/edit-ajax")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> editOutsourcingAjax(
            @Valid @ModelAttribute EnterOutsourcing outsourcing,
            BindingResult bindingResult,
            @RequestParam(value="categoryCodes", required=false) String categoryCodesStr,
            @RequestParam(value="tags", required=false) String tags,
            // ✅ 파라미터 변수 이름을 'portfolioCdsStr'로 수정하여 '등록' 기능과 통일
            @RequestParam(value="portfolioCds", required=false) String portfolioCdsStr) { 
        
        List<String> categoryCodes = new ArrayList<>();
        if (categoryCodesStr != null && !categoryCodesStr.isEmpty()) {
            categoryCodes = Arrays.asList(categoryCodesStr.split(","));
        }
        
        // ✅ '등록' 기능과 동일하게 portfolioCds 변환 로직 추가
        List<String> portfolioCds = new ArrayList<>();
        if(portfolioCdsStr != null && !portfolioCdsStr.isEmpty()) {
        	portfolioCds = Arrays.asList(portfolioCdsStr.split(","));
        }

        if (bindingResult.hasErrors()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "입력 데이터 유효성 검사에 실패했습니다.");
            List<String> fieldErrors = new ArrayList<>();
            bindingResult.getFieldErrors().forEach(error -> {
                fieldErrors.add(error.getField() + ": " + error.getDefaultMessage());
            });
            errorResponse.put("errors", fieldErrors);
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        try {
            // ✅ 서비스 호출 시에도 portfolioCds를 전달하도록 수정
            outsourcingService.updateOutsourcing(outsourcing, categoryCodes, tags, portfolioCds);
            return ResponseEntity.ok(Map.of("success", true, "message", "외주 정보가 성공적으로 수정되었습니다.", "redirectUrl", "/enter/outsourcing/list"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "수정 중 서버 오류가 발생했습니다."));
        }
    }
    
    /**
     * 포트폴리오 검색 API
     * URL: GET /enter/outsourcing/api/portfolio/search?query=검색어
     */
    @GetMapping("/api/portfolio/search")
    @ResponseBody
    public ResponseEntity<List<EnterPortfolio>> searchPortfolios(
            @RequestParam(value="query", required=false, defaultValue="") String query){
    	// 포트폴리오 서비스를 호출하여 결과 반환 
        return ResponseEntity.ok(portfolioService.searchPortfoliosByTitle(query));
    }
}