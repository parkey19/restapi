package io.parkey19.account;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by parkey19 on 2019. 3. 26..
 */
public interface AccountRepository extends JpaRepository<Account, Long> {
}
