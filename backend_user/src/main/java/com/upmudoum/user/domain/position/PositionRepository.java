package com.upmudoum.user.domain.position;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository extends JpaRepository<Position, Long> {

    List<Position> findByComCdOrderBySortSeqAscPositionIdAsc(String comCd);

    List<Position> findByComCdAndEnabledOrderBySortSeqAscPositionIdAsc(String comCd, boolean enabled);

    Optional<Position> findByComCdAndPositionId(String comCd, String positionId);

    void deleteByComCdAndPositionId(String comCd, String positionId);
}
