package com.upmudoum.trade.domain.account.repository;

import com.upmudoum.trade.domain.account.entity.AccountSnapshot;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountSnapshotRepository extends JpaRepository<AccountSnapshot, Long> {

    Optional<AccountSnapshot> findTopByAccountNoOrderByCapturedAtDesc(String accountNo);
}
