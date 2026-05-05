package com.upmudoum.groupware.domain.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageAttachmentRequest {

    @NotBlank
    private String fileName;
    private String contentType;
    @PositiveOrZero
    private long fileSize;
    @NotBlank
    private String storagePath;
}
