package com.upmudoum.gateway.gateway.websocket;

import com.upmudoum.gateway.common.GatewayHeaders;
import com.upmudoum.gateway.config.GatewayProperties;
import java.net.URI;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHttpHeaders;

@Component
public class TradeWebSocketBridgeRequestFactory {

    private final GatewayProperties properties;

    public TradeWebSocketBridgeRequestFactory(GatewayProperties properties) {
        this.properties = properties;
    }

    public URI backendUri(URI frontendUri) {
        URI tradeUrl = properties.getRoutes().getServices().getTradeUrl();
        String scheme = "https".equalsIgnoreCase(tradeUrl.getScheme()) ? "wss" : "ws";
        String query = frontendUri == null ? null : frontendUri.getRawQuery();
        return URI.create(buildUri(scheme, tradeUrl, properties.getWebsocket().getTradeRealtimeBackendPath(), query));
    }

    public WebSocketHttpHeaders backendHeaders(HttpHeaders frontendHeaders) {
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add(GatewayHeaders.INTERNAL_GATEWAY_ID, properties.getInternal().getGatewayId());
        headers.add(GatewayHeaders.INTERNAL_GATEWAY_SECRET, properties.getInternal().getGatewaySecret());
        copyIfPresent(headers, frontendHeaders, GatewayHeaders.REQUEST_ID);
        copyIfPresent(headers, frontendHeaders, GatewayHeaders.USER_ID);
        copyIfPresent(headers, frontendHeaders, GatewayHeaders.COMPANY_CODE);
        copyIfPresent(headers, frontendHeaders, GatewayHeaders.ROLES);
        copyIfPresent(headers, frontendHeaders, GatewayHeaders.TOKEN_TYPE);
        if (properties.getWebsocket().isForwardBrowserCookies()) {
            copyIfPresent(headers, frontendHeaders, HttpHeaders.COOKIE);
        }
        return headers;
    }

    private void copyIfPresent(WebSocketHttpHeaders target, HttpHeaders source, String name) {
        String value = source.getFirst(name);
        if (StringUtils.hasText(value)) {
            target.add(name, value);
        }
    }

    private String buildUri(String scheme, URI baseUri, String path, String query) {
        StringBuilder uri = new StringBuilder();
        uri.append(scheme).append("://").append(baseUri.getHost());
        if (baseUri.getPort() >= 0) {
            uri.append(':').append(baseUri.getPort());
        }
        uri.append(path);
        if (StringUtils.hasText(query)) {
            uri.append('?').append(query);
        }
        return uri.toString();
    }
}
