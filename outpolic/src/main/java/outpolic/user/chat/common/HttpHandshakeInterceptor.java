package outpolic.user.chat.common;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class HttpHandshakeInterceptor implements HandshakeInterceptor {
	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		String ip = "UNKNOWN";
		String userAgent = "UNKNOWN";
		String browser = "UNKNOWN";
		
		if (request instanceof ServletServerHttpRequest servletRequest) {
			HttpServletRequest req = servletRequest.getServletRequest();

			// 클라이언트 IP 추출
			ip = req.getHeader("X-Forwarded-For");
			if (ip == null || ip.isBlank()) {
				ip = req.getRemoteAddr();
			}

			// ✅ 브라우저 User-Agent 추출
			userAgent = req.getHeader("User-Agent");
			if (userAgent != null) {
                browser = detectBrowser(userAgent);
            }
		}
		 // HttpSession을 attributes에 저장
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpSession session = servletRequest.getServletRequest().getSession();
            attributes.put("HTTP_SESSION", session);
        }

		attributes.put("ip", ip); // 세션에 IP 저장
		attributes.put("userAgent", userAgent); // 세션에 브라우저 정보 저장
		attributes.put("browser", browser); // ✅ 브라우저 이름 저장
		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {
	}
	
	private String detectBrowser(String userAgent) {
        userAgent = userAgent.toLowerCase();

        if (userAgent.contains("edg")) {
            return "Edge";
        } else if (userAgent.contains("chrome")) {
            return "Chrome";
        } else if (userAgent.contains("safari")) {
            return "Safari";
        } else if (userAgent.contains("firefox")) {
            return "Firefox";
        } else {
            return "Unknown";
        }
    }
}
