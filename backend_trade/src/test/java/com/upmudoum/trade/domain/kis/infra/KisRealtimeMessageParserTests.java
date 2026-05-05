package com.upmudoum.trade.domain.kis.infra;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upmudoum.trade.domain.marketdata.dto.TradeRealtimeEventDto;
import com.upmudoum.trade.domain.marketdata.vo.TradeRealtimeEventType;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class KisRealtimeMessageParserTests {

    private final KisRealtimeMessageParser parser = new KisRealtimeMessageParser(new ObjectMapper());

    @Test
    void parsesJsonPriceMessage() {
        String raw = """
                {
                  "header": {"tr_id": "H0STCNT0"},
                  "body": {"output": {"stck_shrn_iscd": "005930", "stck_prpr": "78000"}}
                }
                """;

        Optional<TradeRealtimeEventDto> event = parser.parse(raw);

        assertThat(event).isPresent();
        assertThat(event.get().getType()).isEqualTo(TradeRealtimeEventType.PRICE);
        assertThat(event.get().getItemCode()).isEqualTo("005930");
        assertThat(event.get().getPayload()).containsEntry("trId", "H0STCNT0");
    }

    @Test
    void parsesDelimitedOrderbookMessage() {
        Optional<TradeRealtimeEventDto> event = parser.parse("0|H0STASP0|1|005930^78000^100^77900^200");

        assertThat(event).isPresent();
        assertThat(event.get().getType()).isEqualTo(TradeRealtimeEventType.ORDERBOOK);
        assertThat(event.get().getItemCode()).isEqualTo("005930");
        assertThat(event.get().getPayload().get("fields")).isEqualTo(List.of("005930", "78000", "100", "77900", "200"));
    }

    @Test
    void ignoresUnknownOrHeartbeatMessage() {
        assertThat(parser.parse("PINGPONG")).isEmpty();
        assertThat(parser.parse("0|UNKNOWN|1|005930^78000")).isEmpty();
    }
}
