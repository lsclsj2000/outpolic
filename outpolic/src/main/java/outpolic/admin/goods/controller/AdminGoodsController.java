package outpolic.admin.goods.controller;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	private final AdminGoodsService adminGoodsService;

	@PostMapping("/insert")
	@ResponseBody
	public ResponseEntity<?> insertGoods(@RequestBody AdminGoodsDTO dto, HttpSession session) {
	    try {
	        String adminCode = (String) session.getAttribute("SACD");
	        if (adminCode == null) {
	        	adminCode = "ADM_TEST"; // 테스트용 기본값
	        }
	        dto.setGdsRegAdmCd(adminCode);
	        dto.setGdsRegYmdt(new Timestamp(System.currentTimeMillis()));

	        adminGoodsService.registerGoods(dto);
	        return ResponseEntity.ok("상품이 정상적으로 등록되었습니다.");
	    } catch (Exception e) {
	        log.error("상품 등록 중 오류 발생", e);
	        return ResponseEntity.badRequest().body("상품 등록 중 오류가 발생했습니다: " + e.getMessage());
	    }
	}

	@PostMapping("/updateGoods")
	@ResponseBody
	public ResponseEntity<String> updateGoods(@RequestBody AdminGoodsDTO dto, HttpSession session) {
	    try {
	        String adminCode = (String) session.getAttribute("SACD");
	        if (adminCode == null) {
	        	adminCode = "ADM_TEST"; // 테스트용 기본값
	        }
	        dto.setGdsMdfcnAdmCd(adminCode);
	        adminGoodsService.updateGoods(dto);
	        return ResponseEntity.ok("상품이 성공적으로 수정되었습니다.");
	    } catch (Exception e) {
	        log.error("상품 수정 오류: {}", e.getMessage());
	        return ResponseEntity.badRequest().body("상품 수정 중 오류 발생: " + e.getMessage());
	    }
	}

	@GetMapping("/goodsList")
	public String goodsListView(Model model, HttpSession session) {
	    List<AdminGoodsDTO> goodsList = adminGoodsService.getGoodsList(new HashMap<>());
	    model.addAttribute("goodsList", goodsList);
	    
	    String adminCode = (String) session.getAttribute("SACD");
	    model.addAttribute("adminCode", adminCode != null ? adminCode : "ADM_TEST");

	    return "admin/goods/adminGoodsSelectView";
	}

	@GetMapping("/goodsListApi")
	@ResponseBody
	public List<AdminGoodsDTO> getGoodsListApi(
			@RequestParam(required = false) String searchType,
	        @RequestParam(required = false) String searchKeyword,
	        @RequestParam(required = false) String gdsTargetMemberType,
	        @RequestParam(required = false) String gdsStatus,
	        @RequestParam(required = false) String gdsType,
	        @RequestParam(required = false) String startDate,
	        @RequestParam(required = false) String endDate) {
		
		log.info("===== 상품 검색 필터 API 호출 =====");
		log.info("searchType: {}", searchType);
		log.info("searchKeyword: {}", searchKeyword);
		log.info("gdsTargetMemberType: {}", gdsTargetMemberType);
		log.info("gdsStatus: {}", gdsStatus);
		log.info("gdsType: {}", gdsType);
		log.info("===============================");
		
		Map<String, Object> params = new HashMap<>();
	    params.put("searchType", searchType);
	    params.put("searchKeyword", searchKeyword);
	    params.put("gdsTargetMemberType", gdsTargetMemberType);
	    params.put("gdsStatus", gdsStatus);
	    params.put("gdsType", gdsType);
	    params.put("startDate", startDate);
	    params.put("endDate", endDate);
	    
	    return adminGoodsService.getGoodsList(params);
	}

	/**
	 * [추가] 상품 상세 정보 조회 API (JSON 반환)
	 */
	@GetMapping("/getGoodsDetail")
	@ResponseBody
	public ResponseEntity<?> getGoodsDetail(@RequestParam("code") String gdsCd) {
	    AdminGoodsDTO goodsDto = adminGoodsService.getGoodsInfo(gdsCd);

	    if (goodsDto != null) {
	        return new ResponseEntity<>(goodsDto, HttpStatus.OK);
	    } else {
	        // 상품을 찾지 못한 경우 404 응답
	        return new ResponseEntity<>("상품 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
	    }
	}

	@GetMapping("/goodsAdd")
	public String addGoodsView() {
		return "admin/goods/adminGoodsAddView";
	}
}