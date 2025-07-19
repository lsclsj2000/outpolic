package outpolic.user.declaration.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import outpolic.user.declaration.domain.UserDeclaration;
import outpolic.user.declaration.service.UserDeclarationService;

@Controller
@RequestMapping(value="/user")
@RequiredArgsConstructor
public class UserDeclarationController {
	
	private final UserDeclarationService declarationService;
	
	@PostMapping("/declarationWrite")
	@ResponseBody
	public ResponseEntity<?> declarationWriteSubmit(
	        @RequestPart("declaration") UserDeclaration declaration, // 🟢 이 부분이 중요
	        @RequestPart(value = "attachments", required = false) MultipartFile[] attachments,
	        HttpSession session) {

	    String mbrCd = (String) session.getAttribute("SID");
	    if (mbrCd == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
	    }

	    declaration.setMbrCd(mbrCd);

	    // 🟢 서버 콘솔에 출력될 로그:
	    System.out.println("컨트롤러에서 받은 UserDeclaration 객체 상세 정보:");
	    System.out.println("  dtCode: " + declaration.getDtCode());
	    System.out.println("  drCode: " + declaration.getDrCode());
	    System.out.println("  declTargetCd: " + declaration.getDeclTargetCd());
	    System.out.println("  declCn: " + declaration.getDeclCn());
	    System.out.println("  mbrCd: " + declaration.getMbrCd());

	    declarationService.addDeclarationWithAttachments(declaration, attachments);

	    return ResponseEntity.ok(Map.of("dc_cd", declaration.getDeclCode()));
	}
	
	@GetMapping("/searchTarget")
	@ResponseBody
    public List<UserDeclaration> searchTarget(
            @RequestParam String type,
            @RequestParam String keyword) {
		// 신고 대상 유형 검색 범위
        return declarationService.searchTarget(type, keyword);
    }
	
	@GetMapping("/declarationReasons")
	@ResponseBody
	public List<UserDeclaration> getDeclarationReasons(@RequestParam("dtCd") String dtCd) {
		// 신고 사유 드롭다운 조회
	    return declarationService.getDeclarationReasonsByType(dtCd);
	}
	
	@GetMapping("/declarationTypes")
	@ResponseBody
	public List<UserDeclaration> getDeclarationTypes() {
		// 신고 타입 드롭다운 조회
	    return declarationService.getActiveDeclarationTypes();
	}
	
	@GetMapping("/declaration")
	public String declarationPage(HttpSession session, Model model) {
		// 신고 작성 시 세션 불러오기
	    String sid = (String) session.getAttribute("SID");
	    String sname = (String) session.getAttribute("SName");

	    if (sid != null && sname != null) {
	        model.addAttribute("reporter", sname + " (" + sid + ")");
	    } else {
	        model.addAttribute("reporter", "로그인이 필요합니다");
	    }

	    return "user/declarationForm";
	}
	
	@GetMapping("/userDeclarationWrite")
	public String userDeclarationWriteView(HttpSession session, Model model) {
		// 신고 작성 페이지
	    String sid = (String) session.getAttribute("SID");
	    String sname = (String) session.getAttribute("SName");
	    
	    System.out.println("세션 확인: SID = " + sid + ", SName = " + sname);
	    
	    if (sid != null && sname != null) {
	        model.addAttribute("reporter", sname + " (" + sid + ")");
	    } else {
	        model.addAttribute("reporter", "로그인이 필요합니다");
	    }

	    model.addAttribute("title", "신고 작성");
	    return "user/declaration/userDeclarationWriteView";
	}
	
	
	/*
	 * @GetMapping("/userDeclarationNotice") public String
	 * userDeclarationNoticeView(Model model) { // 신고 주의사항 페이지
	 * model.addAttribute("title", "신고 주의사항");
	 * 
	 * return "user/declaration/userDeclarationNoticeView"; }
	 * 
	 * 
	 * @GetMapping("/userDeclaration") public String userDeclarationView(Model
	 * model) { // 나의 신고 내역 목록 페이지 model.addAttribute("title", "신고 내역");
	 * 
	 * return "user/declaration/userDeclarationView"; }
	 */
	
	
	
}
