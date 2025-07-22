package outpolic.systems.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AdminInterceptor implements HandlerInterceptor {
	
	 @Override
	    public boolean preHandle(HttpServletRequest request,
	                             HttpServletResponse response,
	                             Object handler) throws Exception {

	        HttpSession session = request.getSession(false); // 세션 가져오기

	        // 등급이 "ADMIN"인 경우만 허용
	        if (session != null && "ADMIN".equals(session.getAttribute("SGrd"))) {
	            return true; // 관리자 통과
	        }

	        // 세션이 없거나 관리자 등급이 아니면 로그인 페이지로 리다이렉트
	        response.sendRedirect("/admin/login");
	        return false;
	    }
}
