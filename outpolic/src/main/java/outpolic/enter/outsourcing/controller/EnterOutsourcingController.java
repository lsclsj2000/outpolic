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
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import groovy.util.logging.Slf4j;
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

@Slf4j
@Controller
@RequestMapping("/enter/outsourcing")
@RequiredArgsConstructor
public class EnterOutsourcingController {
	
    private final ObjectMapper objectMapper;
    private final EnterOutsourcingService outsourcingService;
    private final CategorySearchService categorySearchService;
    private final EnterPortfolioService portfolioService;

    @GetMapping("/add")
    public String showAddOutsourcingForm(Model model, HttpSession session) {
        session.removeAttribute("outsourcingFormData");
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

            String entCd = outsourcingService.findEntCdByMbrCd(mbrCd);
            formData.setEntCd(entCd);
            formData.setMbrCd(mbrCd);
            String generatedOsCd = outsourcingService.saveStep1Data(formData, session);
            return ResponseEntity.ok(Map.of("success", true, "osCd", generatedOsCd));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "서버 내부 오류: " + e.getMessage()));
        }
    }

    @PostMapping("/save-step2")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveStep2(
            @RequestParam(value = "categoryCodes[]", required = false) List<String> categoryCodes,
            @RequestParam(value = "tags", defaultValue = "") String tags,
            @SessionAttribute("outsourcingFormData") OutsourcingFormDataDto formData) {
        
        formData.setCategoryCodes(categoryCodes);
        formData.setTags(tags);
        
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/save-step3")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveStep3(
            @RequestParam("outsourcingThumbnailFile") MultipartFile thumbnailFile,
            @SessionAttribute("outsourcingFormData") OutsourcingFormDataDto formData) {
        
        FileMetaData uploadedFile = outsourcingService.uploadThumbnail(thumbnailFile);
        if (uploadedFile == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "파일 업로드에 실패했습니다."));
        }
        formData.setThumbnailFile(uploadedFile);

        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/complete-registration")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> completeRegistration(@SessionAttribute("outsourcingFormData") OutsourcingFormDataDto formData, HttpSession session) {
        // categoryCodes가 비어있을 경우 예외 처리 제거 (이제 프론트엔드에서 유효성 검사 담당)
        // if (formData.getCategoryCodes() == null || formData.getCategoryCodes().isEmpty()) {
        //     throw new IllegalArgumentException("카테고리 ID는 필수입니다. 2단계에서 카테고리를 선택해주세요.");
        // }
        
        outsourcingService.completeOutsourcingRegistration(formData, session);
        session.removeAttribute("outsourcingFormData");
        return ResponseEntity.ok(Map.of("success", true, "message", "외주 등록이 완료되었습니다.", "redirectUrl", "/enter/outsourcing/list"));
    }

    // ======================================================
    // ▼▼▼ 외주 "수정" 및 기타 로직 (기존과 동일) ▼▼▼
    // ======================================================
    
    @GetMapping("/edit/{osCd}")
    public String showEditOutsourcingForm(@PathVariable String osCd, Model model) {
        EnterOutsourcing outsourcing = outsourcingService.findOutsourcingDetailsByOsCd(osCd);
        if (outsourcing == null) {
            return "redirect:/enter/outsourcing/list?error=notfound";
        }
        
        try {
            String categoryPathJson = objectMapper.writeValueAsString(outsourcing.getCategories());
            model.addAttribute("categoryPathJson", categoryPathJson);
        } catch (JsonProcessingException e) {
            model.addAttribute("categoryPathJson", "[]");
        }
        
        model.addAttribute("outsourcing", outsourcing);
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
            @RequestParam("osStrtYmdt") @DateTimeFormat(iso = 
DateTimeFormat.ISO.DATE_TIME) LocalDateTime osStrtYmdt,
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
            @RequestParam(value = "outsourcingThumbnailFile", required = false) MultipartFile thumbnailFile) {
        outsourcingService.updateOutsourcingStep3(osCd, thumbnailFile);
        return ResponseEntity.ok(Map.of("success", true, "message", "첨부 파일이 수정되었습니다."));
    }

    @PostMapping("/edit/complete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> completeEdit(@RequestBody Map<String, String> payload) {
        return ResponseEntity.ok(Map.of("success", true, "message", "외주 정보 수정이 완료되었습니다.", "redirectUrl", "/enter/outsourcing/list"));
    }
    
    @GetMapping("/list")
    public String showOutsourcingListView() { 
        return "enter/outsourcing/outsourcingListView";
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

    // 이 엔드포인트는 이미 존재하지만, 매퍼의 쿼리 수정이 필요할 수 있습니다. (아래 XML 수정 확인)
    @GetMapping("/api/categories/search") 
    @ResponseBody
    public ResponseEntity<List<CategorySearchDto>> searchCategories(@RequestParam(defaultValue = "") String query) {
        return ResponseEntity.ok(categorySearchService.searchCategoriesByName(query));
    }

    @GetMapping("/api/tags/search") 
    @ResponseBody
    public ResponseEntity<List<String>> searchOutsourcingTags(@RequestParam(defaultValue = "") String query) {
        return ResponseEntity.ok(outsourcingService.searchTags(query));
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
        String mbrCd = (String) session.getAttribute("SCD");
        if (mbrCd == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.emptyList());
        }
        String entCd = outsourcingService.findEntCdByMbrCd(mbrCd);
        return ResponseEntity.ok(outsourcingService.searchUnlinkedPortfolios(osCd, entCd, query));
    }

    @PostMapping("/link-portfolio") 
    @ResponseBody
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

    @DeleteMapping("/unlink-portfolio") 
    @ResponseBody
    public ResponseEntity<?> unlinkPortfolio(@RequestBody Map<String, String> payload) {
        outsourcingService.unlinkPortfolioFromOutsourcing(payload.get("osCd"), payload.get("prtfCd"));
        return ResponseEntity.ok().build();
    }
}