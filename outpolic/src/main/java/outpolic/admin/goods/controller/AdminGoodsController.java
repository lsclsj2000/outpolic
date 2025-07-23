package outpolic.admin.goods.controller;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Enumeration;
import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.admin.goods.dto.AdminGoodsDTO;
import outpolic.admin.goods.service.AdminGoodsService;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminGoodsController {

    // AdminDeclarationController는 현재 사용되지 않으므로 제거해도 무방합니다.
	private final AdminGoodsService adminGoodsService;

	/**
	 * 상품 등록 API (JSON 요청/응답)
	 */
	@PostMapping("/insert")
	@ResponseBody // JSON 응답을 위해 @ResponseBody 추가
	public ResponseEntity<?> insertGoods(@RequestBody AdminGoodsDTO dto, HttpSession session) {
	    try {
	        String adminCode = (String) session.getAttribute("SACD");
	        if (adminCode == null) {
	            // 실제 운영 시에는 아래 주석을 풀어 로그인되지 않았다는 에러를 반환하세요.
	            // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("세션이 만료되었거나 로그인되지 않았습니다.");
	        	adminCode = "ADM_TEST"; // 테스트용 기본값
	        }
	        // 클라이언트에서 보낸 값 대신 세션의 관리자 코드를 사용 (보안 강화)
	        dto.setGdsRegAdmCd(adminCode);
	        dto.setGdsRegYmdt(new Timestamp(System.currentTimeMillis()));

	        adminGoodsService.registerGoods(dto);
	        return ResponseEntity.ok("상품이 정상적으로 등록되었습니다."); // 성공 메시지 반환
	    } catch (Exception e) {
	        log.error("상품 등록 중 오류 발생", e);
	        return ResponseEntity.badRequest().body("상품 등록 중 오류가 발생했습니다: " + e.getMessage());
	    }
	}

	/**
	 * 상품 수정 API (JSON 요청/응답)
	 */
	@PostMapping("/updateGoods")
	@ResponseBody
	public ResponseEntity<String> updateGoods(@RequestBody AdminGoodsDTO dto, HttpSession session) { // HttpSession 추가
	    try {
	        String adminCode = (String) session.getAttribute("SACD");
	        if (adminCode == null) {
	             // 실제 운영 시에는 아래 주석을 풀어 로그인되지 않았다는 에러를 반환하세요.
	            // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("세션이 만료되었거나 로그인되지 않았습니다.");
	        	adminCode = "ADM_TEST"; // 테스트용 기본값
	        }
	        // DTO에 세션에서 가져온 수정자 코드를 설정
	        dto.setGdsMdfcnAdmCd(adminCode);
	        
	        adminGoodsService.updateGoods(dto);
	        return ResponseEntity.ok("상품이 성공적으로 수정되었습니다.");
	    } catch (Exception e) {
	        log.error("상품 수정 오류: {}", e.getMessage());
	        return ResponseEntity.badRequest().body("상품 수정 중 오류 발생: " + e.getMessage());
	    }
	}

	/**
	 * 상품 목록 조회 페이지
	 */
	@GetMapping("/goodsList")
	public String goodsListView(Model model, HttpSession session) { // Model, HttpSession 추가
		
		// ================= [ 디버깅 코드 시작 ] =================
	    log.info("===== 현재 세션의 모든 속성 목록 =====");
	    Enumeration<String> attributeNames = session.getAttributeNames();
	    while (attributeNames.hasMoreElements()) {
	        String attributeName = attributeNames.nextElement();
	        log.info("세션 Key: {}, Value: {}", attributeName, session.getAttribute(attributeName));
	    }
	    log.info("========================================");
	    // ================= [ 디버깅 코드 종료 ] =================
		
	    List<AdminGoodsDTO> goodsList = adminGoodsService.getGoodsList(new HashMap<>());
	    model.addAttribute("goodsList", goodsList);
	    
	    // 세션에서 관리자 코드를 가져와 뷰(HTML)로 전달
	    String adminCode = (String) session.getAttribute("SACD");
	    model.addAttribute("adminCode", adminCode != null ? adminCode : "ADM_TEST");
	    
	    log.info("Thymeleaf로 전달되는 adminCode 값: {}", model.getAttribute("adminCode"));

	    return "admin/goods/adminGoodsSelectView";
	}

	/**
	 * 상품 목록 조회 API (JSON 반환)
	 */
	@GetMapping("/goodsListApi")
	@ResponseBody
	public List<AdminGoodsDTO> getGoodsListApi(
			@RequestParam(required = false) String searchType,
	        @RequestParam(required = false) String searchKeyword,
	        @RequestParam(required = false) String startDate,
	        @RequestParam(required = false) String endDate,
	        @RequestParam(required = false) String gdsType,
	        @RequestParam(required = false) String gdsStatus) {
		
		Map<String, Object> params = new HashMap<>();
	    params.put("searchType", searchType);
	    params.put("searchKeyword", searchKeyword);
	    params.put("startDate", startDate);
	    params.put("endDate", endDate);
	    params.put("gdsType", gdsType);
	    params.put("gdsStatus", gdsStatus);
	    
	    return adminGoodsService.getGoodsList(params);
	}

	/**
	 * 상품 상세 정보 조회 API (JSON 반환)
	 */
	@GetMapping("/getGoodsDetail")
	@ResponseBody
	public ResponseEntity<?> getGoodsDetail(@RequestParam("code") String gdsCd) {
	    AdminGoodsDTO goodsDto = adminGoodsService.getGoodsInfo(gdsCd);

	    if (goodsDto != null) {
	        return new ResponseEntity<>(goodsDto, HttpStatus.OK);
	    } else {
	        return new ResponseEntity<>("상품 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
	    }
	}

	@GetMapping("/goodsAdd")
	public String addGoodsView() {
		return "admin/goods/adminGoodsAddView";
	}
}