package outpolic.enter.osst.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import outpolic.enter.osst.domain.EnterOsst;
import outpolic.enter.osst.domain.EnterOsstRecord;
import outpolic.enter.osst.service.EnterOsstService;


@Controller
@RequiredArgsConstructor
@RequestMapping("/enter")
public class EnterOsstController {
	
	private final EnterOsstService enterOsstService;
	
	@GetMapping("/enterOsstList")
	public String enterOutsourcingStatusList(Model model) {
		
		// 진행 외주 목록 조회
		List<EnterOsst> osstList = enterOsstService.getEnterOsstList();
		model.addAttribute("title", "진행 외주 목록");
		model.addAttribute("osstList", osstList);
		
		return "enter/osst/enterOutsourcingStatusList";
	}

	@GetMapping("/enterOsstDetail")
	public String enterOutsourcingStatus(@RequestParam("ocd_cd") String osstDetailCode, Model model) {
		// 진행 외주 상세 조회
		EnterOsst EnterOsstDetail = enterOsstService.getEnterOsstDetail(osstDetailCode);
		List<EnterOsstRecord> osstRecord = enterOsstService.getEnterOsstRecord();
		List<EnterOsst> osstStcCd = enterOsstService.getEnterOsstStcCode();
		
		model.addAttribute("title", "진행 외주 상세");
		model.addAttribute("EnterOsstDetail", EnterOsstDetail);
		model.addAttribute("osstRecord", osstRecord);
		model.addAttribute("osstStcCd", osstStcCd);
		
		return "enter/osst/enterOutsourcingStatusDetail";
	}
}
