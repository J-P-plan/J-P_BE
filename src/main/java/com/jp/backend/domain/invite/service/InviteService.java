package com.jp.backend.domain.invite.service;

import com.jp.backend.domain.invite.dto.InviteReqDto;
import com.jp.backend.domain.invite.dto.InviteResDto;

public interface InviteService {
    Boolean inviteUser(InviteReqDto reqDto, String username);
}
