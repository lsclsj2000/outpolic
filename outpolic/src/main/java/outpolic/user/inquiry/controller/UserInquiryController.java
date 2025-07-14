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
import lombok.RequiredArgsConstructor;
import outpolic.systems.file.domain.FileMetaData;
import outpolic.systems.util.FilesUtils;
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
	
	
	// 파일 업로드
	@PostMapping("/userInquiryWrite")
	@ResponseBody
	public ResponseEntity<?> fileuploadPro( UserInquiry inquiry,
								@RequestPart(value="attachment", required=false) MultipartFile[] attachmentFile) {
		inquiry.setMemberCode("MB_C0000041");
		userInquiryService.adduserInquiryWrite(inquiry, attachmentFile);
		
		
		return ResponseEntity.ok().body(Map.of("iq_cd", inquiry.getInquiryCode()));
	}

	@GetMapping("/userInquiryWrite")
	public String userInquiryWriteView(Model model) {
		// 문의 유형 목록
		List<UserInquiryType> inquiryTypeList = userInquiryService.getAllInquiryTypes();
		model.addAttribute("inquiryTypeList", inquiryTypeList);
		model.addAttribute("inquiry", new UserInquiry());
		
		return "user/inquiry/userInquiryWriteView";
	}
	
//	// 문의 삭제
//	@PostMapping("/userInquiryDelete")
//	@ResponseBody
//	public boolean userInquiryDelete(Map<String, String> requestMap) {
//		
//		
//		
//		return ;
//	}
	
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
	
}