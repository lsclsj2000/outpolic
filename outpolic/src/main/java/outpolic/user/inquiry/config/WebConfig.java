package outpolic.user.inquiry.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // 이 어노테이션은 이 클래스가 Spring의 설정 클래스임을 나타냅니다.
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 웹에서 '/inquiryAttach/**' URL 패턴으로 들어오는 요청을 처리합니다.
        // 이 요청들은 실제 파일 시스템의 'C:/Users/ksmart/Desktop/inquiryAttachment/' 폴더를 바라보게 됩니다.
        registry.addResourceHandler("/inquiryAttach/**") // <img> 태그의 src에 사용될 URL 프리픽스
                .addResourceLocations("file:///C:/Users/ksmart/Desktop/inquiryAttachment/"); // 실제 파일이 저장된 절대 경로

        // (선택 사항) 기본 정적 리소스 핸들러도 유지하는 것이 좋습니다.
        // registry.addResourceHandler("/static/**")
        //         .addResourceLocations("classpath:/static/");
        // registry.addResourceHandler("/**")
        //         .addResourceLocations("classpath:/static/");
    }
}