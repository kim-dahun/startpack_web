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
        name = "user_group_members",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_group_members", columnNames = {"com_cd", "service_id", "group_id", "user_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class UserGroupMember extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "com_cd", nullable = false, length = 30)
    private String comCd;

    @Column(name = "service_id", nullable = false, length = 30)
    private String serviceId;

    @Column(name = "group_id", nullable = false, length = 50)
    private String groupId;

    @Column(name = "user_id", nullable = false, length = 80)
    private String userId;

    public UserGroupMember(String comCd, String serviceId, String groupId, String userId) {
        this.comCd = comCd;
        this.serviceId = serviceId;
        this.groupId = groupId;
        this.userId = userId;
    }

}
