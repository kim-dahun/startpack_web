package com.upmudoum.trade.domain.watchlist.controller;

import com.upmudoum.trade.domain.watchlist.dto.CreateWatchlistItemRequest;
import com.upmudoum.trade.domain.watchlist.dto.UpdateWatchlistItemMetadataRequest;
import com.upmudoum.trade.domain.watchlist.dto.WatchlistItemDto;
import com.upmudoum.trade.domain.watchlist.service.WatchlistService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trade/watchlist")
public class WatchlistController {

    private final WatchlistService watchlistService;

    public WatchlistController(WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }

    @GetMapping
    public List<WatchlistItemDto> watchlist(
            @RequestParam String userId,
            @RequestParam(required = false) Long groupId
    ) {
        return watchlistService.findByUserIdAndGroupId(userId, groupId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WatchlistItemDto add(@Valid @RequestBody CreateWatchlistItemRequest request) {
        return watchlistService.add(request);
    }

    @PatchMapping("/{id}/metadata")
    public WatchlistItemDto updateMetadata(
            @PathVariable Long id,
            @RequestBody UpdateWatchlistItemMetadataRequest request
    ) {
        return watchlistService.updateMetadata(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        watchlistService.delete(id);
    }
}
