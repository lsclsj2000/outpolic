package outpolic.systems.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {
	//비밀번호 암호화
	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	// main페이지 띄우기 위한 노력
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		 http
	        .csrf(csrf -> csrf.disable())  // CSRF 비활성화
	        .authorizeHttpRequests(auth -> auth
	            .anyRequest().permitAll()  // 모든 요청 허용
	        )
	        .logout(logout -> logout
	                .logoutUrl("/doNotUseThis") // 일부러 안쓰는 주소 부여.
	                .permitAll()
	            );

	    return http.build();
	}

}
