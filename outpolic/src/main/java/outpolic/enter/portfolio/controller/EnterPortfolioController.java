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
	public ResponseEntity<Map<String, Object>> saveStep1(@RequestBody Map<String, Object> payload, // @ModelAttribute 대신
																									// @RequestBody 사용
			HttpSession session) {

		String mbrCd = (String) session.getAttribute("SCD");
		if (mbrCd == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("success", false, "message", "로그인이 필요합니다."));
		}

		// 세션에서 PortfolioFormDataDto를 가져오거나 새로 생성
		PortfolioFormDataDto formData = (PortfolioFormDataDto) session.getAttribute("portfolioFormData");
		if (formData == null) {
			formData = new PortfolioFormDataDto();
		}

		// 새 prtfCd 생성 및 기본 정보 설정
		String newPrtfCd = portfolioService.generateNewPrtfCd();
		formData.setPrtfCd(newPrtfCd);
		formData.setMbrCd(mbrCd);
		formData.setEntCd(portfolioService.findEntCdByMbrCd(mbrCd));

		// payload에서 prtfTtl과 prtfCn 추출하여 formData에 설정 (여기서 NULL이 해결됨)
		formData.setPrtfTtl((String) payload.get("prtfTtl"));
		formData.setPrtfCn((String) payload.get("prtfCn"));

		session.setAttribute("portfolioFormData", formData); // 업데이트된 formData를 세션에 저장
		return ResponseEntity.ok(Map.of("success", true, "prtfCd", newPrtfCd));
	}

	@PostMapping("/add/step2")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> saveStep2(@RequestBody Map<String, Object> payload, // @RequestParam 대신
																									// @RequestBody 사용
			@SessionAttribute("portfolioFormData") PortfolioFormDataDto formData) {

		// payload에서 날짜 문자열을 추출하여 LocalDate로 파싱
		LocalDate prtfPeriodStart = null;
		if (payload.get("prtfPeriodStart") instanceof String && !((String) payload.get("prtfPeriodStart")).isEmpty()) {
			prtfPeriodStart = LocalDate.parse((String) payload.get("prtfPeriodStart"));
		}
		LocalDate prtfPeriodEnd = null;
		if (payload.get("prtfPeriodEnd") instanceof String && !((String) payload.get("prtfPeriodEnd")).isEmpty()) {
			prtfPeriodEnd = LocalDate.parse((String) payload.get("prtfPeriodEnd"));
		}

		// formData에 값 설정
		formData.setPrtfPeriodStart(prtfPeriodStart);
		formData.setPrtfPeriodEnd(prtfPeriodEnd);
		formData.setPrtfClient((String) payload.get("prtfClient"));
		formData.setPrtfIndustry((String) payload.get("prtfIndustry"));

		// @SessionAttribute로 받은 객체는 직접 변경하면 세션에 반영됨 (명시적인 setAttribute 불필요)
		return ResponseEntity.ok(Map.of("success", true));
	}

	@PostMapping("/add/step3")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> saveStep3(@RequestBody Map<String, Object> payload, // @RequestParam 대신
																									// @RequestBody 사용
			@SessionAttribute("portfolioFormData") PortfolioFormDataDto formData) {

		// categoryCodes와 tags 추출
		@SuppressWarnings("unchecked")
		List<String> categoryCodes = (List<String>) payload.get("categoryCodes");
		String tags = (String) payload.get("tags");

		// 카테고리 유효성 검사 (선택사항, 필요 시 추가)
		if (categoryCodes == null || categoryCodes.isEmpty()) {
			return ResponseEntity.badRequest().body(Map.of("success", false, "message", "카테고리는 최소 하나 이상 선택해야 합니다."));
		}

		formData.setCategoryCodes(categoryCodes);
		formData.setTags(tags);

		// @SessionAttribute로 받은 객체는 직접 변경하면 세션에 반영됨
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
	public ResponseEntity<Map<String, Object>> completeRegistration(
			@SessionAttribute("portfolioFormData") PortfolioFormDataDto formData, HttpSession session) {
		try {
			portfolioService.registerNewPortfolio(formData);
			session.removeAttribute("portfolioFormData");
			return ResponseEntity.ok(Map.of("success", true, "redirectUrl", "/enter/portfolio/list"));
		} catch (IOException e) {
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
		model.addAttribute("portfolio", portfolio);

		// ============ 추가/수정된 부분: 기존 포트폴리오의 모든 카테고리 경로를 가져와 JSON으로 전달 ============
		List<List<CategorySearchDto>> allCategoryPaths = new ArrayList<>();
		// portfolio.getCategories()는 이미 서비스에서 조회된 카테고리 목록(List<CategorySearchDto>)을
		// 가져옵니다.
		// 이 목록은 최종 카테고리 ID를 포함합니다.
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
			model.addAttribute("categoryPathsJson", categoryPathsJson); // 변수명 변경 (initialCategoryPath ->
																		// categoryPathsJson)
		} catch (JsonProcessingException e) {
			log.error("JSON processing error for category paths in edit form", e);
			model.addAttribute("categoryPathsJson", "[]"); // 오류 발생 시 빈 배열로 설정
		}
		// =========================================================================================

		// 세션에 임시 폼 데이터 저장 (수정 중 사용) - 이 부분은 기존 코드와 동일
		PortfolioFormDataDto formData = new PortfolioFormDataDto();
		// ... (기존 portfolio 객체의 필드를 formData에 복사) ...
		formData.setPrtfCd(portfolio.getPrtfCd());
		formData.setEntCd(portfolio.getEntCd());
		formData.setMbrCd(portfolio.getMbrCd());
		formData.setPrtfTtl(portfolio.getPrtfTtl());
		formData.setPrtfCn(portfolio.getPrtfCn());
		formData.setPrtfPeriodStart(portfolio.getPrtfPeriodStart());
		formData.setPrtfPeriodEnd(portfolio.getPrtfPeriodEnd());
		formData.setPrtfClient(portfolio.getPrtfClient());
		formData.setPrtfIndustry(portfolio.getPrtfIndustry());

		session.setAttribute("portfolioEditFormData", formData); // 세션에 폼 데이터 저장

		return "enter/portfolio/editPortfolio";
	}

	// 1단계 수정: 기본 정보 (제목, 내용)
	@PostMapping("/edit/step1")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateStep1(@RequestBody Map<String, Object> payload,
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
	public ResponseEntity<Map<String, Object>> updateStep2(@RequestBody Map<String, Object> payload,
			@SessionAttribute("portfolioEditFormData") PortfolioFormDataDto formData) {

		String prtfCd = (String) payload.get("prtfCd");
		LocalDate prtfPeriodStart = (payload.get("prtfPeriodStart") != null
				&& !((String) payload.get("prtfPeriodStart")).isEmpty())
						? LocalDate.parse((String) payload.get("prtfPeriodStart"))
						: null;
		LocalDate prtfPeriodEnd = (payload.get("prtfPeriodEnd") != null
				&& !((String) payload.get("prtfPeriodEnd")).isEmpty())
						? LocalDate.parse((String) payload.get("prtfPeriodEnd"))
						: null;
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
	public ResponseEntity<Map<String, Object>> updateStep3(@RequestBody Map<String, Object> payload,
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
	public ResponseEntity<Map<String, Object>> updateStep4(@RequestParam("prtfCd") String prtfCd,
			@RequestParam(value = "portfolioImage", required = false) MultipartFile portfolioImage,
			@SessionAttribute("portfolioEditFormData") PortfolioFormDataDto formData) {

		// 새 파일이 업로드된 경우에만 세션의 썸네일 정보를 업데이트합니다.
		if (portfolioImage != null && !portfolioImage.isEmpty()) {
			FileMetaData uploadedFile = portfolioService.uploadThumbnail(portfolioImage);
			if (uploadedFile == null) {
				return ResponseEntity.badRequest().body(Map.of("success", false, "message", "파일 업로드에 실패했습니다."));
			}
			formData.setThumbnailFile(uploadedFile);
		}
		// else 블록을 완전히 삭제하여, 새 파일이 없으면 기존 세션 정보를 그대로 유지하도록 합니다.

		return ResponseEntity.ok(Map.of("success", true));
	}

	// 최종 수정 완료 (5단계)
	@PostMapping("/edit/complete")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> completeEdit(
			@SessionAttribute("portfolioEditFormData") PortfolioFormDataDto formData, HttpSession session) {
		try {
			// 세션에 저장된 모든 단계의 데이터로 최종 업데이트를 수행하는 서비스 호출
			portfolioService.updatePortfolioAllSteps(formData);

			// 처리가 끝났으므로 세션 데이터 삭제
			session.removeAttribute("portfolioEditFormData");

			return ResponseEntity.ok(
					Map.of("success", true, "message", "포트폴리오 수정이 완료되었습니다.", "redirectUrl", "/enter/portfolio/list"));

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