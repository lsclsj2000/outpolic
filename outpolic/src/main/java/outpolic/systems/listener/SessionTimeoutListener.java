package outpolic.systems.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.RequiredArgsConstructor;
import outpolic.admin.empowerment.mapper.AdminEmpowermentMapper;
import outpolic.admin.loginHistory.service.AdminLoginHistoryService;

@Component
@RequiredArgsConstructor
public class SessionTimeoutListener implements HttpSessionListener {
	

    private final AdminLoginHistoryService loginHistoryService;

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        String memberCode = (String) session.getAttribute("SCD");
        if (memberCode != null) {
            loginHistoryService.updateLogoutTimeBySession(memberCode);
        }
    }
}
