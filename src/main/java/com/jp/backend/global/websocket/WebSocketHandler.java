package com.jp.backend.global.websocket;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.jp.backend.global.exception.CustomLogicException;
import com.jp.backend.global.exception.ExceptionCode;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
	private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		sessions.add(session);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		sessions.remove(session);
	}

	public void broadcast(String message) {
		synchronized (sessions) {
			for (WebSocketSession session : sessions) {
				try {
					session.sendMessage(new TextMessage(message));
				} catch (IOException e) {
					throw new CustomLogicException(ExceptionCode.WEBSOCKET_IO_EXCEPTION);
				}
			}
		}
	}
}

