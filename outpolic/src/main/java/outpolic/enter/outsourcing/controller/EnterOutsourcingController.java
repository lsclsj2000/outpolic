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

@Controller
@RequestMapping("/enter/outsourcing")
@RequiredArgsConstructor
public class EnterOutsourcingController {
    
    private final EnterOutsourcingService outsourcingService;
    private final CategorySearchService categorySearchService;
    private final EnterPortfolioService portfolioService;

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
    
    @GetMapping("/ContractList")
    public String showOutsourcingContractListView() {
    	return "enter/outsourcing/outsourcingContractListView";
    }

    @PostMapping("/add-ajax")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addOutsourcingAjax(
            @Valid @ModelAttribute EnterOutsourcing outsourcing,
            BindingResult bindingResult,
            @RequestParam(value="categoryCodes", required=false) String categoryCodesStr,
            @RequestParam(value="tags", required=false) String tags,
            @RequestParam(value="portfolioCds", required=false) List<String> portfolioCds) { // ✅ 문법 오류 수정
        
        List<String> categoryCodes = new ArrayList<>();
        if (categoryCodesStr != null && !categoryCodesStr.isEmpty()) {
            categoryCodes = Arrays.asList(categoryCodesStr.split(","));
        }

        if (bindingResult.hasErrors()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "입력 데이터 유효성 검사에 실패했습니다. 모든 필수 항목을 올바르게 채워주세요.");
            List<String> fieldErrors = new ArrayList<>();
            bindingResult.getFieldErrors().forEach(error -> {
                fieldErrors.add(error.getField() + ": " + error.getDefaultMessage());
            });
            errorResponse.put("errors", fieldErrors);
            return ResponseEntity.badRequest().body(errorResponse);
        }

        try {
            outsourcingService.addOutsourcing(outsourcing, categoryCodes, tags, portfolioCds);
            return ResponseEntity.ok(Map.of("success", true, "message", "외주가 성공적으로 등록되었습니다.", "redirectUrl", "/enter/outsourcing/list"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "등록 중 서버 오류가 발생했습니다. 다시 시도해주세요."));
        }
    }
    
    @GetMapping("/api/categories/search")
    @ResponseBody
    public ResponseEntity<List<CategorySearchDto>> searchCategories(
            @RequestParam(value ="query", required=false, defaultValue="") String query){
        return ResponseEntity.ok(categorySearchService.searchCategoriesByName(query));
    }
    
    @GetMapping("/api/tags/search")
    @ResponseBody
    public ResponseEntity<List<String>> searchOutsourcingTags(@RequestParam(value = "query", required = false, defaultValue = "") String query) {
        return ResponseEntity.ok(outsourcingService.searchTags(query));
    }
    
    @DeleteMapping("/delete/{osCd}")
    @ResponseBody
    public ResponseEntity<?> deleteOutsourcing(@PathVariable String osCd){
        try {
            outsourcingService.deleteOutsourcing(osCd);
            return ResponseEntity.ok(Map.of("success",true));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("success",false,"message", e.getMessage()));
        }
    }
    
    @GetMapping("/edit/{osCd}")
    public String showEditOutsourcingForm(@PathVariable String osCd, Model model) {
        model.addAttribute("outsourcing",outsourcingService.getOutsourcingByOsCd(osCd));
        return "enter/outsourcing/editOutsourcing";
    }
    
    @PostMapping("/edit-ajax")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> editOutsourcingAjax(
            @Valid @ModelAttribute EnterOutsourcing outsourcing,
            BindingResult bindingResult,
            @RequestParam(value="categoryCodes", required=false) String categoryCodesStr,
            @RequestParam(value="tags", required=false) String tags,
            @RequestParam(value="portfolioCds", required=false) List<String> portfolioCds) { // ✅ 기능 누락 수정
        
        List<String> categoryCodes = new ArrayList<>();
        if (categoryCodesStr != null && !categoryCodesStr.isEmpty()) {
            categoryCodes = Arrays.asList(categoryCodesStr.split(","));
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
            // ✅ 서비스 호출 시 portfolioCds 전달
            outsourcingService.updateOutsourcing(outsourcing, categoryCodes, tags, portfolioCds);
            return ResponseEntity.ok(Map.of("success", true, "message", "외주 정보가 성공적으로 수정되었습니다.", "redirectUrl", "/enter/outsourcing/list"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "수정 중 서버 오류가 발생했습니다."));
        }
    }

    @GetMapping("/api/portfolio/search")
    @ResponseBody
    public ResponseEntity<List<EnterPortfolio>> searchPortfolios(
            @RequestParam(value="query", required=false, defaultValue="") String query){
        return ResponseEntity.ok(portfolioService.searchPortfoliosByTitle(query));
    }
}