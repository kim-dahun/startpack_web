package com.upmudoum.groupware.domain.chat.dto;

import java.util.List;

import com.upmudoum.groupware.domain.chat.vo.ChatRoomType;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateChatRoomRequest {

    @NotNull
    private ChatRoomType roomType;

    private String roomName;
    private List<String> memberUserIds;
}
