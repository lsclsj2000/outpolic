package outpolic.enter.outsourcing.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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
import outpolic.systems.file.domain.FileMetaData;
import outpolic.systems.util.FilesUtils;

@Controller
@RequestMapping("/enter/outsourcing")
@RequiredArgsConstructor
public class EnterOutsourcingController {

    private final EnterOutsourcingService outsourcingService;
    private final CategorySearchService categorySearchService;
    private final EnterPortfolioService portfolioService;
    private final FilesUtils filesUtils; // FilesUtils 주입 (파일 업로드/삭제 로직을 FilesUtils에 위임하기 때문)

    // ======================================================
    // ▼▼▼ 외주 "등록" 관련 로직 ▼▼▼
    // ======================================================

    @GetMapping("/add")
    public String showAddOutsourcingForm(Model model, HttpSession session) {
        session.removeAttribute("outsourcingFormData");
        
        OutsourcingFormDataDto formData = new OutsourcingFormDataDto();

        String mbrCd = (String) session.getAttribute("SCD");
        if (mbrCd == null) {
            return "redirect:/login";
        }
        String entCd = outsourcingService.findEntCdByMbrCd(mbrCd);

        model.addAttribute("entCd", entCd);
        model.addAttribute("mbrCd", mbrCd);
        model.addAttribute("outsourcing", new EnterOutsourcing());
        
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

            String entCd = outsourcingService.findEntCdByMbrCd(mbrCd);
            formData.setEntCd(entCd);
            formData.setMbrCd(mbrCd);

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
            @RequestParam(value = "tags", required = false) String tags, HttpSession session) {
        List<String> categoryCodes = (categoryCodesStr != null && !categoryCodesStr.isEmpty()) ?
            Arrays.asList(categoryCodesStr.split(",")) : new ArrayList<>();
        outsourcingService.saveStep2Data(osCd, categoryCodes, tags, session);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/save-step3")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveStep3(
            @RequestParam("osCd") String osCd,
            // 수정: @RequestParam 이름과 타입 변경 (단일 썸네일 파일만 받음)
            @RequestParam(value = "outsourcingThumbnailFile", required = false) MultipartFile thumbnailFile,
            HttpSession session) {
        
        // 서비스 메서드 호출 시, 변경된 파라미터로 전달
        outsourcingService.saveStep3Data(osCd, thumbnailFile, session);
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
    
    @GetMapping("/edit/{osCd}")
    public String showEditOutsourcingForm(@PathVariable String osCd, Model model) {
        EnterOutsourcing outsourcing = outsourcingService.findOutsourcingDetailsByOsCd(osCd);
        if (outsourcing == null) {
            return "redirect:/enter/outsourcing/list?error=notfound";
        }
        
        List<String> existingFileUrls = new ArrayList<>();
        if (outsourcing.getClCd() != null) {
            existingFileUrls = outsourcingService.getFilesByClCd(outsourcing.getClCd());
        }
        
        model.addAttribute("outsourcing", outsourcing);
        model.addAttribute("existingFileUrls", existingFileUrls);
        return "enter/outsourcing/editOutsourcing";
    }

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
        
        outsourcingService.updateOutsourcingStep1(outsourcingToUpdate);
        return ResponseEntity.ok(Map.of("success", true, "message", "기본 정보가 수정되었습니다."));
    }

    @PostMapping("/edit/step2")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateStep2(
            @RequestParam("osCd") String osCd,
            @RequestParam(value = "categoryCodes", required = false) String categoryCodesStr,
            @RequestParam(value = "tags", required = false) String tags) {
        List<String> categoryCodes = (categoryCodesStr != null && !categoryCodesStr.isEmpty()) ?
            Arrays.asList(categoryCodesStr.split(",")) : new ArrayList<>();
        outsourcingService.updateOutsourcingStep2(osCd, categoryCodes, tags);
        return ResponseEntity.ok(Map.of("success", true, "message", "카테고리 및 태그가 수정되었습니다."));
    }
    
    @PostMapping("/edit/step3")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateStep3(
            @RequestParam("osCd") String osCd,
            // 수정: @RequestParam 이름과 타입 변경 (단일 썸네일 파일만 받음)
            @RequestParam(value = "outsourcingThumbnailFile", required = false) MultipartFile thumbnailFile,
            HttpSession session) { // HttpSession은 여기서 사용되지 않지만, 필요하면 유지
            
        // 서비스 메서드 호출 시, 변경된 파라미터로 전달
        outsourcingService.updateOutsourcingStep3(osCd, thumbnailFile); // HttpSession 파라미터도 제거
        return ResponseEntity.ok(Map.of("success", true, "message", "첨부 파일이 수정되었습니다."));
    }

    @PostMapping("/edit/complete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> completeEdit(@RequestBody Map<String, String> payload) {
        String osCd = payload.get("osCd");
        return ResponseEntity.ok(Map.of("success", true, "message", "외주 정보 수정이 완료되었습니다.", "redirectUrl", "/enter/outsourcing/list"));
    }

    // ======================================================
    // ▼▼▼ 파일 업로드/삭제 관련 로직 (CKEditor 미사용, 일반 파일 처리용) ▼▼▼
    //     이 컨트롤러에서는 CKEditor를 사용하지 않으므로, 이전에 추가했던
    //     /uploadImage, /deleteImage 엔드포인트는 제거합니다.
    //     (FilesUtils는 서비스 레이어에서 직접 호출됩니다.)
    // ======================================================

    // (기존의 /uploadImage 및 /deleteImage 메서드는 이 컨트롤러에서 제거됨)


    // ======================================================
    // ▼▼▼ 공통 API 및 기타 로직 ▼▼▼
    // ======================================================
    @GetMapping("/list")
    public String showOutsourcingListView() { return "enter/outsourcing/outsourcingListView";
    }

    @GetMapping("/listData")
    @ResponseBody
    public ResponseEntity<List<EnterOutsourcing>> getOutsourcingListData(HttpSession session) {
        String mbrCd = (String) session.getAttribute("SCD");
        if (mbrCd == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.emptyList());
        }
        String entCd = outsourcingService.findEntCdByMbrCd(mbrCd);
        return ResponseEntity.ok(outsourcingService.getOutsourcingListByEntCd(entCd));
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

    @GetMapping("/{osCd}/unlinked-portfolios")
    @ResponseBody
    public ResponseEntity<List<EnterPortfolio>> searchUnlinkedPortfolios(
            @PathVariable String osCd,
            @RequestParam String query,
            HttpSession session) {
        String mbrCd = (String) session.getAttribute("SCD");
        if (mbrCd == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.emptyList());
        }
        String entCd = outsourcingService.findEntCdByMbrCd(mbrCd);
        return ResponseEntity.ok(outsourcingService.searchUnlinkedPortfolios(osCd, entCd, query));
    }

    @PostMapping("/link-portfolio") @ResponseBody
    public ResponseEntity<?> linkPortfolio(@RequestBody Map<String, String> payload, HttpSession session) {
        String osCd = payload.get("osCd");
        String prtfCd = payload.get("prtfCd");
        String mbrCd = (String) session.getAttribute("SCD");
        if (mbrCd == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "로그인이 필요합니다."));
        }
        String entCd = outsourcingService.findEntCdByMbrCd(mbrCd);
        outsourcingService.linkPortfolioToOutsourcing(osCd, prtfCd, entCd);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/unlink-portfolio") @ResponseBody
    public ResponseEntity<?> unlinkPortfolio(@RequestBody Map<String, String> payload) {
        outsourcingService.unlinkPortfolioFromOutsourcing(payload.get("osCd"), payload.get("prtfCd"));
        return ResponseEntity.ok().build();
    }
}