package io.parkey19.account;


import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by parkey19 on 2019. 3. 25..
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class AccountServiceTest {

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void findByUserName() {
        String email = "1234";
        String name = "1234";
        String password = "1234";
        Set<AccountRole> set = new HashSet();
        set.add(AccountRole.ADMIN);
        set.add(AccountRole.USER);
        Account account = Account.builder()
                                .email(email)
                                .password(password)
                                .roles(set)
                                .build();
        accountRepository.save(account);

        UserDetailsService userDetailsService = (UserDetailsService) accountService;
        UserDetails userDetails = userDetailsService.loadUserByUsername("p19");

        assertThat(userDetails.getPassword()).isEqualTo(password);
    }

    @Test(expected = UsernameNotFoundException.class)
    public void userNameNotFound() {
        String userName = "p@p.com";

        accountService.loadUserByUsername(userName);


    }

    @Test
    public void userNameNotFoundByRule() {
        String userName = "p@p.com";

        expectedException.expect(UsernameNotFoundException.class);
        expectedException.expectMessage(Matchers.containsString(userName));

        accountService.loadUserByUsername(userName);


    }
}