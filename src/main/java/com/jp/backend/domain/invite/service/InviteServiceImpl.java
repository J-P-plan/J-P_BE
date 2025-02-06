package com.jp.backend.domain.invite.service;

import com.jp.backend.domain.invite.dto.InviteReqDto;
import com.jp.backend.domain.invite.dto.InviteResDto;
import com.jp.backend.domain.invite.entity.Invite;
import com.jp.backend.domain.invite.enums.InviteStatus;
import com.jp.backend.domain.invite.repository.JpaInviteRepository;
import com.jp.backend.domain.schedule.entity.Schedule;
import com.jp.backend.domain.schedule.entity.ScheduleUser;
import com.jp.backend.domain.schedule.repository.jpa.JpaScheduleRepository;
import com.jp.backend.domain.schedule.repository.jpa.JpaScheduleUserRepository;
import com.jp.backend.domain.user.entity.User;
import com.jp.backend.domain.user.repository.JpaUserRepository;
import com.jp.backend.global.exception.CustomLogicException;
import com.jp.backend.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InviteServiceImpl implements InviteService {
    private final JpaUserRepository userRepository;
    private final JpaInviteRepository inviteRepository;
    private final JpaScheduleUserRepository scheduleUserRepository;
    private final JpaScheduleRepository scheduleRepository;

    @Override
    public Boolean inviteUser(InviteReqDto reqDto, String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new CustomLogicException(ExceptionCode.USER_NONE));

        Schedule schedule = scheduleRepository.findById(reqDto.getScheduleId())
                .orElseThrow(() -> new CustomLogicException(ExceptionCode.SCHEDULE_NONE));

        // 해당 스케줄에 유저를 이미 초대했는지 검증
        // --> 이미 초대 기록이 있고 && 그 초대의 상태가 수락이나 대기일 때만 이미 초대했다는 에러 띄움
        Invite invite = findInviteByScheduleAndUser(schedule, user);
        if (invite != null) {
            if (!invite.getInviteStatus().equals(InviteStatus.REJECTED)) {
                throw new CustomLogicException(ExceptionCode.ALREADY_INVITED);
            }
            invite.setInviteStatus(InviteStatus.PENDING);
            invite.setRespondedAt(null);
        } else {
            invite = reqDto.toEntity(schedule, user);
        }

        inviteRepository.save(invite);

        return true;
    }

    // TODO 서비스 코드에서는 RedisUtil을 주입받고 Redis에 저장된 teamId에 해당하는 값이 있는지 확인
    //  만약 초대 코드가 이미 생성되어 redis에 해당하는 값이 있다면, 해당 코드를 반환
    //  생성된 초대 코드가 없다면, 랜덤한 문자열을 만들어 value로 지정하고, 유효기간 1일의 TTL을 설정
    //  초대 링크 만든 사람이면 그냥 들어가지게? 그 사람이 들어갔는데 초대한다고 뜨면 안되잖아

    // 수락 시 -> accepted로 바뀌는 동시에 shceduleUser에 추가됨
    public void acceptInvite(Long inviteId) {
        Invite invite = findInviteById(inviteId);

        invite.setInviteStatus(InviteStatus.ACCEPTED);
        invite.setRespondedAt(LocalDateTime.now());
        inviteRepository.save(invite);

        ScheduleUser scheduleUser = new ScheduleUser();
        scheduleUser.setUser(invite.getUser());
        scheduleUser.setSchedule(invite.getSchedule());
        scheduleUser.setIsCreater(false);
        scheduleUserRepository.save(scheduleUser);
    }

    // 거절 시 -> rejected로 바뀌고 shceduleUser에는 포함되지 않음
    public void rejectInvite(Long inviteId) {
        Invite invite = findInviteById(inviteId);

        invite.setInviteStatus(InviteStatus.REJECTED);
        invite.setRespondedAt(LocalDateTime.now());
        inviteRepository.save(invite);
    }

    private Invite findInviteById(Long inviteId) {
        return inviteRepository.findById(inviteId)
                .orElseThrow(() -> new CustomLogicException(ExceptionCode.INVITE_NONE));
    }

    private Invite findInviteByScheduleAndUser(Schedule schedule, User user) {
        return inviteRepository.findByScheduleAndUser(schedule, user)
                .orElseThrow(() -> new CustomLogicException(ExceptionCode.INVITE_NONE));
    }
}
