package outpolic.systems.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import lombok.RequiredArgsConstructor;
import outpolic.systems.interceptor.AdminInterceptor;
import outpolic.systems.interceptor.LogInterceptor;
import outpolic.systems.interceptor.LoginInterceptor;
import outpolic.systems.interceptor.LoginUserBlockInterceptor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer{
	
	@Value("${file.path}")
	private String fileRealPath;
	
	private final LoginInterceptor loginInterceptor;
	
	private final LoginUserBlockInterceptor loginUserBlockInterceptor;
	
	private final LogInterceptor logInterceptor;
	
	private final AdminInterceptor adminInterceptor;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		List<String> logExcludePath = List.of(
												  "/admin/assets/**"
												, "/common/assets/**"
												, "/enter/assets/**" 
												, "/team/css/**" 
												, "/team/img/**" 
												, "/user/assets/**"
											  );
		
		// 로그 기록 인터셉터
		registry.addInterceptor(logInterceptor)
				.addPathPatterns("/**")
				.excludePathPatterns(logExcludePath);
		
		 // 로그인하지 않은 사용자의 접근 제한
	    registry.addInterceptor(loginInterceptor)
	            .addPathPatterns("/**") // 전체 경로에 적용
	            .excludePathPatterns(
	                "/",                 // 메인화면
	                "/main",            // 메인페이지(혹시 따로 있을 경우)
	                "/login",           // 로그인 화면
	                "/user/registerInfo",
	                "/admin/**",
	                "/admin/login",
	                "/userGoodsList",   // 상품리스트
	                "/user/userInquiryList",
	                "/user/userInqueryList/**",
	                "/user/userInqueryList**",
	                "/user/userSearch**",
	                "/user/search/api**",
	                "/user/contents/**",
	                "/user/api/contents/**",
	                "/user/company/**",
	                "/api/user/company/**",
	                "/user/userInquiryTotal**",
	                "/user/userInquiryNotice**",
	                "/user/userInquiryNotice/**",
	                "/user/userInquiryDetail**",
	                "/user/userInquiryDetail**",
	                "/user/products**",
	                "/admin/login",
	                "/favicon*",
	                "/user/assets/**",
	                "/enter/assets/**",
	                "/admin/assets/**" // 정적 리소스
	            );

	    // 로그인된 사용자가 다시 로그인/회원가입 페이지 접근 못하도록 제한
	    registry.addInterceptor(loginUserBlockInterceptor)
	            .addPathPatterns(
	                "/login",                         // 로그인 경로
	                "/choiceRegister",                // 회원가입 선택
	                "/user/registerInfo"              // 회원가입 정보 입력
	            );
	    
	    // admin 페이지 접근제한
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/admin/**") // 관리자 URL만 제한
                .excludePathPatterns("/admin/assets/**",   // 정적 리소스는 허용
                					 "/admin/login");
	    


	}

	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		
		String rootPath = getOSFilePath();
		
		registry.addResourceHandler("/attachment/**")
				.addResourceLocations(rootPath + fileRealPath.trim() + "/attachment/")
				.setCachePeriod(3600)
				.resourceChain(true)
				.addResolver(new PathResourceResolver());
		
		WebMvcConfigurer.super.addResourceHandlers(registry);
	}
	
	public String getOSFilePath() {
		String rootPath = "file:///";
		String os = System.getProperty("os.name").toLowerCase();
		
		if(os.contains("win")) {
			rootPath = "file:///c:";
		}else if(os.contains("linux")) {
			rootPath = "file://";
		}else if(os.contains("mac")) {			
			rootPath = "file://";
		}
		
		
		return rootPath;
	}

}



