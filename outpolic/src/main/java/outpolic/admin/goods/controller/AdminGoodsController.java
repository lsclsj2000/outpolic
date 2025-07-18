package outpolic.admin.goods.controller;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import outpolic.admin.declaration.controller.AdminDeclarationController;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.admin.goods.dto.AdminGoodsDTO;
import outpolic.admin.goods.mapper.AdminGoodsMapper;
import outpolic.admin.goods.service.AdminGoodsService;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminGoodsController {

    private final AdminDeclarationController adminDeclarationController;
	
	private final AdminGoodsService adminGoodsService;

	@PostMapping("/insert")
	public String insertGoods(@RequestBody AdminGoodsDTO dto, HttpSession session, RedirectAttributes redirectAttr) {
	    try {
	        // 세션에서 관리자 코드(ADM_CD)를 가져옵니다.
	        String adminCode = (String) session.getAttribute("SACD");
	        dto.setGdsRegAdmCd(adminCode);
	        
	        // 관리자 코드가 null인지 확인합니다.
	        if (adminCode != null) {
	            dto.setGdsRegAdmCd(adminCode); // DTO에 관리자 코드 설정
	            log.info("adminCode {}", adminCode);
	        } else {
	            // 세션에 관리자 코드가 없는 경우 (로그인되지 않았거나 세션 만료 등)
	            // 오류 메시지를 추가하고 상품 목록 페이지로 리다이렉트합니다.
	        	log.info("adminCode {}", adminCode);
	        	
	            redirectAttr.addFlashAttribute("error", "관리자 정보가 없어 상품을 등록할 수 없습니다. 다시 로그인해주세요.");
	            System.err.println("상품 등록 실패: 세션에 ADM_CD가 없습니다.");
	            return "redirect:/admin/goodsList"; // 상품 목록 페이지로 리다이렉트
	        }

	        // 상품 등록일시를 현재 시간으로 설정합니다.
	        dto.setGdsRegYmdt(new Timestamp(System.currentTimeMillis())); 
	        
	        // 상품 등록 서비스를 호출합니다.
	        adminGoodsService.registerGoods(dto);
	        
	        // 성공 메시지를 플래시 속성으로 추가합니다.
	        redirectAttr.addFlashAttribute("message", "상품이 정상적으로 등록되었습니다!");
	    } catch (Exception e) {
	        // 상품 등록 중 예외 발생 시 오류 메시지를 플래시 속성으로 추가합니다.
	        redirectAttr.addFlashAttribute("error", "상품 등록 중 오류가 발생했습니다: " + e.getMessage());
	        System.err.println("상품 등록 중 오류 발생: " + e.getMessage()); // 서버 로그에 자세한 오류 출력
	        e.printStackTrace(); // 스택 트레이스 출력
	    }
	    // 상품 등록/오류 처리 후 상품 목록 페이지로 리다이렉트합니다.
	    return "redirect:/admin/goodsList";
	}

	
	@GetMapping("/goodsList")
	public String goodsListView(Model model) {
	    List<AdminGoodsDTO> goodsList = adminGoodsService.getGoodsList();
	    model.addAttribute("goodsList", goodsList);
	    return "admin/goods/adminGoodsSelectView";
	}

	
	@GetMapping("/goodsAdd")
	public String addGoodsView() {
		
		return "admin/goods/adminGoodsAddView";
	}
}
