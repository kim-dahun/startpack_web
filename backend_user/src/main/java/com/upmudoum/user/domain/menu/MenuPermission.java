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
        name = "menu_permissions",
        uniqueConstraints = @UniqueConstraint(name = "uk_menu_permissions", columnNames = {"com_cd", "service_id", "group_id", "menu_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class MenuPermission extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "com_cd", nullable = false, length = 30)
    private String comCd;

    @Column(name = "service_id", nullable = false, length = 30)
    private String serviceId;

    @Column(name = "group_id", nullable = false, length = 50)
    private String groupId;

    @Column(name = "menu_id", nullable = false, length = 80)
    private String menuId;

    @Column(nullable = false)
    private boolean readable;

    @Column(nullable = false)
    private boolean writable;

    @Column(nullable = false)
    private boolean deletable;

    @Column(name = "excel_downable", nullable = false)
    private boolean excelDownable;

    public MenuPermission(String comCd, String serviceId, String groupId, String menuId) {
        this.comCd = comCd;
        this.serviceId = serviceId;
        this.groupId = groupId;
        this.menuId = menuId;
    }

    public void update(boolean readable, boolean writable, boolean deletable, boolean excelDownable) {
        this.readable = readable;
        this.writable = writable;
        this.deletable = deletable;
        this.excelDownable = excelDownable;
    }
}
