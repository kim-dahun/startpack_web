package com.upmudoum.groupware.domain.project.dto;

import com.upmudoum.groupware.domain.project.vo.ProjectTaskStatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectTaskStatusRequest {
    @NotNull
    private ProjectTaskStatus status;
}
