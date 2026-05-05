package com.upmudoum.user.domain.menu;

import com.upmudoum.user.domain.common.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "menus",
        uniqueConstraints = @UniqueConstraint(name = "uk_menus_com_service_menu", columnNames = {"com_cd", "service_id", "menu_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class Menu extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "com_cd", nullable = false, length = 30)
    private String comCd;

    @Column(name = "service_id", nullable = false, length = 30)
    private String serviceId;

    @Column(name = "menu_id", nullable = false, length = 80)
    private String menuId;

    @Column(name = "parent_menu_id", length = 80)
    private String parentMenuId;

    @Column(name = "menu_name", nullable = false, length = 150)
    private String menuName;

    @Column(name = "i18n_code", length = 100)
    private String i18nCode;

    @Column(length = 255)
    private String path;

    @Column(nullable = false, length = 80)
    private String icon = "pi pi-circle";

    @Column(nullable = false)
    private int depth;

    @Column(nullable = false)
    private int sortOrder;

    @Column(nullable = false)
    private boolean enabled = true;

    public Menu(String comCd, String serviceId, String menuId, String menuName, int depth, int sortOrder) {
        this.comCd = comCd;
        this.serviceId = serviceId;
        this.menuId = menuId;
        this.menuName = menuName;
        this.depth = depth;
        this.sortOrder = sortOrder;
    }

    public void update(String parentMenuId, String menuName, String i18nCode, String path, String icon, int depth, int sortOrder, boolean enabled) {
        this.parentMenuId = parentMenuId;
        this.menuName = menuName;
        this.i18nCode = i18nCode;
        this.path = path;
        this.icon = icon == null || icon.isBlank() ? "pi pi-circle" : icon;
        this.depth = depth;
        this.sortOrder = sortOrder;
        this.enabled = enabled;
    }
}
