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
@Table(name = "gw_cost_item")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CostItem {

    @Id
    private UUID id;
    private String comCd;
    private String costItemName;
    private boolean enabled;
    private boolean deletedYn;

    public CostItem(UUID id, String comCd, String costItemName, boolean enabled) {
        this.id = id;
        this.comCd = comCd;
        this.costItemName = costItemName;
        this.enabled = enabled;
        this.deletedYn = false;
    }

    public CostItem update(String costItemName, boolean enabled) {
        CostItem item = new CostItem(id, comCd, costItemName, enabled);
        item.deletedYn = deletedYn;
        return item;
    }

    public CostItem delete() {
        CostItem item = new CostItem(id, comCd, costItemName, false);
        item.deletedYn = true;
        return item;
    }
}
