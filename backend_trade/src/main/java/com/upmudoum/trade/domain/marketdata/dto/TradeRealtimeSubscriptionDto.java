package com.upmudoum.trade.domain.marketdata.dto;

import com.upmudoum.trade.domain.marketdata.vo.TradeRealtimeEventType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TradeRealtimeSubscriptionDto {

    private TradeRealtimeEventType type;
    private String itemCode;

    public TradeRealtimeSubscriptionDto(TradeRealtimeEventType type, String itemCode) {
        if (type == null) {
            throw new IllegalArgumentException("type is required");
        }
        if (itemCode == null || itemCode.isBlank()) {
            throw new IllegalArgumentException("itemCode is required");
        }
        this.type = type;
        this.itemCode = itemCode.trim();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TradeRealtimeSubscriptionDto other)) {
            return false;
        }
        return type == other.type && itemCode.equals(other.itemCode);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + itemCode.hashCode();
        return result;
    }
}
