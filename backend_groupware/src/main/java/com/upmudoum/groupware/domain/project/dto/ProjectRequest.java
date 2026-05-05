package com.upmudoum.groupware.domain.project.dto;

import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.upmudoum.groupware.domain.project.vo.ProjectStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequest {

    @NotBlank
    private String name;

    private String description;
    private List<String> memberUserIds;
    private List<String> referenceUserIds;

    @NotNull
    private ProjectStatus status;

    @Min(0)
    @Max(100)
    private int progressRate;

    public ProjectRequest(String name, String description, List<String> memberUserIds, ProjectStatus status, int progressRate) {
        this(name, description, memberUserIds, List.of(), status, progressRate);
    }
}
