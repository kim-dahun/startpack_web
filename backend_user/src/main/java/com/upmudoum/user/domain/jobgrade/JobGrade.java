package com.upmudoum.user.domain.jobgrade;

import com.upmudoum.user.domain.common.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "job_grades",
        uniqueConstraints = @UniqueConstraint(name = "uk_job_grades_com_grade", columnNames = {"com_cd", "job_grade_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class JobGrade extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "com_cd", nullable = false, length = 30)
    private String comCd;

    @Column(name = "job_grade_id", nullable = false, length = 80)
    private String jobGradeId;

    @Column(name = "job_grade_name", nullable = false, length = 150)
    private String jobGradeName;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_grade_type", nullable = false, length = 30)
    private JobGradeType jobGradeType = JobGradeType.CUSTOM;

    @Column(nullable = false)
    private int sortSeq;

    @Column(nullable = false)
    private boolean enabled = true;

    public JobGrade(String comCd, String jobGradeId, String jobGradeName) {
        this.comCd = comCd;
        this.jobGradeId = jobGradeId;
        this.jobGradeName = jobGradeName;
    }

    public void update(String jobGradeName, JobGradeType jobGradeType, int sortSeq, boolean enabled) {
        this.jobGradeName = jobGradeName;
        this.jobGradeType = jobGradeType == null ? JobGradeType.CUSTOM : jobGradeType;
        this.sortSeq = sortSeq;
        this.enabled = enabled;
    }
}
