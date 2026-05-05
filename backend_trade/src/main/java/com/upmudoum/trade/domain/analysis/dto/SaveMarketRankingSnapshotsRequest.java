package com.upmudoum.trade.domain.analysis.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SaveMarketRankingSnapshotsRequest {

    @NotNull
    private LocalDate baseDate;

    @Valid
    @NotEmpty
    private List<MarketRankingSnapshotDto> snapshots;
}
