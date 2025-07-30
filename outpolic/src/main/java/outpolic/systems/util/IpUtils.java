package outpolic.systems.util;

import jakarta.servlet.http.HttpServletRequest;

public class IpUtils {
	 public static String getClientIp(HttpServletRequest request) {
	        String ip = request.getHeader("X-Forwarded-For");

	        if (ip != null && ip.contains(",")) {
	            ip = ip.split(",")[0].trim();  // 여러 IP가 있을 경우 첫 번째 IP 사용
	        }

	        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
	            ip = request.getHeader("Proxy-Client-IP");
	        }
	        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
	            ip = request.getHeader("WL-Proxy-Client-IP");
	        }
	        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
	            ip = request.getRemoteAddr();
	        }

	        return ip;
	    }
}
