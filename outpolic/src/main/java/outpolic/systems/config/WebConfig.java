package outpolic.systems.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import lombok.RequiredArgsConstructor;
import outpolic.systems.interceptor.LogInterceptor;
import outpolic.user.login.interceptor.LoginInterceptor;
import outpolic.user.login.interceptor.LoginUserBlockInterceptor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer{
	
	@Value("${file.path}")
	private String fileRealPath;
	
	private final LoginInterceptor loginInterceptor;
	
	private final LoginUserBlockInterceptor loginUserBlockInterceptor;
	
	private final LogInterceptor logInterceptor;
	
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
		
		registry.addInterceptor(logInterceptor)
				.addPathPatterns("/**")
				.excludePathPatterns(logExcludePath);
		
		/*
		 * // 로그인 안한 이용자 접근 차단 registry.addInterceptor(loginInterceptor)
		 * .addPathPatterns("/**") // 인터셉터로 로그인 안한사람 막음 .excludePathPatterns("/",
		 * "/main") // 메인화면 제외 .excludePathPatterns("/login") //로그인화면 제외
		 * .excludePathPatterns("/forgotPswd") // 비밀번호 찾기 제외
		 * .excludePathPatterns("/userGoodsList") // 상품리스트 제외
		 * .excludePathPatterns("/user/userInquiryList", "/user/userInqueryList/**",
		 * "/user/userInqueryList**")// 문의글 .excludePathPatterns("/favicon*")
		 * .excludePathPatterns("/user/assets/**"); // 정적 리소스 //로그인 한 이용자 접근 차단
		 * registry.addInterceptor(loginUserBlockInterceptor)
		 * .addPathPatterns("/login")// 로그인 경로 .addPathPatterns("/forgotPswd")// 비밀번호 찾기
		 * 경로 .addPathPatterns("/choiceRegister", "/user/registerInfo");// 회원가입 경로
		 * 
		 * WebMvcConfigurer.super.addInterceptors(registry);
		 */


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



