package outpolic.enter.inquiry.controller;

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
import outpolic.enter.inquiry.domain.EnterAnn;
import outpolic.enter.inquiry.domain.EnterInquiry;
import outpolic.enter.inquiry.domain.EnterInquiryType;
import outpolic.enter.inquiry.mapper.EnterInquiryMapper;
import outpolic.enter.inquiry.service.EnterInquiryService;
import outpolic.systems.file.domain.FileMetaData;
import outpolic.systems.util.FilesUtils;

@Controller
@RequestMapping("/enter")
@RequiredArgsConstructor
public class EnterInquiryController {

	@Value("${file.path}")
	private String fileRealPath;

	private final EnterInquiryService enterInquiryService;
	private final EnterInquiryMapper inquiryMapper;
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


	@RequestMapping(value="/enterInquiry/file/download")
	@ResponseBody
	public ResponseEntity<Object> archiveDownload(@RequestParam(value="saCode", required = false) String saCode
												   ,HttpServletRequest request
												   ,HttpServletResponse response) throws URISyntaxException{


		if(saCode != null) {
			var enterInquiryFile = inquiryMapper.getEnterInquiryFileInfoByIdx(saCode);

			File file = new File(fileRealPath + enterInquiryFile.getSaPath());

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
						.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + URLEncoder.encode(enterInquiryFile.getSaOrgnlName(),"UTF-8") + "\";")
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
	@PostMapping("/enterInquiryWrite")
	@ResponseBody
	public ResponseEntity<?> fileuploadPro(EnterInquiry inquiry,
	                            @RequestPart(value="attachment", required=false) MultipartFile[] attachmentFile,
	                            HttpSession session) { // HttpSession 주입

	    String memberCode = (String) session.getAttribute("SCD"); 

	    if (memberCode == null || memberCode.isBlank()) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
	    }

	    inquiry.setMemberCode(memberCode);
	    System.out.println("문의에 설정될 회원 코드 (mbr_cd): " + inquiry.getMemberCode());
	    
	    enterInquiryService.addenterInquiryWrite(inquiry, attachmentFile); // 이 부분에서 DB 저장
	    
	    // 이 시점에 inquiry.getInquiryCode()가 유효한지 확인해야 함
	    return ResponseEntity.ok().body(Map.of("iq_cd", inquiry.getInquiryCode()));
	}

	@GetMapping("/enterInquiryWrite")
    public String enterInquiryWriteView(Model model, HttpSession session) { // HttpSession 주입
        // 작성자 이름만 뷰로 전달 (선택 사항: 보이지 않게 할 경우 불필요)
        String reporter = (String) session.getAttribute("SNAME"); 
        if (reporter == null || reporter.isBlank()) {
            reporter = "익명"; 
        }
        model.addAttribute("reporter", reporter); // 뷰에 작성자 이름을 표시하고 싶을 경우 유지

        // 문의 유형 목록
        List<EnterInquiryType> inquiryTypeList = enterInquiryService.getAllInquiryTypes();
        model.addAttribute("title", "문의 작성");
        model.addAttribute("inquiryTypeList", inquiryTypeList);
        model.addAttribute("inquiry", new EnterInquiry());

        return "enter/inquiry/enterInquiryWriteView";
    }

//	// 문의 삭제
//	@PostMapping("/enterInquiryDelete")
//	@ResponseBody
//	public boolean enterInquiryDelete(Map<String, String> requestMap) {
//
//
//
//		return ;
//	}

	@GetMapping("/enterInquiryDetail")
	public String enterInquiryDetailView(@RequestParam("iq_cd") String inquiryCode, Model model) {
	    // 문의 상세 페이지 조회
	    EnterInquiry inquiry = enterInquiryService.getEnterInquiryByCode(inquiryCode);

	    if (inquiry == null) {
	        System.err.println("Error: Inquiry with code " + inquiryCode + " not found. Redirecting to list page.");
	        return "redirect:/enter/enterInquiryList?error=inquiryNotFound";
	    }

	    model.addAttribute("inquiry", inquiry);
	    model.addAttribute("title", "문의 상세");
	    
	    return "enter/inquiry/enterInquiryDetailView";
	}


	@GetMapping("/enterInquiryList")
	public String enterInquiryListView(
	        Model model,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(defaultValue = "recent") String sort,
	        HttpSession session) {

	    Pageable pageable = PageRequest.of(page, size);
	    String memberCode = (String) session.getAttribute("SCD");

	    Page<EnterInquiry> pageList = enterInquiryService.getEnterInquiryListPaged(pageable, sort, "mine".equals(sort) ? memberCode : null);

	    model.addAttribute("inquiryList", pageList.getContent());
	    model.addAttribute("pageInfo", pageList);
	    model.addAttribute("title", "문의 내역");
	    model.addAttribute("size", size);
	    model.addAttribute("sort", sort);
	    model.addAttribute("sessionMemberCode", memberCode);

	    return "enter/inquiry/enterInquiryListView";
	}


	
	@GetMapping("/enterNoticeDetail")
	public String enterNoticeDetailView(@RequestParam("ann_cd") String annCode, Model model) {
	    // 공지사항 상세 페이지 조회
	    EnterAnn noticeDetail = enterInquiryService.getEnterNoticeByCode(annCode);

	    if (noticeDetail == null) {
	        System.err.println("Error: Inquiry with code " + annCode + " not found. Redirecting to list page.");
	        return "redirect:/enter/enterInquiryList?error=inquiryNotFound";
	    }

	    model.addAttribute("noticeDetail", noticeDetail);
	    model.addAttribute("title", "문의 상세");
	    
	    return "enter/inquiry/enterNoticeDetailView";
	}

	@GetMapping("/enterInquiryNotice")
	public String enterInquiryNoticeView(Model model,
	                                    @RequestParam(defaultValue = "0") int page,
	                                    @RequestParam(defaultValue = "10") int size,
	                                    @RequestParam(defaultValue = "recent") String sort) {
	    // 🔽 정렬 조건 적용
	    Sort sorting = switch (sort) {
	        case "old" -> Sort.by("annRegYmdt").ascending();
	        default -> Sort.by("annRegYmdt").descending();
	    };
	    Pageable pageable = PageRequest.of(page, size, sorting);

	    // 🔽 페이징된 공지사항 조회
	    Page<EnterAnn> noticeList = enterInquiryService.getEnterNoticeList(pageable, sort);

	    model.addAttribute("noticeList", noticeList.getContent());
	    model.addAttribute("pageInfo", noticeList);
	    model.addAttribute("size", size); // ✅ 필터 유지용
	    model.addAttribute("sort", sort); // ✅ 필터 유지용
	    model.addAttribute("title", "공지사항");

	    return "enter/inquiry/enterNoticeListView";
	}

	@GetMapping("/enterInquiryTotal")
	public String enterInquiryTotalView(Model model,
	                                   @RequestParam(defaultValue = "0") int page,
	                                   @RequestParam(defaultValue = "10") int size,
	                                   @RequestParam(defaultValue = "recent") String sort) {
	    Sort sorting = switch (sort) {
	        case "old" -> Sort.by("reg_date").ascending();
	        default -> Sort.by("reg_date").descending();
	    };
	    Pageable pageable = PageRequest.of(page, size, sorting);

	    Page<EnterAnn> totalList = enterInquiryService.getEnterTotalList(pageable, sort);

	    model.addAttribute("totalList", totalList.getContent());
	    model.addAttribute("pageInfo", totalList);
	    model.addAttribute("size", size); // ✅ 필터 유지용
	    model.addAttribute("sort", sort); // ✅ 필터 유지용
	    model.addAttribute("title", "전체 게시판");

	    return "enter/inquiry/enterInquiryTotalView";
	}

	@GetMapping("/enterInquiryFaq")
	public String enterInquiryFaqView(Model model) {
		// 자주 묻는 질문
		var faqList = enterInquiryService.getEnterFaqList();
		model.addAttribute("title", "FAQ");
		model.addAttribute("faqList", faqList);

		return "enter/inquiry/enterInquiryFqaView";
	}

}