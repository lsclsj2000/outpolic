package outpolic.enter.portfolio.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
import lombok.extern.slf4j.Slf4j;
import outpolic.enter.POAddtional.domain.CategorySearchDto;
import outpolic.enter.POAddtional.service.CategorySearchService;
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
	public ResponseEntity<Map<String, Object>> saveStep1(@RequestBody Map<String, Object> payload, HttpSession session) {
		String mbrCd = (String) session.getAttribute("SCD");
		if (mbrCd == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("success", false, "message", "로그인이 필요합니다."));
		}

		PortfolioFormDataDto formData = (PortfolioFormDataDto) session.getAttribute("portfolioFormData");
		if (formData == null) {
			formData = new PortfolioFormDataDto();
		}

		String newPrtfCd = portfolioService.generateNewPrtfCd();
		formData.setPrtfCd(newPrtfCd);
		formData.setMbrCd(mbrCd);
		formData.setEntCd(portfolioService.findEntCdByMbrCd(mbrCd));
		formData.setPrtfTtl((String) payload.get("prtfTtl"));
		formData.setPrtfCn((String) payload.get("prtfCn"));

		session.setAttribute("portfolioFormData", formData);
		return ResponseEntity.ok(Map.of("success", true, "prtfCd", newPrtfCd));
	}

	@PostMapping("/add/step2")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> saveStep2(@RequestBody Map<String, Object> payload, @SessionAttribute("portfolioFormData") PortfolioFormDataDto formData) {
		LocalDate prtfPeriodStart = null;
		if (payload.get("prtfPeriodStart") instanceof String && !((String) payload.get("prtfPeriodStart")).isEmpty()) {
			prtfPeriodStart = LocalDate.parse((String) payload.get("prtfPeriodStart"));
		}
		LocalDate prtfPeriodEnd = null;
		if (payload.get("prtfPeriodEnd") instanceof String && !((String) payload.get("prtfPeriodEnd")).isEmpty()) {
			prtfPeriodEnd = LocalDate.parse((String) payload.get("prtfPeriodEnd"));
		}
		formData.setPrtfPeriodStart(prtfPeriodStart);
		formData.setPrtfPeriodEnd(prtfPeriodEnd);
		formData.setPrtfClient((String) payload.get("prtfClient"));
		formData.setPrtfIndustry((String) payload.get("prtfIndustry"));
		return ResponseEntity.ok(Map.of("success", true));
	}

	@PostMapping("/add/step3")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> saveStep3(@RequestBody Map<String, Object> payload, @SessionAttribute("portfolioFormData") PortfolioFormDataDto formData) {
		@SuppressWarnings("unchecked")
		List<String> categoryCodes = (List<String>) payload.get("categoryCodes");
		String tags = (String) payload.get("tags");

		if (categoryCodes == null || categoryCodes.isEmpty()) {
			return ResponseEntity.badRequest().body(Map.of("success", false, "message", "카테고리는 최소 하나 이상 선택해야 합니다."));
		}
		formData.setCategoryCodes(categoryCodes);
		formData.setTags(tags);
		return ResponseEntity.ok(Map.of("success", true));
	}

	@PostMapping("/add/step4")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> saveStep4(@RequestParam("portfolioImage") MultipartFile portfolioImage, @SessionAttribute("portfolioFormData") PortfolioFormDataDto formData) {
		FileMetaData uploadedFile = portfolioService.uploadThumbnail(portfolioImage);
		if (uploadedFile == null) {
			return ResponseEntity.badRequest().body(Map.of("success", false, "message", "파일 업로드에 실패했습니다."));
		}
		formData.setThumbnailFile(uploadedFile);
		return ResponseEntity.ok(Map.of("success", true));
	}

	@PostMapping("/add/complete")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> completeRegistration(
	        // FormData에 담긴 데이터들을 @RequestParam으로 하나씩 받습니다.
	        @RequestParam("prtfTtl") String prtfTtl,
	        @RequestParam("prtfCn") String prtfCn,
	        @RequestParam(value = "prtfPeriodStart", required = false) String prtfPeriodStart,
	        @RequestParam(value = "prtfPeriodEnd", required = false) String prtfPeriodEnd,
	        @RequestParam(value = "prtfClient", required = false) String prtfClient,
	        @RequestParam(value = "prtfIndustry",
 required = false) String prtfIndustry,
	        @RequestParam(value = "categoryCodes", required = false) List<String> categoryCodes,
	        @RequestParam(value = "tags", required = false) String tags,
	        @RequestParam(value = "thumbnailFile", required = false) MultipartFile thumbnailFile,
	        @RequestParam(value = "bodyImageFiles", required = false) List<MultipartFile> bodyImageFiles,
	        HttpSession session) {

	    try {
	        // 모든 데이터를 서비스 계층으로 전달하기 위해 DTO에 담습니다.
	        PortfolioFormDataDto formData = new PortfolioFormDataDto();
	        formData.setPrtfTtl(prtfTtl);
	        formData.setPrtfCn(prtfCn);
	        if (prtfPeriodStart != null && !prtfPeriodStart.isEmpty()) {
	            formData.setPrtfPeriodStart(LocalDate.parse(prtfPeriodStart));
	        }
	        if (prtfPeriodEnd != null && !prtfPeriodEnd.isEmpty()) {
	            formData.setPrtfPeriodEnd(LocalDate.parse(prtfPeriodEnd));
	        }
	        // [!code diff --start]
	        formData.setPrtfClient(prtfClient);
	        formData.setPrtfIndustry(prtfIndustry);
	        // [!code diff --end]
	        formData.setCategoryCodes(categoryCodes);
	        formData.setTags(tags);
	        // 서비스 메서드 호출
	        portfolioService.registerNewPortfolio(formData, thumbnailFile, bodyImageFiles, session);
	        
	        return ResponseEntity.ok(Map.of("success", true, "redirectUrl", "/enter/portfolio/list"));
	    } catch (Exception e) {
	        log.error("포트폴리오 최종 등록 실패", e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Map.of("success", false, "message", "등록 중 오류가 발생했습니다."));
	    }
	}

	// --- 포트폴리오 수정 (다단계 방식) ---
	@GetMapping("/edit/{prtfCd}")
	public String showEditPortfolioForm(@PathVariable String prtfCd, Model model, HttpSession session) {
		EnterPortfolio portfolio = portfolioService.getPortfolioByPrtfCd(prtfCd);
		if (portfolio == null) {
			return "redirect:/enter/portfolio/list?error=notfound";
		}
		
		// 🚨【수정된 부분】: 대표 카테고리 ID를 이용해 전체 경로를 조회합니다.
		try {
			List<List<CategorySearchDto>> allCategoryPaths = new ArrayList<>();
			String representativeCtgryId = portfolio.getCtgryId();
			// 포트폴리오의 대표 카테고리 ID

			if (representativeCtgryId != null && !representativeCtgryId.isEmpty()) {
				// 대표 카테고리 ID로 전체 경로를 조회합니다.
				List<CategorySearchDto> fullPath = categorySearchService.getCategoryPath(representativeCtgryId);
				if (!fullPath.isEmpty()) {
					// 경로 목록에 추가합니다.
					allCategoryPaths.add(fullPath);
				}
			}

			String categoryPathsJson = objectMapper.writeValueAsString(allCategoryPaths);
			model.addAttribute("categoryPathsJson", categoryPathsJson);
		} catch (JsonProcessingException e) {
			log.error("JSON processing error for category paths in edit form", e);
			model.addAttribute("categoryPathsJson", "[]");
		}
		
		model.addAttribute("portfolio", portfolio);
		
		// 세션에 임시 폼 데이터 저장
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
		session.setAttribute("portfolioEditFormData", formData);

		return "enter/portfolio/editPortfolio";
	}

	@PostMapping("/edit/step1")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateStep1(@RequestBody Map<String, Object> payload, @SessionAttribute("portfolioEditFormData") PortfolioFormDataDto formData) {
		String prtfCd = (String) payload.get("prtfCd");
		String prtfTtl = (String) payload.get("prtfTtl");
		String prtfCn = (String) payload.get("prtfCn");
		formData.setPrtfCd(prtfCd);
		formData.setPrtfTtl(prtfTtl);
		formData.setPrtfCn(prtfCn);
		return ResponseEntity.ok(Map.of("success", true));
	}

	@PostMapping("/edit/step2")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateStep2(@RequestBody Map<String, Object> payload, @SessionAttribute("portfolioEditFormData") PortfolioFormDataDto formData) {
		String prtfCd = (String) payload.get("prtfCd");
		LocalDate prtfPeriodStart = (payload.get("prtfPeriodStart") != null && !((String) payload.get("prtfPeriodStart")).isEmpty())
						? LocalDate.parse((String) payload.get("prtfPeriodStart")) : null;
		LocalDate prtfPeriodEnd = (payload.get("prtfPeriodEnd") != null && !((String) payload.get("prtfPeriodEnd")).isEmpty())
						? LocalDate.parse((String) payload.get("prtfPeriodEnd")) : null;
		String prtfClient = (String) payload.get("prtfClient");
		String prtfIndustry = (String) payload.get("prtfIndustry");
		formData.setPrtfCd(prtfCd);
		formData.setPrtfPeriodStart(prtfPeriodStart);
		formData.setPrtfPeriodEnd(prtfPeriodEnd);
		formData.setPrtfClient(prtfClient);
		formData.setPrtfIndustry(prtfIndustry);
		return ResponseEntity.ok(Map.of("success", true));
	}

	@PostMapping("/edit/step3")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateStep3(@RequestBody Map<String, Object> payload, @SessionAttribute("portfolioEditFormData") PortfolioFormDataDto formData) {
		String prtfCd = (String) payload.get("prtfCd");
		@SuppressWarnings("unchecked")
		List<String> categoryCodes = (List<String>) payload.get("categoryCodes");
		String tags = (String) payload.get("tags");
		formData.setPrtfCd(prtfCd);
		formData.setCategoryCodes(categoryCodes);
		formData.setTags(tags);
		return ResponseEntity.ok(Map.of("success", true));
	}

	@PostMapping("/edit/step4")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateStep4(@RequestParam("prtfCd") String prtfCd, @RequestParam(value = "portfolioImage", required = false) MultipartFile portfolioImage, @SessionAttribute("portfolioEditFormData") PortfolioFormDataDto formData) {
		if (portfolioImage != null && !portfolioImage.isEmpty()) {
			FileMetaData uploadedFile = portfolioService.uploadThumbnail(portfolioImage);
			if (uploadedFile == null) {
				return ResponseEntity.badRequest().body(Map.of("success", false, "message", "파일 업로드에 실패했습니다."));
			}
			formData.setThumbnailFile(uploadedFile);
		}
		return ResponseEntity.ok(Map.of("success", true));
	}

	// [!code diff --start]
	@PostMapping("/edit/step5")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateStep5(
			@RequestParam("prtfCd") String prtfCd,
			@RequestParam(value = "newBodyImageFiles", required = false) List<MultipartFile> newBodyImageFiles,
			@RequestParam(value = "deletedBodyImageCds", required = false) List<String> deletedBodyImageCds,
			@SessionAttribute("portfolioEditFormData") PortfolioFormDataDto formData) {

		formData.setPrtfCd(prtfCd);
		formData.setNewBodyImageFiles(newBodyImageFiles);
		formData.setDeletedBodyImageCds(deletedBodyImageCds);
		return ResponseEntity.ok(Map.of("success", true));
	}
	// [!code diff --end]

	@PostMapping("/edit/complete")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> completeEdit(@SessionAttribute("portfolioEditFormData") PortfolioFormDataDto formData, HttpSession session) {
		try {
			portfolioService.updatePortfolioAllSteps(formData);
			session.removeAttribute("portfolioEditFormData");
			return ResponseEntity.ok(Map.of("success", true, "message", "포트폴리오 수정이 완료되었습니다.", "redirectUrl", "/enter/portfolio/list"));
		} catch (IOException e) {
			log.error("포트폴리오 최종 수정 실패", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("success", false, "message", "수정 중 오류가 발생했습니다."));
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

	// ✨【추가된 부분】: Summernote 이미지 업로드 API
	
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