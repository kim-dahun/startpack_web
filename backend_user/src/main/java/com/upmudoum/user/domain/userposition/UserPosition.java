package com.upmudoum.user.domain.userposition;

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
        name = "user_positions",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_positions_com_mapping", columnNames = {"com_cd", "user_position_id"}),
                @UniqueConstraint(name = "uk_user_positions_com_user_dept_position", columnNames = {"com_cd", "user_id", "department_id", "position_id"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class UserPosition extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_position_id", nullable = false, length = 160)
    private String userPositionId;

    @Column(name = "com_cd", nullable = false, length = 30)
    private String comCd;

    @Column(name = "user_id", nullable = false, length = 80)
    private String userId;

    @Column(name = "position_id", nullable = false, length = 80)
    private String positionId;

    @Column(name = "department_id", nullable = false, length = 80)
    private String departmentId;

    @Column(name = "primary_yn", nullable = false)
    private boolean primaryYn;

    @Column(nullable = false)
    private boolean enabled = true;

    public UserPosition(String userPositionId, String comCd, String userId, String departmentId, String positionId) {
        this.userPositionId = userPositionId;
        this.comCd = comCd;
        this.userId = userId;
        this.departmentId = departmentId;
        this.positionId = positionId;
    }

    public void updateMapping(String userPositionId, String userId, String departmentId, String positionId, boolean enabled) {
        this.userPositionId = userPositionId;
        this.userId = userId;
        this.departmentId = departmentId;
        this.positionId = positionId;
        this.enabled = enabled;
    }

    public void updatePrimaryYn(boolean primaryYn) {
        this.primaryYn = primaryYn;
    }
}
