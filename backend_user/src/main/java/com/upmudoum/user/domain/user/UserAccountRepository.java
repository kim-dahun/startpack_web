package com.upmudoum.user.domain.user;

import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    List<UserAccount> findByComCdOrderByUserIdAsc(String comCd);

    List<UserAccount> findByComCdAndStatusOrderByUserNameAscUserIdAsc(String comCd, UserStatus status);

    Optional<UserAccount> findByComCdAndUserId(String comCd, String userId);

    boolean existsByComCdAndUserId(String comCd, String userId);

    void deleteByComCdAndUserId(String comCd, String userId);
}
