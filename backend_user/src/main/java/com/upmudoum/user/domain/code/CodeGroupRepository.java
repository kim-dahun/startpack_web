package com.upmudoum.user.domain.code;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodeGroupRepository extends JpaRepository<CodeGroup, Long> {

    List<CodeGroup> findByComCdAndServiceIdOrderByCodeGroupIdAsc(String comCd, String serviceId);

    Optional<CodeGroup> findByComCdAndServiceIdAndCodeGroupId(String comCd, String serviceId, String codeGroupId);

    void deleteByComCdAndServiceIdAndCodeGroupId(String comCd, String serviceId, String codeGroupId);
}
