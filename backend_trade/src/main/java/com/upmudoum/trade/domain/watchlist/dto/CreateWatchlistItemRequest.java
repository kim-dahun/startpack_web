package com.upmudoum.trade.domain.watchlist.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateWatchlistItemRequest {

    @NotBlank
    private String userId;

    @NotBlank
    private String itemCode;

    @NotBlank
    private String itemName;

    private Long groupId;

    private String memo;

    private List<String> tags;

}
