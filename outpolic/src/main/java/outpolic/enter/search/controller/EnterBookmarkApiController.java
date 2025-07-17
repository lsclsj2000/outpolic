package outpolic.enter.search.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import outpolic.common.domain.Member;
import outpolic.enter.search.service.EnterBookmarkService;

@RestController
@RequestMapping("/api/enter/bookmarks") // 404 오류를 해결한 경로로 유지
@RequiredArgsConstructor
public class EnterBookmarkApiController {

    private final EnterBookmarkService bookmarkService;

    // 찜 추가 API
    @PostMapping
    public ResponseEntity<String> addBookmark(@RequestBody Map<String, String> payload, HttpServletRequest request) {
        
        HttpSession session = request.getSession(false);
        final String SESSION_USER_ID_KEY = "SCD"; // ✨✨✨ 바로 이 이름입니다! ✨✨✨

        if (session == null || session.getAttribute(SESSION_USER_ID_KEY) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        // 세션 값을 String으로 직접 받습니다.
        String userId = (String) session.getAttribute(SESSION_USER_ID_KEY);
        
        String clCd = payload.get("clCd");
        
        try {
            bookmarkService.addBookmark(userId, clCd);
            return ResponseEntity.ok("찜 목록에 추가되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 찜한 항목이거나 처리 중 오류가 발생했습니다.");
        }
    }

    // 찜 해제 API
    @DeleteMapping("/{clCd}")
    public ResponseEntity<String> deleteBookmark(@PathVariable String clCd, HttpServletRequest request) {
        
        HttpSession session = request.getSession(false);
        final String SESSION_USER_ID_KEY = "SCD"; // ✨ 여기도 동일한 이름을 사용합니다.

        if (session == null || session.getAttribute(SESSION_USER_ID_KEY) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        String userId = (String) session.getAttribute(SESSION_USER_ID_KEY);
        
        try {
            bookmarkService.deleteBookmark(userId, clCd);
            return ResponseEntity.ok("찜 목록에서 삭제되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("처리 중 오류가 발생했습니다.");
        }
    }
}
