package com.jp.backend.domain.diary.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.jp.backend.domain.comment.dto.CommentResDto;
import com.jp.backend.domain.comment.entity.Comment;
import com.jp.backend.domain.diary.entity.Diary;
import com.jp.backend.domain.file.dto.FileResDto;
import com.jp.backend.domain.schedule.entity.Schedule;
import com.jp.backend.domain.user.dto.UserCompactResDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiaryResDto {
	@Schema(description = "여행기 아이디")
	private Long id;

	@Schema(description = "제목")
	private String subject;

	@Schema(description = "내용")
	private String content;

	@Schema(description = "일정 시작일")
	private LocalDate scheduleStartDate;

	@Schema(description = "일정 종료일")
	private LocalDate scheduleEndDate;

	@Schema(description = "작성자 정보")
	private UserCompactResDto userCompactResDto;

	@Schema(description = "좋아요 개수")
	private Long likeCnt;

	@Schema(description = "조회수")
	private Integer viewCnt;

	@Schema(description = "좋아요 눌렀는지 여부")
	private Boolean isLiked;

	@Schema(description = "찜 눌렀는지 여부")
	private Boolean isBookmarked;

	@Schema(description = "댓글 개수")
	private Long commentCnt;

	@Schema(description = "댓글 리스트")
	private List<CommentResDto> commentResDtoList;

	@Schema(description = "해당 리뷰의 파일 정보")
	private List<FileResDto> fileInfos;

	@Schema(description = "공개 여부")
	private Boolean isPublic;

	// TODO 태그

	@Builder
	public DiaryResDto(Diary diary, Schedule schedule, Long likeCnt, Boolean isLiked, Boolean isBookmarked,
		Long commentCnt, List<Comment> commentList,
		List<FileResDto> fileInfos) {
		this.id = diary.getId();
		this.subject = diary.getSubject();
		this.content = diary.getContent();
		this.scheduleStartDate = schedule.getStartDate();
		this.scheduleEndDate = schedule.getEndDate();
		this.userCompactResDto = UserCompactResDto.builder().user(diary.getUser()).build();
		this.likeCnt = likeCnt;
		this.viewCnt = (diary.getViewCnt() != null) ? diary.getViewCnt() : 0;
		this.isLiked = isLiked;
		this.isBookmarked = isBookmarked;
		this.commentCnt = (commentCnt != null) ? commentCnt : 0L;
		this.commentResDtoList = (commentList != null) ? commentList.stream()
			.map(comment -> CommentResDto.builder().comment(comment).build())
			.toList() : new ArrayList<>();
		this.fileInfos = fileInfos != null ? fileInfos : new ArrayList<>();
		this.isPublic = diary.getIsPublic();
	}
}
