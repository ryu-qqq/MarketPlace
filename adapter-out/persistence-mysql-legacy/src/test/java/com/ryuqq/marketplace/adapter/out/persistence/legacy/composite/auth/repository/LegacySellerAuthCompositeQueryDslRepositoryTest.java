package com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.auth.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.PersistenceMysqlLegacyTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.LegacyCompositeSellerTestHelper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.auth.dto.LegacySellerAuthQueryDto;
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
 * LegacySellerAuthCompositeQueryDslRepository 통합 테스트.
 *
 * <p>administrators + admin_auth_group + auth_group + seller 4테이블 JOIN 쿼리를 검증합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("integration")
@DataJpaTest
@ContextConfiguration(classes = PersistenceMysqlLegacyTestApplication.class)
@TestPropertySource(
        properties = {
            "spring.flyway.enabled=false",
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.jpa.show-sql=false",
            "persistence.legacy.enabled=false"
        })
@DisplayName("LegacySellerAuthCompositeQueryDslRepository 통합 테스트")
class LegacySellerAuthCompositeQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private LegacySellerAuthCompositeQueryDslRepository repository;
    private LegacyCompositeSellerTestHelper helper;

    @BeforeEach
    void setUp() {
        repository = new LegacySellerAuthCompositeQueryDslRepository(
                new JPAQueryFactory(entityManager));
        helper = new LegacyCompositeSellerTestHelper(entityManager);
    }

    @Nested
    @DisplayName("findByEmail 메서드 테스트")
    class FindByEmailTest {

        @Test
        @DisplayName("4테이블 JOIN으로 인증 정보를 조회합니다")
        void findByEmail_WithFullData_ReturnsAuthDto() {
            // given
            String email = helper.setupFullAuthData();

            // when
            Optional<LegacySellerAuthQueryDto> result = repository.findByEmail(email);

            // then
            assertThat(result).isPresent();
            LegacySellerAuthQueryDto dto = result.get();
            assertThat(dto.sellerId()).isEqualTo(10L);
            assertThat(dto.email()).isEqualTo(email);
            assertThat(dto.passwordHash()).isEqualTo("$2a$10$hashvalue");
            assertThat(dto.authGroupType()).isEqualTo("MASTER");
            assertThat(dto.approvalStatus()).isEqualTo("APPROVED");
        }

        @Test
        @DisplayName("존재하지 않는 이메일로 조회 시 빈 Optional을 반환합니다")
        void findByEmail_WithNonExistentEmail_ReturnsEmpty() {
            // when
            Optional<LegacySellerAuthQueryDto> result =
                    repository.findByEmail("unknown@test.com");

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("SELLER 역할의 관리자도 올바르게 조회됩니다")
        void findByEmail_WithSellerRole_ReturnsSellerAuth() {
            // given
            helper.insertSeller(20L, "셀러B");
            helper.insertAdministrator(2L, 20L, "seller@test.com", "$2a$10$hash2", "APPROVED");
            helper.insertAuthGroup(2L, "SELLER");
            helper.insertAdminAuthGroup(2L, 2L);
            helper.flushAndClear();

            // when
            Optional<LegacySellerAuthQueryDto> result =
                    repository.findByEmail("seller@test.com");

            // then
            assertThat(result).isPresent();
            assertThat(result.get().sellerId()).isEqualTo(20L);
            assertThat(result.get().authGroupType()).isEqualTo("SELLER");
        }

        @Test
        @DisplayName("PENDING 승인 상태의 관리자도 조회됩니다")
        void findByEmail_WithPendingStatus_ReturnsPendingAuth() {
            // given
            helper.insertSeller(30L, "셀러C");
            helper.insertAdministrator(3L, 30L, "pending@test.com", "$2a$10$hash3", "PENDING");
            helper.insertAuthGroup(3L, "MASTER");
            helper.insertAdminAuthGroup(3L, 3L);
            helper.flushAndClear();

            // when
            Optional<LegacySellerAuthQueryDto> result =
                    repository.findByEmail("pending@test.com");

            // then
            assertThat(result).isPresent();
            assertThat(result.get().approvalStatus()).isEqualTo("PENDING");
        }

        @Test
        @DisplayName("같은 셀러에 여러 관리자가 있어도 이메일로 정확히 조회됩니다")
        void findByEmail_WithMultipleAdmins_ReturnsExactMatch() {
            // given
            helper.insertSeller(40L, "셀러D");
            helper.insertAdministrator(4L, 40L, "admin1@test.com", "$2a$10$hash4", "APPROVED");
            helper.insertAdministrator(5L, 40L, "admin2@test.com", "$2a$10$hash5", "APPROVED");
            helper.insertAuthGroup(4L, "MASTER");
            helper.insertAuthGroup(5L, "SELLER");
            helper.insertAdminAuthGroup(4L, 4L);
            helper.insertAdminAuthGroup(5L, 5L);
            helper.flushAndClear();

            // when
            Optional<LegacySellerAuthQueryDto> result =
                    repository.findByEmail("admin2@test.com");

            // then
            assertThat(result).isPresent();
            assertThat(result.get().email()).isEqualTo("admin2@test.com");
            assertThat(result.get().authGroupType()).isEqualTo("SELLER");
        }
    }
}
