package com.jp.backend.auth.oauth;

import java.io.Serializable;

import com.jp.backend.domain.user.entity.User;

import lombok.Getter;

//세션에 사용자 정보를 저장하기 위한 DTO 클래스.
//SessionUser은 인증된 사용자 정보만 필요하고, 그 외 정보들은 필요가 없어 name, email, picture만 필드로 선언한다.
@Getter
public class SessionUser implements Serializable {
  // SessionUser는 인증된 사용자 정보만 필요하므로 아래 필드만 선언한다.
  private String name;
  private String email;
  private String picture;

  public SessionUser(User user) {
    this.name = user.getName();
    this.email = user.getEmail();
    this.picture = user.getPicture();
  }
}
