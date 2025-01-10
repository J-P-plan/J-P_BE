package com.jp.backend.domain.invite.service;

import com.jp.backend.domain.invite.dto.InviteReqDto;
import com.jp.backend.domain.invite.dto.InviteResDto;
import com.jp.backend.domain.invite.entity.Invite;
import com.jp.backend.domain.invite.enums.InviteStatus;
import com.jp.backend.domain.invite.repository.JpaInviteRepository;
import com.jp.backend.domain.schedule.entity.ScheduleUser;
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

    @Override
    public InviteResDto inviteUser(InviteReqDto reqDto, String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new CustomLogicException(ExceptionCode.USER_NONE));


        // TODO invite / ScheduleUser 따로 해서
        //  invite -
        //  inviteStatus가 Accepted가 되었을 때 scheduleUser에 추가
        //  즉, 처음에는 모두 pending으로 들어있음
        //  - 누가 수락하면 accepted로 바뀌는 동시에 shceduleUser에 추가됨
        //  - 누가 거절하면 rejected로 바뀌고 shceduleUser에는 포함되지 않음


        return null;
    }

    // TODO 서비스 코드에서는 RedisUtil을 주입받고 Redis에 저장된 teamId에 해당하는 값이 있는지 확인
    //  만약 초대 코드가 이미 생성되어 redis에 해당하는 값이 있다면, 해당 코드를 반환
    //  생성된 초대 코드가 없다면, 랜덤한 문자열을 만들어 value로 지정하고, 유효기간 1일의 TTL을 설정
    //  초대 링크 만든 사람이면 그냥 들어가지게? 그 사람이 들어갔는데 초대한다고 뜨면 안되잖아

    // TODO 수락 시 -> accepted로 바뀌는 동시에 shceduleUser에 추가됨
    public void acceptInvite(Long inviteId) {
        Invite invite = findInvite(inviteId);

        invite.setInviteStatus(InviteStatus.ACCEPTED);
        invite.setRespondedAt(LocalDateTime.now());
        inviteRepository.save(invite);

        ScheduleUser scheduleUser = new ScheduleUser();
        scheduleUser.setUser(invite.getUser());
        scheduleUser.setSchedule(invite.getSchedule());
        scheduleUser.setIsCreater(false);
        scheduleUserRepository.save(scheduleUser);
    }

    // TODO 거절 시 -> rejected로 바뀌고 shceduleUser에는 포함되지 않음
    public void rejectInvite(Long inviteId) {
        Invite invite = findInvite(inviteId);

        invite.setInviteStatus(InviteStatus.REJECTED);
        invite.setRespondedAt(LocalDateTime.now());
        inviteRepository.save(invite);
    }

    private Invite findInvite(Long inviteId) {
        return inviteRepository.findById(inviteId)
                .orElseThrow(() -> new CustomLogicException(ExceptionCode.INVITE_NONE));
    }
}
