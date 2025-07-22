package outpolic.systems.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class LoginUserBlockInterceptor implements HandlerInterceptor{
	
	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        HttpSession session = request.getSession(false);
        boolean isProcess = true;

        // 로그인한 사용자가 로그인/회원가입 페이지 등에 접근하지 못하도록
        if (session != null && session.getAttribute("SID") != null) {
            isProcess = false;
            response.sendRedirect("/");
        }

        return isProcess;
    }
	

}

