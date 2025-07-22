package outpolic.enter.outsourcing.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException; // DateTimeParseException 임포트 추가
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
import org.springframework.web.bind.annotation.RequestBody; // @RequestBody 임포트 추가
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

import org.slf4j.Logger; // Logger 임포트 추가
import org.slf4j.LoggerFactory; // LoggerFactory 임포트 추가


@Slf4j
@Controller
@RequestMapping("/enter/outsourcing")
@RequiredArgsConstructor
public class EnterOutsourcingController {
	
    private static final Logger log = LoggerFactory.getLogger(EnterOutsourcingController.class); // log 객체 명시적 선언

	
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

    // saveStep1: @ModelAttribute 대신 @RequestBody 사용
    @PostMapping("/save-step1")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveStep1(
            @RequestBody Map<String, Object> payload, // JSON 데이터를 Map으로 받음
            HttpSession session) {

        try {
            String mbrCd = (String) session.getAttribute("SCD");
            if (mbrCd == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "로그인이 필요합니다."));
            }

            // 세션에서 OutsourcingFormDataDto를 가져오거나 새로 생성
            OutsourcingFormDataDto formData = (OutsourcingFormDataDto) session.getAttribute("outsourcingFormData");
            if (formData == null) {
                formData = new OutsourcingFormDataDto();
            }

            // payload에서 데이터 추출 및 formData에 설정
            String osCd = (String) payload.get("osCd");
            String osTtl = (String) payload.get("osTtl");
            String osExpln = (String) payload.get("osExpln");
            // LocalDateTime 파싱 (ISO_LOCAL_DATE_TIME 패턴: "yyyy-MM-dd'T'HH:mm")
            LocalDateTime osStrtYmdt = null;
            if (payload.get("osStrtYmdt") instanceof String && !((String)payload.get("osStrtYmdt")).isEmpty()) {
                try {
                    osStrtYmdt = LocalDateTime.parse((String) payload.get("osStrtYmdt"));
                } catch (DateTimeParseException e) {
                    return ResponseEntity.badRequest().body(Map.of("success", false, "message", "희망 작업 시작일시 형식이 올바르지 않습니다."));
                }
            }
            LocalDateTime osEndYmdt = null;
            if (payload.get("osEndYmdt") instanceof String && !((String)payload.get("osEndYmdt")).isEmpty()) {
                try {
                    osEndYmdt = LocalDateTime.parse((String) payload.get("osEndYmdt"));
                } catch (DateTimeParseException e) {
                    return ResponseEntity.badRequest().body(Map.of("success", false, "message", "희망 작업 종료일시 형식이 올바르지 않습니다."));
                }
            }
            
            BigDecimal osAmt = null;
            if (payload.get("osAmt") != null) {
                try {
                    // 숫자 타입으로 변환 (Integer, Double 등)
                    if (payload.get("osAmt") instanceof Integer) {
                        osAmt = BigDecimal.valueOf((Integer)payload.get("osAmt"));
                    } else if (payload.get("osAmt") instanceof String) {
                        osAmt = new BigDecimal((String)payload.get("osAmt"));
                    } else if (payload.get("osAmt") instanceof Double) { // JSON 기본 파싱이 Double로 할 수 있음
                         osAmt = BigDecimal.valueOf((Double)payload.get("osAmt"));
                    }
                } catch (NumberFormatException | ClassCastException e) {
                    return ResponseEntity.badRequest().body(Map.of("success", false, "message", "희망 금액 형식이 올바르지 않습니다."));
                }
            }

            int osFlfmtCnt = 0;
            if (payload.get("osFlfmtCnt") != null) {
                try {
                    if (payload.get("osFlfmtCnt") instanceof Integer) {
                        osFlfmtCnt = (Integer) payload.get("osFlfmtCnt");
                    } else if (payload.get("osFlfmtCnt") instanceof String) {
                        osFlfmtCnt = Integer.parseInt((String) payload.get("osFlfmtCnt"));
                    } else if (payload.get("osFlfmtCnt") instanceof Double) { // JSON 기본 파싱이 Double로 할 수 있음
                        osFlfmtCnt = ((Double) payload.get("osFlfmtCnt")).intValue();
                    }
                } catch (NumberFormatException | ClassCastException e) {
                    return ResponseEntity.badRequest().body(Map.of("success", false, "message", "수행 가능 인원 형식이 올바르지 않습니다."));
                }
            }


            formData.setOsCd(osCd);
            formData.setOsTtl(osTtl);
            formData.setOsExpln(osExpln);
            formData.setOsStrtYmdt(osStrtYmdt);
            formData.setOsEndYmdt(osEndYmdt);
            formData.setOsAmt(osAmt);
            formData.setOsFlfmtCnt(osFlfmtCnt);

            String entCd = outsourcingService.findEntCdByMbrCd(mbrCd);
            formData.setEntCd(entCd);
            formData.setMbrCd(mbrCd);
            if (formData.getOsCd() == null || formData.getOsCd().isEmpty()) {
                String generatedOsCd = outsourcingService.saveStep1Data(formData, session);
                return ResponseEntity.ok(Map.of("success", true, "osCd", generatedOsCd));
            } else {
                session.setAttribute("outsourcingFormData", formData);
                return ResponseEntity.ok(Map.of("success", true, "osCd", formData.getOsCd()));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "서버 내부 오류: " + e.getMessage()));
        }
    }

    // saveStep2: @RequestParam 대신 @RequestBody 사용
    @PostMapping("/save-step2")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveStep2(
            @RequestBody Map<String, Object> payload, // JSON 데이터를 Map으로 받음
            @SessionAttribute("outsourcingFormData") OutsourcingFormDataDto formData) {
        
        // payload에서 데이터 추출
        @SuppressWarnings("unchecked")
        List<String> categoryCodes = (List<String>) payload.get("categoryCodes");
        String tags = (String) payload.get("tags");
        
        formData.setCategoryCodes(categoryCodes);
        formData.setTags(tags);
        
        return ResponseEntity.ok(Map.of("success", true));
    }

    // saveStep3 (파일 업로드)는 기존 @RequestParam MultipartFile 유지
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

    // [!code diff --start]
    @PostMapping("/save-step4") // <-- 이 메서드가 올바르게 존재하는지 확인
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveStep4(
            @RequestParam(value = "bodyImageFiles", required = false) List<MultipartFile> bodyImageFiles,
            @SessionAttribute("outsourcingFormData") OutsourcingFormDataDto formData) {
        
        // 이 메서드가 호출되는지 확인하는 로그를 추가
        log.info("saveStep4 호출됨. 받은 파일 수: {}", bodyImageFiles != null ? bodyImageFiles.size() : 0);
        
        formData.setNewBodyImageFiles(bodyImageFiles);
        
        return ResponseEntity.ok(Map.of("success", true));
    }
    // [!code diff --end]

    @PostMapping("/complete-registration")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> completeRegistration(
            // JS에서 FormData에 담아 보낸 모든 데이터를 RequestParam으로 받습니다.
            @RequestParam("osTtl") String osTtl,
            @RequestParam("osExpln") String osExpln,
            @RequestParam("osStrtYmdt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime osStrtYmdt,
            @RequestParam("osEndYmdt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime osEndYmdt,
            @RequestParam("osAmt") BigDecimal osAmt,
            @RequestParam("osFlfmtCnt") int osFlfmtCnt,
            @RequestParam(value = "categoryCodes", required = false) List<String> categoryCodes,
            @RequestParam(value = "tags", required = false) String tags,
            @RequestParam(value = "outsourcingThumbnailFile", required = false) MultipartFile thumbnailFile,
            @RequestParam(value = "bodyImageFiles", required = false) List<MultipartFile> bodyImageFiles,
            HttpSession session) {

        try {
            // 받은 파라미터를 DTO에 설정
            OutsourcingFormDataDto formData = new OutsourcingFormDataDto();
            formData.setOsTtl(osTtl);
            formData.setOsExpln(osExpln);
            formData.setOsStrtYmdt(osStrtYmdt);
            formData.setOsEndYmdt(osEndYmdt);
            formData.setOsAmt(osAmt);
            formData.setOsFlfmtCnt(osFlfmtCnt);
            formData.setCategoryCodes(categoryCodes);
            formData.setTags(tags);
            
            // 서비스 메서드 호출 시 파일도 함께 전달
            outsourcingService.completeOutsourcingRegistration(formData, thumbnailFile, bodyImageFiles, session);

            return ResponseEntity.ok(Map.of("success", true, "message", "외주 등록이 완료되었습니다.", "redirectUrl", "/enter/outsourcing/list"));
        } catch (Exception e) {
            log.error("외주 최종 등록 실패", e); // log는 Slf4j 로거를 사용해야 합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "등록 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    // ======================================================
    // ▼▼▼ 외주 "수정" 및 기타 로직 (기존과 동일) ▼▼▼
    // ======================================================
    
    @GetMapping("/edit/{osCd}")
    public String showEditOutsourcingForm(@PathVariable String osCd, Model model) {
        // 1. osCd로 외주 상세 정보 조회
        EnterOutsourcing outsourcing = outsourcingService.findOutsourcingDetailsByOsCd(osCd);
        if (outsourcing == null) {
            return "redirect:/enter/outsourcing/list?error=notfound";
        }
        
        try {
            List<List<CategorySearchDto>> allCategoryPaths = new ArrayList<>();
            // 2. 조회된 외주 정보에서 대표 카테고리 ID를 가져옴
            String representativeCtgryId = outsourcing.getCtgryId();
            // 3. 카테고리 ID가 존재하면, 서비스 로직을 호출해 전체 경로를 조회
            if (representativeCtgryId != null && !representativeCtgryId.isEmpty()) {
                List<CategorySearchDto> fullPath = categorySearchService.getCategoryPath(representativeCtgryId);
                if (!fullPath.isEmpty()) {
                    allCategoryPaths.add(fullPath);
                }
            }

            // 4. 조회된 경로 정보를 JSON 문자열로 변환하여 모델에 추가
            String categoryPathsJson = objectMapper.writeValueAsString(allCategoryPaths);
            model.addAttribute("categoryPathsJson", categoryPathsJson);
        } catch (JsonProcessingException e) {
            // 오류 발생 시 빈 배열 전달
            model.addAttribute("categoryPathsJson", "[]");
        }
        
        model.addAttribute("outsourcing", outsourcing);
        return "enter/outsourcing/editOutsourcing";
    }

    // updateStep1: @RequestParam 대신 @RequestBody 사용
    @PostMapping("/edit/step1")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateStep1(
            @RequestBody Map<String, Object> payload, // JSON 데이터를 Map으로 받음
            HttpSession session) {

        EnterOutsourcing outsourcingToUpdate = new EnterOutsourcing();
        // payload에서 데이터 추출 및 EnterOutsourcing 객체에 설정
        String osCd = (String) payload.get("osCd");
        String osTtl = (String) payload.get("osTtl");
        String osExpln = (String) payload.get("osExpln");
        
        LocalDateTime osStrtYmdt = null;
        if (payload.get("osStrtYmdt") instanceof String && !((String)payload.get("osStrtYmdt")).isEmpty()) {
            try {
                osStrtYmdt = LocalDateTime.parse((String) payload.get("osStrtYmdt"));
            } catch (DateTimeParseException e) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "희망 작업 시작일시 형식이 올바르지 않습니다."));
            }
        }
        LocalDateTime osEndYmdt = null;
        if (payload.get("osEndYmdt") instanceof String && !((String)payload.get("osEndYmdt")).isEmpty()) {
            try {
                osEndYmdt = LocalDateTime.parse((String) payload.get("osEndYmdt"));
            } catch (DateTimeParseException e) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "희망 작업 종료일시 형식이 올바르지 않습니다."));
            }
        }
        
        BigDecimal osAmt = null;
        if (payload.get("osAmt") != null) {
            try {
                if (payload.get("osAmt") instanceof Integer) {
                    osAmt = BigDecimal.valueOf((Integer)payload.get("osAmt"));
                } else if (payload.get("osAmt") instanceof String) {
                    osAmt = new BigDecimal((String)payload.get("osAmt"));
                } else if (payload.get("osAmt") instanceof Double) {
                     osAmt = BigDecimal.valueOf((Double)payload.get("osAmt"));
                }
            } catch (NumberFormatException | ClassCastException e) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "희망 금액 형식이 올바르지 않습니다."));
            }
        }

        int osFlfmtCnt = 0;
        if (payload.get("osFlfmtCnt") != null) {
            try {
                if (payload.get("osFlfmtCnt") instanceof Integer) {
                    osFlfmtCnt = (Integer) payload.get("osFlfmtCnt");
                } else if (payload.get("osFlfmtCnt") instanceof String) {
                    osFlfmtCnt = Integer.parseInt((String) payload.get("osFlfmtCnt"));
                } else if (payload.get("osFlfmtCnt") instanceof Double) {
                    osFlfmtCnt = ((Double) payload.get("osFlfmtCnt")).intValue();
                }
            } catch (NumberFormatException | ClassCastException e) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "수행 가능 인원 형식이 올바르지 않습니다."));
            }
        }
        
        outsourcingToUpdate.setOsCd(osCd);
        outsourcingToUpdate.setOsTtl(osTtl);
        outsourcingToUpdate.setOsExpln(osExpln);
        outsourcingToUpdate.setOsAmt(osAmt);
        outsourcingToUpdate.setOsFlfmtCnt(osFlfmtCnt);
        outsourcingToUpdate.setOsStrtYmdt(osStrtYmdt);
        outsourcingToUpdate.setOsEndYmdt(osEndYmdt);
        
        outsourcingService.updateOutsourcingStep1(outsourcingToUpdate);
        return ResponseEntity.ok(Map.of("success", true, "message", "기본 정보가 수정되었습니다."));
    }

    // updateStep2: @RequestParam 대신 @RequestBody 사용
    @PostMapping("/edit/step2")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateStep2(
            @RequestBody Map<String, Object> payload, // JSON 데이터를 Map으로 받음
            HttpSession session) { // HttpSession을 통해 OutsourcingFormDataDto에 접근 가능
        
        // payload에서 데이터 추출
        String osCd = (String) payload.get("osCd");
        @SuppressWarnings("unchecked")
        List<String> categoryCodes = (List<String>) payload.get("categoryCodes");
        String tags = (String) payload.get("tags");
        outsourcingService.updateOutsourcingStep2(osCd, categoryCodes, tags);
        return ResponseEntity.ok(Map.of("success", true, "message", "카테고리 및 태그가 수정되었습니다."));
    }
    
    // updateStep3 (파일 업로드)는 기존 @RequestParam MultipartFile 유지
    @PostMapping("/edit/step3")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateStep3(
            @RequestParam("osCd") String osCd,
            @RequestParam(value = "outsourcingThumbnailFile", required = false) MultipartFile thumbnailFile,
            @RequestParam(value = "newBodyImageFiles", required = false) List<MultipartFile> newBodyImageFiles, // [!code ++]
            @RequestParam(value = "deletedBodyImageCds", required = false) List<String> deletedBodyImageCds // [!code ++]
            ) {
        outsourcingService.updateOutsourcingStep3(osCd, thumbnailFile, newBodyImageFiles, deletedBodyImageCds); // [!code modified]
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
    public ResponseEntity<Map<String, Object>> linkPortfolio(@RequestBody Map<String, String> payload, HttpSession session) { // 반환 타입을 명시적으로 변경
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
    
    
    
    /**
     * 기업의 전체 포트폴리오를 제목으로 검색하는 API
     * @param query 검색어
     * @param entCd 기업 코드
     * @return 검색된 포트폴리오 목록
     */
    @GetMapping("/api/portfolio/search")
    @ResponseBody
    public ResponseEntity<List<EnterPortfolio>> searchCompanyPortfolios(
            @RequestParam("query") String query,
            @RequestParam("entCd") String
 entCd) {
        
        // 포트폴리오 서비스(EnterPortfolioService)를 사용하여 검색 로직 수행
        List<EnterPortfolio> searchResults = portfolioService.searchByTitleForLinking(query, entCd);
        return ResponseEntity.ok(searchResults);
    }
}