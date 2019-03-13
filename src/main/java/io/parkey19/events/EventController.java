package io.parkey19.events;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.Binding;
import java.net.URI;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

/**
 * Created by parkey19 on 2019. 3. 11..
 */
@RestController
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EventValidator eventValidator;

    @PostMapping
    public HttpEntity createEvent(@RequestBody @Validated EventDto eventDto, Errors errors) {

        if(errors.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        eventValidator.validate(eventDto, errors);

        if(errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }

        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        Event newEvent = this.eventRepository.save(event);
        ControllerLinkBuilder selfLink = linkTo(EventController.class).slash(newEvent.getId());
        URI createdUri = selfLink.toUri();
        EventResource eventResource = new EventResource(newEvent);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(selfLink.withRel("update-event"));
//        newEvent.setId(10);
        return ResponseEntity.created(createdUri).body(eventResource);
    }
}
