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
        name = "common_codes",
        uniqueConstraints = @UniqueConstraint(name = "uk_common_codes", columnNames = {"com_cd", "service_id", "code_group_id", "code_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class CommonCode extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "com_cd", nullable = false, length = 30)
    private String comCd;

    @Column(name = "service_id", nullable = false, length = 30)
    private String serviceId;

    @Column(name = "code_group_id", nullable = false, length = 80)
    private String codeGroupId;

    @Column(name = "code_id", nullable = false, length = 80)
    private String codeId;

    @Column(name = "parent_code_id", length = 80)
    private String parentCodeId;

    @Column(name = "code_name", nullable = false, length = 150)
    private String codeName;

    @Column(name = "parent_code_group_id", length = 80)
    private String parentCodeGroupId;

    @Column(name = "sub_info1", length = 255)
    private String subInfo1;

    @Column(name = "sub_info2", length = 255)
    private String subInfo2;

    @Column(name = "sub_info3", length = 255)
    private String subInfo3;

    @Column(nullable = false)
    private int sortOrder;

    @Column(nullable = false)
    private boolean enabled = true;

    public CommonCode(String comCd, String serviceId, String codeGroupId, String codeId, String codeName, int sortOrder) {
        this.comCd = comCd;
        this.serviceId = serviceId;
        this.codeGroupId = codeGroupId;
        this.codeId = codeId;
        this.codeName = codeName;
        this.sortOrder = sortOrder;
    }

    public void update(String parentCodeGroupId, String parentCodeId, String codeName, String subInfo1, String subInfo2, String subInfo3, int sortOrder, boolean enabled) {
        this.parentCodeGroupId = parentCodeGroupId;
        this.parentCodeId = parentCodeId;
        this.codeName = codeName;
        this.subInfo1 = subInfo1;
        this.subInfo2 = subInfo2;
        this.subInfo3 = subInfo3;
        this.sortOrder = sortOrder;
        this.enabled = enabled;
    }
}
