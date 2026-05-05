package com.upmudoum.trade.domain.watchlist.controller;

import com.upmudoum.trade.domain.watchlist.dto.CreateWatchlistGroupRequest;
import com.upmudoum.trade.domain.watchlist.dto.WatchlistGroupDto;
import com.upmudoum.trade.domain.watchlist.service.WatchlistGroupService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trade/watchlist/groups")
public class WatchlistGroupController {

    private final WatchlistGroupService groupService;

    public WatchlistGroupController(WatchlistGroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping
    public List<WatchlistGroupDto> groups(@RequestParam String userId) {
        return groupService.findByUserId(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WatchlistGroupDto add(@Valid @RequestBody CreateWatchlistGroupRequest request) {
        return groupService.add(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        groupService.delete(id);
    }
}
