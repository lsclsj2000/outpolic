package outpolic.enter.portfolio.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.enter.POAddtional.domain.CategorySearchDto;
import outpolic.enter.POAddtional.service.CategorySearchService;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;
import outpolic.enter.outsourcing.service.EnterOutsourcingService;
import outpolic.enter.portfolio.domain.EnterPortfolio;
import outpolic.enter.portfolio.domain.PortfolioFormDataDto;
import outpolic.enter.portfolio.service.EnterPortfolioService;
import outpolic.systems.file.domain.FileMetaData;


@Slf4j
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.emptyList());
        }
        String entCd = portfolioService.findEntCdByMbrCd(mbrCd);
        return ResponseEntity.ok(portfolioService.getPortfolioListByEntCd(entCd));
    }

    // --- 포트폴리오 등록 (다단계 방식) ---
    
    /**
     * 포트폴리오 등록 폼을 보여주는 메서드. 세션을 초기화합니다.
     */
    @GetMapping("/add")
    public String showAddPortfolioForm(Model model, HttpSession session) {
        session.removeAttribute("portfolioFormData");
        String mbrCd = (String) session.getAttribute("SCD");
        model.addAttribute("mbrCd", mbrCd);
        return "enter/portfolio/addPortfolio";
    }

    /**
     * 1단계: 기본 정보(제목, 내용)를 세션에 저장합니다.
     */
    @PostMapping("/add/step1")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveStep1(PortfolioFormDataDto formData, HttpSession session) {
        String mbrCd = (String) session.getAttribute("SCD");
        if (mbrCd == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "로그인이 필요합니다."));
        }
        
        String newPrtfCd = "PO_C_" + UUID.randomUUID().toString();
        formData.setPrtfCd(newPrtfCd);
        formData.setMbrCd(mbrCd);
        formData.setEntCd(portfolioService.findEntCdByMbrCd(mbrCd));
        
        session.setAttribute("portfolioFormData", formData);
        return ResponseEntity.ok(Map.of("success", true, "prtfCd", newPrtfCd));
    }

    /**
     * 2단계: 카테고리, 태그 정보를 세션에 저장합니다.
     */
    @PostMapping("/add/step2")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveStep2(@RequestParam(value = "categoryCodes[]", required = false) List<String> categoryCodes,
                                                          @RequestParam(value = "tags", defaultValue = "") String tags,
                                                          @SessionAttribute("portfolioFormData") PortfolioFormDataDto formData) {
        formData.setCategoryCodes(categoryCodes);
        formData.setTags(tags);
        return ResponseEntity.ok(Map.of("success", true));
    }

    /**
     * 3단계: 썸네일 이미지를 업로드하고 파일 정보를 세션에 저장합니다.
     */
    @PostMapping("/add/step3")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveStep3(@RequestParam("portfolioImage") MultipartFile portfolioImage,
                                                          @SessionAttribute("portfolioFormData") PortfolioFormDataDto formData) {
        FileMetaData uploadedFile = portfolioService.uploadThumbnail(portfolioImage);
        if (uploadedFile == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "파일 업로드에 실패했습니다."));
        }
        formData.setThumbnailFile(uploadedFile);
        return ResponseEntity.ok(Map.of("success", true));
    }

    /**
     * 최종 등록: 세션에 저장된 모든 정보로 DB에 저장합니다.
     */
    @PostMapping("/add/complete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> completeRegistration(@SessionAttribute("portfolioFormData") PortfolioFormDataDto formData, HttpSession session) {
        try {
            portfolioService.registerNewPortfolio(formData);
            session.removeAttribute("portfolioFormData"); // 등록 완료 후 세션 데이터 삭제
            return ResponseEntity.ok(Map.of("success", true, "redirectUrl", "/enter/portfolio/list"));
        } catch (IOException e) {
            log.error("포트폴리오 최종 등록 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "message", "등록 중 오류가 발생했습니다."));
        }
    }

    // --- 포트폴리오 수정 관련 ---
    
    @GetMapping("/edit/{prtfCd}")
    public String showEditPortfolioForm(@PathVariable String prtfCd, Model model) {
        EnterPortfolio portfolio = portfolioService.getPortfolioByPrtfCd(prtfCd);
        model.addAttribute("portfolio", portfolio);
        
        if (portfolio.getCtgryId() != null && !portfolio.getCtgryId().isEmpty()) {
            List<CategorySearchDto> categoryPath = categorySearchService.getCategoryPath(portfolio.getCtgryId());
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

        // ... 유효성 검사 로직 ...
        if (bindingResult.hasErrors() || categoryCodes == null || categoryCodes.isEmpty()) {
             // ... 에러 처리 ...
             return ResponseEntity.badRequest().body(new HashMap<>()); // 간단히 처리
        }

        try {
            portfolioService.updatePortfolio(portfolio, categoryCodes, tags, portfolioImage);
            return ResponseEntity.ok(Map.of("success", true, "message", "수정되었습니다.", "redirectUrl", "/enter/portfolio/list")); 
        } catch (Exception e) {
            log.error("포트폴리오 수정 중 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "message", "수정 중 오류 발생"));
        }
    }

    // --- 포트폴리오 삭제 (비동기 처리) ---

    @DeleteMapping("/delete/{prtfCd}")
    @ResponseBody
    public ResponseEntity<?> deletePortfolio(@PathVariable String prtfCd) {
        try {
            portfolioService.deletePortfolio(prtfCd); // 비동기 삭제를 호출하는 동기 메서드
            return ResponseEntity.ok(Map.of("success", true, "message", "삭제 요청이 접수되었습니다. 잠시 후 반영됩니다."));
        } catch (Exception e) {
            log.error("포트폴리오 삭제 요청 실패", e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    // --- 기타 API ---
    
    @GetMapping("/api/tags/search")
    @ResponseBody
    public ResponseEntity<List<String>> searchTags(@RequestParam(value = "query", defaultValue = "") String query) {
        return ResponseEntity.ok(portfolioService.searchTags(query));
    }
}