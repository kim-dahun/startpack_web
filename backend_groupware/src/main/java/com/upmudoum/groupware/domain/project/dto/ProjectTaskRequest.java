package com.upmudoum.groupware.domain.project.dto;

import java.time.LocalDate;

import com.upmudoum.groupware.domain.project.vo.ProjectTaskStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectTaskRequest {

    @NotBlank
    private String title;
    private String description;
    private String assigneeUserId;
    private LocalDate dueDate;
    @NotNull
    private ProjectTaskStatus status;
}
