package com.upmudoum.user.domain.menu.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.upmudoum.user.domain.menu.Menu;
import com.upmudoum.user.domain.menu.MenuPermission;
import com.upmudoum.user.domain.menu.MenuPermissionRepository;
import com.upmudoum.user.domain.menu.MenuRepository;
import com.upmudoum.user.domain.menu.dto.MenuDtos.MenuPermissionResponse;
import com.upmudoum.user.domain.menu.dto.MenuDtos.MenuPermissionTreeResponse;
import com.upmudoum.user.domain.menu.dto.MenuDtos.MenuResponse;
import com.upmudoum.user.domain.menu.dto.MenuDtos.MenuTreeResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MenuManagementServiceTest {

    private MenuRepository menuRepository;
    private MenuPermissionRepository permissionRepository;
    private MenuManagementService menuManagementService;

    @BeforeEach
    void setUp() {
        menuRepository = mock(MenuRepository.class);
        permissionRepository = mock(MenuPermissionRepository.class);
        menuManagementService = new MenuManagementService(menuRepository, permissionRepository);
    }

    @Test
    void menuTreeBuildsNestedThreeDepthStructureByServiceId() {
        when(menuRepository.findByComCdAndServiceIdOrderByDepthAscSortOrderAsc("COM001", "ERP"))
                .thenReturn(List.of(rootMenu(), childMenu(), grandChildMenu()));

        List<MenuTreeResponse> result = menuManagementService.menuTree("COM001", "ERP");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getServiceId()).isEqualTo("ERP");
        assertThat(result.get(0).getMenuId()).isEqualTo("ROOT");
        assertThat(result.get(0).getChildren().get(0).getMenuId()).isEqualTo("CHILD");
        assertThat(result.get(0).getChildren().get(0).getChildren().get(0).getMenuId()).isEqualTo("GRAND");
    }

    @Test
    void menuFlatListKeepsRepositoryOrderByServiceId() {
        when(menuRepository.findByComCdAndServiceIdOrderByDepthAscSortOrderAsc("COM001", "ERP"))
                .thenReturn(List.of(rootMenu(), childMenu(), grandChildMenu()));

        List<MenuResponse> result = menuManagementService.menus("COM001", "ERP");

        assertThat(result).extracting(MenuResponse::getMenuId).containsExactly("ROOT", "CHILD", "GRAND");
        assertThat(result).extracting(MenuResponse::getServiceId).containsOnly("ERP");
    }

    @Test
    void permissionTreeMergesMenuHierarchyWithGroupPermissionsByServiceId() {
        when(menuRepository.findByComCdAndServiceIdOrderByDepthAscSortOrderAsc("COM001", "ERP"))
                .thenReturn(List.of(rootMenu(), childMenu(), grandChildMenu()));
        when(permissionRepository.findByComCdAndServiceIdAndGroupId("COM001", "ERP", "ADMIN"))
                .thenReturn(List.of(readablePermission("ROOT"), writablePermission("GRAND")));

        List<MenuPermissionTreeResponse> result = menuManagementService.permissionTree("COM001", "ERP", "ADMIN");

        MenuPermissionTreeResponse root = result.get(0);
        MenuPermissionTreeResponse child = root.getChildren().get(0);
        MenuPermissionTreeResponse grand = child.getChildren().get(0);
        assertThat(root.isPermitRead()).isTrue();
        assertThat(child.isPermitRead()).isFalse();
        assertThat(grand.isPermitWrite()).isTrue();
    }

    @Test
    void permissionFlatListReturnsRepositoryPermissionsByServiceId() {
        when(permissionRepository.findByComCdAndServiceIdAndGroupId("COM001", "ERP", "ADMIN"))
                .thenReturn(List.of(readablePermission("ROOT"), writablePermission("GRAND")));

        List<MenuPermissionResponse> result = menuManagementService.permissions("COM001", "ERP", "ADMIN");

        assertThat(result).extracting(MenuPermissionResponse::getMenuId).containsExactly("ROOT", "GRAND");
        assertThat(result).extracting(MenuPermissionResponse::getServiceId).containsOnly("ERP");
    }

    private Menu rootMenu() {
        Menu menu = new Menu("COM001", "ERP", "ROOT", "Root", 1, 1);
        menu.update(null, "Root", "menu.root", "/root", "pi pi-home", 1, 1, true);
        return menu;
    }

    private Menu childMenu() {
        Menu menu = new Menu("COM001", "ERP", "CHILD", "Child", 2, 1);
        menu.update("ROOT", "Child", "menu.child", "/child", "pi pi-folder", 2, 1, true);
        return menu;
    }

    private Menu grandChildMenu() {
        Menu menu = new Menu("COM001", "ERP", "GRAND", "Grand", 3, 1);
        menu.update("CHILD", "Grand", "menu.grand", "/grand", "pi pi-file", 3, 1, true);
        return menu;
    }

    private MenuPermission readablePermission(String menuId) {
        MenuPermission permission = new MenuPermission("COM001", "ERP", "ADMIN", menuId);
        permission.update(true, false, false, false);
        return permission;
    }

    private MenuPermission writablePermission(String menuId) {
        MenuPermission permission = new MenuPermission("COM001", "ERP", "ADMIN", menuId);
        permission.update(false, true, false, false);
        return permission;
    }
}
