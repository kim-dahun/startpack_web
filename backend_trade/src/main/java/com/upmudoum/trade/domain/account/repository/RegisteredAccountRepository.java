package com.upmudoum.trade.domain.account.repository;

import com.upmudoum.trade.domain.account.entity.RegisteredAccount;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegisteredAccountRepository extends JpaRepository<RegisteredAccount, Long> {

    List<RegisteredAccount> findByActiveTrueOrderByCreatedAtDesc();

    Optional<RegisteredAccount> findByAccountNo(String accountNo);
}
