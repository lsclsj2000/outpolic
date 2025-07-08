package outpolic.enter.outsourcing.controller;

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
import org.springframework.web.bind.annotation.RequestBody; // @RequestBody 추가
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
    private final EnterPortfolioService portfolioService; // 포트폴리오 검색을 위해 계속 필요

    @GetMapping("/list")
    public String showOutsourcingListView() {
        return "enter/outsourcing/outsourcingListView";
    }

    @GetMapping("/listData")
    @ResponseBody
    public ResponseEntity<List<EnterOutsourcing>> getOutsourcingListData(HttpSession session){
        String currentEntCd = "EI_C00001"; // TODO: 세션에서 실제 기업 코드 가져오기
        return ResponseEntity.ok(outsourcingService.getOutsourcingListByEntCd(currentEntCd));
    }

    @GetMapping("/add")
    public String showAddOutsourcingForm(Model model) {
        model.addAttribute("entCd","EI_C00001");
        model.addAttribute("mbrCd", "MB_C0000036");
        return "enter/outsourcing/addOutsourcingListView";
    }

    @PostMapping("/add-ajax")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addOutsourcingAjax(
            @Valid @ModelAttribute EnterOutsourcing outsourcing,
            BindingResult bindingResult,
            @RequestParam(value="categoryCodes", required=false) String categoryCodesStr,
            @RequestParam(value="tags", required=false) String tags) { // portfolioCdsStr 제거

        List<String> categoryCodes = new ArrayList<>();
        if (categoryCodesStr != null && !categoryCodesStr.isEmpty()) {
            categoryCodes = Arrays.asList(categoryCodesStr.split(","));
        }

        // portfolioCds는 이제 이 메서드에서 처리하지 않습니다.

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
            outsourcingService.addOutsourcing(outsourcing, categoryCodes, tags); // portfolioCds 파라미터 제거
            return ResponseEntity.ok(Map.of("success", true, "message", "외주가 성공적으로 등록되었습니다.", "redirectUrl", "/enter/outsourcing/list"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "등록 중 서버 오류가 발생했습니다."));
        }
    }

    @GetMapping("/api/categories/search")
    @ResponseBody
    public ResponseEntity<List<CategorySearchDto>> searchCategories(@RequestParam(value ="query", required=false, defaultValue="") String query){
        return ResponseEntity.ok(categorySearchService.searchCategoriesByName(query));
    }

    @GetMapping("/api/tags/search")
    @ResponseBody
    public ResponseEntity<List<String>> searchOutsourcingTags(@RequestParam(value = "query", required = false, defaultValue = "") String query) {
        return ResponseEntity.ok(outsourcingService.searchTags(query));
    }

    @GetMapping("/api/portfolio/search")
    @ResponseBody
    public ResponseEntity<List<EnterPortfolio>> searchPortfolios(@RequestParam(value="query", required=false, defaultValue="") String query){
        // 이 검색 API는 외주 등록 시 사용되지는 않지만, 외주-포폴 연결 기능 분리 후 검색 UI에서 활용될 수 있습니다.
        return ResponseEntity.ok(portfolioService.searchPortfoliosByTitle(query));
    }

    @GetMapping("/edit/{osCd}")
    public String showEditOutsourcingForm(@PathVariable String osCd, Model model) {
        EnterOutsourcing outsourcing = outsourcingService.getOutsourcingByOsCd(osCd);
        model.addAttribute("outsourcing", outsourcing);
        return "enter/outsourcing/editOutsourcing";
    }

    @PostMapping("/edit-ajax")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> editOutsourcingAjax(
            @Valid @ModelAttribute EnterOutsourcing outsourcing,
            BindingResult bindingResult,
            @RequestParam(value="categoryCodes", required=false) String categoryCodesStr,
            @RequestParam(value="tags", required=false) String tags) { // portfolioCdsStr 제거 (수정 시에도 분리)

        List<String> categoryCodes = new ArrayList<>();
        if (categoryCodesStr != null && !categoryCodesStr.isEmpty()) {
            categoryCodes = Arrays.asList(categoryCodesStr.split(","));
        }

        // portfolioCds는 이제 이 메서드에서 처리하지 않습니다.

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
            outsourcingService.updateOutsourcing(outsourcing, categoryCodes, tags); // portfolioCds 파라미터 제거
            return ResponseEntity.ok(Map.of("success", true, "message", "외주가 성공적으로 수정되었습니다.", "redirectUrl", "/enter/outsourcing/list"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "수정 중 서버 오류가 발생했습니다."));
        }
    }

    @DeleteMapping("/delete/{osCd}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteOutsourcing(@PathVariable String osCd) {
        try {
            outsourcingService.deleteOutsourcing(osCd);
            return ResponseEntity.ok(Map.of("success", true, "message", "외주가 성공적으로 삭제되었습니다."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "외주 삭제 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    // --- 외주-포트폴리오 연결을 위한 새로운 API 엔드포인트들 (추가) ---

    // 특정 외주에 이미 연결된 포트폴리오 목록 조회
    @GetMapping("/{osCd}/linked-portfolios")
    @ResponseBody
    public ResponseEntity<List<EnterPortfolio>> getLinkedPortfolios(@PathVariable String osCd) {
        return ResponseEntity.ok(outsourcingService.getLinkedPortfoliosByOsCd(osCd));
    }

    @GetMapping("/{osCd}/unlinked-portfolios")
    @ResponseBody
    public ResponseEntity<List<EnterPortfolio>> searchUnlinkedPortfolios(
            @PathVariable String osCd,
            @RequestParam String query,
            HttpSession session) {
        String entCd = "EI_C00001"; // TODO: 세션에서 실제 기업 코드 가져오기
        return ResponseEntity.ok(outsourcingService.searchUnlinkedPortfolios(osCd, entCd, query));
    }

    @PostMapping("/link-portfolio")
    @ResponseBody
    public ResponseEntity<?> linkPortfolio(@RequestBody Map<String, String> payload, HttpSession session) {
        String osCd = payload.get("osCd");
        String prtfCd = payload.get("prtfCd");
        String entCd = "EI_C00001"; // TODO: 세션에서 실제 기업 코드 가져오기
        outsourcingService.linkPortfolioToOutsourcing(osCd, prtfCd, entCd);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/unlink-portfolio")
    @ResponseBody
    public ResponseEntity<?> unlinkPortfolio(@RequestBody Map<String, String> payload) {
        String osCd = payload.get("osCd");
        String prtfCd = payload.get("prtfCd");
        outsourcingService.unlinkPortfolioFromOutsourcing(osCd, prtfCd);
        return ResponseEntity.ok().build();
    }
}