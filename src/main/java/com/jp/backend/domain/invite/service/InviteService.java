package com.jp.backend.domain.invite.service;

import com.jp.backend.domain.invite.dto.InviteReqDto;
import com.jp.backend.domain.invite.dto.InviteResDto;

public interface InviteService {
    InviteResDto inviteUser(InviteReqDto reqDto, String username);
}
