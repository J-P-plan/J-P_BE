package com.jp.backend.global.serializers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class CustomDateSerializer extends JsonSerializer<Date> {
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm");

	// static {
	// 	dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul")); // 시간대를 KST로 설정
	// }

	@Override
	public void serialize(Date date, JsonGenerator gen, SerializerProvider provider) throws IOException {
		String formattedDate = dateFormat.format(date);
		gen.writeString(formattedDate);
	}
}
