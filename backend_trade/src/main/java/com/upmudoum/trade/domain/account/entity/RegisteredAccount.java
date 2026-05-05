package com.upmudoum.trade.domain.account.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "registered_account", uniqueConstraints = {
        @UniqueConstraint(name = "uk_registered_account_no", columnNames = "account_no")
})
public class RegisteredAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_no", nullable = false, length = 30)
    private String accountNo;

    @Column(nullable = false, length = 100)
    private String accountName;

    @Column(nullable = false, length = 10)
    private String productCode;

    @Column(length = 100)
    private String aliasName;

    @Column(length = 1000)
    private String memo;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;
}
