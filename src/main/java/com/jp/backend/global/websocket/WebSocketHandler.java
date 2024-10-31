package com.jp.backend.global.websocket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
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

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WebSocketHandler extends TextWebSocketHandler {
	private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		sessions.add(session);

		Long scheduleId = getScheduleIdFromSession(session);
		session.getAttributes().put("scheduleId", scheduleId); // scheduleId를 세션 속성에 저장

		// 접속한 유저 정보 로그 출력
		String sessionId = session.getId();
		InetSocketAddress clientAddress = session.getRemoteAddress();
		log.info("새로운 웹소켓 연결이 있습니다. Session ID: {}, IP: {}", sessionId, clientAddress);

	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		sessions.remove(session);
		String sessionId = session.getId();
		log.info("웹소켓 연결이 종료되었습니다. Session ID: {}, Close Status: {}", sessionId, status);
	}

	//특정 scehcule을 보고있는 유저에게만 braodcast 요청
	//모든 세션 다도는게 좀 비효율적??
	public void broadcastToSchedule(Long scheduleId, String message) {
		synchronized (sessions) {
			for (WebSocketSession session : sessions) {
				Long sessionScheduleId = (Long)session.getAttributes().get("scheduleId");
				if (scheduleId.equals(sessionScheduleId) && session.isOpen()) {
					try {
						session.sendMessage(new TextMessage(message));
					} catch (IOException e) {
						throw new CustomLogicException(ExceptionCode.WEBSOCKET_IO_EXCEPTION);
					}
				}
			}
		}
	}

	//세션에서 scheduleId를 파싱
	private Long getScheduleIdFromSession(WebSocketSession session) {
		URI uri = session.getUri();
		if (uri != null) {
			String query = uri.getQuery();
			if (query != null && query.contains("scheduleId")) {
				String scheduleId = query.split("scheduleId=")[1];
				return Long.parseLong(scheduleId);
			}
		}
		return null;
	}

}

