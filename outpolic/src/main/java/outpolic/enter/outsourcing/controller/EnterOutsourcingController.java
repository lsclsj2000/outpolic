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
import outpolic.enter.outsourcing.domain.OutsourcingFormDataDto;
import outpolic.enter.outsourcing.service.EnterOutsourcingService;
import org.springframework.http.HttpStatus;
import outpolic.enter.portfolio.domain.EnterPortfolio;
import outpolic.enter.portfolio.service.EnterPortfolioService;

@Controller
@RequestMapping("/enter/outsourcing")
@RequiredArgsConstructor
public class EnterOutsourcingController {

    private final EnterOutsourcingService outsourcingService;
    private final CategorySearchService categorySearchService;
    private final EnterPortfolioService portfolioService;

    // --- 외주 등록 메인 뷰 (단계별 폼을 포함) ---
    @GetMapping("/add")
    public String showAddOutsourcingForm(Model model, HttpSession session) {
        // TODO: 세션에서 실제 기업/회원 코드 가져오기
        model.addAttribute("entCd","EI_C00001"); 
        model.addAttribute("mbrCd", "MB_C0000036");
        
        // 세션에 임시 저장된 데이터가 있다면 불러와서 폼에 채울 수 있음 (단계별 복귀 시)
        OutsourcingFormDataDto formData = (OutsourcingFormDataDto) session.getAttribute("outsourcingFormData");
        if (formData != null) {
        	formData = new OutsourcingFormDataDto();
        }
        model.addAttribute("formData",formData);
        return "enter/outsourcing/addOutsourcingListView";
    }

    // --- 1단계: 기본 정보 저장 (텍스트 필드만) ---
    @PostMapping("/save-step1")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveStep1(
            @ModelAttribute OutsourcingFormDataDto formData, // DTO로 받음
            HttpSession session) {
        try {
            // TODO: 세션에서 실제 기업/회원 코드 가져오기
            String entCd = "EI_C00001";
            String mbrCd = "MB_C0000036";
            formData.setEntCd(entCd);
            formData.setMbrCd(mbrCd);

            String generatedOsCd = outsourcingService.saveStep1Data(formData, session);

            return ResponseEntity.ok(Map.of("success", true, "osCd", generatedOsCd, "message", "1단계 정보가 저장되었습니다."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("success", false, "message", "1단계 저장 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    // --- 2단계: 카테고리 및 태그 저장 ---
    @PostMapping("/save-step2")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveStep2(
            @RequestParam("osCd") String osCd, // Hidden 필드나 세션에서 osCd 받음
            @RequestParam(value="categoryCodes", required=false) String categoryCodesStr,
            @RequestParam(value="tags", required=false) String tags,
            HttpSession session) {
        try {
            List<String> categoryCodes = new ArrayList<>();
            if (categoryCodesStr != null && !categoryCodesStr.isEmpty()) {
                categoryCodes = Arrays.asList(categoryCodesStr.split(","));
            }
            
            outsourcingService.saveStep2Data(osCd, categoryCodes, tags, session);

            return ResponseEntity.ok(Map.of("success", true, "message", "2단계 정보가 저장되었습니다."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("success", false, "message", "2단계 저장 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    // --- 3단계: 첨부 파일 업로드 ---
    @PostMapping("/save-step3")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveStep3(
            @RequestParam("osCd") String osCd,
            @RequestParam(value="outsourcingReferenceFiles", required=false) MultipartFile[] files, // 파일 받음
            HttpSession session) {
        try {
            List<String> fileUrls = outsourcingService.saveStep3Data(osCd, files, session);

            return ResponseEntity.ok(Map.of("success", true, "message", "3단계 파일이 업로드되었습니다.", "fileUrls", fileUrls));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("success", false, "message", "3단계 파일 업로드 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    // --- 4단계: 최종 등록 완료 (모든 데이터 DB 저장) ---
    @PostMapping("/complete-registration")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> completeRegistration(HttpSession session) {
        try {
            outsourcingService.completeOutsourcingRegistration(session);

            return ResponseEntity.ok(Map.of("success", true, "message", "외주 등록이 완료되었습니다.", "redirectUrl", "/enter/outsourcing/list"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("success", false, "message", "최종 등록 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }


    // --- 기존 API 유지 (리스트, 수정, 삭제, 연결 관리 등) ---
    @GetMapping("/list")
    public String showOutsourcingListView() { 
        return "enter/outsourcing/outsourcingListView";
    }

    @GetMapping("/listData")
    @ResponseBody
    public ResponseEntity<List<EnterOutsourcing>> getOutsourcingListData(HttpSession session){ 
        String currentEntCd = "EI_C00001"; 
        return ResponseEntity.ok(outsourcingService.getOutsourcingListByEntCd(currentEntCd));
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
            @RequestParam(value="tags", required=false) String tags) {

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
            outsourcingService.updateOutsourcing(outsourcing, categoryCodes, tags);
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
        return ResponseEntity.ok(portfolioService.searchPortfoliosByTitle(query));
    }

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
        String entCd = "EI_C00001"; 
        return ResponseEntity.ok(outsourcingService.searchUnlinkedPortfolios(osCd, entCd, query));
    }

    @PostMapping("/link-portfolio")
    @ResponseBody
    public ResponseEntity<?> linkPortfolio(@RequestBody Map<String, String> payload, HttpSession session) {
        String osCd = payload.get("osCd");
        String prtfCd = payload.get("prtfCd");
        String entCd = "EI_C00001"; 
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
