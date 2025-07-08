package outpolic.enter.outsourcingRequest.controller;

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

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import outpolic.enter.outsourcingRequest.domain.OutsourcingRequest;
import outpolic.enter.outsourcingRequest.service.OutsourcingRequestService;

@Controller
@RequestMapping("/enter/requests")
@RequiredArgsConstructor
public class OutsourcingRequestController {
	
	private final OutsourcingRequestService requestService;
	//private final EnterpriseService enterpriseService;
	
	/**
	 * 외주 직접 신청 페이지(View)를 보여주는 메서드
	 * @Param supplierCd 공급자 목록에서 선택한 기업의 코드
	 * 
	 */
	@GetMapping("/new")
	public String showNewRequestForm(@RequestParam("supplierCd") String supplierCd,Model model) {
		// 실제로는 EnterpriseService를 이용해 공급자 정보를 DB에 조회해야 합니다.
		// Enterprise supplier = enterpriseService.getEnterpriseByCd(supplierCd);
		// model.addAttribute("supplier",supplier);
		
		model.addAttribute("supplierCd",supplierCd);
		model.addAttribute("supplierName","임시 공급자 이름");
		
		// resources/templates/enter/outsourcing/outsourcingRequestListView.html 파일을 보여줍니다.
		return "enter/outsourcing/outsourcingRequestListView";
	}
	
	/**
	 * 작성된 신청 내역을 DB에 저장하는 메서드 (AJAX로 호출됨)
	 * @Param request HTML form의 데이터가 자동으로 담기는 객체
	 */
	@PostMapping("/add")
	@ResponseBody
	public ResponseEntity<?> addNewRequest(@ModelAttribute OutsourcingRequest request,HttpSession session) {
		// TODO: 실제 운영 시에는 세션에서 로그인한 수요자(회원)의 코드를 가져와야 합니다.
		// String memberCd = (String) session.getAttribute("loginMemberCd");
		// request.setMbrCd(memberCd);
		request.setMbrCd("MB_C0000007"); // 현재는 테스트를 위해 임시 코드를 사용
		
		try {
			// 서비스 로직을 호출하여 DB에 저장
			requestService.addOutsourcingRequest(request);
			// 성공 시 프론트앤드에 성공 메세지 전달
			return ResponseEntity.ok(Map.of("successs",true,"message","외주 신청이 정상적으로 접수되었습니다."));
			
		} catch (Exception e) {
			e.printStackTrace();
			// 실패 시 에러 메세지 전달
			return ResponseEntity.status(500).body(Map.of("success",false,"message","신청 처리 중 오류가 발생했습니다."));
		}
	}
}