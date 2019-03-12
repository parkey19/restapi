package io.parkey19.events;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void createTest() throws Exception {
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
                .location("강남")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.DRAFT)
                .build();
//        event.setId(10);

//        Mockito.when(eventRepository.save(event)).thenReturn(event);


        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON_UTF8) //요청타입
                .accept(MediaTypes.HAL_JSON) //받고싶은 타입
                .content(objectMapper.writeValueAsString(event))) //event를 json을 String으로 맵핑
                .andDo(print())
                .andExpect(status().isCreated()) // 201 상태인지 확인
                .andExpect(jsonPath("id").exists()) //ID가 있는지 확인
                .andExpect(header().exists(HttpHeaders.LOCATION)) // HEADER에 Location 있는지 확인
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_UTF8_VALUE)) //Content-Type 값 확인
                .andExpect(jsonPath("id").value(Matchers.not(100))) // ID가 100이 아니면
                .andExpect(jsonPath("free").value(Matchers.not(true))) // free가 true가
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()));


    }

    @Test
    public void badRequestTest() throws Exception {
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
                .location("강남")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.DRAFT)
                .build();


        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON_UTF8) //요청타입
                .accept(MediaTypes.HAL_JSON) //받고싶은 타입
                .content(objectMapper.writeValueAsString(event))) //event를 json을 String으로 맵핑
                .andDo(print())
                .andExpect(status().isBadRequest()); // 400 상태인지 확인


    }
}