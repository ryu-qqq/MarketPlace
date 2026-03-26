package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.LegacyProductIdMappingJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.LegacyProductIdMappingJpaEntity;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/** LegacyProductIdMappingQueryDslRepositoryTest - 레거시 상품 ID 매핑 QueryDslRepository 통합 테스트. */
@Tag("integration")
@DataJpaTest
@ContextConfiguration(classes = PersistenceMysqlTestApplication.class)
@TestPropertySource(
        properties = {
            "spring.flyway.enabled=false",
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.jpa.show-sql=false"
        })
@DisplayName("LegacyProductIdMappingQueryDslRepository 통합 테스트")
class LegacyProductIdMappingQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private LegacyProductIdMappingQueryDslRepository repository() {
        return new LegacyProductIdMappingQueryDslRepository(new JPAQueryFactory(entityManager));
    }

    private LegacyProductIdMappingJpaEntity persist(LegacyProductIdMappingJpaEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    @Nested
    @DisplayName("findByLegacyProductId")
    class FindByLegacyProductIdTest {

        @Test
        @DisplayName("legacyProductId로 매핑을 조회합니다")
        void findByLegacyProductId_WithExistingId_ReturnsEntity() {
            // given
            long legacyProductId = 50001L;
            persist(
                    LegacyProductIdMappingJpaEntityFixtures.newEntityWithLegacyProductId(
                            legacyProductId));

            // when
            Optional<LegacyProductIdMappingJpaEntity> result =
                    repository().findByLegacyProductId(legacyProductId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getLegacyProductId()).isEqualTo(legacyProductId);
        }

        @Test
        @DisplayName("존재하지 않는 legacyProductId로 조회 시 빈 Optional을 반환합니다")
        void findByLegacyProductId_WithNonExistingId_ReturnsEmpty() {
            // when
            Optional<LegacyProductIdMappingJpaEntity> result =
                    repository().findByLegacyProductId(99999999L);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByInternalProductId")
    class FindByInternalProductIdTest {

        @Test
        @DisplayName("internalProductId로 매핑을 조회합니다")
        void findByInternalProductId_WithExistingId_ReturnsEntity() {
            // given
            long internalProductId = 60001L;
            persist(
                    LegacyProductIdMappingJpaEntityFixtures.newEntityWithInternalProductId(
                            internalProductId));

            // when
            Optional<LegacyProductIdMappingJpaEntity> result =
                    repository().findByInternalProductId(internalProductId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getInternalProductId()).isEqualTo(internalProductId);
        }

        @Test
        @DisplayName("존재하지 않는 internalProductId로 조회 시 빈 Optional을 반환합니다")
        void findByInternalProductId_WithNonExistingId_ReturnsEmpty() {
            // when
            Optional<LegacyProductIdMappingJpaEntity> result =
                    repository().findByInternalProductId(99999999L);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByLegacyProductGroupId")
    class FindByLegacyProductGroupIdTest {

        @Test
        @DisplayName("같은 legacyProductGroupId에 속한 매핑들을 모두 조회합니다")
        void findByLegacyProductGroupId_WithExistingGroupId_ReturnsEntities() {
            // given
            long legacyGroupId = 10001L;
            long internalGroupId = 40001L;
            persist(
                    LegacyProductIdMappingJpaEntityFixtures.newEntityWithGroupId(
                            70001L, 80001L, legacyGroupId, internalGroupId));
            persist(
                    LegacyProductIdMappingJpaEntityFixtures.newEntityWithGroupId(
                            70002L, 80002L, legacyGroupId, internalGroupId));
            persist(LegacyProductIdMappingJpaEntityFixtures.newEntity()); // 다른 그룹

            // when
            List<LegacyProductIdMappingJpaEntity> result =
                    repository().findByLegacyProductGroupId(legacyGroupId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(e -> e.getLegacyProductGroupId().equals(legacyGroupId));
        }

        @Test
        @DisplayName("존재하지 않는 legacyProductGroupId로 조회 시 빈 목록을 반환합니다")
        void findByLegacyProductGroupId_WithNonExistingGroupId_ReturnsEmpty() {
            // when
            List<LegacyProductIdMappingJpaEntity> result =
                    repository().findByLegacyProductGroupId(99999999L);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByInternalProductGroupId")
    class FindByInternalProductGroupIdTest {

        @Test
        @DisplayName("같은 internalProductGroupId에 속한 매핑들을 모두 조회합니다")
        void findByInternalProductGroupId_WithExistingGroupId_ReturnsEntities() {
            // given
            long legacyGroupId = 10002L;
            long internalGroupId = 40002L;
            persist(
                    LegacyProductIdMappingJpaEntityFixtures.newEntityWithGroupId(
                            70003L, 80003L, legacyGroupId, internalGroupId));
            persist(
                    LegacyProductIdMappingJpaEntityFixtures.newEntityWithGroupId(
                            70004L, 80004L, legacyGroupId, internalGroupId));

            // when
            List<LegacyProductIdMappingJpaEntity> result =
                    repository().findByInternalProductGroupId(internalGroupId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(e -> e.getInternalProductGroupId().equals(internalGroupId));
        }
    }

    @Nested
    @DisplayName("findByLegacyProductGroupIds")
    class FindByLegacyProductGroupIdsTest {

        @Test
        @DisplayName("여러 legacyProductGroupId로 매핑들을 일괄 조회합니다")
        void findByLegacyProductGroupIds_WithMultipleGroupIds_ReturnsEntities() {
            // given
            long legacyGroupId1 = 10003L;
            long legacyGroupId2 = 10004L;
            long internalGroupId = 40003L;
            persist(
                    LegacyProductIdMappingJpaEntityFixtures.newEntityWithGroupId(
                            70005L, 80005L, legacyGroupId1, internalGroupId));
            persist(
                    LegacyProductIdMappingJpaEntityFixtures.newEntityWithGroupId(
                            70006L, 80006L, legacyGroupId2, internalGroupId));
            persist(LegacyProductIdMappingJpaEntityFixtures.newEntity()); // 다른 그룹

            // when
            List<LegacyProductIdMappingJpaEntity> result =
                    repository()
                            .findByLegacyProductGroupIds(List.of(legacyGroupId1, legacyGroupId2));

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("빈 목록 입력 시 빈 목록을 반환합니다")
        void findByLegacyProductGroupIds_WithEmptyList_ReturnsEmpty() {
            // when
            List<LegacyProductIdMappingJpaEntity> result =
                    repository().findByLegacyProductGroupIds(List.of());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("null 입력 시 빈 목록을 반환합니다")
        void findByLegacyProductGroupIds_WithNull_ReturnsEmpty() {
            // when
            List<LegacyProductIdMappingJpaEntity> result =
                    repository().findByLegacyProductGroupIds(null);

            // then
            assertThat(result).isEmpty();
        }
    }
}
