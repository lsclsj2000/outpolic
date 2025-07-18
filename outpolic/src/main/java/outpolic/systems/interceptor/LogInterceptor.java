package outpolic.systems.interceptor;

import java.util.Set;
import java.util.StringJoiner;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LogInterceptor implements HandlerInterceptor{

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// 사용자 요청 파라미터 ?memberId=id1&memberPw=pw1 -> memberId, memberPw
		// 요청 파라미터 키 추출
		Set<String> paramkey = request.getParameterMap().keySet();
		
		StringJoiner param = new StringJoiner(", ");
		
		// memberId:id1, memberPw:pw1, ......
		for(String key : paramkey) {
			param.add(key + ": " + request.getParameter(key));
		}
		
		log.info("========== Access LOG START ==========================");
		log.info("PORT 			:::: 		{}", request.getLocalPort());
		log.info("SERVERNAME 		:::: 		{}", request.getServerName());
		log.info("HTTP METHOD 		:::: 		{}", request.getMethod());
		log.info("URI 			:::: 		{}		", request.getRequestURI());
		log.info("CLIENT IP 		:::: 		{}	", request.getRemoteAddr());
		if(param.length() > 0) {			
			log.info("PARAMETER 		:::: 		{}", param);
		}
		log.info("========== Access LOG END =============================");
		
		return HandlerInterceptor.super.preHandle(request, response, handler);
	}
}
