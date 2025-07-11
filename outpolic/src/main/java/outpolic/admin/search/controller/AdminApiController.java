package outpolic.admin.search.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import outpolic.admin.search.service.AdminSearchService;

@RestController
@RequestMapping("/admin/api")
public class AdminApiController {
	
	private final AdminSearchService adminSearchService;
	
	// 생성자 주입
	public AdminApiController(AdminSearchService adminSearchService) {
		this.adminSearchService = adminSearchService;
	}
	
	/**
	 * @param contentsId 경로 변수로 전달된 콘텐츠 ID
	 * @return 성공 또는 실패에 대한 응답
	 */
	@DeleteMapping("/contents/{contentsId}")
	public ResponseEntity<?> deleteContent(@PathVariable String contentsId){
		// 실제 서비스에서 try-catch로 예외 처리를 해야 합니다.
		try {
			// 1. 서비스 레이어에 삭제 로직 위임 (아래는 예시)
			// contentService.deleteContentById(contentsId);
			adminSearchService.deleteContent(contentsId);
			
			// 성공 응답 반환
			Map<String, String> response = new HashMap<>();
			response.put("message", "콘텐츠("+ contentsId+")가 성공적으로 삭제되었습니다.");
			return ResponseEntity.ok(response);
			
		}catch (Exception e) {
			// 3. 실패 응답 반환
			// log.error("콘텐츠 삭제 실패: {}", contentId, e);
			Map<String, String> errorResponse = new HashMap<>();
			errorResponse.put("message", "콘텐츠 삭제 중 서버 오류가 발생했습니다.");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}
}
