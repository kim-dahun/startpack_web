package com.upmudoum.erp.domain.batch.entity;

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
@Table(name = "erp_batch_definitions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErpBatchDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 200)
    private String description;

    @Column(nullable = false, length = 50)
    private String triggerPolicy;

    @Column(nullable = false)
    private boolean required;

    public ErpBatchDefinition(String code, String description, String triggerPolicy, boolean required) {
        this.code = code;
        this.description = description;
        this.triggerPolicy = triggerPolicy;
        this.required = required;
    }

    public void update(String description, String triggerPolicy, boolean required) {
        this.description = description;
        this.triggerPolicy = triggerPolicy;
        this.required = required;
    }
}
