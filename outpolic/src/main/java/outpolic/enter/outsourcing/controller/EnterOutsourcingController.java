package outpolic.enter.outsourcing.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import outpolic.enter.POAddtional.service.CategorySearchService;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;
import outpolic.enter.outsourcing.service.EnterOutsourcingService;

@Controller
@RequestMapping("/enter/outsourcing")
@RequiredArgsConstructor
public class EnterOutsourcingController {
    
    private final EnterOutsourcingService outsourcingService;
    private final CategorySearchService categorySearchService;
    
    @GetMapping("/list")
    public String showPortfolioListView() { 
        return "enter/outsourcing/outsourcingListView";
    }
    
    @GetMapping("/listData")
    @ResponseBody
    public ResponseEntity<List<EnterOutsourcing>> getOutsourcingListData(HttpSession session){
        String currentEntCd = "EI_C00001";
        return ResponseEntity.ok(outsourcingService.getOutsourcingListByEntCd(currentEntCd));
    }

    @GetMapping("/add")
    public String showAddOutsourcingForm(Model model) {
        model.addAttribute("entCd","EI_C00001");
        model.addAttribute("mbrCd", "MB_C0000036");
        return "enter/outsourcing/addOutsourcingListView";
    }

    @PostMapping("/add-ajax")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addOutsourcingAjax(
            @ModelAttribute EnterOutsourcing outsourcing,
            @RequestParam(value="categoryCodes", required=false) List<String> categoryCodes,
            @RequestParam(value="tags", required=false) String tags,
            RedirectAttributes redirectAttributes) {
        
        // TODO: 실제 세션에서 수정자(관리자) 정보 설정
        outsourcing.setAdmCd("ADM_C0001"); 
        
        try {
            outsourcingService.addOutsourcing(outsourcing, categoryCodes, tags);
            return ResponseEntity.ok(Map.of("success", true, "message", "외주가 성공적으로 등록되었습니다.", "redirectUrl", "/enter/outsourcing/list"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "등록 중 서버 오류가 발생했습니다."));
        }
    }
}