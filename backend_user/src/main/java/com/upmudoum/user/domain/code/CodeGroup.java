package com.upmudoum.user.domain.code;

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
        name = "code_groups",
        uniqueConstraints = @UniqueConstraint(name = "uk_code_groups_com_service_group", columnNames = {"com_cd", "service_id", "code_group_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class CodeGroup extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "com_cd", nullable = false, length = 30)
    private String comCd;

    @Column(name = "service_id", nullable = false, length = 30)
    private String serviceId;

    @Column(name = "code_group_id", nullable = false, length = 80)
    private String codeGroupId;

    @Column(name = "code_group_name", nullable = false, length = 150)
    private String codeGroupName;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private boolean enabled = true;

    public CodeGroup(String comCd, String serviceId, String codeGroupId, String codeGroupName) {
        this.comCd = comCd;
        this.serviceId = serviceId;
        this.codeGroupId = codeGroupId;
        this.codeGroupName = codeGroupName;
    }

    public void update(String codeGroupName, String description, boolean enabled) {
        this.codeGroupName = codeGroupName;
        this.description = description;
        this.enabled = enabled;
    }
}
