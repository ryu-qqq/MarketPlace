package com.ryuqq.marketplace.adapter.out.persistence.cancel.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.cancel.CancelJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.cancel.condition.CancelConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.cancel.entity.CancelJpaEntity;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/**
 * CancelQueryDslRepository 통합 테스트.
 *
 * <p>findAllByOrderItemId 메서드의 조회 정확성을 검증합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
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
@DisplayName("CancelQueryDslRepository 통합 테스트")
class CancelQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private CancelQueryDslRepository repository() {
        return new CancelQueryDslRepository(
                new JPAQueryFactory(entityManager), new CancelConditionBuilder());
    }

    private CancelJpaEntity persist(CancelJpaEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    // ========================================================================
    // findAllByOrderItemId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findAllByOrderItemId")
    class FindAllByOrderItemIdTest {

        @Test
        @DisplayName("동일 orderItemId에 Cancel 2건이 있으면 2건 모두 반환합니다")
        void findAllByOrderItemId_WithTwoCancels_ReturnsBoth() {
            // given
            String orderItemId = "01900000-0000-7000-0000-000000000010";
            persist(
                    CancelJpaEntityFixtures.requestedEntityWithNumber(
                            "cancel-id-001", "CAN-001", orderItemId, 10L));
            persist(
                    CancelJpaEntityFixtures.requestedEntityWithNumber(
                            "cancel-id-002", "CAN-002", orderItemId, 10L));

            // when
            List<CancelJpaEntity> result = repository().findAllByOrderItemId(orderItemId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result)
                    .extracting(CancelJpaEntity::getId)
                    .containsExactlyInAnyOrder("cancel-id-001", "cancel-id-002");
        }

        @Test
        @DisplayName("다른 orderItemId의 Cancel은 조회되지 않습니다")
        void findAllByOrderItemId_WithDifferentOrderItemIds_ReturnsOnlyMatching() {
            // given
            String orderItemIdA = "01900000-0000-7000-0000-000000000011";
            String orderItemIdB = "01900000-0000-7000-0000-000000000012";
            persist(
                    CancelJpaEntityFixtures.requestedEntityWithNumber(
                            "cancel-id-A", "CAN-A", orderItemIdA, 10L));
            persist(
                    CancelJpaEntityFixtures.requestedEntityWithNumber(
                            "cancel-id-B", "CAN-B", orderItemIdB, 10L));

            // when
            List<CancelJpaEntity> result = repository().findAllByOrderItemId(orderItemIdA);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo("cancel-id-A");
            assertThat(result.get(0).getOrderItemId()).isEqualTo(orderItemIdA);
        }

        @Test
        @DisplayName("해당 orderItemId에 Cancel이 없으면 빈 목록을 반환합니다")
        void findAllByOrderItemId_WithNoCancels_ReturnsEmptyList() {
            // given
            String orderItemId = "01900000-0000-7000-0000-000000000099";

            // when
            List<CancelJpaEntity> result = repository().findAllByOrderItemId(orderItemId);

            // then
            assertThat(result).isEmpty();
        }
    }
}
