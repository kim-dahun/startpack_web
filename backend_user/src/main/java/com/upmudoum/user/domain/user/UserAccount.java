package com.upmudoum.user.domain.user;

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
import java.time.Instant;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(name = "uk_users_com_user", columnNames = {"com_cd", "user_id"})
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserAccount extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "com_cd", nullable = false, length = 30)
    private String comCd;

    @Column(name = "user_id", nullable = false, length = 80)
    private String userId;

    @Column(name = "user_name", nullable = false, length = 100)
    private String userName;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(length = 150)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(length = 255)
    private String address;

    @Column(name = "job_grade_id", length = 80)
    private String jobGradeId;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    public UserAccount(String comCd, String userId, String userName, String passwordHash) {
        this.comCd = comCd;
        this.userId = userId;
        this.userName = userName;
        this.passwordHash = passwordHash;
    }

    public void updateProfile(String userName, String email, String phone, String address, UserStatus status) {
        this.userName = userName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.status = status == null ? UserStatus.ACTIVE : status;
    }

    public void updateJobGrade(String jobGradeId) {
        this.jobGradeId = jobGradeId;
    }

    public void changePasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void markLoggedIn() {
        this.lastLoginAt = Instant.now();
    }
}
