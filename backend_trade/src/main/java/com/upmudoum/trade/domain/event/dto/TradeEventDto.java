package com.upmudoum.trade.domain.event.dto;

import com.upmudoum.trade.domain.event.vo.TradeEventType;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TradeEventDto {

    private Long id;
    private TradeEventType eventType;
    private String itemCode;
    private String title;
    private LocalDate eventDate;
    private String description;
}
