package outpolic.user.inquiry.controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; // HttpSession import 추가
import lombok.RequiredArgsConstructor;
import outpolic.systems.file.domain.FileMetaData;
import outpolic.systems.util.FilesUtils;
import outpolic.user.inquiry.domain.UserAnn;
import outpolic.user.inquiry.domain.UserInquiry;
import outpolic.user.inquiry.domain.UserInquiryType;
import outpolic.user.inquiry.mapper.UserInquiryMapper;
import outpolic.user.inquiry.service.UserInquiryService;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserInquiryController {

	@Value("${file.path}")
	private String fileRealPath;

	private final UserInquiryService userInquiryService;
	private final UserInquiryMapper inquiryMapper;
	private final FilesUtils filesUtils;


	@PostMapping("/uploadImage")
	@ResponseBody
	public Map<String, Object> uploadImage(@RequestParam("upload") MultipartFile multipartFile){

		Map<String, Object> response = new HashMap<String, Object>();
		FileMetaData fileInfo = filesUtils.uploadFile(multipartFile, "inquiry");

		if(fileInfo != null) {
			response.put("url", fileInfo.getFilePath());
			response.put("uploaded", "1");
			response.put("fileName", fileInfo.getFileOriginalName());
		}else {
			Map<String, Object> error = new HashMap<String, Object>();
			error.put("message", "파일이미지 업로드 실패");
			response.put("uploaded", "0");
			response.put("error", error);
		}

		return response;
	}

	@PostMapping("/deleteImage")
	@ResponseBody
	public ResponseEntity<?> deleteImage(@RequestParam("imageUrl") String imagePath){

		if(imagePath == null || imagePath.isBlank()) {
			return ResponseEntity.badRequest().body("잘못된 이미지 경로");
		}
		boolean isDelete = filesUtils.deleteFileByPath(imagePath);
		if(isDelete) return ResponseEntity.ok("성공");
		return ResponseEntity.ok("실패");
	}


	@RequestMapping(value="/userInquiry/file/download")
	@ResponseBody
	public ResponseEntity<Object> archiveDownload(@RequestParam(value="saCode", required = false) String saCode
												   ,HttpServletRequest request
												   ,HttpServletResponse response) throws URISyntaxException{


		if(saCode != null) {
			var userInquiryFile = inquiryMapper.getUserInquiryFileInfoByIdx(saCode);

			File file = new File(fileRealPath + userInquiryFile.getSaPath());

			Path path = Paths.get(file.getAbsolutePath());
			Resource resource;
			try {
				resource = new UrlResource(path.toUri());
				String contentType = null;
				contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
				if(contentType == null) {
					contentType = "application/octet-stream";
				}
				return ResponseEntity.ok()
						.contentType(MediaType.parseMediaType(contentType))
						.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + URLEncoder.encode(userInquiryFile.getSaOrgnlName(),"UTF-8") + "\";")
						.body(resource);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		URI redirectUri = new URI("/");
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(redirectUri);

		return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
	}


	// 파일 업로드 (문의 작성)
	@PostMapping("/userInquiryWrite")
	@ResponseBody
	public ResponseEntity<?> fileuploadPro(UserInquiry inquiry,
	                            @RequestPart(value="attachment", required=false) MultipartFile[] attachmentFile,
	                            HttpSession session) { // HttpSession 주입

	    String memberCode = (String) session.getAttribute("SCD"); 

	    if (memberCode == null || memberCode.isBlank()) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
	    }

	    inquiry.setMemberCode(memberCode);
	    System.out.println("문의에 설정될 회원 코드 (mbr_cd): " + inquiry.getMemberCode());
	    
	    userInquiryService.adduserInquiryWrite(inquiry, attachmentFile);
	    
	    return ResponseEntity.ok().body(Map.of("iq_cd", inquiry.getInquiryCode()));
	}

	@GetMapping("/userInquiryWrite")
    public String userInquiryWriteView(Model model, HttpSession session) {
        String reporter = (String) session.getAttribute("SNAME"); 
        if (reporter == null || reporter.isBlank()) {
            reporter = "익명"; 
        }
        model.addAttribute("reporter", reporter);

        // 문의 유형 목록
        List<UserInquiryType> inquiryTypeList = userInquiryService.getAllInquiryTypes();
        model.addAttribute("title", "문의 작성");
        model.addAttribute("inquiryTypeList", inquiryTypeList);
        model.addAttribute("inquiry", new UserInquiry());

        return "user/inquiry/userInquiryWriteView";
    }

	@GetMapping("/userInquiryDetail")
	public String userInquiryDetailView(@RequestParam("iq_cd") String inquiryCode, Model model) {
	    // 문의 상세 페이지 조회
	    UserInquiry inquiry = userInquiryService.getUserInquiryByCode(inquiryCode);

	    if (inquiry == null) {
	        System.err.println("Error: Inquiry with code " + inquiryCode + " not found. Redirecting to list page.");
	        return "redirect:/user/userInquiryList?error=inquiryNotFound";
	    }

	    model.addAttribute("inquiry", inquiry);
	    model.addAttribute("title", "문의 상세");
	    
	    return "user/inquiry/userInquiryDetailView";
	}


	@GetMapping("/userInquiryList")
	public String userInquiryListView(
	        Model model,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(defaultValue = "recent") String sort,
	        HttpSession session) {

	    Pageable pageable = PageRequest.of(page, size);
	    String memberCode = (String) session.getAttribute("SCD");

	    Page<UserInquiry> pageList = userInquiryService.getUserInquiryListPaged(pageable, sort, "mine".equals(sort) ? memberCode : null);

	    model.addAttribute("inquiryList", pageList.getContent());
	    model.addAttribute("pageInfo", pageList);
	    model.addAttribute("title", "문의 내역");
	    model.addAttribute("size", size);
	    model.addAttribute("sort", sort);
	    model.addAttribute("sessionMemberCode", memberCode);

	    return "user/inquiry/userInquiryListView";
	}


	
	@GetMapping("/userNoticeDetail")
	public String userNoticeDetailView(@RequestParam("ann_cd") String annCode, Model model) {
	    // 공지사항 상세 페이지 조회
	    UserAnn noticeDetail = userInquiryService.getUserNoticeByCode(annCode);

	    if (noticeDetail == null) {
	        System.err.println("Error: Inquiry with code " + annCode + " not found. Redirecting to list page.");
	        return "redirect:/user/userInquiryList?error=inquiryNotFound";
	    }

	    model.addAttribute("noticeDetail", noticeDetail);
	    model.addAttribute("title", "문의 상세");
	    
	    return "user/inquiry/userNoticeDetailView";
	}

	@GetMapping("/userInquiryNotice")
	public String userInquiryNoticeView(Model model,
	                                    @RequestParam(defaultValue = "0") int page,
	                                    @RequestParam(defaultValue = "10") int size,
	                                    @RequestParam(defaultValue = "recent") String sort) {
	    // 정렬 조건
	    Sort sorting = switch (sort) {
	        case "old" -> Sort.by("annRegYmdt").ascending();
	        default -> Sort.by("annRegYmdt").descending();
	    };
	    Pageable pageable = PageRequest.of(page, size, sorting);

	    Page<UserAnn> noticeList = userInquiryService.getUserNoticeList(pageable, sort);

	    model.addAttribute("noticeList", noticeList.getContent());
	    model.addAttribute("pageInfo", noticeList);
	    model.addAttribute("size", size);
	    model.addAttribute("sort", sort);
	    model.addAttribute("title", "공지사항");

	    return "user/inquiry/userNoticeListView";
	}

	@GetMapping("/userInquiryTotal")
	public String userInquiryTotalView(Model model,
	                                   @RequestParam(defaultValue = "0") int page,
	                                   @RequestParam(defaultValue = "10") int size,
	                                   @RequestParam(defaultValue = "recent") String sort,
	                                   HttpSession session) {

	    Sort sorting = switch (sort) {
	        case "old" -> Sort.by("reg_date").ascending();
	        default -> Sort.by("reg_date").descending();
	    };
	    Pageable pageable = PageRequest.of(page, size, sorting);

	    Page<UserAnn> totalList = userInquiryService.getUserTotalList(pageable, sort);

	    String memberCode = (String) session.getAttribute("SCD");

	    model.addAttribute("totalList", totalList.getContent());
	    model.addAttribute("pageInfo", totalList);
	    model.addAttribute("size", size);
	    model.addAttribute("sort", sort);
	    model.addAttribute("title", "전체 게시판");
	    model.addAttribute("sessionMemberCode", memberCode);

	    return "user/inquiry/userInquiryTotalView";
	}


	@GetMapping("/userInquiryFaq")
	public String userInquiryFaqView(Model model) {
		// 자주 묻는 질문
		var faqList = userInquiryService.getUserFaqList();
		model.addAttribute("title", "FAQ");
		model.addAttribute("faqList", faqList);

		return "user/inquiry/userInquiryFqaView";
	}

}