package io.parkey19.events;

import io.parkey19.account.Account;
import io.parkey19.account.CurrentUser;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collection;
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
    public HttpEntity createEvent(@RequestBody @Validated EventDto eventDto, Errors errors, @CurrentUser Account currentUser) {

        if(errors.hasErrors())
            return badRequest(errors);

        eventValidator.validate(eventDto, errors);
        if(errors.hasErrors()) {
            return badRequest(errors);
        }

        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        event.setManager(currentUser);
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
    public ResponseEntity queryEvent(Pageable pageable, PagedResourcesAssembler<Event> assembler,
                                     @CurrentUser Account account
                                    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Page<Event> page = this.eventRepository.findAll(pageable);
        PagedResources<Resource<Event>> resources = assembler.toResource(page, event -> new EventResource(event));
        resources.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));
        if (account != null) {
            resources.add(linkTo(EventController.class).withRel("create-event"));
        }
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id, @CurrentUser Account account) {
        Optional<Event> eventOptional = this.eventRepository.findById(id);
        if (!eventOptional.isPresent()) return ResponseEntity.notFound().build();

        Event event = eventOptional.get();

        EventResource eventResource = new EventResource(event);
        eventResource.add(new Link("/docs/index.html#resources-events-get").withRel("profile"));
        if (account != null) {
            eventResource.add(linkTo(EventController.class).slash(event.getId()).withRel("update-event"));
        }
        return ResponseEntity.ok(eventResource);


    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id, @RequestBody @Validated EventDto eventDto,
                                      Errors errors, @CurrentUser Account currentUser) {

        Optional<Event> eventOptional = this.eventRepository.findById(id);
        if (!eventOptional.isPresent()) return ResponseEntity.notFound().build();

        if(errors.hasErrors())
            return badRequest(errors);

        eventValidator.validate(eventDto, errors);
        if(errors.hasErrors()) {
            return badRequest(errors);
        }
        Event event = eventOptional.get();

        if (!event.getManager().equals(currentUser)) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

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
