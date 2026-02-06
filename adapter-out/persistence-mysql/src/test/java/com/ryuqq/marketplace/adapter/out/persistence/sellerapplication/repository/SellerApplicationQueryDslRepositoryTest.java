package com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.SellerApplicationJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.condition.SellerApplicationConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.entity.SellerApplicationJpaEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/**
 * SellerApplicationQueryDslRepositoryTest - 입점 신청 QueryDslRepository 통합 테스트.
 *
 * <p>SellerApplication은 soft-delete 대상이 아니지만, 조회 쿼리 검증을 수행합니다.
 */
@Tag("integration")
@DataJpaTest
@ContextConfiguration(classes = PersistenceMysqlTestApplication.class)
@TestPropertySource(
        properties = {
            "spring.flyway.enabled=false",
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.jpa.show-sql=false"
        })
@DisplayName("SellerApplicationQueryDslRepository 통합 테스트")
class SellerApplicationQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private SellerApplicationQueryDslRepository repository() {
        return new SellerApplicationQueryDslRepository(
                new JPAQueryFactory(entityManager), new SellerApplicationConditionBuilder());
    }

    private SellerApplicationJpaEntity persist(SellerApplicationJpaEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    @Nested
    @DisplayName("findById")
    class FindByIdTest {

        @Test
        @DisplayName("ID로 입점 신청을 조회합니다")
        void findById_ReturnsEntity() {
            SellerApplicationJpaEntity saved =
                    persist(SellerApplicationJpaEntityFixtures.pendingEntity());

            var result = repository().findById(saved.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(saved.getId());
        }

        @Test
        @DisplayName("존재하지 않는 ID는 빈 Optional을 반환합니다")
        void findById_WithNonExistent_ReturnsEmpty() {
            var result = repository().findById(999L);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsById")
    class ExistsByIdTest {

        @Test
        @DisplayName("존재하는 ID는 true를 반환합니다")
        void existsById_WithExisting_ReturnsTrue() {
            SellerApplicationJpaEntity saved =
                    persist(SellerApplicationJpaEntityFixtures.pendingEntity());

            boolean exists = repository().existsById(saved.getId());

            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 ID는 false를 반환합니다")
        void existsById_WithNonExistent_ReturnsFalse() {
            boolean exists = repository().existsById(999L);

            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("existsPendingByRegistrationNumber")
    class ExistsPendingByRegistrationNumberTest {

        @Test
        @DisplayName("PENDING 상태의 사업자등록번호가 존재하면 true를 반환합니다")
        void existsPendingByRegistrationNumber_WithPending_ReturnsTrue() {
            String regNumber = "123-45-67890";
            persist(SellerApplicationJpaEntityFixtures.pendingEntity(null, regNumber));

            boolean exists = repository().existsPendingByRegistrationNumber(regNumber);

            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("PENDING이 아닌 상태의 사업자등록번호는 false를 반환합니다")
        void existsPendingByRegistrationNumber_WithApproved_ReturnsFalse() {
            // given
            // APPROVED 상태 신청 생성 (고유 사업자등록번호 사용)
            SellerApplicationJpaEntity approved =
                    persist(SellerApplicationJpaEntityFixtures.approvedEntity(1L));

            // when
            // APPROVED 상태의 사업자등록번호로 PENDING 존재 여부 확인
            boolean exists =
                    repository()
                            .existsPendingByRegistrationNumber(approved.getRegistrationNumber());

            // then
            // APPROVED 상태이므로 PENDING 검색에서는 false 반환
            assertThat(exists).isFalse();
        }

        @Test
        @DisplayName("존재하지 않는 사업자등록번호는 false를 반환합니다")
        void existsPendingByRegistrationNumber_WithNonExistent_ReturnsFalse() {
            boolean exists = repository().existsPendingByRegistrationNumber("000-00-00000");

            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("Integration - Status Filter")
    class StatusFilterTest {

        @Test
        @DisplayName("PENDING 상태만 조회됩니다")
        void existsPendingByRegistrationNumber_OnlyPending() {
            String regNumber = "111-22-33333";
            persist(SellerApplicationJpaEntityFixtures.pendingEntity(null, regNumber));
            persist(SellerApplicationJpaEntityFixtures.approvedEntity(2L)); // 다른 번호

            boolean exists = repository().existsPendingByRegistrationNumber(regNumber);

            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("REJECTED 상태는 PENDING 검색에서 제외됩니다")
        void existsPendingByRegistrationNumber_ExcludesRejected() {
            String regNumber = "222-33-44444";
            SellerApplicationJpaEntity rejected =
                    persist(SellerApplicationJpaEntityFixtures.rejectedEntity());
            // rejectedEntity는 DEFAULT_REGISTRATION_NUMBER 사용

            // 다른 번호로 테스트
            persist(
                    SellerApplicationJpaEntityFixtures.pendingEntityWithRegistrationNumber(
                            regNumber));

            boolean existsForRejected =
                    repository()
                            .existsPendingByRegistrationNumber(
                                    SellerApplicationJpaEntityFixtures.DEFAULT_REGISTRATION_NUMBER);
            boolean existsForPending = repository().existsPendingByRegistrationNumber(regNumber);

            assertThat(existsForRejected).isFalse(); // REJECTED는 제외
            assertThat(existsForPending).isTrue(); // PENDING만 조회
        }
    }
}
