package io.parkey19.events;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by parkey19 on 2019. 3. 10..
 */
public class EventTest {
    @Test
    public void build() {
        Event event = Event.builder().build();
        assertThat(event).isNotNull();

        Event event2 = Event.builder()
                .name("rest api")
                .description("rest api dev")
                .build();
        assertThat(event2).isNotNull();
    }

    @Test
    public void javaBean() {
        Event event = new Event();
        String name = "ev";
        String spring = "spring";
        event.setName(name);
        event.setDescription(spring);

        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(spring);
    }
}