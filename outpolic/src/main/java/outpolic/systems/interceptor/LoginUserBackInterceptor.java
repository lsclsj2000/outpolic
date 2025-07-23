package outpolic.systems.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import outpolic.user.login.controller.UserLoginController;

@Component
@Slf4j
public class LoginUserBackInterceptor implements HandlerInterceptor {
	
	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		//로그인성공시 로그인요청시에 있던 페이지를 저장하는 인터셉터
		String uri = request.getRequestURI();
	    String query = request.getQueryString();
	    String fullUrl = uri + (query != null ? "?" + query : "");
	
	    // 로그인, 회원가입, 정적 리소스 제외
	    if (!uri.contains("/login") && !uri.contains("/register") && !uri.contains("/assets")) {
	        HttpSession session = request.getSession(); // 없으면 생성
	        session.setAttribute("prevPage", fullUrl);
	        log.info("📌 prevPage 저장됨: {}", fullUrl);
	    }
	
	    return true; // 무조건 통과 (막지 않음)
	}
}
