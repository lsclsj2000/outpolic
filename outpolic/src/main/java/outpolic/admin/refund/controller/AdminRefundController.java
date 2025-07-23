package outpolic.admin.refund.controller;

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
import outpolic.admin.refund.dto.AdminRefundDTO;
import outpolic.admin.refund.service.AdminRefundService;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminRefundController {

    private final AdminRefundService adminRefundService;

    @GetMapping("/refundList")
    public String refundListView(Model model) {
        List<AdminRefundDTO> refundList = adminRefundService.getRefundHistory(new HashMap<>());
        model.addAttribute("refundList", refundList);
        return "admin/refund/adminRefundListView";
    }

    @GetMapping("/api/refundList")
    @ResponseBody
    public List<AdminRefundDTO> getRefundListApi(@RequestParam Map<String, Object> params) {
        return adminRefundService.getRefundHistory(params);
    }
}