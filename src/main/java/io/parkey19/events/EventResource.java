package io.parkey19.events;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;

/**
 * Created by parkey19 on 2019. 3. 13..
 */
public class EventResource extends Resource<Event> {
    public EventResource(Event content, Link... links) {
        super(content, links);
    }
}
