package com.upmudoum.user.domain.dropdown.querydsl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.upmudoum.user.domain.dropdown.dto.DropdownUserOptionRow;
import com.upmudoum.user.domain.jobgrade.QJobGrade;
import com.upmudoum.user.domain.user.QUserAccount;
import com.upmudoum.user.domain.user.UserStatus;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class DropdownQueryRepository {

    private final JPAQueryFactory queryFactory;

    public DropdownQueryRepository(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    public List<DropdownUserOptionRow> findUserDropdownRows(String comCd) {
        QUserAccount user = QUserAccount.userAccount;
        QJobGrade jobGrade = QJobGrade.jobGrade;

        return queryFactory
                .select(Projections.bean(
                        DropdownUserOptionRow.class,
                        user.userId.as("userId"),
                        user.userName.as("userName"),
                        user.phone.as("phone"),
                        user.jobGradeId.as("jobGradeId"),
                        jobGrade.jobGradeName.as("jobGradeName")
                ))
                .from(user)
                .leftJoin(jobGrade).on(
                        jobGrade.comCd.eq(user.comCd),
                        jobGrade.jobGradeId.eq(user.jobGradeId),
                        jobGrade.enabled.isTrue()
                )
                .where(
                        user.comCd.eq(comCd),
                        user.status.eq(UserStatus.ACTIVE)
                )
                .orderBy(user.userName.asc(), user.userId.asc())
                .fetch();
    }
}
