package com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.auth;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.PersistenceMysqlLegacyTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.LegacyCompositeSellerTestHelper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.auth.adapter.LegacySellerAuthCompositeQueryAdapter;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.auth.mapper.LegacySellerAuthCompositeMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.auth.repository.LegacySellerAuthCompositeQueryDslRepository;
import com.ryuqq.marketplace.application.legacy.auth.dto.result.LegacySellerAuthResult;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/**
 * 레거시 셀러 인증 Composite 조회 E2E 테스트.
 *
 * <p>Adapter → Mapper → QueryDSL Repository → H2 DB 전체 흐름을 실제 객체로 검증합니다.
 * administrators + admin_auth_group + auth_group + seller 4테이블 JOIN.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@DataJpaTest
@ContextConfiguration(classes = PersistenceMysqlLegacyTestApplication.class)
@TestPropertySource(
        properties = {
            "spring.flyway.enabled=false",
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.jpa.show-sql=false",
            "persistence.legacy.enabled=false"
        })
@DisplayName("레거시 셀러 인증 Composite 조회 E2E 테스트")
class LegacySellerAuthCompositeE2ETest {

    @Autowired private EntityManager entityManager;

    private LegacySellerAuthCompositeQueryAdapter adapter;
    private LegacyCompositeSellerTestHelper helper;

    @BeforeEach
    void setUp() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        LegacySellerAuthCompositeQueryDslRepository repository =
                new LegacySellerAuthCompositeQueryDslRepository(queryFactory);
        LegacySellerAuthCompositeMapper mapper = new LegacySellerAuthCompositeMapper();
        adapter = new LegacySellerAuthCompositeQueryAdapter(repository, mapper);
        helper = new LegacyCompositeSellerTestHelper(entityManager);
    }

    @Nested
    @DisplayName("셀러 인증 E2E 시나리오")
    class FindByEmailE2ETest {

        @Test
        @DisplayName("MASTER 관리자 — 4테이블 JOIN 결과가 LegacySellerAuthResult로 올바르게 변환됩니다")
        void masterAdmin_ReturnsCompleteAuthResult() {
            // given
            String email = helper.setupFullAuthData();

            // when
            Optional<LegacySellerAuthResult> result = adapter.findByEmail(email);

            // then
            assertThat(result).isPresent();
            LegacySellerAuthResult auth = result.get();
            assertThat(auth.sellerId()).isEqualTo(10L);
            assertThat(auth.email()).isEqualTo(email);
            assertThat(auth.passwordHash()).isEqualTo("$2a$10$hashvalue");
            assertThat(auth.roleType()).isEqualTo("MASTER");
            assertThat(auth.approvalStatus()).isEqualTo("APPROVED");
            assertThat(auth.isApproved()).isTrue();
        }

        @Test
        @DisplayName("SELLER 역할 — SELLER 권한으로 올바르게 조회됩니다")
        void sellerRole_ReturnsSellerAuthResult() {
            // given
            helper.insertSeller(20L, "셀러B");
            helper.insertAdministrator(2L, 20L, "seller@test.com", "$2a$hash2", "APPROVED");
            helper.insertAuthGroup(2L, "SELLER");
            helper.insertAdminAuthGroup(2L, 2L);
            helper.flushAndClear();

            // when
            Optional<LegacySellerAuthResult> result = adapter.findByEmail("seller@test.com");

            // then
            assertThat(result).isPresent();
            assertThat(result.get().sellerId()).isEqualTo(20L);
            assertThat(result.get().roleType()).isEqualTo("SELLER");
            assertThat(result.get().isApproved()).isTrue();
        }

        @Test
        @DisplayName("PENDING 상태 — 미승인 관리자도 조회되지만 isApproved는 false")
        void pendingStatus_ReturnsNotApproved() {
            // given
            helper.insertSeller(30L, "셀러C");
            helper.insertAdministrator(3L, 30L, "pending@test.com", "$2a$hash3", "PENDING");
            helper.insertAuthGroup(3L, "MASTER");
            helper.insertAdminAuthGroup(3L, 3L);
            helper.flushAndClear();

            // when
            Optional<LegacySellerAuthResult> result = adapter.findByEmail("pending@test.com");

            // then
            assertThat(result).isPresent();
            assertThat(result.get().approvalStatus()).isEqualTo("PENDING");
            assertThat(result.get().isApproved()).isFalse();
        }

        @Test
        @DisplayName("존재하지 않는 이메일 — 빈 Optional 반환")
        void nonExistentEmail_ReturnsEmpty() {
            // when
            Optional<LegacySellerAuthResult> result =
                    adapter.findByEmail("unknown@test.com");

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("같은 셀러에 여러 관리자 — 이메일로 정확한 관리자만 조회됩니다")
        void multipleAdminsPerSeller_ReturnsExactMatch() {
            // given
            helper.insertSeller(40L, "셀러D");
            helper.insertAdministrator(4L, 40L, "master@test.com", "$2a$hash4", "APPROVED");
            helper.insertAdministrator(5L, 40L, "sub@test.com", "$2a$hash5", "APPROVED");
            helper.insertAuthGroup(4L, "MASTER");
            helper.insertAuthGroup(5L, "SELLER");
            helper.insertAdminAuthGroup(4L, 4L);
            helper.insertAdminAuthGroup(5L, 5L);
            helper.flushAndClear();

            // when
            Optional<LegacySellerAuthResult> masterResult = adapter.findByEmail("master@test.com");
            Optional<LegacySellerAuthResult> subResult = adapter.findByEmail("sub@test.com");

            // then
            assertThat(masterResult).isPresent();
            assertThat(masterResult.get().roleType()).isEqualTo("MASTER");

            assertThat(subResult).isPresent();
            assertThat(subResult.get().roleType()).isEqualTo("SELLER");

            // 같은 셀러
            assertThat(masterResult.get().sellerId()).isEqualTo(subResult.get().sellerId());
        }

        @Test
        @DisplayName("다른 셀러의 관리자끼리 이메일이 다르면 독립적으로 조회됩니다")
        void differentSellers_IndependentResults() {
            // given
            helper.insertSeller(50L, "셀러E");
            helper.insertSeller(60L, "셀러F");
            helper.insertAdministrator(6L, 50L, "e-admin@test.com", "$2a$hash6", "APPROVED");
            helper.insertAdministrator(7L, 60L, "f-admin@test.com", "$2a$hash7", "APPROVED");
            helper.insertAuthGroup(6L, "MASTER");
            helper.insertAuthGroup(7L, "MASTER");
            helper.insertAdminAuthGroup(6L, 6L);
            helper.insertAdminAuthGroup(7L, 7L);
            helper.flushAndClear();

            // when
            Optional<LegacySellerAuthResult> eResult = adapter.findByEmail("e-admin@test.com");
            Optional<LegacySellerAuthResult> fResult = adapter.findByEmail("f-admin@test.com");

            // then
            assertThat(eResult).isPresent();
            assertThat(eResult.get().sellerId()).isEqualTo(50L);

            assertThat(fResult).isPresent();
            assertThat(fResult.get().sellerId()).isEqualTo(60L);
        }
    }
}
