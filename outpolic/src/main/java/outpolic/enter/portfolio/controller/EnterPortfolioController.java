package outpolic.enter.portfolio.controller;

import outpolic.enter.POAddtional.domain.CategorySearchDto;
import outpolic.enter.POAddtional.service.CategorySearchService;
import outpolic.enter.portfolio.domain.EnterPortfolio;
import outpolic.enter.portfolio.service.EnterPortfolioService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/enter/portfolio")
@RequiredArgsConstructor
public class EnterPortfolioController {

    private final EnterPortfolioService portfolioService;
    private final CategorySearchService categorySearchService;

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
    
    @PostMapping("/add-ajax")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addPortfolioAjax(@ModelAttribute EnterPortfolio portfolio, 
                                                                @RequestParam("portfolioFiles") List<MultipartFile> portfolioFiles, 
                                                                @RequestParam(value="categoryCodes", required=false) List<String> categoryCodes, 
                                                                @RequestParam(value="tags", required=false) String tags) {
        portfolio.setAdmCd("ADM_C001");
        try {
            portfolioService.addPortfolio(portfolio, portfolioFiles, categoryCodes, tags);
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

    @PostMapping("/edit")
    public String editPortfolio(@ModelAttribute EnterPortfolio portfolio, 
                                @RequestParam("portfolioFiles") List<MultipartFile> portfolioFiles, 
                                @RequestParam(value="categoryCodes", required=false) List<String> categoryCodes, 
                                @RequestParam(value="tags", required=false) String tags, 
                                RedirectAttributes redirectAttributes) {
        try {
            portfolio.setAdmCd("ADM_C001"); // 수정자 정보
            portfolioService.updatePortfolio(portfolio, portfolioFiles, categoryCodes, tags);
            redirectAttributes.addFlashAttribute("successMessage", "수정되었습니다.");
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "수정 중 오류 발생");
        }
        return "redirect:/enter/portfolio/list";
    }
}