package outpolic.user.login.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class LoginUserBlockInterceptor implements HandlerInterceptor{
	/*
	 * @Override public boolean preHandle(HttpServletRequest request,
	 * HttpServletResponse response, Object handler) throws Exception { HttpSession
	 * session = request.getSession(false); boolean isProcess = true; // 로그인 세션 쿠키가
	 * 있으면 이동 못함(로그인페이지, 회원가입 페이지 등) if (session != null &&
	 * session.getAttribute("SID") != null) { isProcess = false;
	 * response.sendRedirect("/"); } return isProcess; }
	 */
}

