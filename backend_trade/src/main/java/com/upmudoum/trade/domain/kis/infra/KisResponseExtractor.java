package com.upmudoum.trade.domain.kis.infra;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public final class KisResponseExtractor {

    private KisResponseExtractor() {
    }

    public static Map<String, Object> object(Map<String, Object> response, String fieldName) {
        Object value = response.get(fieldName);
        if (value instanceof Map<?, ?> map) {
            return map.entrySet().stream()
                    .collect(java.util.stream.Collectors.toMap(entry -> String.valueOf(entry.getKey()), Map.Entry::getValue));
        }
        return Map.of();
    }

    public static List<Map<String, Object>> list(Map<String, Object> response, String fieldName) {
        Object value = response.get(fieldName);
        if (value instanceof List<?> list) {
            return list.stream()
                    .filter(Map.class::isInstance)
                    .map(Map.class::cast)
                    .map(KisResponseExtractor::stringKeyMap)
                    .toList();
        }
        return List.of();
    }

    public static String text(Map<String, Object> source, String... keys) {
        for (String key : keys) {
            Object value = source.get(key);
            if (value != null && !String.valueOf(value).isBlank()) {
                return String.valueOf(value);
            }
        }
        return "";
    }

    public static BigDecimal decimal(Map<String, Object> source, String... keys) {
        String value = text(source, keys).replace(",", "");
        if (value.isBlank()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(value);
    }

    private static Map<String, Object> stringKeyMap(Map<?, ?> map) {
        return map.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(entry -> String.valueOf(entry.getKey()), Map.Entry::getValue));
    }
}
