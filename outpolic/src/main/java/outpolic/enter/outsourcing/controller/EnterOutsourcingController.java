package outpolic.enter.outsourcing.controller;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import org.springframework.web.bind.annotation.RestController;
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
        // ▼▼▼ [수정] 세션에 남아있는 이전 데이터를 무조건 삭제합니다. ▼▼▼
        session.removeAttribute("outsourcingFormData");
        
        // 항상 새로운 빈 폼 데이터 객체를 생성합니다.
        OutsourcingFormDataDto formData = new OutsourcingFormDataDto();

        String mbrCd = (String) session.getAttribute("SCD");
        if (mbrCd == null) {
            return "redirect:/login";
        }
        String entCd = outsourcingService.findEntCdByMbrCd(mbrCd);

        model.addAttribute("entCd", entCd);
        model.addAttribute("mbrCd", mbrCd);
        model.addAttribute("formData", formData);
        
        return "enter/outsourcing/addOutsourcingListView";
    }
    
    @PostMapping("/save-step1")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveStep1(
            @ModelAttribute OutsourcingFormDataDto formData,
            HttpSession session) {

        try {
            String mbrCd = (String) session.getAttribute("SCD");
            if (mbrCd == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "로그인이 필요합니다."));
            }

            System.out.println("📌 받은 제목: " + formData.getOsTtl());
            System.out.println("📌 받은 시작일: " + formData.getOsStrtYmdt());

            // 세션에서 기업 코드 찾아서 DTO에 세팅
            String entCd = outsourcingService.findEntCdByMbrCd(mbrCd);
            formData.setEntCd(entCd);
            formData.setMbrCd(mbrCd);

            // 저장 로직
            String generatedOsCd = outsourcingService.saveStep1Data(formData, session);

            return ResponseEntity.ok(Map.of("success", true, "osCd", generatedOsCd));
        } catch (Exception e) {
            e.printStackTrace(); // 콘솔에 로그 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "서버 내부 오류: " + e.getMessage()));
        }
    }

    @PostMapping("/save-step2")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveStep2(
            @RequestParam("osCd") String osCd,
            @RequestParam(value = "categoryCodes", required = false) String categoryCodesStr,
            @RequestParam(value = "tags", required = false) String tags,
            HttpSession session) {
        List<String> categoryCodes = (categoryCodesStr != null 
 && !categoryCodesStr.isEmpty()) ?
Arrays.asList(categoryCodesStr.split(",")) : new ArrayList<>(); // [cite: 99]
        outsourcingService.saveStep2Data(osCd, categoryCodes, tags, session); // [cite: 99]
        return ResponseEntity.ok(Map.of("success", true)); // [cite: 100]
    }

    @PostMapping("/save-step3")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveStep3(
            @RequestParam("osCd") String osCd,
            @RequestParam(value = "outsourcingReferenceFiles", required = false) MultipartFile[] files,
            HttpSession session) {
        
        outsourcingService.saveStep3Data(osCd, files, session); // [cite: 100]
        return ResponseEntity.ok(Map.of("success", true)); // [cite: 101]
    }

    @PostMapping("/complete-registration")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> completeRegistration(@RequestBody Map<String, String> payload, HttpSession session) {
        String osCd = payload.get("osCd"); // [cite: 101]
        if (osCd == null || osCd.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "외주 코드가 유효하지 않습니다.")); // [cite: 102]
        }
        outsourcingService.completeOutsourcingRegistration(osCd, session); // [cite: 103]
        return ResponseEntity.ok(Map.of("success", true, "message", "외주 등록이 완료되었습니다.", "redirectUrl", "/enter/outsourcing/list")); // [cite: 103]
    }

    // ======================================================
    // ▼▼▼ 외주 "수정" 관련 로직 ▼▼▼
    // ======================================================
    
    @GetMapping("/edit/{osCd}")
    public String showEditOutsourcingForm(@PathVariable String osCd, Model model) {
        EnterOutsourcing outsourcing = outsourcingService.findOutsourcingDetailsByOsCd(osCd); // [cite: 104]
        if (outsourcing == null) {
            return "redirect:/enter/outsourcing/list?error=notfound"; // [cite: 105]
        }
        
        // 기존 첨부 파일 URL들을 가져와서 모델에 추가 (수정 페이지에서 미리보기 위함)
        List<String> existingFileUrls = new ArrayList<>(); // [cite: 106]
        if (outsourcing.getClCd() != null) {
            existingFileUrls = outsourcingService.getFilesByClCd(outsourcing.getClCd()); // [cite: 107]
        }
        
        model.addAttribute("outsourcing", outsourcing); // [cite: 108]
        model.addAttribute("existingFileUrls", existingFileUrls); // [cite: 108]
        return "enter/outsourcing/editOutsourcing"; // [cite: 109]
    }

    @PostMapping("/edit/step1")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateStep1(
            @RequestParam("osCd") String osCd,
            @RequestParam("osTtl") String osTtl,
            @RequestParam("osExpln") String osExpln,
            @RequestParam("osAmt") Integer osAmt,
            @RequestParam("osFlfmtCnt") Integer osFlfmtCnt,
            @RequestParam("osStrtYmdt") 
 @DateTimeFormat(iso = 
DateTimeFormat.ISO.DATE_TIME) LocalDateTime osStrtYmdt,
            @RequestParam("osEndYmdt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime osEndYmdt) {
        
        EnterOutsourcing outsourcingToUpdate = new EnterOutsourcing(); // [cite: 110]
        outsourcingToUpdate.setOsCd(osCd); // [cite: 111]
        outsourcingToUpdate.setOsTtl(osTtl); // [cite: 111]
        outsourcingToUpdate.setOsExpln(osExpln); // [cite: 111]
        if (osAmt != null) outsourcingToUpdate.setOsAmt(BigDecimal.valueOf(osAmt)); // [cite: 111]
        outsourcingToUpdate.setOsFlfmtCnt(osFlfmtCnt); // [cite: 111]
        outsourcingToUpdate.setOsStrtYmdt(osStrtYmdt); // [cite: 111]
        outsourcingToUpdate.setOsEndYmdt(osEndYmdt); // [cite: 111]
        
        outsourcingService.updateOutsourcingStep1(outsourcingToUpdate); // [cite: 111]
        return ResponseEntity.ok(Map.of("success", true, "message", "기본 정보가 수정되었습니다.")); // [cite: 112]
    }

    @PostMapping("/edit/step2")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateStep2(
            @RequestParam("osCd") String osCd,
            @RequestParam(value = "categoryCodes", required = false) String categoryCodesStr,
            @RequestParam(value = "tags", required = false) String tags) {
        
        List<String> categoryCodes = (categoryCodesStr != null 
 && !categoryCodesStr.isEmpty()) ?
Arrays.asList(categoryCodesStr.split(",")) : new ArrayList<>(); // [cite: 113]
        outsourcingService.updateOutsourcingStep2(osCd, categoryCodes, tags); // [cite: 113]
        return ResponseEntity.ok(Map.of("success", true, "message", "카테고리 및 태그가 수정되었습니다.")); // [cite: 114]
    }
    
    @PostMapping("/edit/step3")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateStep3(
            @RequestParam("osCd") String osCd,
            @RequestParam(value = "outsourcingReferenceFiles", required = false) MultipartFile[] files,
            // existingFileUrlsList 파라미터를 @RequestParam으로 받지 않고, 필요하면 서비스에서 직접 조회하도록 합니다.
            HttpSession session) {
            
        outsourcingService.updateOutsourcingStep3(osCd, files); // existingFileUrlsList 매개변수 제거 [cite: 286]
        return ResponseEntity.ok(Map.of("success", true, "message", "첨부 파일이 수정되었습니다.")); // [cite: 115]
    }

    @PostMapping("/edit/complete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> completeEdit(@RequestBody Map<String, String> payload) {
        String osCd = payload.get("osCd"); // [cite: 115]
        return ResponseEntity.ok(Map.of("success", true, "message", "외주 정보 수정이 완료되었습니다.", "redirectUrl", "/enter/outsourcing/list")); // [cite: 116]
    }


    // ======================================================
    // ▼▼▼ 공통 API 및 기타 로직 ▼▼▼
    // ======================================================
    @GetMapping("/list")
    public String showOutsourcingListView() { return "enter/outsourcing/outsourcingListView"; // [cite: 117]
 }

    @GetMapping("/listData")
    @ResponseBody
    public ResponseEntity<List<EnterOutsourcing>> getOutsourcingListData(HttpSession session) {
        String mbrCd = (String) session.getAttribute("SCD"); // [cite: 118]
        if (mbrCd == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.emptyList()); // [cite: 119]
        }
        String entCd = outsourcingService.findEntCdByMbrCd(mbrCd); // [cite: 120]
        return ResponseEntity.ok(outsourcingService.getOutsourcingListByEntCd(entCd)); // [cite: 120]
    }
    
    @DeleteMapping("/delete/{osCd}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteOutsourcing(@PathVariable String osCd) {
        outsourcingService.deleteOutsourcing(osCd); // [cite: 121]
        return ResponseEntity.ok(Map.of("success", true, "message", "삭제되었습니다.")); // [cite: 122]
    }

    @GetMapping("/api/categories/search") @ResponseBody
    public ResponseEntity<List<CategorySearchDto>> searchCategories(@RequestParam(defaultValue = "") String query) {
        return ResponseEntity.ok(categorySearchService.searchCategoriesByName(query)); // [cite: 122]
    }

    @GetMapping("/api/tags/search") @ResponseBody
    public ResponseEntity<List<String>> searchOutsourcingTags(@RequestParam(defaultValue = "") String query) {
        return ResponseEntity.ok(outsourcingService.searchTags(query)); // [cite: 123]
    }

    @GetMapping("/{osCd}/linked-portfolios") @ResponseBody
    public ResponseEntity<List<EnterPortfolio>> getLinkedPortfolios(@PathVariable String osCd) {
        return ResponseEntity.ok(outsourcingService.getLinkedPortfoliosByOsCd(osCd)); // [cite: 124]
    }

    @GetMapping("/{osCd}/unlinked-portfolios")
    @ResponseBody
    public ResponseEntity<List<EnterPortfolio>> searchUnlinkedPortfolios(
            @PathVariable String osCd,
            @RequestParam String query,
            HttpSession session) {
        String mbrCd = (String) session.getAttribute("SCD"); // [cite: 125]
        if (mbrCd == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.emptyList()); // [cite: 126]
        }
        String entCd = outsourcingService.findEntCdByMbrCd(mbrCd); // [cite: 127]
        return ResponseEntity.ok(outsourcingService.searchUnlinkedPortfolios(osCd, entCd, query)); // [cite: 127]
    }

    @PostMapping("/link-portfolio") @ResponseBody
    public ResponseEntity<?> linkPortfolio(@RequestBody Map<String, String> payload, HttpSession session) {
        String osCd = payload.get("osCd"); // [cite: 128]
        String prtfCd = payload.get("prtfCd"); // [cite: 129]
        String mbrCd = (String) session.getAttribute("SCD"); // [cite: 129]
        if (mbrCd == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "로그인이 필요합니다.")); // [cite: 130]
        }
        String entCd = outsourcingService.findEntCdByMbrCd(mbrCd); // [cite: 130]
        outsourcingService.linkPortfolioToOutsourcing(osCd, prtfCd, entCd); // [cite: 130]
        return ResponseEntity.ok().build(); // [cite: 131]
    }

    @DeleteMapping("/unlink-portfolio") @ResponseBody
    public ResponseEntity<?> unlinkPortfolio(@RequestBody Map<String, String> payload) {
        outsourcingService.unlinkPortfolioFromOutsourcing(payload.get("osCd"), payload.get("prtfCd")); // [cite: 131]
        return ResponseEntity.ok().build(); // [cite: 132]
    }
    
   
}