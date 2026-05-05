package com.upmudoum.trade.domain.workspace.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class WorkspaceChartIntervalTests {

    @Test
    void parsesMinuteAlias() {
        assertThat(WorkspaceChartInterval.from("1m")).isEqualTo(WorkspaceChartInterval.MIN_1);
        assertThat(WorkspaceChartInterval.from("15m")).isEqualTo(WorkspaceChartInterval.MIN_15);
    }

    @Test
    void parsesDailyDefault() {
        assertThat(WorkspaceChartInterval.from(null)).isEqualTo(WorkspaceChartInterval.DAY);
        assertThat(WorkspaceChartInterval.from("DAY")).isEqualTo(WorkspaceChartInterval.DAY);
    }

    @Test
    void rejectsUnsupportedInterval() {
        assertThatThrownBy(() -> WorkspaceChartInterval.from("2m"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
