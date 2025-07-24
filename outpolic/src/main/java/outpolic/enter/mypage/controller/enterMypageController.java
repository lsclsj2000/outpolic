package outpolic.enter.mypage.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import outpolic.common.dto.OutsourcingReviewDTO;
import outpolic.enter.inquiry.domain.EnterInquiry;
import outpolic.enter.inquiry.service.EnterInquiryService;
import outpolic.enter.mypage.dto.CorpInfo;
import outpolic.enter.mypage.dto.EnterInfo;
import outpolic.enter.mypage.mapper.EnterpriseMapper;
import outpolic.enter.mypage.service.EnterMypageService;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;
import outpolic.enter.portfolio.domain.EnterPortfolio;
import outpolic.enter.withdrawn.dto.EnterOsInfoDTO;
import outpolic.enter.withdrawn.service.EnterWithdrawnService;
import outpolic.systems.file.domain.FileMetaData;
import outpolic.systems.util.FilesUtils;

@Controller
@RequiredArgsConstructor
public class enterMypageController {
	
	private final EnterMypageService enterMypageService;
	private final PasswordEncoder passwordEncoder;
	private final EnterInquiryService enterInquiryService;
	private final EnterWithdrawnService enterWithdrawnService;
	private final FilesUtils filesUtils;
	private final EnterpriseMapper enterpriseMapper;
	
	// 기업 마이페이지 
    @GetMapping("/enter/mypage")
    public String myPage(HttpSession session, Model model) {
    	String memberCode = (String) session.getAttribute("SCD");
    	String memberId = (String) session.getAttribute("SID");
    	String gradeCode = (String) session.getAttribute("SGrd");
    	if(gradeCode == null ||!"ENTER".equals(gradeCode)) {
    		model.addAttribute("msg", "접근 권한이 없습니다.");
    		model.addAttribute("url", "/");
    		return "enter/mypage/alert";
    	}
    	
    	// 기업정보 담는거
    	CorpInfo corpInfo = enterMypageService.getEnterpriseInfoByCode(memberCode);
    	System.out.println("기업 프로필 이미지 경로: " + corpInfo.getEntImg());
    	model.addAttribute("corpInfo", corpInfo);
    	
    	//회원정보
 	    EnterInfo enterInfo = enterMypageService.getEnterInfoByCode(memberCode);
 	    model.addAttribute("enterInfo", enterInfo);
 	    
 	    //리뷰
 	    List<OutsourcingReviewDTO> reviewList = enterMypageService.getOutsourcingReviewList(memberCode);
	    model.addAttribute("reviewList", reviewList);
 	    
 	    //문의
 	    List<EnterInquiry> inquiryList = enterInquiryService.getEnterInquiryListByCode(memberCode);
 	    model.addAttribute("title", "문의 내역");
		model.addAttribute("inquiryList", inquiryList);
		
		//외주글
		List<EnterOutsourcing> outsourcingList = enterMypageService.EnterOsSelectByCode(memberCode);
		model.addAttribute("outsourcingList",outsourcingList);
		
		//완료 외주 수
		int endedOs = enterMypageService.EnterEndedOsSelectByCode(memberCode);
		model.addAttribute("endedOs", endedOs);
		
		// 작성한 포트폴리오 글 
		List<EnterPortfolio> portfolioList = enterMypageService.EnterPfSelectByCode(memberCode);
		model.addAttribute("portfolioList", portfolioList);
		
		// 받은 외주 요청 수(읽기전만)
		int incomeOs = enterMypageService.EnterIncomingOsByCode(memberCode);
		model.addAttribute("incomeOs", incomeOs);
		
		//진행중 외주 수 조회
		List<EnterOsInfoDTO> ingOs = enterWithdrawnService.EnterOsIngSelectByCode(memberCode);
		model.addAttribute("title", "진행중 외주");
		model.addAttribute("ingOs", ingOs);
		
    	return "enter/mypage/enterMypageView";
    }
    
 // 중복체크
    @PostMapping("/check/enter/{type}")
    public ResponseEntity<Boolean> checkEnterInfo(@PathVariable String type,
    											  HttpSession session,
									              @RequestParam(required = false) String memberNickname,
									              @RequestParam(required = false) String memberEmail,
									              @RequestParam(required = false) String memberTelNo) {
    	String memberCode = (String) session.getAttribute("SCD");
        boolean duplicated = enterMypageService.isEnterInfoDuple(type, memberCode, memberNickname, memberEmail, memberTelNo);
        return ResponseEntity.ok(duplicated);
    }
    
    // 비밀번호 입력 후 기업/개인정보 수정 선택 페이지로 이동
    @PostMapping("/enterEditChoice")
    public String enterEditChoiceView(@RequestParam("password") String memberPw, HttpSession session, Model model) {
    	String memberCode = (String) session.getAttribute("SCD");
    	EnterInfo enterInfo = enterMypageService.getEnterInfoByCode(memberCode);
    	
    	if(memberCode == null) {
    		model.addAttribute("msg", "로그인이 만료되었습니다. 다시 로그인 해주세요");
    		model.addAttribute("url", "/login");
    		return "enter/mypage/alert";
    	}
    	
    	if(passwordEncoder.matches(memberPw, enterInfo.getMemberPw())) {
    		model.addAttribute("enterInfo", enterInfo);
    		return "enter/mypage/enterEditChoiceView";
			/*
			 * return "enter/mypage/enterProfileEditView";
			 */    	}else {
    		model.addAttribute("msg", "비밀번호가 일치하지 않습니다");
  			model.addAttribute("url", "/enter/mypage");
  			return "enter/mypage/alert";
    	}
    }
    
    //분기 갈림길
    // 개인정보 수정 페이지 이동
    @GetMapping("/enter/enterEditView")
    public String enterProfileEditView(Model model, HttpSession session) {
    	String memberCode = (String) session.getAttribute("SCD");
    	if (memberCode == null) {
            return "redirect:/login"; // 또는 오류 페이지
        }
    	 EnterInfo enterInfo = enterMypageService.getEnterInfoByCode(memberCode);
    	 model.addAttribute("enterInfo", enterInfo);

    	return "enter/mypage/enterProfileEditView";
    }
    // 기업 개인정보 불러오기
    @GetMapping("/enter/enterEdit/info")
    @ResponseBody 
    public EnterInfo getEnterInfo(HttpSession session) { 
    	String memberCode = (String) session.getAttribute("SCD"); 
    	return enterMypageService.getEnterInfoByCode(memberCode); 
    }
    
    // 기업 개인정보 업데이트
    @PostMapping("/enter/enterEdit/update")
    @ResponseBody
    public EnterInfo getEnterInfoAjax(HttpSession session) {
    	String memberCode=(String) session.getAttribute("SCD");
    	return enterMypageService.getEnterInfoByCode(memberCode);
    }
    
    // 기업 개인정보 수정
    @PostMapping("/enter/enterEdit")
    public String enterProfileEdit(EnterInfo enterInfo,
    		Model model) {
    	enterMypageService.editEnterInfo(enterInfo);
    	model.addAttribute("title", "개인정보 수정");
    	model.addAttribute("enterInfo", enterInfo);
    	return "redirect:/enter/mypage";
    }
    
    
    // 기업정보 수정 페이지 이동
    @GetMapping("/enterpriseEditView")
    public String enterpriseEditView(Model model, HttpSession session) {
    	String memberCode = (String) session.getAttribute("SCD"); // 세션에서 memberCode 꺼냄
        if (memberCode == null) {
            model.addAttribute("msg", "로그인이 만료되었습니다. 다시 로그인해주세요.");
            model.addAttribute("url", "/login");
            return "enter/mypage/alert";
        }
        CorpInfo corpInfo = enterMypageService.getEnterpriseInfoByCode(memberCode);
    	model.addAttribute("corpInfo", corpInfo);
    	
    	return "enter/mypage/enterEnterpriseInfoEditView";
    }
    
    // 기업 기업정보 불러오기
    @GetMapping("/enterpriseEdit/info")
    @ResponseBody
    public CorpInfo getEnterpriseInfo(HttpSession session) {
        String memberCode = (String) session.getAttribute("SCD");
        return enterMypageService.getEnterpriseInfoByCode(memberCode);
    }
    
	/*
	 * // 기업 기업정보 페이지 수정
	 * 
	 * @PostMapping("/enterpriseEdit") public String saveEnterpriseInfo(Model model,
	 * CorpInfo corpInfo) { enterMypageService.editEnterpriseInfo(corpInfo);
	 * model.addAttribute("title", "개인정보 수정"); model.addAttribute("enterpriseInfo",
	 * corpInfo); return "redirect:/enter/mypage"; }
	 */
    @PostMapping("/enterpriseEdit")
    @ResponseBody
    public Map<String, Object> updateEnterpriseInfo(@RequestBody CorpInfo corpInfo, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        String memberCode = (String) session.getAttribute("SCD");
        if (memberCode == null) {
            result.put("status", "fail");
            result.put("msg", "세션이 만료되었습니다.");
            return result;
        }

        corpInfo.setMemberCode(memberCode);
        enterMypageService.editEnterpriseInfo(corpInfo);

        result.put("status", "success");
        result.put("msg", "기업 정보가 성공적으로 수정되었습니다.");
        return result;
    }
    
    // 기업 기업정보 소개 페이지
    @GetMapping("/enterpriseInfo")
    public String enterpriseInfoView() {
    	return "enter/enterpriseInfo/enterpriseInfo";
    }

    @GetMapping("/reviewListView") // URL 경로를 더 구체적으로 변경하여 충돌 방지
	public String showOutsourcingPage4() {
		return "/enter/review/reviewListView"; // 뷰 이름은 그대로 유지하거나 변경
	}

    @Value("${file.path}")
    private String uploadPath;
    @PostMapping("/enter/profile/upload")
    public String uploadCorpProfileImage(@RequestParam("profileImage") MultipartFile file,
                                         HttpSession session,
                                         RedirectAttributes redirectAttributes,
                                         Model model) {
        String memberCode = (String) session.getAttribute("SCD");
        
        if (memberCode == null) {
            redirectAttributes.addFlashAttribute("msg", "세션이 만료되었습니다. 다시 로그인해주세요.");
            return "redirect:/login";
        }

        try {
            FileMetaData fileMetaData = filesUtils.uploadFile(file, "enterMypageProfile");
            String imagePath = fileMetaData.getFilePath();
            if (!imagePath.startsWith("/")) {
            	imagePath = "/" + imagePath;
            }
            enterMypageService.updateCorpProfileImage(memberCode, imagePath);
            Map<String, Object> param = new HashMap<>();
            param.put("memberCode", memberCode);
            param.put("imagePath", imagePath);
            enterpriseMapper.updateCorpProfileImg(param);
            System.out.println("memberCode = " + memberCode);
            System.out.println("imagePath = " + imagePath);
            
            model.addAttribute("msg", "프로필사진이 성공적으로 수정되었습니다");
            model.addAttribute("url", "/enter/mypage");

            //redirectAttributes.addFlashAttribute("msg", "프로필 사진이 저장되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("msg", "업로드 중 오류가 발생했습니다.");
            model.addAttribute("url", "/enter/mypage");
            //redirectAttributes.addFlashAttribute("msg", "업로드 중 오류가 발생했습니다.");
        }
        
        return "enter/mypage/alert";
    }
    
   
    
  

}
