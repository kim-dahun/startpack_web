package com.upmudoum.trade.domain.kis.repository;

import com.upmudoum.trade.domain.kis.entity.KisOpenApiToken;
import com.upmudoum.trade.domain.kis.vo.KisTradeMode;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KisOpenApiTokenRepository extends JpaRepository<KisOpenApiToken, Long> {

    Optional<KisOpenApiToken> findByTradeMode(KisTradeMode tradeMode);
}
