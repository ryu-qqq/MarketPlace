package com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.SalesChannelBrandJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.condition.SalesChannelBrandConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.entity.SalesChannelBrandJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.saleschannelbrand.query.SalesChannelBrandSearchCriteria;
import com.ryuqq.marketplace.domain.saleschannelbrand.query.SalesChannelBrandSortKey;
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

/**
 * SalesChannelBrandQueryDslRepositoryTest - SalesChannelBrand QueryDslRepository 통합 테스트.
 *
 * <p>SalesChannelBrand는 soft-delete를 적용하지 않으므로 존재 여부 및 조회 동작을 검증합니다.
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
@DisplayName("SalesChannelBrandQueryDslRepository 통합 테스트")
class SalesChannelBrandQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private SalesChannelBrandQueryDslRepository repository() {
        return new SalesChannelBrandQueryDslRepository(
                new JPAQueryFactory(entityManager), new SalesChannelBrandConditionBuilder());
    }

    private SalesChannelBrandJpaEntity persist(SalesChannelBrandJpaEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    private SalesChannelBrandSearchCriteria defaultCriteria() {
        return SalesChannelBrandSearchCriteria.of(
                List.of(),
                List.of(),
                null,
                null,
                QueryContext.defaultOf(SalesChannelBrandSortKey.defaultKey()));
    }

    @Nested
    @DisplayName("findById")
    class FindByIdTest {

        @Test
        @DisplayName("존재하는 Entity를 ID로 조회합니다")
        void findById_WithExistingId_ReturnsEntity() {
            SalesChannelBrandJpaEntity saved =
                    persist(SalesChannelBrandJpaEntityFixtures.newEntity());

            Optional<SalesChannelBrandJpaEntity> result = repository().findById(saved.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(saved.getId());
        }

        @Test
        @DisplayName("존재하지 않는 ID는 빈 결과를 반환합니다")
        void findById_WithNonExistentId_ReturnsEmpty() {
            Optional<SalesChannelBrandJpaEntity> result = repository().findById(999L);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("비활성 Entity도 ID로 조회됩니다")
        void findById_WithInactiveEntity_ReturnsEntity() {
            SalesChannelBrandJpaEntity inactive =
                    persist(SalesChannelBrandJpaEntityFixtures.inactiveEntity());

            Optional<SalesChannelBrandJpaEntity> result = repository().findById(inactive.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getStatus()).isEqualTo("INACTIVE");
        }
    }

    @Nested
    @DisplayName("existsBySalesChannelIdAndExternalCode")
    class ExistsBySalesChannelIdAndExternalCodeTest {

        @Test
        @DisplayName("존재하는 salesChannelId와 externalBrandCode 조합은 true를 반환합니다")
        void existsBySalesChannelIdAndExternalCode_WithExisting_ReturnsTrue() {
            // given
            String externalCode = "UNIQUE-BRAND-CODE";
            persist(
                    SalesChannelBrandJpaEntityFixtures.newEntityWithCodeAndChannel(
                            SalesChannelBrandJpaEntityFixtures.DEFAULT_SALES_CHANNEL_ID,
                            externalCode));

            // when
            boolean result =
                    repository()
                            .existsBySalesChannelIdAndExternalCode(
                                    SalesChannelBrandJpaEntityFixtures.DEFAULT_SALES_CHANNEL_ID,
                                    externalCode);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 조합은 false를 반환합니다")
        void existsBySalesChannelIdAndExternalCode_WithNonExistent_ReturnsFalse() {
            boolean result =
                    repository().existsBySalesChannelIdAndExternalCode(999L, "NON-EXISTENT-CODE");

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("다른 salesChannelId의 코드는 false를 반환합니다")
        void existsBySalesChannelIdAndExternalCode_WithDifferentSalesChannelId_ReturnsFalse() {
            // given
            String externalCode = "BRAND-CODE-CHANNEL";
            persist(
                    SalesChannelBrandJpaEntityFixtures.newEntityWithCodeAndChannel(
                            SalesChannelBrandJpaEntityFixtures.DEFAULT_SALES_CHANNEL_ID,
                            externalCode));

            // when
            boolean result = repository().existsBySalesChannelIdAndExternalCode(999L, externalCode);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("비활성 Entity의 코드도 존재 확인됩니다")
        void existsBySalesChannelIdAndExternalCode_WithInactiveEntity_ReturnsTrue() {
            // given
            String externalCode = "INACTIVE-BRAND-CODE";
            persist(
                    SalesChannelBrandJpaEntityFixtures.inactiveEntityWithParams(
                            SalesChannelBrandJpaEntityFixtures.DEFAULT_SALES_CHANNEL_ID,
                            externalCode,
                            "비활성 브랜드"));

            // when
            boolean result =
                    repository()
                            .existsBySalesChannelIdAndExternalCode(
                                    SalesChannelBrandJpaEntityFixtures.DEFAULT_SALES_CHANNEL_ID,
                                    externalCode);

            // then
            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("findByCriteria")
    class FindByCriteriaTest {

        @Test
        @DisplayName("기본 조건으로 Entity 목록을 조회합니다")
        void findByCriteria_WithDefaultCriteria_ReturnsEntities() {
            // given
            SalesChannelBrandJpaEntity brand1 =
                    persist(SalesChannelBrandJpaEntityFixtures.newEntity());
            SalesChannelBrandJpaEntity brand2 =
                    persist(SalesChannelBrandJpaEntityFixtures.newEntity());

            // when
            List<SalesChannelBrandJpaEntity> result =
                    repository().findByCriteria(defaultCriteria());

            // then
            assertThat(result)
                    .extracting(SalesChannelBrandJpaEntity::getId)
                    .contains(brand1.getId(), brand2.getId());
        }

        @Test
        @DisplayName("특정 salesChannelId로 필터링합니다")
        void findByCriteria_WithSalesChannelIdFilter_ReturnsFilteredEntities() {
            // given
            Long targetChannelId = 10L;
            SalesChannelBrandJpaEntity targetBrand =
                    persist(
                            SalesChannelBrandJpaEntityFixtures.activeEntityWithSalesChannel(
                                    targetChannelId));
            persist(SalesChannelBrandJpaEntityFixtures.activeEntityWithSalesChannel(20L));

            SalesChannelBrandSearchCriteria criteria =
                    SalesChannelBrandSearchCriteria.of(
                            List.of(targetChannelId),
                            List.of(),
                            null,
                            null,
                            QueryContext.defaultOf(SalesChannelBrandSortKey.defaultKey()));

            // when
            List<SalesChannelBrandJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result)
                    .extracting(SalesChannelBrandJpaEntity::getId)
                    .contains(targetBrand.getId());
            assertThat(result).allMatch(e -> e.getSalesChannelId().equals(targetChannelId));
        }
    }

    @Nested
    @DisplayName("countByCriteria")
    class CountByCriteriaTest {

        @Test
        @DisplayName("전체 Entity 개수를 반환합니다")
        void countByCriteria_ReturnsCorrectCount() {
            // given
            persist(SalesChannelBrandJpaEntityFixtures.newEntity());
            persist(SalesChannelBrandJpaEntityFixtures.newEntity());

            // when
            long count = repository().countByCriteria(defaultCriteria());

            // then
            assertThat(count).isGreaterThanOrEqualTo(2);
        }

        @Test
        @DisplayName("Entity가 없으면 0을 반환합니다")
        void countByCriteria_WithNoEntities_ReturnsZero() {
            long count = repository().countByCriteria(defaultCriteria());

            assertThat(count).isZero();
        }
    }
}
