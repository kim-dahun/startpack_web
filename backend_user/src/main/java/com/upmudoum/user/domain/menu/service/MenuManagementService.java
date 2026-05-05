package com.upmudoum.user.domain.menu.service;

import com.upmudoum.user.common.dto.BulkRequestDto;
import com.upmudoum.user.common.dto.BulkResultDto;
import com.upmudoum.user.common.exception.BusinessException;
import com.upmudoum.user.common.exception.ErrorCode;
import com.upmudoum.user.domain.menu.Menu;
import com.upmudoum.user.domain.menu.MenuPermission;
import com.upmudoum.user.domain.menu.MenuPermissionRepository;
import com.upmudoum.user.domain.menu.MenuRepository;
import com.upmudoum.user.domain.menu.dto.MenuDtos.MenuPermissionRequest;
import com.upmudoum.user.domain.menu.dto.MenuDtos.MenuPermissionResponse;
import com.upmudoum.user.domain.menu.dto.MenuDtos.MenuPermissionTreeResponse;
import com.upmudoum.user.domain.menu.dto.MenuDtos.MenuRequest;
import com.upmudoum.user.domain.menu.dto.MenuDtos.MenuResponse;
import com.upmudoum.user.domain.menu.dto.MenuDtos.MenuTreeResponse;
import com.upmudoum.user.domain.serviceaccess.ServiceId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MenuManagementService {

    private final MenuRepository menuRepository;
    private final MenuPermissionRepository permissionRepository;

    public MenuManagementService(MenuRepository menuRepository, MenuPermissionRepository permissionRepository) {
        this.menuRepository = menuRepository;
        this.permissionRepository = permissionRepository;
    }

    @Transactional(readOnly = true)
    public List<MenuResponse> menus(String comCd, String serviceId) {
        return menuRepository.findByComCdAndServiceIdOrderByDepthAscSortOrderAsc(comCd, normalizeServiceId(serviceId)).stream().map(this::toMenu).toList();
    }

    @Transactional(readOnly = true)
    public List<MenuTreeResponse> menuTree(String comCd, String serviceId) {
        List<Menu> menus = menuRepository.findByComCdAndServiceIdOrderByDepthAscSortOrderAsc(comCd, normalizeServiceId(serviceId));
        Map<String, List<Menu>> childrenByParent = new LinkedHashMap<>();
        for (Menu menu : menus) {
            childrenByParent.computeIfAbsent(normalizeParentKey(menu.getParentMenuId()), ignored -> new ArrayList<>()).add(menu);
        }
        return childrenByParent.getOrDefault("", List.of()).stream()
                .map(menu -> toMenuTree(menu, childrenByParent, new HashSet<>()))
                .toList();
    }

    @Transactional
    public BulkResultDto saveMenus(BulkRequestDto<MenuRequest> request) {
        request.getAdded().forEach(item -> {
            Menu menu = new Menu(item.getComCd(), normalizeServiceId(item.getServiceId()), item.getMenuId(), item.getMenuName(), item.getMenuLevel(), item.getSortSeq());
            menu.update(item.getMenuParentId(), item.getMenuName(), item.getI18nCode(), item.getMenuUrl(), item.getIcon(), item.getMenuLevel(), item.getSortSeq(), item.isEnabled());
            menuRepository.save(menu);
        });
        request.getUpdated().forEach(item -> menuRepository.findByComCdAndServiceIdAndMenuId(item.getComCd(), normalizeServiceId(item.getServiceId()), item.getMenuId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Menu was not found."))
                .update(item.getMenuParentId(), item.getMenuName(), item.getI18nCode(), item.getMenuUrl(), item.getIcon(), item.getMenuLevel(), item.getSortSeq(), item.isEnabled()));
        request.getDeleted().forEach(item -> menuRepository.deleteByComCdAndServiceIdAndMenuId(item.getComCd(), normalizeServiceId(item.getServiceId()), item.getMenuId()));
        return new BulkResultDto(request.getAdded().size(), request.getUpdated().size(), request.getDeleted().size());
    }

    @Transactional(readOnly = true)
    public List<MenuPermissionResponse> permissions(String comCd, String serviceId, String groupId) {
        return permissionRepository.findByComCdAndServiceIdAndGroupId(comCd, normalizeServiceId(serviceId), groupId).stream().map(this::toPermission).toList();
    }

    @Transactional(readOnly = true)
    public List<MenuPermissionTreeResponse> permissionTree(String comCd, String serviceId, String groupId) {
        String normalizedServiceId = normalizeServiceId(serviceId);
        List<Menu> menus = menuRepository.findByComCdAndServiceIdOrderByDepthAscSortOrderAsc(comCd, normalizedServiceId);
        Map<String, MenuPermission> permissionByMenu = new LinkedHashMap<>();
        for (MenuPermission permission : permissionRepository.findByComCdAndServiceIdAndGroupId(comCd, normalizedServiceId, groupId)) {
            permissionByMenu.put(permission.getMenuId(), permission);
        }
        Map<String, List<Menu>> childrenByParent = new LinkedHashMap<>();
        for (Menu menu : menus) {
            childrenByParent.computeIfAbsent(normalizeParentKey(menu.getParentMenuId()), ignored -> new ArrayList<>()).add(menu);
        }
        return childrenByParent.getOrDefault("", List.of()).stream()
                .map(menu -> toPermissionTree(menu, groupId, permissionByMenu, childrenByParent, new HashSet<>()))
                .toList();
    }

    @Transactional
    public BulkResultDto savePermissions(BulkRequestDto<MenuPermissionRequest> request) {
        request.getAdded().forEach(item -> {
            MenuPermission permission = new MenuPermission(item.getComCd(), normalizeServiceId(item.getServiceId()), item.getGroupId(), item.getMenuId());
            permission.update(item.isPermitRead(), item.isPermitWrite(), item.isPermitDelete(), item.isPermitExcel());
            permissionRepository.save(permission);
        });
        request.getUpdated().forEach(item -> permissionRepository.findByComCdAndServiceIdAndGroupIdAndMenuId(item.getComCd(), normalizeServiceId(item.getServiceId()), item.getGroupId(), item.getMenuId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "MenuPermission was not found."))
                .update(item.isPermitRead(), item.isPermitWrite(), item.isPermitDelete(), item.isPermitExcel()));
        request.getDeleted().forEach(item -> permissionRepository.deleteByComCdAndServiceIdAndGroupIdAndMenuId(item.getComCd(), normalizeServiceId(item.getServiceId()), item.getGroupId(), item.getMenuId()));
        return new BulkResultDto(request.getAdded().size(), request.getUpdated().size(), request.getDeleted().size());
    }

    private MenuResponse toMenu(Menu menu) {
        return new MenuResponse(menu.getComCd(), menu.getServiceId(), menu.getMenuId(), menu.getParentMenuId(), menu.getMenuName(), menu.getPath(), menu.getI18nCode(), menu.getIcon(), menu.getDepth(), menu.getSortOrder(), menu.isEnabled());
    }

    private MenuTreeResponse toMenuTree(Menu menu, Map<String, List<Menu>> childrenByParent, Set<String> visited) {
        if (!visited.add(menu.getMenuId())) {
            return new MenuTreeResponse(menu.getComCd(), menu.getServiceId(), menu.getMenuId(), menu.getParentMenuId(), menu.getMenuName(), menu.getPath(), menu.getI18nCode(), menu.getIcon(), menu.getDepth(), menu.getSortOrder(), menu.isEnabled(), List.of());
        }
        List<MenuTreeResponse> children = childrenByParent.getOrDefault(menu.getMenuId(), List.of()).stream()
                .map(child -> toMenuTree(child, childrenByParent, new HashSet<>(visited)))
                .toList();
        return new MenuTreeResponse(menu.getComCd(), menu.getServiceId(), menu.getMenuId(), menu.getParentMenuId(), menu.getMenuName(), menu.getPath(), menu.getI18nCode(), menu.getIcon(), menu.getDepth(), menu.getSortOrder(), menu.isEnabled(), children);
    }

    private MenuPermissionResponse toPermission(MenuPermission permission) {
        return new MenuPermissionResponse(permission.getComCd(), permission.getServiceId(), permission.getGroupId(), permission.getMenuId(), permission.isReadable(), permission.isWritable(), permission.isDeletable(), permission.isExcelDownable());
    }

    private MenuPermissionTreeResponse toPermissionTree(Menu menu, String groupId, Map<String, MenuPermission> permissionByMenu, Map<String, List<Menu>> childrenByParent, Set<String> visited) {
        if (!visited.add(menu.getMenuId())) {
            return new MenuPermissionTreeResponse(menu.getComCd(), menu.getServiceId(), groupId, menu.getMenuId(), menu.getParentMenuId(), menu.getMenuName(), false, false, false, false, List.of());
        }
        MenuPermission permission = permissionByMenu.get(menu.getMenuId());
        List<MenuPermissionTreeResponse> children = childrenByParent.getOrDefault(menu.getMenuId(), List.of()).stream()
                .map(child -> toPermissionTree(child, groupId, permissionByMenu, childrenByParent, new HashSet<>(visited)))
                .toList();
        return new MenuPermissionTreeResponse(
                menu.getComCd(),
                menu.getServiceId(),
                groupId,
                menu.getMenuId(),
                menu.getParentMenuId(),
                menu.getMenuName(),
                permission != null && permission.isReadable(),
                permission != null && permission.isWritable(),
                permission != null && permission.isDeletable(),
                permission != null && permission.isExcelDownable(),
                children
        );
    }

    private String normalizeParentKey(String parentMenuId) {
        return parentMenuId == null || parentMenuId.isBlank() ? "" : parentMenuId;
    }

    private String normalizeServiceId(String value) {
        try {
            return ServiceId.from(value).name();
        } catch (RuntimeException ex) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "Unsupported serviceId.");
        }
    }
}
