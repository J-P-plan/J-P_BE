package com.jp.backend.global.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(new WebSocketHandler(), "/ws").setAllowedOrigins("*");
	}
}

//WebSocketChatHandler를 이용하여 Websocket을 활성화하기 위해 Config를 생성한다.
//
// @EnableWebSocket을 선언하여 Websocket을 활성화한다. Websocket에 접속하기 위해 /ws/chat으로 접속한다. 다른 서버에서도 접속할 수 있게 CORS를 모두 허용한다. 이제 클라이언트가 ws://localhost:8080/ws/chat으로 커넥션을 연결하고 메시지를 통신할 수 있다.