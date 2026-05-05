package com.upmudoum.user.domain.department;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    List<Department> findByComCdOrderBySortSeqAscDepartmentIdAsc(String comCd);

    List<Department> findByComCdAndEnabledOrderBySortSeqAscDepartmentIdAsc(String comCd, boolean enabled);

    Optional<Department> findByComCdAndDepartmentId(String comCd, String departmentId);

    void deleteByComCdAndDepartmentId(String comCd, String departmentId);
}
