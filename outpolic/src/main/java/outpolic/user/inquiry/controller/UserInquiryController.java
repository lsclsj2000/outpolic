package outpolic.user.inquiry.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import outpolic.user.inquiry.domain.UserInquiry;
import outpolic.user.inquiry.domain.UserInquiryAttachment;
import outpolic.user.inquiry.domain.UserInquiryType; // ✅ UserInquiryType DTO 임포트 추가
import outpolic.user.inquiry.service.UserInquiryService;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserInquiryController {
	
	private final UserInquiryService userInquiryService;
	
	@Value("${file.upload-dir}")
	private String uploadDir;
	
	@PostMapping("/userInquiryWrite")
	public String adduserInquiryWrite(UserInquiry inquiry,
									  @RequestParam(value="attachment", required=false) MultipartFile attachmentFile,
									  RedirectAttributes redirect) {
		
		// 1. 문의 기본 정보 설정 및 저장
		String inquiryCode = "INQ" + System.currentTimeMillis(); // 문의 코드 생성
        inquiry.setInquiryCode(inquiryCode);
        inquiry.setMemberCode("MB_C0000041"); // 예시 멤버 코드 (실제 로그인 유저 정보로 변경 필요)

        userInquiryService.adduserInquiryWrite(inquiry); // 문의 먼저 DB에 저장
        
        // 2. 첨부 파일 처리
        if (attachmentFile != null && !attachmentFile.isEmpty()) {
            try {
                // 파일 저장 경로 설정
                File uploadDirectory = new File(uploadDir);
                if (!uploadDirectory.exists()) {
                    uploadDirectory.mkdirs(); // 디렉토리가 없으면 생성
                }

                // 원본 파일 이름
                String originalFileName = attachmentFile.getOriginalFilename();
                // 파일 확장자 추출
                String fileExtension = "";
                if (originalFileName != null && originalFileName.contains(".")) {
                    fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                }
                // 서버에 저장될 파일 이름 (UUID 사용하여 중복 방지)
                String serverFileName = UUID.randomUUID().toString() + fileExtension;
                // 파일 경로
                String filePath = uploadDir + File.separator + serverFileName;

                // 파일 저장
                File dest = new File(filePath);
                attachmentFile.transferTo(dest);

                // UserInquiryAttachment 객체 생성 및 데이터 설정
                UserInquiryAttachment attachment = new UserInquiryAttachment();
                attachment.setSaCode("SA" + System.currentTimeMillis()); // 첨부파일 코드 생성 (예시)
                attachment.setSaReferCode(inquiryCode); // 문의 코드와 연결 (sa_refer_cd에 inquiryCode 저장)
                attachment.setSaOrgnlName(originalFileName);
                attachment.setSaSrvrName(serverFileName);
                attachment.setSaPath(uploadDir); // 파일이 저장된 서버 경로
                attachment.setSaExtn(fileExtension.replace(".", "")); // 확장자에서 '.' 제거
                attachment.setSaSize((int) attachmentFile.getSize()); // 파일 크기 (long을 int로 캐스팅)
                attachment.setMbrCode("MB_C0000041"); // 예시 멤버 코드 (실제 로그인 유저 정보로 변경 필요)

                userInquiryService.adduserInquiryAttachment(attachment); // 첨부파일 DB에 저장

            } catch (IOException e) {
                System.err.println("파일 업로드 실패: " + e.getMessage());
                redirect.addFlashAttribute("error", "파일 업로드에 실패했습니다.");
                return "redirect:/user/userInquiryWrite"; // 실패 시 다시 작성 페이지로
            }
        }

        redirect.addFlashAttribute("msg", "문의가 성공적으로 등록되었습니다.");
        return "redirect:/user/userInquiryList";
		
	}
	
	/*
	 * @PostMapping("/userInquiryWrite") public String
	 * adduserInquiryWrite(UserInquiry inquiry, RedirectAttributes redirect) {
	 * inquiry.setMemberCode("MB_C0000041"); inquiry.setInquiryCode("INQ" +
	 * System.currentTimeMillis());
	 * 
	 * userInquiryService.adduserInquiryWrite(inquiry);
	 * redirect.addFlashAttribute("msg", "문의가 등록되었습니다.");
	 * 
	 * return "redirect:/user/userInquiryList"; }
	 */
	
	@GetMapping("/userInquiryDetail")
	public String userInquiryDetailView(@RequestParam("iq_cd") String inquiryCode, Model model) {
		// 문의 상세내용 조회
		UserInquiry detail = userInquiryService.getUserInquiryByCode(inquiryCode);
	    model.addAttribute("inquiry", detail);
	    return "user/inquiry/userInquiryDetailView";
	}

	
	@GetMapping("/userInquiryList")
	public String userInquiryListView(Model model) {
		// 문의 목록 조회
		var inquiryList = userInquiryService.getUserInquiryList();
		
		model.addAttribute("title", "문의");
		model.addAttribute("inquiryList", inquiryList);
		
		return "user/inquiry/userInquiryListView";
	}
	
	@GetMapping("/userInquiryNotice")
	public String userInquiryNoticeView() {
		// 공지사항 게시판 조회
		return "user/inquiry/userInquiryNoticeView";
	}
	
	@GetMapping("/userInquiryTotal")
	public String userInquiryTotalView() {
		// 전체 게시판 조회
		return "user/inquiry/userInquiryTotalView";
	}
	
	@GetMapping("/userInquiryFaq")
	public String userInquiryFaqView() {
		// 자주 묻는 질문
		return "user/inquiry/userInquiryFqaView";
	}
	
	@GetMapping("/userInquiryWrite")
	public String userInquiryWriteView(Model model) {
		// 문의 유형 목록
		List<UserInquiryType> inquiryTypeList = userInquiryService.getAllInquiryTypes();
		model.addAttribute("inquiryTypeList", inquiryTypeList);
		model.addAttribute("inquiry", new UserInquiry());
		
		return "user/inquiry/userInquiryWriteView";
	}
}