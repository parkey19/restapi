package io.parkey19.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Account byEmail = accountRepository.findByEmail(userName).orElseThrow(() -> new UsernameNotFoundException(userName));
        new User(byEmail.getEmail(), byEmail.getPassword(), authorities(byEmail.getRoles()));
        return null;
    }

    private Collection<? extends GrantedAuthority> authorities(Set<AccountRole> roles) {
        return roles.stream()
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r.name()))
                    .collect(Collectors.toSet());
    }


}
