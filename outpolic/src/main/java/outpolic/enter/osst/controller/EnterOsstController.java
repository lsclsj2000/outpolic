package outpolic.enter.osst.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import outpolic.enter.osst.domain.EnterOsst;
import outpolic.enter.osst.domain.EnterStepData;
import outpolic.enter.osst.service.EnterOsstService;


@Controller
@RequiredArgsConstructor
@RequestMapping("/enter")
public class EnterOsstController {
	
	private final EnterOsstService enterOsstService;
	
	@GetMapping("/enterOsstList")
	public String enterOutsourcingStatusList(Model model, HttpSession session) {
		// 세션에서 회원 코드 가져오기
		String memberCode = (String) session.getAttribute("SCD");

		// 로그인된 회원 코드가 없으면 (로그인 안 된 상태) 리다이렉트 또는 에러 처리
		if (memberCode == null || memberCode.isBlank()) {
			
			return "redirect:/login";
		}

		// 진행 외주 목록 조회 (회원 코드를 서비스로 전달)
		List<EnterOsst> osstList = enterOsstService.getEnterOsstList(memberCode);
		model.addAttribute("title", "진행 외주 목록");
		model.addAttribute("osstList", osstList);

		return "enter/osst/enterOutsourcingStatusList";
	}

	@GetMapping("/enterOsstDetail")
	public String enterOutsourcingStatus(@RequestParam("ocd_cd") String osstDetailCode, Model model) {
		
		// 진행 외주 상세 조회
		EnterOsst enterOsstDetail = enterOsstService.getEnterOsstDetail(osstDetailCode);
		List<EnterStepData> groupedStepData = enterOsstService.getGroupedStepData(osstDetailCode);

		model.addAttribute("title", "진행 외주 상세");
		model.addAttribute("EnterOsstDetail", enterOsstDetail);
		model.addAttribute("groupedStepData", groupedStepData);

		return "enter/osst/enterOutsourcingStatusDetail";
	}
}
