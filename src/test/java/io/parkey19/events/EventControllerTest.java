package io.parkey19.events;


import io.parkey19.account.Account;
import io.parkey19.account.AccountRepository;
import io.parkey19.account.AccountRole;
import io.parkey19.account.AccountService;
import io.parkey19.common.AppProperties;
import io.parkey19.common.BaseControllerTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by parkey19 on 2019. 3. 11..
 */

public class EventControllerTest extends BaseControllerTest {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AppProperties appProperties;

    @Before
    public void before() {
        this.eventRepository.deleteAll();
        this.accountRepository.deleteAll();
    }

    @Test
    public void createTest() throws Exception {
        EventDto event = EventDto.builder()
                .name("rest api 만들기")
                .description("spring boot rest api ")
                .beginEnrollmentDateTime(LocalDateTime.of(2019,3,11,10,0,0))
                .closeEnrollmentDateTime(LocalDateTime.of(2019,3,12,10,0,0))
                .beginEventDateTime(LocalDateTime.of(2019,3,13,10,0,0))
                .endEventDateTime(LocalDateTime.of(2019,3,14,10,0,0))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남")
                .build();


        mockMvc.perform(post("/api/events/")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8) //요청타입
                .accept(MediaTypes.HAL_JSON) //받고싶은 타입
                .content(objectMapper.writeValueAsString(event))) //event를 json을 String으로 맵핑
                .andDo(print())
                .andExpect(status().isCreated()) // 201 상태인지 확인
                .andExpect(jsonPath("id").exists()) //ID가 있는지 확인
                .andExpect(header().exists(HttpHeaders.LOCATION)) // HEADER에 Location 있는지 확인
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_UTF8_VALUE)) //Content-Type 값 확인
                .andExpect(jsonPath("free").value(false)) // free가 true가
                .andExpect(jsonPath("offline").value(true)) // free가 true가
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("update-event").description("link to update an existing event"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders( //요청 헤더 문서화
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields( //요청 필드 문서화
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("date time of begin of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment"),
                                fieldWithPath("eventStatus").description("event status")
                        ),
                        responseHeaders( //응답 헤더 문서화
                                headerWithName(HttpHeaders.LOCATION).description("Location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields( //응답 본문 문서화
                                fieldWithPath("id").description("identifier of new event"),
                                fieldWithPath("name").description("event name"),
                                fieldWithPath("description").description("date time of begin of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment"),
                                fieldWithPath("free").description("it tells if this event is free or not"),
                                fieldWithPath("offline").description("it tells if this event is offline meeting or not"),
                                fieldWithPath("eventStatus").description("event status"),
                                fieldWithPath("manager").description("manager"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-events.href").description("link to query events list"),
                                fieldWithPath("_links.update-event.href").description("link to update an existing event"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                        ))
        ;



    }

    private String getBearerToken() throws Exception {
        return "Bearer " + getAccessToken();
    }

    /**
     * 인증 토큰을 발급
     * @return
     * @throws Exception
     */
    private String getAccessToken() throws Exception {
        // Given
        Set<AccountRole> set = new HashSet();
        set.add(AccountRole.ADMIN);
        set.add(AccountRole.USER);

        Account account = Account.builder()
                .email(appProperties.getUserUsername())
                .password(appProperties.getUserPassword())
                .roles(set)
                .build();
        accountService.saveAccount(account);


        ResultActions perform = this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret())) // Basic OAuth Header
                .param("username", appProperties.getUserUsername())
                .param("password", appProperties.getUserPassword())
                .param("grant_type", "password"));
        String responseBody = perform.andReturn().getResponse().getContentAsString();
        Jackson2JsonParser parser = new Jackson2JsonParser();
        return parser.parseMap(responseBody).get("access_token").toString();
    }

    @Test
    @TestDescription("DTO 필드 외에 값을 req시 400")
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
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8) //요청타입
                .accept(MediaTypes.HAL_JSON) //받고싶은 타입
                .content(objectMapper.writeValueAsString(event))) //event를 json을 String으로 맵핑
                .andDo(print())
                .andExpect(status().isBadRequest()); // 400 상태인지 확인


    }

    @Test
    @TestDescription("입력 값이 비어있는 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("입력값 잘못된 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 26, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
                .endEventDateTime(LocalDateTime.of(2018, 11, 23, 14, 21))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                .build();

        this.mockMvc.perform(post("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("content[0].code").exists())
                .andExpect(jsonPath("_links.index").exists())
        ;
    }

    @Test
    @TestDescription("30개이벤트를 조회")
    public void queryEvents() throws Exception{
        //given
        IntStream.range(0,30).forEach(this::generateEvent);

        //when
        this.mockMvc.perform(get("/api/events")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort", "name,DESC")
                        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events",
                        links(
                                linkWithRel("first").description("link to first page"),
                                linkWithRel("prev").description("link to prev page"),
                                linkWithRel("self").description("link to self"),
                                linkWithRel("next").description("link to next page"),
                                linkWithRel("last").description("link to last page"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestParameters( //요청 필드 문서화
                                parameterWithName("page").description("page 0부터 시작"),
                                parameterWithName("size").description("한번에 보여줄 리스트 개수"),
                                parameterWithName("sort").description("정렬 컬럼")
                        ),
                        responseHeaders( //응답 헤더 문서화
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields( //응답 본문 문서화
                                fieldWithPath("_embedded.eventList[0].id").description("identifier of new event"),
                                fieldWithPath("_embedded.eventList[0].name").description("event name"),
                                fieldWithPath("_embedded.eventList[0].description").description("date time of begin of new event"),
                                fieldWithPath("_embedded.eventList[0].beginEnrollmentDateTime").description("date time of begin of new event"),
                                fieldWithPath("_embedded.eventList[0].closeEnrollmentDateTime").description("date time of close of new event"),
                                fieldWithPath("_embedded.eventList[0].beginEventDateTime").description("date time of begin of new event"),
                                fieldWithPath("_embedded.eventList[0].endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("_embedded.eventList[0].location").description("location of new event"),
                                fieldWithPath("_embedded.eventList[0].basePrice").description("base price of new event"),
                                fieldWithPath("_embedded.eventList[0].maxPrice").description("max price of new event"),
                                fieldWithPath("_embedded.eventList[0].limitOfEnrollment").description("limit of enrollment"),
                                fieldWithPath("_embedded.eventList[0].free").description("it tells if this event is free or not"),
                                fieldWithPath("_embedded.eventList[0].offline").description("it tells if this event is offline meeting or not"),
                                fieldWithPath("_embedded.eventList[0].eventStatus").description("event status"),
                                fieldWithPath("_embedded.eventList[0].manager").description("manager"),
                                fieldWithPath("_embedded.eventList[0]._links.self.href").description("self href"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.profile.href").description("link to profile"),
                                fieldWithPath("_links.first.href").description("link to first page"),
                                fieldWithPath("_links.prev.href").description("link to prev page"),
                                fieldWithPath("_links.next.href").description("link to next page"),
                                fieldWithPath("_links.last.href").description("link to last page"),
                                fieldWithPath("page.size").description("page size"),
                                fieldWithPath("page.totalElements").description("page total elements"),
                                fieldWithPath("page.totalPages").description("total page size"),
                                fieldWithPath("page.number").description("page number")
                        )
                        ))
        ;

    }

    @Test
    @TestDescription("기존의 이벤트를 하나 조회하기")
    public void getEvent() throws Exception {
        // Given
        Event event = this.generateEvent(100);

        // When & Then
        this.mockMvc.perform((get("/api/events/{id}", event.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-an-event"))
        ;
    }

    @Test
    @TestDescription("없는 이벤트는 조회했을 때 404 응답받기")
    public void getEvent404() throws Exception {
        // When & Then
        this.mockMvc.perform(get("/api/events/11883"))
                .andExpect(status().isNotFound());
    }

    private Event generateEvent(int index) {
        Event event = Event.builder()
                .name("event " + index)
                .description("test event")
                .build();

        return this.eventRepository.save(event);
    }

    @Test
    @TestDescription("기존의 이벤트를 하나 수정하기")
    public void getEventUpdate() throws Exception {
        //given
        Event updateTargetEvent = Event.builder()
                .name("rest api 만들기 22")
                .description("spring boot rest api ")
                .beginEnrollmentDateTime(LocalDateTime.of(2019,3,11,10,0,0))
                .closeEnrollmentDateTime(LocalDateTime.of(2019,3,12,10,0,0))
                .beginEventDateTime(LocalDateTime.of(2019,3,13,10,0,0))
                .endEventDateTime(LocalDateTime.of(2019,3,14,10,0,0))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남")
                .build();

        Event event = this.eventRepository.save(updateTargetEvent);
        event.setName("수정 중");
        event.setEventStatus(EventStatus.PUBLISHED);
        EventDto eventDto = modelMapper.map(event, EventDto.class);

        mockMvc.perform((put("/api/events/{id}", event.getId()))
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8) //요청타입
                .accept(MediaTypes.HAL_JSON) //받고싶은 타입
                .content(objectMapper.writeValueAsString(eventDto))) //event를 json을 String으로 맵핑
                .andDo(print())
                .andExpect(status().isOk()) // 200 상태인지 확인
                .andExpect(jsonPath("id").exists()) //ID가 있는지 확인
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_UTF8_VALUE))
                .andExpect(jsonPath("name").value("수정 중"))
                .andExpect(jsonPath("eventStatus").value(EventStatus.PUBLISHED.name()))
                .andExpect(jsonPath("_links.self").exists())
//                .andExpect(jsonPath("_links.update-event").exists())
//                .andExpect(jsonPath("_links.query-events").exists())
                .andDo(document("update-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders( //요청 헤더 문서화
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields( //요청 필드 문서화
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("date time of begin of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment"),
                                fieldWithPath("eventStatus").description("event status")
                        ),
                        responseHeaders( //응답 헤더 문서화
//                                headerWithName(HttpHeaders.LOCATION).description("Location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        )//,
//                        responseFields( //응답 본문 문서화
//                                fieldWithPath("id").description("identifier of new event"),
//                                fieldWithPath("name").description("event name"),
//                                fieldWithPath("description").description("date time of begin of new event"),
//                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
//                                fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
//                                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
//                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
//                                fieldWithPath("location").description("location of new event"),
//                                fieldWithPath("basePrice").description("base price of new event"),
//                                fieldWithPath("maxPrice").description("max price of new event"),
//                                fieldWithPath("limitOfEnrollment").description("limit of enrollment"),
//                                fieldWithPath("free").description("it tells if this event is free or not"),
//                                fieldWithPath("offline").description("it tells if this event is offline meeting or not"),
//                                fieldWithPath("eventStatus").description("event status"),
//                                fieldWithPath("_links.self.href").description("link to self"),
//                                fieldWithPath("_links.query-events.href").description("link to query events list"),
//                                fieldWithPath("_links.update-event.href").description("link to update an existing event"),
//                                fieldWithPath("_links.profile.href").description("link to profile")
//                        )
                ));



    }

    @Test
    @TestDescription("없는 이벤트는 조회했을 때 404 응답받기")
    public void getEventUpdate400() throws Exception {
        //given
        Event event = this.generateEvent(100);
        // When & Then
        this.mockMvc.perform(put("/api/events/11883")
                .contentType(MediaType.APPLICATION_JSON_UTF8) //요청타입
                .accept(MediaTypes.HAL_JSON) //받고싶은 타입
                .content(objectMapper.writeValueAsString(event))) //event를 json을 String으로 맵핑
                .andDo(print())
                .andExpect(status().is4xxClientError())
        ;


    }

    @Test
    @TestDescription("없는 이벤트는 조회했을 때 404 응답받기")
    public void getEventUpdate404() throws Exception {
        //given
        EventDto event = EventDto.builder()
                .name("rest api 만들기")
                .description("spring boot rest api ")
                .beginEnrollmentDateTime(LocalDateTime.of(2019,3,11,10,0,0))
                .closeEnrollmentDateTime(LocalDateTime.of(2019,3,12,10,0,0))
                .beginEventDateTime(LocalDateTime.of(2019,3,13,10,0,0))
                .endEventDateTime(LocalDateTime.of(2019,3,14,10,0,0))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남")
                .build();

        // When & Then
        this.mockMvc.perform(put("/api/events/11883")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8) //요청타입
                .accept(MediaTypes.HAL_JSON) //받고싶은 타입
                .content(objectMapper.writeValueAsString(event))) //event를 json을 String으로 맵핑
                .andDo(print())
                .andExpect(status().isNotFound())
        ;


    }
}