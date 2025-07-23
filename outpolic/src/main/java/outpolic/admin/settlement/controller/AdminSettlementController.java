package outpolic.admin.settlement.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import lombok.RequiredArgsConstructor;
import outpolic.admin.settlement.dto.AdminSettlementDTO;
import outpolic.admin.settlement.service.AdminSettlementService;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminSettlementController {

    private final AdminSettlementService adminSettlementService;

    @GetMapping("/settlementList")
    public String settlementListView(Model model) {
        List<AdminSettlementDTO> settlementList = adminSettlementService.getSettlementHistory(new HashMap<>());
        model.addAttribute("settlementList", settlementList);
        return "admin/settlement/adminSettlementListView";
    }

    @GetMapping("/api/settlementList")
    @ResponseBody
    public List<AdminSettlementDTO> getSettlementListApi(@RequestParam Map<String, Object> params) {
        return adminSettlementService.getSettlementHistory(params);
    }
}