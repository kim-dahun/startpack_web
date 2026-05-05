package com.upmudoum.user.domain.jobgrade;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobGradeRepository extends JpaRepository<JobGrade, Long> {

    List<JobGrade> findByComCdOrderBySortSeqAscJobGradeIdAsc(String comCd);

    List<JobGrade> findByComCdAndEnabledOrderBySortSeqAscJobGradeIdAsc(String comCd, boolean enabled);

    Optional<JobGrade> findByComCdAndJobGradeId(String comCd, String jobGradeId);

    void deleteByComCdAndJobGradeId(String comCd, String jobGradeId);
}
