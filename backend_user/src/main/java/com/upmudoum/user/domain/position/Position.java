package com.upmudoum.user.domain.position;

import com.upmudoum.user.domain.common.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
        name = "positions",
        uniqueConstraints = @UniqueConstraint(name = "uk_positions_com_position", columnNames = {"com_cd", "position_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class Position extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "com_cd", nullable = false, length = 30)
    private String comCd;

    @Column(name = "position_id", nullable = false, length = 80)
    private String positionId;

    @Column(name = "position_name", nullable = false, length = 150)
    private String positionName;

    @Enumerated(EnumType.STRING)
    @Column(name = "position_type", nullable = false, length = 30)
    private PositionType positionType = PositionType.CUSTOM;

    @Column(nullable = false)
    private int sortSeq;

    @Column(nullable = false)
    private boolean enabled = true;

    public Position(String comCd, String positionId, String positionName) {
        this.comCd = comCd;
        this.positionId = positionId;
        this.positionName = positionName;
    }

    public void update(String positionName, PositionType positionType, int sortSeq, boolean enabled) {
        this.positionName = positionName;
        this.positionType = positionType == null ? PositionType.CUSTOM : positionType;
        this.sortSeq = sortSeq;
        this.enabled = enabled;
    }
}
