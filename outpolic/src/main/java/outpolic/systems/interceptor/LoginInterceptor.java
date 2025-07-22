
package outpolic.systems.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class LoginInterceptor implements HandlerInterceptor {
	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        HttpSession session = request.getSession();
        String sessionId = (String) session.getAttribute("SID");

        boolean isProcess = true;

        // 세션에 저장된 아이디가 없으면 로그인 페이지로 이동
        if (sessionId == null) {
            isProcess = false;
            response.sendRedirect("/login");
        } else {
            String requestUri = request.getRequestURI();
            // 필요 시 requestUri에 대한 로직 추가 가능
        }

        return isProcess;
    }

}

