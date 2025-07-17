package outpolic.admin.goods.controller;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import outpolic.admin.goods.dto.AdminGoodsDTO;
import outpolic.admin.goods.mapper.AdminGoodsMapper;
import outpolic.admin.goods.service.AdminGoodsService;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminGoodsController {
	
	private final AdminGoodsService adminGoodsService;

	
	@PostMapping("/insert")
	public String insertGoods(AdminGoodsDTO dto, HttpSession session, RedirectAttributes redirectAttr) {
	    try {
	        dto.setGdsRegAdmCd((String) session.getAttribute("ADM_CD"));
	        dto.setGdsRegYmdt(new Timestamp(System.currentTimeMillis()));
	        adminGoodsService.registerGoods(dto);
	        redirectAttr.addFlashAttribute("message", "상품이 정상적으로 등록되었습니다!");
	    } catch (Exception e) {
	        redirectAttr.addFlashAttribute("error", "상품 등록 중 오류가 발생했습니다.");
	    }
	    return "redirect:/admin/goods/adminGoodsSelectView";
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
