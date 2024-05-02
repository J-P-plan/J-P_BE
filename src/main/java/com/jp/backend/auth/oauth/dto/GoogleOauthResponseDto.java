package com.jp.backend.auth.oauth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class GoogleOauthResponseDto {
	private String access_token;
	private String expires_in;
	private String scope;
	private String token_type;
	private String id_token;

	//회원 정보를 가지고 있는 feild는 id_token임
	//이 id_token은 Base64ULRL로 암호화 되어있음(공식문서)
}
