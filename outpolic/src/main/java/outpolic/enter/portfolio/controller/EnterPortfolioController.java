package outpolic.enter.portfolio.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
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
import outpolic.enter.outsourcing.service.EnterOutsourcingService;
import outpolic.enter.portfolio.domain.EnterPortfolio;
import outpolic.enter.portfolio.service.EnterPortfolioService;

@Controller
@RequestMapping("/enter/portfolio")
@RequiredArgsConstructor
public class EnterPortfolioController {

    private final EnterPortfolioService portfolioService;
    private final CategorySearchService categorySearchService;
    private final EnterOutsourcingService outsourcingService;

    // --- 포트폴리오 목록 관련 ---
    @GetMapping("/list")
    public String showPortfolioListView() {
        return "enter/portfolio/portfolioListView";
    }

    @GetMapping("/listData")
    @ResponseBody
    public ResponseEntity<List<EnterPortfolio>> getPortfolioListData(HttpSession session) {
        String mbrCd = (String) session.getAttribute("SCD");
        if (mbrCd == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(java.util.Collections.emptyList());
        }
        String entCd = portfolioService.findEntCdByMbrCd(mbrCd);
        return ResponseEntity.ok(portfolioService.getPortfolioListByEntCd(entCd));
    }

    // --- 포트폴리오 등록 관련 ---
    @GetMapping("/add")
    public String showAddPortfolioForm(Model model, HttpSession session) {
        String mbrCd = (String) session.getAttribute("SCD");
        String entCd = portfolioService.findEntCdByMbrCd(mbrCd);
        model.addAttribute("entCd", entCd);
        model.addAttribute("mbrCd", mbrCd);
        model.addAttribute("portfolio", new EnterPortfolio());
        return "enter/portfolio/addPortfolio";
    }
    
    @PostMapping("/add-ajax")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addPortfolioAjax(
            @Valid @ModelAttribute EnterPortfolio portfolio, BindingResult bindingResult,
            // ▼▼▼ 이 파라미터로 여러 카테고리 ID를 받습니다. ▼▼▼
            @RequestParam(value = "categoryCodes", required = false) List<String> categoryCodes,
            @RequestParam(value = "tags", required = false) String tags,
            @RequestParam(value = "portfolioImage", required = false) MultipartFile portfolioImage,
            HttpSession session) {
        
        // ▼▼▼ [수정된 유효성 검사] portfolio.getCtgryId() 대신 categoryCodes 목록을 검사합니다. ▼▼▼
        if (categoryCodes == null || categoryCodes.stream().allMatch(String::isEmpty)) {
            // 에러를 categoryError에 맞추기 위해 임의의 필드 이름(예: categories) 사용
            bindingResult.rejectValue("categories", "NotEmpty", "카테고리는 최소 하나 이상 선택해야 합니다.");
        }
        
        if (portfolioImage == null || portfolioImage.isEmpty()) {
            bindingResult.rejectValue("prtfThumbnailUrl", "NotEmpty", "대표 이미지는 필수입니다.");
        }
        
        if (bindingResult.hasErrors()) {
            Map<String, Object> errors = new HashMap<>();
            errors.put("success", false);
            bindingResult.getFieldErrors().forEach(error -> {
                // 카테고리 오류의 경우, 프론트엔드 ID("categoryError")에 맞게 키를 설정
                if (error.getField().equals("categories")) {
                    errors.put("categoryError", error.getDefaultMessage());
                } else {
                    errors.put(error.getField() + "Error", error.getDefaultMessage());
                }
            });
            return ResponseEntity.badRequest().body(errors);
        }

        String mbrCd = (String) session.getAttribute("SCD");
        String entCd = portfolioService.findEntCdByMbrCd(mbrCd);
        portfolio.setEntCd(entCd);
        portfolio.setMbrCd(mbrCd);
        try {
            // 서비스 호출 시 categoryCodes 전달
            portfolioService.addPortfolio(portfolio, categoryCodes, tags, portfolioImage);
            return ResponseEntity.ok(Map.of("success", true, "message", "포트폴리오가 성공적으로 등록되었습니다.", "redirectUrl", "/enter/portfolio/list"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("success", false, "message", "포트폴리오 등록 중 서버 오류가 발생했습니다: " + e.getMessage()));
        }
    }
    // --- 포트폴리오 수정 관련 ---
    @GetMapping("/edit/{prtfCd}")
    public String showEditPortfolioForm(@PathVariable String prtfCd, Model model) {
        // 1. 수정할 포트폴리오의 모든 정보를 조회합니다.
        EnterPortfolio portfolio = portfolioService.getPortfolioByPrtfCd(prtfCd);
        model.addAttribute("portfolio", portfolio);
        
        // 2. 포트폴리오에 저장된 대표 카테고리 ID가 있는지 확인합니다.
        if (portfolio.getCtgryId() != null && !portfolio.getCtgryId().isEmpty()) {
            // 3. ID가 있다면, 서비스에 요청하여 해당 카테고리의 전체 경로(1차>2차>3차)를 조회합니다.
            List<CategorySearchDto> categoryPath = categorySearchService.getCategoryPath(portfolio.getCtgryId());
            // 4. 조회된 경로를 "categoryPath"라는 이름으로 모델에 담아 View로 전달합니다.
            model.addAttribute("categoryPath", categoryPath);
        }
        
        return "enter/portfolio/editPortfolio";
    }
    @PostMapping("/edit-ajax")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> editPortfolioAjax(
            @Valid @ModelAttribute EnterPortfolio portfolio, BindingResult bindingResult,
            @RequestParam(value = "categoryCodes", required = false) List<String> categoryCodes,
            @RequestParam(value = "tags", required = false) String tags,
            @RequestParam(value = "portfolioImage", required = false) MultipartFile portfolioImage) {

        Map<String, Object> errors = new HashMap<>();

        // 1. 일반적인 필드 유효성 검사
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error -> 
                errors.put(error.getField() + "Error", error.getDefaultMessage())
            );
        }

        // 2. 카테고리 목록에 대한 유효성 검사를 수동으로 추가
        if (categoryCodes == null || categoryCodes.stream().allMatch(String::isEmpty)) {
            errors.put("categoryError", "카테고리는 최소 하나 이상 선택해야 합니다.");
        }

        // 3. 오류가 하나라도 있으면 400 응답을 반환
        if (!errors.isEmpty()) {
            errors.put("success", false);
            return ResponseEntity.badRequest().body(errors);
        }

        // 4. 오류가 없으면 서비스 호출
        try {
            portfolioService.updatePortfolio(portfolio, categoryCodes, tags, portfolioImage); 
            return ResponseEntity.ok(Map.of("success", true, "message", "수정되었습니다.", "redirectUrl", "/enter/portfolio/list")); 
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("success", false, "message", "수정 중 오류 발생: " + e.getMessage())); 
        }
    }

    // --- (이하 삭제, 링크 등 나머지 코드는 동일) ---
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

    @GetMapping("/{prtfCd}/linked-outsourcings")
    @ResponseBody
    public ResponseEntity<List<EnterOutsourcing>> getLinkedOutsourcings(@PathVariable String prtfCd) {
        return ResponseEntity.ok(portfolioService.getLinkedOutsourcings(prtfCd));
    }

    @GetMapping("/{prtfCd}/unlinked-outsourcings")
    @ResponseBody
    public ResponseEntity<List<EnterOutsourcing>> searchUnlinkedOutsourcings(
            @PathVariable String prtfCd,
            @RequestParam String query,
            HttpSession session) {
        String mbrCd = (String) session.getAttribute("SCD");
        String entCd = portfolioService.findEntCdByMbrCd(mbrCd);
        return ResponseEntity.ok(portfolioService.searchUnlinkedOutsourcings(prtfCd, entCd, query));
    }

    @PostMapping("/link-outsourcing")
    @ResponseBody
    public ResponseEntity<?> linkOutsourcing(@RequestBody Map<String, String> payload, HttpSession session) {
        String prtfCd = payload.get("prtfCd");
        String osCd = payload.get("osCd");
        String mbrCd = (String) session.getAttribute("SCD");
        String entCd = portfolioService.findEntCdByMbrCd(mbrCd);
        outsourcingService.linkPortfolioToOutsourcing(osCd, prtfCd, entCd);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/unlink-outsourcing")
    @ResponseBody
    public ResponseEntity<?> unlinkOutsourcing(@RequestBody Map<String, String> payload) {
        String prtfCd = payload.get("prtfCd");
        String osCd = payload.get("osCd");
        outsourcingService.unlinkPortfolioFromOutsourcing(osCd, prtfCd);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/api/tags/search")
    @ResponseBody
    public ResponseEntity<List<String>> searchTags(@RequestParam(value = "query", required = false, defaultValue = "") String query) {
        return ResponseEntity.ok(portfolioService.searchTags(query));
    }
    
    @GetMapping("/api/countByEntCd")
    @ResponseBody
    public ResponseEntity<Integer> countPortfoliosForEnterprise(HttpSession session){
        String mbrCd =  (String) session.getAttribute("SCD");
        if(mbrCd == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(0);
        }
        String entCd = portfolioService.findEntCdByMbrCd(mbrCd);
        int count= portfolioService.countPortfoliosByEntCd(entCd);
        return ResponseEntity.ok(count);
    }
}