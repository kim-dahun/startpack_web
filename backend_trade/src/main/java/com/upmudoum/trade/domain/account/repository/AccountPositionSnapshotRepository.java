package com.upmudoum.trade.domain.account.repository;

import com.upmudoum.trade.domain.account.entity.AccountPositionSnapshot;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountPositionSnapshotRepository extends JpaRepository<AccountPositionSnapshot, Long> {

    List<AccountPositionSnapshot> findTop100ByAccountNoOrderByCapturedAtDesc(String accountNo);

    Optional<AccountPositionSnapshot> findTopByAccountNoAndItemCodeOrderByCapturedAtDesc(String accountNo, String itemCode);
}
