package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.LegacyOrderIdMappingJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.LegacyOrderIdMappingJpaEntity;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/** LegacyOrderIdMappingQueryDslRepositoryTest - 레거시 주문 ID 매핑 QueryDslRepository 통합 테스트. */
@Tag("integration")
@DataJpaTest
@ContextConfiguration(classes = PersistenceMysqlTestApplication.class)
@TestPropertySource(
        properties = {
            "spring.flyway.enabled=false",
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.jpa.show-sql=false"
        })
@DisplayName("LegacyOrderIdMappingQueryDslRepository 통합 테스트")
class LegacyOrderIdMappingQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private LegacyOrderIdMappingQueryDslRepository repository() {
        return new LegacyOrderIdMappingQueryDslRepository(new JPAQueryFactory(entityManager));
    }

    private LegacyOrderIdMappingJpaEntity persist(LegacyOrderIdMappingJpaEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    @Nested
    @DisplayName("findByLegacyOrderId")
    class FindByLegacyOrderIdTest {

        @Test
        @DisplayName("legacyOrderId로 매핑을 조회합니다")
        void findByLegacyOrderId_WithExistingId_ReturnsEntity() {
            // given
            long legacyOrderId = 80001L;
            persist(
                    LegacyOrderIdMappingJpaEntityFixtures.newEntityWithLegacyOrderId(
                            legacyOrderId));

            // when
            Optional<LegacyOrderIdMappingJpaEntity> result =
                    repository().findByLegacyOrderId(legacyOrderId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getLegacyOrderId()).isEqualTo(legacyOrderId);
        }

        @Test
        @DisplayName("존재하지 않는 legacyOrderId로 조회 시 빈 Optional을 반환합니다")
        void findByLegacyOrderId_WithNonExistingId_ReturnsEmpty() {
            // when
            Optional<LegacyOrderIdMappingJpaEntity> result =
                    repository().findByLegacyOrderId(99999999L);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByLegacyOrderId")
    class ExistsByLegacyOrderIdTest {

        @Test
        @DisplayName("매핑이 존재하면 true를 반환합니다")
        void existsByLegacyOrderId_WithExistingMapping_ReturnsTrue() {
            // given
            long legacyOrderId = 80002L;
            persist(
                    LegacyOrderIdMappingJpaEntityFixtures.newEntityWithLegacyOrderId(
                            legacyOrderId));

            // when
            boolean result = repository().existsByLegacyOrderId(legacyOrderId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("매핑이 없으면 false를 반환합니다")
        void existsByLegacyOrderId_WithNoMapping_ReturnsFalse() {
            // when
            boolean result = repository().existsByLegacyOrderId(99999998L);

            // then
            assertThat(result).isFalse();
        }
    }
}
