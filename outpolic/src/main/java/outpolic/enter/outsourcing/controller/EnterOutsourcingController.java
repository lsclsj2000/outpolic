package outpolic.enter.outsourcing.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

    private static final Logger log = LoggerFactory.getLogger(EnterOutsourcingController.class);
    private final ObjectMapper objectMapper;
    private final EnterOutsourcingService outsourcingService;
    private final CategorySearchService categorySearchService;
    private final EnterPortfolioService portfolioService;

    // ======================================================
    // ▼▼▼ 외주 "신규 등록" 관련 로직 ▼▼▼
    // ======================================================

    @GetMapping("/add")
    public String showAddOutsourcingForm(Model model, HttpSession session) {
        session.removeAttribute("outsourcingFormData");
        String mbrCd = (String) session.getAttribute("SCD");
        if (mbrCd == null) {
            return "redirect:/login";
        }
        
        OutsourcingFormDataDto formData = new OutsourcingFormDataDto();
        String entCd = outsourcingService.findEntCdByMbrCd(mbrCd);
        formData.setEntCd(entCd);
        formData.setMbrCd(mbrCd);
        
        session.setAttribute("outsourcingFormData", formData);
        
        model.addAttribute("entCd", entCd);
        return "enter/outsourcing/addOutsourcingListView";
    }

    @PostMapping("/save-step1")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveStep1(
            @RequestBody Map<String, Object> payload,
            @SessionAttribute("outsourcingFormData") OutsourcingFormDataDto formData,
            HttpSession session) { // 서비스 메서드가 session을 사용할 수 있으므로 파라미터 추가
        
        try {
            // 1. 페이로드 데이터를 세션의 formData DTO에 업데이트 (기존 로직)
            formData.setOsTtl((String) payload.get("osTtl"));
            formData.setOsExpln((String) payload.get("osExpln"));
            formData.setOsStrtYmdt(LocalDateTime.parse((String) payload.get("osStrtYmdt")));
            formData.setOsEndYmdt(LocalDateTime.parse((String) payload.get("osEndYmdt")));
            formData.setOsAmt(new BigDecimal(payload.get("osAmt").toString()));
            formData.setOsFlfmtCnt(Integer.parseInt(payload.get("osFlfmtCnt").toString()));
            
            // ✨ 2. [핵심 수정] 서비스 로직을 호출하여 osCd를 생성/조회하고 DTO에 설정합니다.
            //    (saveStep1Data는 osCd를 생성하고 DTO에 설정한 후, osCd를 반환해야 합니다.)
            String osCd = outsourcingService.saveStep1Data(formData, session);

            // ✨ 3. [핵심 수정] 응답에 생성된 osCd를 포함하여 반환합니다.
            return ResponseEntity.ok(Map.of("success", true, "osCd", osCd));

        } catch (Exception e) {
            log.error("1단계 저장 데이터 형식 오류", e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "입력 값 형식이 올바르지 않습니다."));
        }
    }

    @PostMapping("/save-step2")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveStep2(
            @RequestBody Map<String, Object> payload,
            @SessionAttribute("outsourcingFormData") OutsourcingFormDataDto formData) {
        
        formData.setCategoryCodes((List<String>) payload.get("categoryCodes"));
        formData.setTags((String) payload.get("tags"));
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/complete-registration")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> completeRegistration(
            @SessionAttribute("outsourcingFormData") OutsourcingFormDataDto formData,
            @RequestParam(value = "outsourcingThumbnailFile", required = false) MultipartFile thumbnailFile,
            @RequestParam(value = "bodyImageFiles", required = false) List<MultipartFile> bodyImageFiles,
            HttpSession session) {

        try {
            outsourcingService.completeOutsourcingRegistration(formData, thumbnailFile, bodyImageFiles, session);
            session.removeAttribute("outsourcingFormData");
            return ResponseEntity.ok(Map.of("success", true, "message", "외주 등록이 완료되었습니다.", "redirectUrl", "/enter/outsourcing/list"));
        } catch (Exception e) {
            log.error("외주 최종 등록 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "등록 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    // ======================================================
    // ▼▼▼ 외주 "수정" 관련 로직 ▼▼▼
    // ======================================================
    
    @GetMapping("/edit/{osCd}")
    public String showEditOutsourcingForm(@PathVariable String osCd, Model model, HttpSession session) {
        EnterOutsourcing outsourcing = outsourcingService.findOutsourcingDetailsByOsCd(osCd);
        if (outsourcing == null) {
            return "redirect:/enter/outsourcing/list?error=notfound";
        }
        
        OutsourcingFormDataDto formData = new OutsourcingFormDataDto();
        formData.setOsCd(outsourcing.getOsCd());
        formData.setOsTtl(outsourcing.getOsTtl());
        formData.setOsExpln(outsourcing.getOsExpln());
        formData.setOsStrtYmdt(outsourcing.getOsStrtYmdt());
        formData.setOsEndYmdt(outsourcing.getOsEndYmdt());
        formData.setOsAmt(outsourcing.getOsAmt());
        formData.setOsFlfmtCnt(outsourcing.getOsFlfmtCnt());
        if (outsourcing.getTagNames() != null) {
             formData.setTags(String.join(", ", outsourcing.getTagNames()));
        }
        
        session.setAttribute("outsourcingFormData", formData);
        
        try {
            List<List<CategorySearchDto>> allCategoryPaths = new ArrayList<>();
            String representativeCtgryId = outsourcing.getCtgryId();
            if (representativeCtgryId != null && !representativeCtgryId.isEmpty()) {
                List<CategorySearchDto> fullPath = categorySearchService.getCategoryPath(representativeCtgryId);
                if (!fullPath.isEmpty()) {
                    allCategoryPaths.add(fullPath);
                }
            }
            model.addAttribute("categoryPathsJson", objectMapper.writeValueAsString(allCategoryPaths));
        } catch (JsonProcessingException e) {
            model.addAttribute("categoryPathsJson", "[]");
        }
        
        model.addAttribute("outsourcing", outsourcing);
        return "enter/outsourcing/editOutsourcing";
    }

    @PostMapping("/edit/step1")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateStep1(
            @RequestBody Map<String, Object> payload,
            @SessionAttribute("outsourcingFormData") OutsourcingFormDataDto formData,
            HttpSession session) { // ✨ 1. HttpSession을 파라미터로 추가합니다.

        // '새 외주 등록'의 1단계 저장 로직을 재사용하여 세션 DTO를 업데이트
        // ✨ 2. saveStep1 호출 시 session을 전달합니다.
        return saveStep1(payload, formData, session); 
    }

    @PostMapping("/edit/step2")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateStep2(
            @RequestBody Map<String, Object> payload,
            @SessionAttribute("outsourcingFormData") OutsourcingFormDataDto formData) {
        // '새 외주 등록'의 2단계 저장 로직을 재사용
        return saveStep2(payload, formData);
    }
    
    @PostMapping("/edit/complete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> completeEdit(
            @SessionAttribute("outsourcingFormData") OutsourcingFormDataDto formData,
            @RequestParam("osCd") String osCd,
            @RequestParam(value = "outsourcingThumbnailFile", required = false) MultipartFile thumbnailFile,
            @RequestParam(value = "newBodyImageFiles", required = false) List<MultipartFile> newBodyImageFiles,
            @RequestParam(value = "deletedBodyImageCds", required = false) List<String> deletedBodyImageCds,
            HttpSession session) {

        try {
            // ✨ [3단계] 여러 줄로 나뉘어 있던 호출을 아래 한 줄로 변경합니다.
            outsourcingService.updateOutsourcingAll(formData, osCd, thumbnailFile, newBodyImageFiles, deletedBodyImageCds);
            
            session.removeAttribute("outsourcingFormData");
            return ResponseEntity.ok(Map.of("success", true, "message", "외주 정보 수정이 완료되었습니다.", "redirectUrl", "/enter/outsourcing/list"));
        } catch (Exception e) {
            log.error("외주 최종 수정 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "수정 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
    
    // ======================================================
    // ▼▼▼ 공통 로직 (리스트, API 등) ▼▼▼
    // ======================================================
    
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
    public ResponseEntity<Map<String, Object>> linkPortfolio(@RequestBody Map<String, String> payload, HttpSession session) {
        String osCd = payload.get("osCd");
        String prtfCd = payload.get("prtfCd");
        String mbrCd = (String) session.getAttribute("SCD");
        if (mbrCd == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "로그인이 필요합니다."));
        }
        String entCd = outsourcingService.findEntCdByMbrCd(mbrCd);
        outsourcingService.linkPortfolioToOutsourcing(osCd, prtfCd, entCd);
        return ResponseEntity.ok(Map.of("success", true, "message", "포트폴리오가 성공적으로 연결되었습니다."));
    }

    @DeleteMapping("/unlink-portfolio")    
    @ResponseBody
    public ResponseEntity<Map<String, Object>> unlinkPortfolio(@RequestBody Map<String, String> payload) {
        outsourcingService.unlinkPortfolioFromOutsourcing(payload.get("osCd"), payload.get("prtfCd"));
        return ResponseEntity.ok(Map.of("success", true, "message", "연결이 해제되었습니다."));
    }
    
    @GetMapping("/api/portfolio/search")
    @ResponseBody
    public ResponseEntity<List<EnterPortfolio>> searchCompanyPortfolios(
            @RequestParam("query") String query,
            @RequestParam("entCd") String entCd) {
        List<EnterPortfolio> searchResults = portfolioService.searchByTitleForLinking(query, entCd);
        return ResponseEntity.ok(searchResults);
    }
}