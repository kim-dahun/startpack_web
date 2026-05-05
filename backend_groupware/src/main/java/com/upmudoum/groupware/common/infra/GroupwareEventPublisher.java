package com.upmudoum.groupware.common.infra;

import java.time.Instant;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.upmudoum.groupware.common.dto.GroupwareEvent;
import com.upmudoum.groupware.common.vo.TenantKey;

@Component
public class GroupwareEventPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public GroupwareEventPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void publishToUser(TenantKey tenant, String channel, String eventType, Map<String, Object> payload) {
        GroupwareEvent event = new GroupwareEvent(
                tenant.getComCd(),
                tenant.getUserId(),
                channel,
                eventType,
                payload,
                Instant.now());
        messagingTemplate.convertAndSend(userDestination(tenant, channel), event);
    }

    public static String userDestination(TenantKey tenant, String channel) {
        return "/topic/groupware/" + tenant.getComCd() + "/users/" + tenant.getUserId() + "/" + channel;
    }
}
