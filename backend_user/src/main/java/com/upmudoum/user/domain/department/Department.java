package com.upmudoum.user.domain.department;

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
        name = "departments",
        uniqueConstraints = @UniqueConstraint(name = "uk_departments_com_department", columnNames = {"com_cd", "department_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class Department extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "com_cd", nullable = false, length = 30)
    private String comCd;

    @Column(name = "department_id", nullable = false, length = 80)
    private String departmentId;

    @Column(name = "department_name", nullable = false, length = 150)
    private String departmentName;

    @Column(name = "parent_department_id", length = 80)
    private String parentDepartmentId;

    @Column(name = "department_head_user_id", length = 80)
    private String departmentHeadUserId;

    @Column(name = "department_head_position_id", length = 80)
    private String departmentHeadPositionId;

    @Column(nullable = false)
    private int sortSeq;

    @Column(nullable = false)
    private boolean enabled = true;

    public Department(String comCd, String departmentId, String departmentName) {
        this.comCd = comCd;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
    }

    public void update(String parentDepartmentId, String departmentName, String departmentHeadUserId, String departmentHeadPositionId, int sortSeq, boolean enabled) {
        this.parentDepartmentId = parentDepartmentId;
        this.departmentName = departmentName;
        this.departmentHeadUserId = departmentHeadUserId;
        this.departmentHeadPositionId = departmentHeadPositionId;
        this.sortSeq = sortSeq;
        this.enabled = enabled;
    }
}
