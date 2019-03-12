package io.parkey19.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Created by parkey19 on 2019. 3. 12..
 */
@Component
public class EventValidator {

    public void validate(EventDto eventDto, Errors errors) {
        String wrongValue = "wrongValue";
        if(eventDto.getBasePrice() > eventDto.getMaxPrice() && eventDto.getMaxPrice() > 0) {
            errors.rejectValue("basePrice", wrongValue, "BasePrice is wrong");
            errors.rejectValue("maxPrice", wrongValue, "MaxPrice is wrong");
        }

        LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
        if(endEventDateTime.isBefore(eventDto.getBeginEventDateTime()) ||
                endEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime()) ||
                endEventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime())) {
            errors.rejectValue("endEventDateTime", wrongValue, "endEventDateTime is wrong");
        }

        LocalDateTime beginEventDateTime = eventDto.getBeginEventDateTime();
        if(beginEventDateTime.isAfter(eventDto.getEndEventDateTime()) ||
                beginEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime()) ||
                beginEventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime())) {
            errors.rejectValue("beginEventDateTime", wrongValue, "beginEventDateTime is wrong");
        }

        LocalDateTime beginEnrollmentDateTime = eventDto.getBeginEnrollmentDateTime();
        if(beginEnrollmentDateTime.isAfter(eventDto.getCloseEnrollmentDateTime()) ||
                beginEnrollmentDateTime.isAfter(eventDto.getBeginEventDateTime()) ||
                beginEnrollmentDateTime.isAfter(eventDto.getEndEventDateTime())) {
            errors.rejectValue("beginEnrollmentDateTime", wrongValue, "beginEnrollmentDateTime is wrong");
        }

        LocalDateTime closeEnrollmentDateTime = eventDto.getCloseEnrollmentDateTime();
        if(closeEnrollmentDateTime.isBefore(eventDto.getBeginEnrollmentDateTime()) ||
                closeEnrollmentDateTime.isAfter(eventDto.getBeginEventDateTime()) ||
                closeEnrollmentDateTime.isAfter(eventDto.getEndEventDateTime())) {
            errors.rejectValue("closeEnrollmentDateTime", wrongValue, "closeEnrollmentDateTime is wrong");
        }

    }
}
