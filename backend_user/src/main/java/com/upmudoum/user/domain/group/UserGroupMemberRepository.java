package com.upmudoum.user.domain.group;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGroupMemberRepository extends JpaRepository<UserGroupMember, Long> {

    List<UserGroupMember> findByComCdAndServiceIdAndUserId(String comCd, String serviceId, String userId);

    List<UserGroupMember> findByComCdAndServiceIdAndGroupId(String comCd, String serviceId, String groupId);

    void deleteByComCdAndServiceIdAndGroupIdAndUserId(String comCd, String serviceId, String groupId, String userId);
}
