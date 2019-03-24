package io.parkey19.events;

import io.parkey19.common.ErrorsResource;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

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

        if(errors.hasErrors())
            return badRequest(errors);

        eventValidator.validate(eventDto, errors);
        if(errors.hasErrors()) {
            return badRequest(errors);
        }

        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        Event newEvent = this.eventRepository.save(event);
        ControllerLinkBuilder selfLink = linkTo(EventController.class).slash(newEvent.getId());
        URI createdUri = selfLink.toUri();
        EventResource eventResource = new EventResource(newEvent);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(selfLink.withRel("update-event"));
        eventResource.add(new Link("/docs/index.html").withRel("profile"));
        return ResponseEntity.created(createdUri).body(eventResource);
    }

    @GetMapping
    public ResponseEntity queryEvent(Pageable pageable, PagedResourcesAssembler<Event> assembler) {
        Page<Event> page = this.eventRepository.findAll(pageable);
        PagedResources<Resource<Event>> resources = assembler.toResource(page, event -> new EventResource(event));
        resources.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id) {
        Optional<Event> eventOptional = this.eventRepository.findById(id);
        if (!eventOptional.isPresent()) return ResponseEntity.notFound().build();

        Event event = eventOptional.get();

        EventResource eventResource = new EventResource(event);
        eventResource.add(new Link("/docs/index.html#resources-events-get").withRel("profile"));
        return ResponseEntity.ok(eventResource);


    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id, @RequestBody @Validated EventDto eventDto, Errors errors) {

        Optional<Event> eventOptional = this.eventRepository.findById(id);
        if (!eventOptional.isPresent()) return ResponseEntity.notFound().build();

        if(errors.hasErrors())
            return badRequest(errors);

        eventValidator.validate(eventDto, errors);
        if(errors.hasErrors()) {
            return badRequest(errors);
        }
        Event event = eventOptional.get();
        modelMapper.map(eventDto, event);

        Event updateEvent = this.eventRepository.save(event);
        ControllerLinkBuilder selfLink = linkTo(EventController.class).slash(updateEvent.getId());
        EventResource eventResource = new EventResource(updateEvent);

        eventResource.add(new Link("/docs/index.html#resources-events-update").withRel("profile"));
        return ResponseEntity.ok(eventResource);
    }

    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }
}
