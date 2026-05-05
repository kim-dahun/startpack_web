package com.upmudoum.user.domain.dropdown.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DropdownUserOptionRow {

    private String userId;
    private String userName;
    private String phone;
    private String jobGradeId;
    private String jobGradeName;

    public DropdownOptionResponse toOption() {
        return new DropdownOptionResponse(label(), userId);
    }

    private String label() {
        StringBuilder label = new StringBuilder(userName);
        if (phone != null && !phone.isBlank()) {
            label.append(" / ").append(phone);
        }
        if (jobGradeId != null && !jobGradeId.isBlank()) {
            String jobGradeLabel = jobGradeName == null || jobGradeName.isBlank() ? jobGradeId : jobGradeName;
            label.append(" / ").append(jobGradeLabel);
        }
        return label.toString();
    }
}
