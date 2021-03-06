package io.parkey19.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Created by parkey19 on 2019. 3. 12..
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EventDto {

    @NotEmpty
    private String name; //이벤트 네임
    @NotEmpty
    private String description; // 설명
    @NotNull
    private LocalDateTime beginEnrollmentDateTime; //등록 시작일시
    @NotNull
    private LocalDateTime closeEnrollmentDateTime; //종료일시
    @NotNull
    private LocalDateTime beginEventDateTime; //이벤트 시작일시
    @NotNull
    private LocalDateTime endEventDateTime;   //이벤트 종료일시
    private String location; // (optional) 이벤트 위치 이게 없으면 온라인 모임
    @Min(0)
    private int basePrice; // (optional) 기본 금액
    @Min(0)
    private int maxPrice; // (optional) 최고 금액
    @Min(0)
    private int limitOfEnrollment; //등록한도
    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus = EventStatus.DRAFT;

}
