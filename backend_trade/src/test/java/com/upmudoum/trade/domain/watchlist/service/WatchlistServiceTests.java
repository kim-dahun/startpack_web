package com.upmudoum.trade.domain.watchlist.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.upmudoum.trade.config.QuerydslConfig;
import com.upmudoum.trade.domain.watchlist.dto.CreateWatchlistItemRequest;
import com.upmudoum.trade.domain.watchlist.dto.UpdateWatchlistItemMetadataRequest;
import com.upmudoum.trade.domain.watchlist.entity.WatchlistGroup;
import com.upmudoum.trade.domain.watchlist.querydsl.WatchlistQueryRepository;
import com.upmudoum.trade.domain.watchlist.repository.WatchlistGroupRepository;
import com.upmudoum.trade.domain.watchlist.repository.WatchlistRepository;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({QuerydslConfig.class, WatchlistQueryRepository.class})
class WatchlistServiceTests {

    @Autowired
    private WatchlistRepository repository;

    @Autowired
    private WatchlistGroupRepository groupRepository;

    @Autowired
    private WatchlistQueryRepository queryRepository;

    @Test
    void addRejectsDuplicatedUserItem() {
        WatchlistService service = new WatchlistService(repository, groupRepository, queryRepository);
        CreateWatchlistItemRequest request = request("user-1", "005930", "Samsung");

        service.add(request);

        assertThatThrownBy(() -> service.add(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void findByUserIdReturnsOnlyMatchedUserItems() {
        WatchlistService service = new WatchlistService(repository, groupRepository, queryRepository);
        service.add(request("user-1", "005930", "Samsung"));
        service.add(request("user-2", "000660", "SK Hynix"));

        assertThat(service.findByUserId("user-1"))
                .hasSize(1)
                .first()
                .extracting("itemCode")
                .isEqualTo("005930");
    }

    @Test
    void addAndUpdateMetadataStoresGroupMemoAndTags() {
        WatchlistService service = new WatchlistService(repository, groupRepository, queryRepository);
        WatchlistGroup group = group("user-1", "main");
        CreateWatchlistItemRequest request = request("user-1", "005930", "Samsung");
        request.setGroupId(group.getId());
        request.setMemo("core holding");
        request.setTags(List.of("large-cap", "dividend", "large-cap"));

        Long id = service.add(request).getId();
        UpdateWatchlistItemMetadataRequest update = new UpdateWatchlistItemMetadataRequest();
        update.setGroupId(group.getId());
        update.setMemo("updated memo");
        update.setTags(List.of("ai", "semiconductor"));

        assertThat(service.updateMetadata(id, update))
                .extracting("memo")
                .isEqualTo("updated memo");
        assertThat(service.findByUserIdAndGroupId("user-1", group.getId()))
                .hasSize(1)
                .first()
                .extracting("tags")
                .isEqualTo(List.of("ai", "semiconductor"));
    }

    private CreateWatchlistItemRequest request(String userId, String itemCode, String itemName) {
        CreateWatchlistItemRequest request = new CreateWatchlistItemRequest();
        request.setUserId(userId);
        request.setItemCode(itemCode);
        request.setItemName(itemName);
        return request;
    }

    private WatchlistGroup group(String userId, String groupName) {
        WatchlistGroup group = new WatchlistGroup();
        group.setUserId(userId);
        group.setGroupName(groupName);
        group.setCreatedAt(Instant.now());
        return groupRepository.save(group);
    }
}
