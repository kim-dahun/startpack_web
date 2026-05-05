package com.upmudoum.user.domain.group;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {

    boolean existsByComCdAndServiceIdAndGroupId(String comCd, String serviceId, String groupId);

    Optional<UserGroup> findByComCdAndServiceIdAndGroupId(String comCd, String serviceId, String groupId);

    List<UserGroup> findByComCdAndServiceIdOrderByGroupIdAsc(String comCd, String serviceId);

    void deleteByComCdAndServiceIdAndGroupId(String comCd, String serviceId, String groupId);
}
