package outpolic.systems.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({ NoResourceFoundException.class })
    public String handleAllErrors(HttpServletRequest request, Exception ex, Model model) {
        String uri = request.getRequestURI();
        StackTraceElement origin = ex.getStackTrace()[0];

        log.error("[Exception] {}\n[method]:{} ({} : {}) - message={}", 
            origin.getClassName(),
            origin.getMethodName(),
            origin.getFileName(),
            origin.getLineNumber(),
            ex.getMessage()
        );

        // 공통 에러 뷰
        return "error";  // ✅ 공통 에러 페이지 하나로 퉁침
    }
}
















