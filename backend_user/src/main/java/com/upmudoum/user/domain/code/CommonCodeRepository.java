package com.upmudoum.user.domain.code;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommonCodeRepository extends JpaRepository<CommonCode, Long> {

    List<CommonCode> findByComCdAndServiceIdAndCodeGroupIdAndEnabledOrderBySortOrderAsc(String comCd, String serviceId, String codeGroupId, boolean enabled);

    List<CommonCode> findByComCdAndServiceIdAndCodeGroupIdOrderBySortOrderAsc(String comCd, String serviceId, String codeGroupId);

    Optional<CommonCode> findByComCdAndServiceIdAndCodeGroupIdAndCodeId(String comCd, String serviceId, String codeGroupId, String codeId);

    void deleteByComCdAndServiceIdAndCodeGroupIdAndCodeId(String comCd, String serviceId, String codeGroupId, String codeId);
}
