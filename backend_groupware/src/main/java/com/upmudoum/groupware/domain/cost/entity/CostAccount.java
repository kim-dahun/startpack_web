package com.upmudoum.groupware.domain.cost.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "gw_cost_account")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CostAccount {

    @Id
    private UUID id;
    private String comCd;
    private String accountName;
    private boolean enabled;
    private boolean deletedYn;

    public CostAccount(UUID id, String comCd, String accountName, boolean enabled) {
        this.id = id;
        this.comCd = comCd;
        this.accountName = accountName;
        this.enabled = enabled;
        this.deletedYn = false;
    }

    public CostAccount update(String accountName, boolean enabled) {
        CostAccount account = new CostAccount(id, comCd, accountName, enabled);
        account.deletedYn = deletedYn;
        return account;
    }

    public CostAccount delete() {
        CostAccount account = new CostAccount(id, comCd, accountName, false);
        account.deletedYn = true;
        return account;
    }
}
