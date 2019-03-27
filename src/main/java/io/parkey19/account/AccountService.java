package io.parkey19.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by parkey19 on 2019. 3. 25..
 */
@Service
public class AccountService implements UserDetailsService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public Account saveAccount(Account account) {
        account.setPassword(this.passwordEncoder.encode(account.getPassword()));
        return this.accountRepository.save(account);
    }


    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Account byEmail = accountRepository.findByEmail(userName).orElseThrow(() -> new UsernameNotFoundException(userName));
        return new User(byEmail.getEmail(), byEmail.getPassword(), authorities(byEmail.getRoles()));
    }

    private Collection<? extends GrantedAuthority> authorities(Set<AccountRole> roles) {
        return roles.stream()
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r.name()))
                    .collect(Collectors.toSet());
    }


}
