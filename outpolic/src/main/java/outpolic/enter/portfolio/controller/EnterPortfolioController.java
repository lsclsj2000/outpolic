package outpolic.enter.portfolio.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList; // ArrayList 임포트 추가
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
import org.springframework.format.annotation.DateTimeFormat;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    private final ObjectMapper objectMapper; 

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
    
    @GetMapping("/add")
    public String showAddPortfolioForm(Model model, HttpSession session) {
        session.removeAttribute("portfolioFormData");
        String mbrCd = (String) session.getAttribute("SCD");
        model.addAttribute("mbrCd", mbrCd);
        return "enter/portfolio/addPortfolio";
    }

    @PostMapping("/add/step1")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveStep1(@ModelAttribute PortfolioFormDataDto formData, HttpSession session) {
        String mbrCd = (String) session.getAttribute("SCD");
        if (mbrCd == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "로그인이 필요합니다."));
        }
        String newPrtfCd = portfolioService.generateNewPrtfCd();
        formData.setPrtfCd(newPrtfCd);
        formData.setMbrCd(mbrCd);
        formData.setEntCd(portfolioService.findEntCdByMbrCd(mbrCd));
        
        session.setAttribute("portfolioFormData", formData);
        return ResponseEntity.ok(Map.of("success", true, "prtfCd", newPrtfCd));
    }

    @PostMapping("/add/step2")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveStep2(
            @RequestParam(value = "prtfPeriodStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate prtfPeriodStart, 
            @RequestParam(value = "prtfPeriodEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate prtfPeriodEnd,     
            @RequestParam(value = "prtfClient", required = false) String prtfClient,
            @RequestParam(value = "prtfIndustry", required = false) String prtfIndustry,
            @SessionAttribute("portfolioFormData") PortfolioFormDataDto formData) {
        
        formData.setPrtfPeriodStart(prtfPeriodStart);
        formData.setPrtfPeriodEnd(prtfPeriodEnd);
        formData.setPrtfClient(prtfClient);
        formData.setPrtfIndustry(prtfIndustry);

        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/add/step3")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveStep3(
            @RequestParam(value = "categoryCodes[]", required = false) List<String> categoryCodes,
            @RequestParam(value = "tags", defaultValue = "") String tags,
            @SessionAttribute("portfolioFormData") PortfolioFormDataDto formData) {
        
        formData.setCategoryCodes(categoryCodes);
        formData.setTags(tags);
        
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/add/step4")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveStep4(@RequestParam("portfolioImage") MultipartFile portfolioImage,
                                                          @SessionAttribute("portfolioFormData") PortfolioFormDataDto formData) {
        FileMetaData uploadedFile = portfolioService.uploadThumbnail(portfolioImage);
        if (uploadedFile == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "파일 업로드에 실패했습니다."));
        }
        formData.setThumbnailFile(uploadedFile);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/add/complete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> completeRegistration(@SessionAttribute("portfolioFormData") PortfolioFormDataDto formData, HttpSession session) {
        try {
            portfolioService.registerNewPortfolio(formData);
            session.removeAttribute("portfolioFormData");
            return ResponseEntity.ok(Map.of("success", true, "redirectUrl", "/enter/portfolio/list"));
        } catch (IOException e) {
            log.error("포트폴리오 최종 등록 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "message", "등록 중 오류가 발생했습니다."));
        }
    }

    // --- 포트폴리오 수정 (다단계 방식) ---
    @GetMapping("/edit/{prtfCd}")
    public String showEditPortfolioForm(@PathVariable String prtfCd, Model model, HttpSession session) {
        EnterPortfolio portfolio = portfolioService.getPortfolioByPrtfCd(prtfCd);
        if (portfolio == null) {
            return "redirect:/enter/portfolio/list?error=notfound";
        }
        model.addAttribute("portfolio", portfolio);
        
        // ============ 수정된 부분: 기존 포트폴리오의 모든 카테고리 경로를 가져와 JSON으로 전달 ============
        List<List<CategorySearchDto>> allCategoryPaths = new ArrayList<>();
        if (portfolio.getCategories() != null) {
            for (CategorySearchDto category : portfolio.getCategories()) {
                // 각 최종 카테고리 ID에 대해 전체 경로를 조회
                List<CategorySearchDto> fullPath = categorySearchService.getCategoryPath(category.getCtgryId());
                if (!fullPath.isEmpty()) {
                    allCategoryPaths.add(fullPath);
                }
            }
        }
        try {
            // List<List<CategorySearchDto>>를 JSON 문자열로 변환
            String categoryPathsJson = objectMapper.writeValueAsString(allCategoryPaths);
            model.addAttribute("categoryPathsJson", categoryPathsJson); // 변수명 변경 (initialCategoryPath -> categoryPathsJson)
        } catch (JsonProcessingException e) {
            log.error("JSON processing error for category paths in edit form", e);
            model.addAttribute("categoryPathsJson", "[]"); 
        }
        // =========================================================================================

        // 세션에 임시 폼 데이터 저장 (수정 중 사용)
        PortfolioFormDataDto formData = new PortfolioFormDataDto();
        formData.setPrtfCd(portfolio.getPrtfCd());
        formData.setEntCd(portfolio.getEntCd());
        formData.setMbrCd(portfolio.getMbrCd());
        formData.setPrtfTtl(portfolio.getPrtfTtl());
        formData.setPrtfCn(portfolio.getPrtfCn());
        formData.setPrtfPeriodStart(portfolio.getPrtfPeriodStart());
        formData.setPrtfPeriodEnd(portfolio.getPrtfPeriodEnd());
        formData.setPrtfClient(portfolio.getPrtfClient());
        formData.setPrtfIndustry(portfolio.getPrtfIndustry());
        
        // formData.setCategoryCodes(portfolio.getCategories().stream().map(CategorySearchDto::getCtgryId).collect(Collectors.toList()));
        // formData.setTags(String.join(", ", portfolio.getTagNames())); // 태그도 초기화 (뷰에서 직접 처리)
        
        session.setAttribute("portfolioEditFormData", formData); 
        
        return "enter/portfolio/editPortfolio";
    }

    // 1단계 수정: 기본 정보 (제목, 내용)
    @PostMapping("/edit/step1")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateStep1(
            @RequestBody Map<String, Object> payload, 
            @SessionAttribute("portfolioEditFormData") PortfolioFormDataDto formData) {
        
        String prtfCd = (String) payload.get("prtfCd");
        String prtfTtl = (String) payload.get("prtfTtl");
        String prtfCn = (String) payload.get("prtfCn");

        formData.setPrtfCd(prtfCd); 
        formData.setPrtfTtl(prtfTtl);
        formData.setPrtfCn(prtfCn);
        
        return ResponseEntity.ok(Map.of("success", true));
    }

    // 2단계 수정: 참여 기간 및 클라이언트 정보
    @PostMapping("/edit/step2")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateStep2(
            @RequestBody Map<String, Object> payload, 
            @SessionAttribute("portfolioEditFormData") PortfolioFormDataDto formData) {
        
        String prtfCd = (String) payload.get("prtfCd");
        LocalDate prtfPeriodStart = (payload.get("prtfPeriodStart") != null && !((String)payload.get("prtfPeriodStart")).isEmpty()) ? LocalDate.parse((String) payload.get("prtfPeriodStart")) : null; 
        LocalDate prtfPeriodEnd = (payload.get("prtfPeriodEnd") != null && !((String)payload.get("prtfPeriodEnd")).isEmpty()) ? LocalDate.parse((String) payload.get("prtfPeriodEnd")) : null;     
        String prtfClient = (String) payload.get("prtfClient");
        String prtfIndustry = (String) payload.get("prtfIndustry");

        formData.setPrtfCd(prtfCd); 
        formData.setPrtfPeriodStart(prtfPeriodStart);
        formData.setPrtfPeriodEnd(prtfPeriodEnd);
        formData.setPrtfClient(prtfClient);
        formData.setPrtfIndustry(prtfIndustry);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // 3단계 수정: 카테고리 및 태그
    @PostMapping("/edit/step3")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateStep3(
            @RequestBody Map<String, Object> payload, 
            @SessionAttribute("portfolioEditFormData") PortfolioFormDataDto formData) {
        
        String prtfCd = (String) payload.get("prtfCd");
        @SuppressWarnings("unchecked")
        List<String> categoryCodes = (List<String>) payload.get("categoryCodes"); 
        String tags = (String) payload.get("tags");

        formData.setPrtfCd(prtfCd); 
        formData.setCategoryCodes(categoryCodes);
        formData.setTags(tags);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // 4단계 수정: 대표 이미지
    @PostMapping("/edit/step4")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateStep4(
            @RequestParam("prtfCd") String prtfCd, 
            @RequestParam(value = "portfolioImage", required = false) MultipartFile portfolioImage,
            @SessionAttribute("portfolioEditFormData") PortfolioFormDataDto formData) { 
        
        if (portfolioImage != null && !portfolioImage.isEmpty()) {
            FileMetaData uploadedFile = portfolioService.uploadThumbnail(portfolioImage);
            if (uploadedFile == null) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "파일 업로드에 실패했습니다."));
            }
            formData.setThumbnailFile(uploadedFile);
        } else {
            formData.setThumbnailFile(null); 
        }
        return ResponseEntity.ok(Map.of("success", true));
    }


    // 최종 수정 완료 (5단계)
    @PostMapping("/edit/complete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> completeEdit(@SessionAttribute("portfolioEditFormData") PortfolioFormDataDto formData, HttpSession session) {
        try {
            portfolioService.updatePortfolioAllSteps(formData); 
            session.removeAttribute("portfolioEditFormData"); 
            return ResponseEntity.ok(Map.of("success", true, "message", "포트폴리오 수정이 완료되었습니다.", "redirectUrl", "/enter/portfolio/list"));
        } catch (IOException e) {
            log.error("포트폴리오 최종 수정 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "message", "수정 중 오류가 발생했습니다."));
        }
    }
    
    // --- 포트폴리오 삭제 (비동기 처리) ---

    @DeleteMapping("/delete/{prtfCd}")
    @ResponseBody
    public ResponseEntity<?> deletePortfolio(@PathVariable String prtfCd) {
        try {
            portfolioService.deletePortfolio(prtfCd);
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
    
    @GetMapping("/api/countByEntCd")
    @ResponseBody
    public ResponseEntity<Integer> countPortfoliosForEnterprise(HttpSession session) {
        String mbrCd = (String) session.getAttribute("SCD");
        if (mbrCd == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(0);
        }
        
        String entCd = portfolioService.findEntCdByMbrCd(mbrCd);
        int count = portfolioService.countPortfoliosByEntCd(entCd);
        
        return ResponseEntity.ok(count);
    }
}