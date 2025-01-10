package com.jp.backend.domain.invite.dto;

import com.jp.backend.domain.schedule.dto.ScheduleCompactResDto;
import com.jp.backend.domain.user.dto.UserCompactResDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InviteDetailResDto { // 초대 받은 사람이 받을 정보
    @Schema(description = "초대한 일정 정보")
    private ScheduleCompactResDto scheduleCompactResDto;
    // TODO 근데 여기 여행갈 도시 정보는 없어서 어떻게 할지 고민둥

    @Schema(description = "초대한 유저 정보")
    private UserCompactResDto userCompactResDto;
}
