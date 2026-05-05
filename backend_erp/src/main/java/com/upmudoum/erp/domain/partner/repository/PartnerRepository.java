package com.upmudoum.erp.domain.partner.repository;

import com.upmudoum.erp.domain.partner.entity.Partner;
import com.upmudoum.erp.domain.partner.vo.PartnerStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerRepository extends JpaRepository<Partner, Long> {

    boolean existsByCodeValue(String code);

    List<Partner> findByPartnerTypeAndStatusOrderByCodeValueAsc(String partnerType, PartnerStatus status);

    List<Partner> findByPartnerTypeOrderByCodeValueAsc(String partnerType);

    List<Partner> findByStatusOrderByCodeValueAsc(PartnerStatus status);
}
