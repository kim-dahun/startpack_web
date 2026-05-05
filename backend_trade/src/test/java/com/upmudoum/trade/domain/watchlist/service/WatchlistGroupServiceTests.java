package com.upmudoum.trade.domain.watchlist.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.upmudoum.trade.domain.watchlist.dto.CreateWatchlistGroupRequest;
import com.upmudoum.trade.domain.watchlist.repository.WatchlistGroupRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
class WatchlistGroupServiceTests {

    @Autowired
    private WatchlistGroupRepository repository;

    @Test
    void addRejectsDuplicatedUserGroupName() {
        WatchlistGroupService service = new WatchlistGroupService(repository);
        CreateWatchlistGroupRequest request = request("user-1", "main");

        service.add(request);

        assertThatThrownBy(() -> service.add(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void findByUserIdReturnsOnlyMatchedGroups() {
        WatchlistGroupService service = new WatchlistGroupService(repository);
        service.add(request("user-1", "main"));
        service.add(request("user-2", "main"));

        assertThat(service.findByUserId("user-1"))
                .hasSize(1)
                .first()
                .extracting("groupName")
                .isEqualTo("main");
    }

    private CreateWatchlistGroupRequest request(String userId, String groupName) {
        CreateWatchlistGroupRequest request = new CreateWatchlistGroupRequest();
        request.setUserId(userId);
        request.setGroupName(groupName);
        return request;
    }
}
