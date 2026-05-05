package com.upmudoum.user;

import com.upmudoum.user.domain.user.UserAccount;
import com.upmudoum.user.domain.user.UserAccountRepository;
import com.upmudoum.user.domain.user.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect",
        "spring.jpa.properties.hibernate.default_schema=user_service",
        "spring.datasource.hikari.initialization-fail-timeout=-1"
})
class BackendUserApplicationTests {

    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
//    @Test
//    void registerMaster(){
//
//
//        userAccountRepository.save(UserAccount.builder()
//                        .comCd("00001")
//                        .userId("tester")
//                        .address("서울시 중구 오대로")
//                        .email("ekgnsl2002@naver.com")
//                        .jobGradeId("001")
//                        .phone("010-1234-5678")
//                        .status(UserStatus.ACTIVE)
//                        .userName("테스터")
//                        .passwordHash(passwordEncoder.encode("tester"))
//                .build());
//        userAccountRepository.flush();
//    }


//    @Autowired
//    private Environment environment;
//
//    @Test
//    void contextLoads() {
//    }
//
//    @Test
//    void usesCommonPostgresDatabaseAndUserServiceSchema() {
//        assertThat(environment.getProperty("spring.datasource.url"))
//                .contains("jdbc:postgresql://localhost:5432/postgres");
//        assertThat(environment.getProperty("spring.jpa.properties.hibernate.default_schema"))
//                .isEqualTo("user_service");
//        assertThat(environment.getProperty("spring.datasource.hikari.schema"))
//                .isEqualTo("user_service");
//    }
}
