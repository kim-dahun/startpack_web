package com.upmudoum.groupware.domain.chat.dto;

import com.upmudoum.groupware.domain.chat.vo.ChatMessageType;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SendChatMessageRequest {

    private ChatMessageType messageType = ChatMessageType.TEXT;

    @NotBlank
    private String content;

    public SendChatMessageRequest(String content) {
        this(null, content);
    }
}
