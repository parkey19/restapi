package io.parkey19.events;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by parkey19 on 2019. 3. 11..
 */
@RunWith(SpringRunner.class)
@WebMvcTest
public class EventControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    EventRepository eventRepository;

    public Event createEvent() {
        Event event = Event.builder()
                .name("rest api 만들기")
                .description("spring boot rest api ")
                .beginEnrollmentDateTime(LocalDateTime.of(2019,3,11,10,0,0))
                .closeEnrollmentDateTime(LocalDateTime.of(2019,3,11,10,0,0))
                .beginEventDateTime(LocalDateTime.of(2019,3,11,10,0,0))
                .endEventDateTime(LocalDateTime.of(2019,3,11,10,0,0))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("online")
                .build();
        event.setId(10);
        return event;
    }

    @Test
    public void createTest() throws Exception {
        Event event = createEvent();

        Mockito.when(eventRepository.save(event)).thenReturn(event);


        mockMvc.perform(post("/api/event")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(redirectedUrl("http://localhost/api/event/10"))
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_UTF8_VALUE));


    }
}