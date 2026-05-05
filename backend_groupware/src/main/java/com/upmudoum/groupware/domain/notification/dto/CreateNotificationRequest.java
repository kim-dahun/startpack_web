package com.upmudoum.groupware.domain.notification.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateNotificationRequest {

    @NotBlank
    private String targetUserId;

    @NotBlank
    private String title;

    private String content;
    private String referenceType;
    private String referenceId;
}
