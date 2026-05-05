package com.upmudoum.user.domain.group;

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
        name = "user_groups",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_groups_com_service_group", columnNames = {"com_cd", "service_id", "group_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class UserGroup extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "com_cd", nullable = false, length = 30)
    private String comCd;

    @Column(name = "service_id", nullable = false, length = 30)
    private String serviceId;

    @Column(name = "group_id", nullable = false, length = 50)
    private String groupId;

    @Column(name = "group_name", nullable = false, length = 100)
    private String groupName;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private boolean enabled = true;

    public UserGroup(String comCd, String serviceId, String groupId, String groupName) {
        this.comCd = comCd;
        this.serviceId = serviceId;
        this.groupId = groupId;
        this.groupName = groupName;
    }

    public void update(String groupName, String description, boolean enabled) {
        this.groupName = groupName;
        this.description = description;
        this.enabled = enabled;
    }
}
