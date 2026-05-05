package com.upmudoum.user.domain.menu;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuPermissionRepository extends JpaRepository<MenuPermission, Long> {

    List<MenuPermission> findByComCdAndServiceIdAndGroupIdIn(String comCd, String serviceId, List<String> groupIds);

    List<MenuPermission> findByComCdAndServiceIdAndGroupId(String comCd, String serviceId, String groupId);

    Optional<MenuPermission> findByComCdAndServiceIdAndGroupIdAndMenuId(String comCd, String serviceId, String groupId, String menuId);

    void deleteByComCdAndServiceIdAndGroupIdAndMenuId(String comCd, String serviceId, String groupId, String menuId);
}
