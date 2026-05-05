package com.upmudoum.user.domain.menu;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    List<Menu> findByComCdAndServiceIdAndEnabledOrderByDepthAscSortOrderAsc(String comCd, String serviceId, boolean enabled);

    List<Menu> findByComCdAndServiceIdOrderByDepthAscSortOrderAsc(String comCd, String serviceId);

    Optional<Menu> findByComCdAndServiceIdAndMenuId(String comCd, String serviceId, String menuId);

    void deleteByComCdAndServiceIdAndMenuId(String comCd, String serviceId, String menuId);
}
