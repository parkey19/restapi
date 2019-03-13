package io.parkey19.events;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by parkey19 on 2019. 3. 10..
 */
@RunWith(JUnitParamsRunner.class)
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

    @Test
    @Parameters
    public void testFree(int basePrice, int maxPrice, boolean isFree) {
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();
        event.update();

        assertThat(event.isFree()).isEqualTo(isFree);
    }

    private Object[] parametersForTestFree() {
        return new Object[] {
                new Object[] {0, 0, true},
                new Object[] {100, 0, false},
                new Object[] {0, 100, false},
                new Object[] {100, 200, false}
        };
    }

    @Test
    @Parameters
    public void testOffline(String location, boolean isOffline) {
        Event event = Event.builder()
                .location(location)
                .build();

        event.update();

        assertThat(event.isOffline()).isEqualTo(isOffline);

    }

    private Object[] parametersForTestOffline() {
        return new Object[] {
                new Object[] {"강남", true},
                new Object[] {null, false},
                new Object[] {"", false}
        };
    }

}