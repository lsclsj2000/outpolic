package outpolic.user.chat.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 클라이언트가 구독할 prefix (서버가 메시지를 발행하는 경로)
        config.enableSimpleBroker("/topic");
        // 클라이언트가 서버로 메시지를 보낼 때 붙이는 prefix
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 웹소켓 엔드포인트 등록 (SockJS fallback 지원)
        // CORS 문제 해결을 위해 setAllowedOriginPatterns 사용
        registry.addEndpoint("/ws/chat")
        		.addInterceptors(new HttpHandshakeInterceptor())  // ✅ 이 부분
                .setAllowedOriginPatterns("*") // 개발 중에는 "*" 사용 가능, 운영 시에는 구체적 도메인 지정 권장
                .withSockJS();
    }
}