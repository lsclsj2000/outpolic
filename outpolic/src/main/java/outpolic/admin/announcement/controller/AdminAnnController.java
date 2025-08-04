package outpolic.admin.announcement.controller;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.admin.announcement.domain.AdminAnn;
import outpolic.admin.announcement.mapper.AdminAnnMapper;
import outpolic.admin.announcement.service.AdminAnnService;
import outpolic.systems.file.domain.FileMetaData;
import outpolic.systems.util.FilesUtils;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(value="/admin")
public class AdminAnnController {

	@Value("${file.path}")
	private String fileRealPath;

	private final AdminAnnService adminAnnService;
	private final AdminAnnMapper adminAnnMapper;
	private final FilesUtils filesUtils;
	
	// 공지사항 수정 저장
	@PostMapping("/updateAdminAnn")
	@ResponseBody
	public String updateAdminAnn(@RequestBody AdminAnn adminAnn, HttpSession session) {
		String modifier = (String) session.getAttribute("SACD"); // 관리자 코드
		adminAnn.setAnnMdfcnAdmCode(modifier);
		
		int result = adminAnnService.updateAdminAnn(adminAnn);
		return result > 0 ? "success" : "fail";
	}
	
	// 공지사항 상세 정보 조회 API 추가
	@GetMapping("/getAdminAnnDetail")
	@ResponseBody
	public AdminAnn getAdminAnnDetail(@RequestParam("code") String annCode) {
		log.info("📌 공지사항 단건 조회: {}", annCode);
		
		// annCode를 사용하여 서비스에서 상세 정보를 조회하는 로직
		AdminAnn adminAnn = adminAnnService.getAdminAnnDetail(annCode);	

		// 조회된 공지사항 객체를 반환
		return adminAnn;
	}
	
	@GetMapping("/adminAnn")
	public String adminAnnView(
		@RequestParam(required = false) String searchField,
		@RequestParam(required = false) String searchKeyword,
		@RequestParam(required = false) String status,
		@RequestParam(required = false) String dateField,
		@RequestParam(required = false) String startDate,
		@RequestParam(required = false) String endDate,
		Model model
	) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("searchField", searchField);
		paramMap.put("searchKeyword", searchKeyword);
		paramMap.put("status", status);
		paramMap.put("dateField", dateField);
		paramMap.put("startDate", startDate);
		paramMap.put("endDate", endDate);

		log.info("📌 공지사항 필터 파라미터: {}", paramMap);

		List<AdminAnn> adminAnnList = adminAnnService.getAdminAnnListFiltered(paramMap);
		if (adminAnnList == null) adminAnnList = List.of();

		log.info("📌 공지사항 조회 결과: {}건", adminAnnList.size());

		model.addAttribute("adminAnnList", adminAnnList);
		model.addAttribute("searchField", searchField);
		model.addAttribute("searchKeyword", searchKeyword);
		model.addAttribute("status", status);
		model.addAttribute("dateField", dateField);
		model.addAttribute("startDate", startDate);
		model.addAttribute("endDate", endDate);

		return "admin/announcement/adminAnnView";
	}


	
	@GetMapping("/adminAnnWrite")
	public String adminAnnWriteView(Model model, HttpSession session) {
		
		List<String> permissions = (List<String>) session.getAttribute("SPermissions");
		if (!permissions.contains("SYSTEM_ADMIN") && !permissions.contains("CS_ADMIN")) {
			model.addAttribute("msg", "접근 권한이 없습니다.");
			model.addAttribute("url", "/admin"); // 또는 돌아갈 페이지
			return "admin/login/alert"; // alert.html이라는 공용 alert 페이지
		}
		// 공지사항 작성
		
		return "admin/announcement/adminAnnWriteView";
	}
	
	// 기존 @PostMapping("/addAdminAnnWrite") 메서드를 수정하여 파일 첨부 기능을 추가합니다.
	// HTML에서 호출하는 URL과 일치시키고, RequestPart로 파일을 받도록 변경했습니다.
	@PostMapping("/addAdminAnnWrite")
	@ResponseBody
	public ResponseEntity<?> addAdminAnnWrite(@RequestParam("annTitle") String annTitle,
	 										@RequestParam("annCn") String annCn,
	 										@RequestParam("annStcCode") String annStcCode,
	 										@RequestPart(value="attachment", required=false) MultipartFile[] attachmentFile,
	 										HttpSession session) {
		// 공지사항 등록 저장
		
		String adminCode = (String) session.getAttribute("SACD");

		if (adminCode == null || adminCode.isBlank()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
		}

		AdminAnn adminAnn = new AdminAnn();
		adminAnn.setAnnTitle(annTitle);
		adminAnn.setAnnCn(annCn);
		adminAnn.setAnnStcCode(annStcCode);
		adminAnn.setAnnAdmCode(adminCode);

		// 파일 첨부 로직을 포함하여 공지사항 등록 메서드 호출
		adminAnnService.addAdminAnnWrite(adminAnn, attachmentFile);

		return ResponseEntity.ok().body("{\"message\":\"공지사항이 성공적으로 등록되었습니다.\"}");
	}
	
	@PostMapping("/uploadImage")
	@ResponseBody
	public Map<String, Object> uploadImage(@RequestParam("upload") MultipartFile multipartFile){

		Map<String, Object> response = new HashMap<String, Object>();
		FileMetaData fileInfo = filesUtils.uploadFile(multipartFile, "Ann");

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


	@RequestMapping(value="/adminAnn/file/download")
	@ResponseBody
	public ResponseEntity<Object> archiveDownload(@RequestParam(value="saCode", required = false) String saCode
												,HttpServletRequest request
												,HttpServletResponse response) throws URISyntaxException{


		if(saCode != null) {
			var adminAnnFile = adminAnnMapper.getAdminAnnFileInfoByIdx(saCode);

			File file = new File(fileRealPath + adminAnnFile.getSaPath());

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
						.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + URLEncoder.encode(adminAnnFile.getSaOrgnlName(),"UTF-8") + "\";")
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
}
