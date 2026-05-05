package com.upmudoum.trade.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.upmudoum.trade.domain.account.entity.AccountSnapshot;
import com.upmudoum.trade.domain.account.entity.AccountPositionSnapshot;
import com.upmudoum.trade.domain.account.entity.DailyBalanceSnapshot;
import com.upmudoum.trade.domain.account.entity.RegisteredAccount;
import com.upmudoum.trade.domain.analysis.entity.MarketRankingSnapshot;
import com.upmudoum.trade.domain.chart.entity.ChartDrawing;
import com.upmudoum.trade.domain.event.entity.TradeEvent;
import com.upmudoum.trade.domain.item.entity.FrequentSearchItem;
import com.upmudoum.trade.domain.item.entity.ItemMaster;
import com.upmudoum.trade.domain.kis.entity.KisApiCallLog;
import com.upmudoum.trade.domain.kis.entity.KisOpenApiToken;
import com.upmudoum.trade.domain.master.entity.TradeMasterImportHistory;
import com.upmudoum.trade.domain.master.entity.TradeMasterImportLock;
import com.upmudoum.trade.domain.marketdata.entity.RealtimeReceiveLog;
import com.upmudoum.trade.domain.marketdata.entity.RealtimeReconnectHistory;
import com.upmudoum.trade.domain.trade.entity.TradeHistory;
import com.upmudoum.trade.domain.watchlist.entity.WatchlistGroup;
import com.upmudoum.trade.domain.watchlist.entity.WatchlistItem;
import jakarta.persistence.Table;
import java.util.List;
import org.junit.jupiter.api.Test;

class SchemaMappingTests {

    @Test
    void allJpaTablesRelyOnHibernateDefaultSchema() {
        List<Class<?>> entities = List.of(
                AccountSnapshot.class,
                AccountPositionSnapshot.class,
                RegisteredAccount.class,
                MarketRankingSnapshot.class,
                ChartDrawing.class,
                DailyBalanceSnapshot.class,
                TradeEvent.class,
                FrequentSearchItem.class,
                ItemMaster.class,
                KisApiCallLog.class,
                KisOpenApiToken.class,
                TradeMasterImportHistory.class,
                TradeMasterImportLock.class,
                RealtimeReceiveLog.class,
                RealtimeReconnectHistory.class,
                TradeHistory.class,
                WatchlistGroup.class,
                WatchlistItem.class
        );

        assertThat(entities)
                .allSatisfy(entity -> assertThat(entity.getAnnotation(Table.class).schema()).isBlank());
    }
}
