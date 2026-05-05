package com.upmudoum.trade.domain.workspace.vo;

import java.util.Locale;

public enum WorkspaceChartInterval {
    MIN_1("1m", "MINUTE"),
    MIN_5("5m", "MINUTE"),
    MIN_15("15m", "MINUTE"),
    MIN_30("30m", "MINUTE"),
    MIN_60("60m", "MINUTE"),
    DAY("DAY", "DAY"),
    WEEK("WEEK", "WEEK"),
    MONTH("MONTH", "MONTH"),
    YEAR("YEAR", "YEAR");

    private final String requestValue;
    private final String kisPeriodType;

    WorkspaceChartInterval(String requestValue, String kisPeriodType) {
        this.requestValue = requestValue;
        this.kisPeriodType = kisPeriodType;
    }

    public String getRequestValue() {
        return requestValue;
    }

    public String getKisPeriodType() {
        return kisPeriodType;
    }

    public boolean isMinute() {
        return kisPeriodType.equals("MINUTE");
    }

    public static WorkspaceChartInterval from(String value) {
        if (value == null || value.isBlank()) {
            return DAY;
        }
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        for (WorkspaceChartInterval interval : values()) {
            if (interval.name().equals(normalized) || interval.requestValue.toUpperCase(Locale.ROOT).equals(normalized)) {
                return interval;
            }
        }
        throw new IllegalArgumentException("unsupported chart interval: " + value);
    }
}
