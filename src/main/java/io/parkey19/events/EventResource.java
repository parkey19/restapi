package io.parkey19.events;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

/**
 * Created by parkey19 on 2019. 3. 13..
 */
public class EventResource extends Resource<Event> {
    public EventResource(Event event, Link... links) {
        super(event, links);
        // add(new Link("http://localhost:8080/api/events" + event.getId()));
        // 셀프 링크 생성 위와 동일한 링크
        add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
    }
}
