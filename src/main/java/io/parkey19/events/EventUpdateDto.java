package io.parkey19.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Created by parkey19 on 2019. 3. 24..
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventUpdateDto {

    @NotNull
    private Integer id;
    @NotNull
    private String name;
    @NotNull
    private String description;
    @NotNull
    private LocalDateTime beginEnrollmentDateTime;
    @NotNull
    private LocalDateTime closeEnrollmentDateTime;
    @NotNull
    private LocalDateTime beginEventDateTime;
    @NotNull
    private LocalDateTime endEventDateTime;
    private String location; // (optional) 이게 없으면 온라인 모임
    @Min(0)
    private int basePrice; // (optional)
    @Min(0)
    private int maxPrice; // (optional)
    @Min(0)
    private int limitOfEnrollment;
    @NotNull
    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus;

}
