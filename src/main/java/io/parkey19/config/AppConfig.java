package io.parkey19.config;

import io.parkey19.account.Account;
import io.parkey19.account.AccountRole;
import io.parkey19.account.AccountService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by parkey19 on 2019. 3. 27..
 */
@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public ApplicationRunner applicationRunner() {
        return new ApplicationRunner() {
            @Autowired
            AccountService accountService;
            @Override
            public void run(ApplicationArguments applicationArguments) throws Exception {
                Set<AccountRole> roleSet = new HashSet();
                roleSet.add(AccountRole.ADMIN);
                roleSet.add(AccountRole.USER);
                Account account = Account.builder()
                        .email("pp@gmail.com")
                        .password("1234")
                        .roles(roleSet)
                        .build();
                accountService.saveAccount(account);
            }
        };
    }
}