package io.parkey19.config;

import io.parkey19.account.Account;
import io.parkey19.account.AccountService;
import io.parkey19.common.BaseControllerTest;
import io.parkey19.events.TestDescription;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by parkey19 on 2019. 3. 27..
 */
public class AuthServerConfigTest extends BaseControllerTest {

    @Autowired
    AccountService accountService;

    @Test
    @TestDescription("인증 토큰을 발급받는 테스트")
    public void getAuthToken() throws Exception {
        //given
        String userName = "pp1@gmail.com";
        String password = "1234";
        Account account = Account.builder()
                            .email(userName)
                            .password(password)
                            .build();

        accountService.saveAccount(account);

        String clientId="myApp";
        String clientSecret="pass";

        mockMvc.perform(post("/oauth/token")
                .with(httpBasic(clientId, clientSecret))
                .param("username", userName)
                .param("password", password)
                .param("grant_type", "password"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists());
    }

}