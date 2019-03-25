package io.parkey19.account;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by parkey19 on 2019. 3. 25..
 */
@Entity
@Getter @Setter
@EqualsAndHashCode(of = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id @GeneratedValue
    private Integer id;
    private String email;
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<AccountRole> roles;
}
