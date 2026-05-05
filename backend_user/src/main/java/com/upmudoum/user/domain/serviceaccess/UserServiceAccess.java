package com.upmudoum.user.domain.serviceaccess;

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
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "user_service_accesses",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_service_accesses", columnNames = {"com_cd", "user_id", "service_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserServiceAccess extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "com_cd", nullable = false, length = 30)
    private String comCd;

    @Column(name = "user_id", nullable = false, length = 80)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_id", nullable = false, length = 30)
    private ServiceId serviceId;

    @Column(nullable = false)
    private boolean accessible;

    public UserServiceAccess(String comCd, String userId, ServiceId serviceId, boolean accessible) {
        this.comCd = comCd;
        this.userId = userId;
        this.serviceId = serviceId;
        this.accessible = accessible;
    }

    public void update(boolean accessible) {
        this.accessible = accessible;
    }
}
