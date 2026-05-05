package com.upmudoum.erp.domain.process.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "erp_processes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErpProcess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 30)
    private String processType;

    @Column(length = 200)
    private String description;

    @Column(nullable = false)
    private boolean enabled = true;

    public ErpProcess(String code, String name, String processType, String description) {
        this.code = code;
        this.name = name;
        this.processType = processType;
        this.description = description;
    }

    public void update(String name, String processType, String description, boolean enabled) {
        this.name = name;
        this.processType = processType;
        this.description = description;
        this.enabled = enabled;
    }
}
