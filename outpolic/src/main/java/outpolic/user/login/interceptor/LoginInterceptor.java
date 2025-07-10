
package outpolic.user.login.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class LoginInterceptor implements HandlerInterceptor {
	/*
	 * @Override public boolean preHandle(HttpServletRequest request,
	 * HttpServletResponse response, Object handler) throws Exception { HttpSession
	 * session = request.getSession(); String sessionId = (String)
	 * session.getAttribute("SID");
	 * 
	 * boolean isProcess = true; // 세션에 저장된 아이디가 없으면 진입 못하게 막는다. if(sessionId ==
	 * null) { isProcess = false; response.sendRedirect("/login"); }else { String
	 * requestUri = request.getRequestURI(); } return isProcess;
	 * HandlerInterceptor.super.preHandle(request, response, handler); }
	 */

}

