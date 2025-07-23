package outpolic.systems.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class LoginUserBackInterceptor implements HandlerInterceptor {
	
	
	//로그인성공시 로그인요청시에 있던 페이지를 저장하는 인터셉터
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		HttpSession session = request.getSession(false);
		// 로그인 요청 들어온 주소를 저장
		if (session == null || session.getAttribute("SCD") == null) {
			String uri = request.getRequestURI();
			String query = request.getQueryString();
			String fullUrl = uri + (query != null ? "?" + query : "");
			
			//로그인 페이지는 저장x
			if (!uri.contains("/login")) {
	            session = request.getSession(); // 없으면 생성
	            session.setAttribute("prevPage", fullUrl);
	        }

	        response.sendRedirect("/login");
	        return false;

		}

		return true;
	}
}
