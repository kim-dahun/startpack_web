package com.upmudoum.user.domain.userposition;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPositionRepository extends JpaRepository<UserPosition, Long> {

    List<UserPosition> findByComCdAndUserIdOrderByPrimaryYnDescPositionIdAsc(String comCd, String userId);

    List<UserPosition> findByComCdAndDepartmentIdOrderByPositionIdAscUserIdAsc(String comCd, String departmentId);

    List<UserPosition> findByComCdAndDepartmentIdAndEnabledOrderByPositionIdAscUserIdAsc(String comCd, String departmentId, boolean enabled);

    List<UserPosition> findByComCdAndDepartmentIdAndPositionIdAndEnabledOrderByPrimaryYnDescUserIdAsc(String comCd, String departmentId, String positionId, boolean enabled);

    Optional<UserPosition> findByComCdAndUserPositionId(String comCd, String userPositionId);

    Optional<UserPosition> findByComCdAndUserIdAndDepartmentIdAndPositionId(String comCd, String userId, String departmentId, String positionId);

    void deleteByComCdAndUserPositionId(String comCd, String userPositionId);

    void deleteByComCdAndUserIdAndDepartmentIdAndPositionId(String comCd, String userId, String departmentId, String positionId);

    void deleteByComCdAndDepartmentId(String comCd, String departmentId);
}
