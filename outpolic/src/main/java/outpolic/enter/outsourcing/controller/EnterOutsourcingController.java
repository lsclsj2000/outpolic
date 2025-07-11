package outpolic.enter.outsourcing.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
import lombok.RequiredArgsConstructor;
import outpolic.enter.POAddtional.domain.CategorySearchDto;
import outpolic.enter.POAddtional.service.CategorySearchService;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;
import outpolic.enter.outsourcing.domain.OutsourcingFormDataDto;
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

    // ======================================================
    // ▼▼▼ 외주 "등록" 관련 로직 ▼▼▼
    // ======================================================

    @GetMapping("/add")
    public String showAddOutsourcingForm(Model model, HttpSession session) {
        session.removeAttribute("outsourcingFormData");
        model.addAttribute("entCd", "EI_C00001");
        model.addAttribute("mbrCd", "MB_C0000036");
        model.addAttribute("formData", new OutsourcingFormDataDto());
        return "enter/outsourcing/addOutsourcingListView";
    }

    @PostMapping("/save-step1")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveStep1(@ModelAttribute OutsourcingFormDataDto formData, HttpSession session) {
        String entCd = "EI_C00001";
        String mbrCd = "MB_C0000036";
        formData.setEntCd(entCd);
        formData.setMbrCd(mbrCd);
        String generatedOsCd = outsourcingService.saveStep1Data(formData, session);
        return ResponseEntity.ok(Map.of("success", true, "osCd", generatedOsCd));
    }

    @PostMapping("/save-step2")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveStep2(
            @RequestParam("osCd") String osCd,
            @RequestParam(value = "categoryCodes", required = false) String categoryCodesStr,
            @RequestParam(value = "tags", required = false) String tags,
            HttpSession session) {
        List<String> categoryCodes = (categoryCodesStr != null && !categoryCodesStr.isEmpty()) ? Arrays.asList(categoryCodesStr.split(",")) : new ArrayList<>();
        outsourcingService.saveStep2Data(osCd, categoryCodes, tags, session);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/save-step3")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveStep3(
            @RequestParam("osCd") String osCd,
            @RequestParam(value = "outsourcingReferenceFiles", required = false) MultipartFile[] files,
            HttpSession session) {
        outsourcingService.saveStep3Data(osCd, files, session);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/complete-registration")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> completeRegistration(@RequestBody Map<String, String> payload, HttpSession session) {
        String osCd = payload.get("osCd");
        if (osCd == null || osCd.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "외주 코드가 유효하지 않습니다."));
        }
        outsourcingService.completeOutsourcingRegistration(osCd, session);
        return ResponseEntity.ok(Map.of("success", true, "message", "외주 등록이 완료되었습니다.", "redirectUrl", "/enter/outsourcing/list"));
    }

    // ======================================================
    // ▼▼▼ 외주 "수정" 관련 로직 ▼▼▼
    // ======================================================
    
    // ★★★ 추가: 수정 페이지로 진입하는 컨트롤러 메서드 ★★★
    @GetMapping("/edit/{osCd}")
    public String showEditOutsourcingForm(@PathVariable String osCd, Model model) {
        EnterOutsourcing outsourcing = outsourcingService.findOutsourcingDetailsByOsCd(osCd);
        if (outsourcing == null) {
            return "redirect:/enter/outsourcing/list?error=notfound";
        }
        model.addAttribute("outsourcing", outsourcing);
        return "enter/outsourcing/editOutsourcing";
    }

    // ★★★ 수정 1단계: 기본 정보 업데이트 ★★★
    @PostMapping("/edit/step1")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateStep1(
            @RequestParam("osCd") String osCd,
            @RequestParam("osTtl") String osTtl,
            @RequestParam("osExpln") String osExpln,
            @RequestParam("osAmt") Integer osAmt,
            @RequestParam("osFlfmtCnt") Integer osFlfmtCnt,
            @RequestParam("osStrtYmdt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime osStrtYmdt,
            @RequestParam("osEndYmdt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime osEndYmdt) {
        
        EnterOutsourcing outsourcingToUpdate = new EnterOutsourcing();
        outsourcingToUpdate.setOsCd(osCd);
        outsourcingToUpdate.setOsTtl(osTtl);
        outsourcingToUpdate.setOsExpln(osExpln);
        if (osAmt != null) outsourcingToUpdate.setOsAmt(BigDecimal.valueOf(osAmt));
        outsourcingToUpdate.setOsFlfmtCnt(osFlfmtCnt);
        outsourcingToUpdate.setOsStrtYmdt(osStrtYmdt);
        outsourcingToUpdate.setOsEndYmdt(osEndYmdt);
        
        outsourcingService.updateOutsourcingStep1(outsourcingToUpdate); // 서비스에 새 메서드 필요
        return ResponseEntity.ok(Map.of("success", true, "message", "기본 정보가 수정되었습니다."));
    }

    // ★★★ 수정 2단계: 카테고리/태그 업데이트 ★★★
    @PostMapping("/edit/step2")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateStep2(
            @RequestParam("osCd") String osCd,
            @RequestParam(value = "categoryCodes", required = false) String categoryCodesStr,
            @RequestParam(value = "tags", required = false) String tags) {
        
        List<String> categoryCodes = (categoryCodesStr != null && !categoryCodesStr.isEmpty()) ? Arrays.asList(categoryCodesStr.split(",")) : new ArrayList<>();
        outsourcingService.updateOutsourcingStep2(osCd, categoryCodes, tags); // 서비스에 새 메서드 필요
        return ResponseEntity.ok(Map.of("success", true, "message", "카테고리 및 태그가 수정되었습니다."));
    }
    
    // ★★★ 수정 3단계: 파일 업데이트 ★★★
    @PostMapping("/edit/step3")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateStep3(
            @RequestParam("osCd") String osCd,
            @RequestParam(value = "outsourcingReferenceFiles", required = false) MultipartFile[] files) {
            
        outsourcingService.updateOutsourcingStep3(osCd, files); // 서비스에 새 메서드 필요
        return ResponseEntity.ok(Map.of("success", true, "message", "첨부 파일이 수정되었습니다."));
    }

    // ★★★ 수정 4단계: 최종 완료 (포트폴리오 연결은 이미 개별 처리됨) ★★★
    @PostMapping("/edit/complete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> completeEdit(@RequestBody Map<String, String> payload) {
        // 실제로는 별다른 DB 작업이 없을 수 있으나, 최종 완료 확인용으로 사용
        String osCd = payload.get("osCd");
        // 필요한 최종 정리 작업이 있다면 여기에 추가
        return ResponseEntity.ok(Map.of("success", true, "message", "외주 정보 수정이 완료되었습니다.", "redirectUrl", "/enter/outsourcing/list"));
    }
    // ======================================================
    // ▼▼▼ 공통 API 및 기타 로직 ▼▼▼
    // ======================================================
    @GetMapping("/list")
    public String showOutsourcingListView() { return "enter/outsourcing/outsourcingListView"; }

    @GetMapping("/listData")
    @ResponseBody
    public ResponseEntity<List<EnterOutsourcing>> getOutsourcingListData() {
        String currentEntCd = "EI_C00001";
        return ResponseEntity.ok(outsourcingService.getOutsourcingListByEntCd(currentEntCd));
    }
    
    @DeleteMapping("/delete/{osCd}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteOutsourcing(@PathVariable String osCd) {
        outsourcingService.deleteOutsourcing(osCd);
        return ResponseEntity.ok(Map.of("success", true, "message", "삭제되었습니다."));
    }

    @GetMapping("/api/categories/search") @ResponseBody
    public ResponseEntity<List<CategorySearchDto>> searchCategories(@RequestParam(defaultValue = "") String query) {
        return ResponseEntity.ok(categorySearchService.searchCategoriesByName(query));
    }

    @GetMapping("/api/tags/search") @ResponseBody
    public ResponseEntity<List<String>> searchOutsourcingTags(@RequestParam(defaultValue = "") String query) {
        return ResponseEntity.ok(outsourcingService.searchTags(query));
    }

    @GetMapping("/{osCd}/linked-portfolios") @ResponseBody
    public ResponseEntity<List<EnterPortfolio>> getLinkedPortfolios(@PathVariable String osCd) {
        return ResponseEntity.ok(outsourcingService.getLinkedPortfoliosByOsCd(osCd));
    }

    @GetMapping("/{osCd}/unlinked-portfolios") @ResponseBody
    public ResponseEntity<List<EnterPortfolio>> searchUnlinkedPortfolios(@PathVariable String osCd, @RequestParam String query) {
        String entCd = "EI_C00001";
        return ResponseEntity.ok(outsourcingService.searchUnlinkedPortfolios(osCd, entCd, query));
    }

    @PostMapping("/link-portfolio") @ResponseBody
    public ResponseEntity<?> linkPortfolio(@RequestBody Map<String, String> payload) {
        outsourcingService.linkPortfolioToOutsourcing(payload.get("osCd"), payload.get("prtfCd"), "EI_C00001");
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/unlink-portfolio") @ResponseBody
    public ResponseEntity<?> unlinkPortfolio(@RequestBody Map<String, String> payload) {
        outsourcingService.unlinkPortfolioFromOutsourcing(payload.get("osCd"), payload.get("prtfCd"));
        return ResponseEntity.ok().build();
    }
}