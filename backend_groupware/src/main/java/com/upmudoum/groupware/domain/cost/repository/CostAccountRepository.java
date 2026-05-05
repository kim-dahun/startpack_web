package com.upmudoum.groupware.domain.cost.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.upmudoum.groupware.domain.cost.entity.CostAccount;

public interface CostAccountRepository extends JpaRepository<CostAccount, UUID> {

    List<CostAccount> findByComCdAndEnabledTrueAndDeletedYnFalseOrderByAccountNameAsc(String comCd);

    Optional<CostAccount> findByIdAndComCdAndDeletedYnFalse(UUID id, String comCd);
}
