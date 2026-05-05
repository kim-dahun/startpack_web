package com.upmudoum.user.domain.serviceaccess;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserServiceAccessRepository extends JpaRepository<UserServiceAccess, Long> {

    List<UserServiceAccess> findByComCdAndUserIdOrderByServiceIdAsc(String comCd, String userId);

    List<UserServiceAccess> findByComCdAndUserIdAndAccessibleOrderByServiceIdAsc(String comCd, String userId, boolean accessible);

    Optional<UserServiceAccess> findByComCdAndUserIdAndServiceId(String comCd, String userId, ServiceId serviceId);

    void deleteByComCdAndUserIdAndServiceId(String comCd, String userId, ServiceId serviceId);
}
