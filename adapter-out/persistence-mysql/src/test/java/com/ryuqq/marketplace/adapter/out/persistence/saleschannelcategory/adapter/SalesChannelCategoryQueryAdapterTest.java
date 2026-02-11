package com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.SalesChannelCategoryJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.entity.SalesChannelCategoryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.mapper.SalesChannelCategoryJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.repository.SalesChannelCategoryQueryDslRepository;
import com.ryuqq.marketplace.domain.saleschannelcategory.SalesChannelCategoryFixtures;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.saleschannelcategory.aggregate.SalesChannelCategory;
import com.ryuqq.marketplace.domain.saleschannelcategory.id.SalesChannelCategoryId;
import com.ryuqq.marketplace.domain.saleschannelcategory.query.SalesChannelCategorySearchCriteria;
import com.ryuqq.marketplace.domain.saleschannelcategory.query.SalesChannelCategorySortKey;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * SalesChannelCategoryQueryAdapterTest - SalesChannelCategory Query Adapter 단위 테스트.
 *
 * <p>PER-ADP-002: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-004: Entity -> Domain 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SalesChannelCategoryQueryAdapter 단위 테스트")
class SalesChannelCategoryQueryAdapterTest {

    @Mock private SalesChannelCategoryQueryDslRepository repository;

    @Mock private SalesChannelCategoryJpaEntityMapper mapper;

    @InjectMocks private SalesChannelCategoryQueryAdapter queryAdapter;

    // ========================================================================
    // 1. findById 테스트
    // ========================================================================

    @Nested
    @DisplayName("findById 메서드 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("ID로 SalesChannelCategory를 조회합니다")
        void findById_WithValidId_ReturnsCategory() {
            // given
            SalesChannelCategoryId id = SalesChannelCategoryFixtures.defaultSalesChannelCategoryId();
            SalesChannelCategoryJpaEntity entity =
                    SalesChannelCategoryJpaEntityFixtures.activeEntity(1L);
            SalesChannelCategory domain =
                    SalesChannelCategoryFixtures.activeSalesChannelCategory();

            given(repository.findById(id.value())).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<SalesChannelCategory> result = queryAdapter.findById(id);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
            then(repository).should().findById(id.value());
            then(mapper).should().toDomain(entity);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 빈 Optional을 반환합니다")
        void findById_WithNonExistentId_ReturnsEmpty() {
            // given
            SalesChannelCategoryId id = SalesChannelCategoryFixtures.salesChannelCategoryId(999L);

            given(repository.findById(id.value())).willReturn(Optional.empty());

            // when
            Optional<SalesChannelCategory> result = queryAdapter.findById(id);

            // then
            assertThat(result).isEmpty();
            then(repository).should().findById(id.value());
        }
    }

    // ========================================================================
    // 2. findByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByCriteria 메서드 테스트")
    class FindByCriteriaTest {

        @Test
        @DisplayName("검색 조건에 맞는 SalesChannelCategory 목록을 조회합니다")
        void findByCriteria_WithValidCriteria_ReturnsList() {
            // given
            QueryContext<SalesChannelCategorySortKey> queryContext =
                    QueryContext.defaultOf(SalesChannelCategorySortKey.defaultKey());
            SalesChannelCategorySearchCriteria criteria =
                    SalesChannelCategorySearchCriteria.of(null, null, null, null, queryContext);
            SalesChannelCategoryJpaEntity entity1 =
                    SalesChannelCategoryJpaEntityFixtures.activeEntity(1L);
            SalesChannelCategoryJpaEntity entity2 =
                    SalesChannelCategoryJpaEntityFixtures.activeEntity(2L);
            List<SalesChannelCategoryJpaEntity> entities = List.of(entity1, entity2);

            SalesChannelCategory domain1 =
                    SalesChannelCategoryFixtures.activeSalesChannelCategory(1L);
            SalesChannelCategory domain2 =
                    SalesChannelCategoryFixtures.activeSalesChannelCategory(2L);

            given(repository.findByCriteria(criteria)).willReturn(entities);
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<SalesChannelCategory> result = queryAdapter.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).contains(domain1, domain2);
            then(repository).should().findByCriteria(criteria);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 리스트를 반환합니다")
        void findByCriteria_WithNoResults_ReturnsEmptyList() {
            // given
            QueryContext<SalesChannelCategorySortKey> queryContext =
                    QueryContext.defaultOf(SalesChannelCategorySortKey.defaultKey());
            SalesChannelCategorySearchCriteria criteria =
                    SalesChannelCategorySearchCriteria.of(null, null, null, null, queryContext);

            given(repository.findByCriteria(criteria)).willReturn(List.of());

            // when
            List<SalesChannelCategory> result = queryAdapter.findByCriteria(criteria);

            // then
            assertThat(result).isEmpty();
            then(repository).should().findByCriteria(criteria);
        }
    }

    // ========================================================================
    // 3. countByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("countByCriteria 메서드 테스트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("검색 조건에 맞는 SalesChannelCategory 수를 반환합니다")
        void countByCriteria_WithValidCriteria_ReturnsCount() {
            // given
            QueryContext<SalesChannelCategorySortKey> queryContext =
                    QueryContext.defaultOf(SalesChannelCategorySortKey.defaultKey());
            SalesChannelCategorySearchCriteria criteria =
                    SalesChannelCategorySearchCriteria.of(null, null, null, null, queryContext);
            long expectedCount = 10L;

            given(repository.countByCriteria(criteria)).willReturn(expectedCount);

            // when
            long result = queryAdapter.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(expectedCount);
            then(repository).should().countByCriteria(criteria);
        }

        @Test
        @DisplayName("검색 결과가 없으면 0을 반환합니다")
        void countByCriteria_WithNoResults_ReturnsZero() {
            // given
            QueryContext<SalesChannelCategorySortKey> queryContext =
                    QueryContext.defaultOf(SalesChannelCategorySortKey.defaultKey());
            SalesChannelCategorySearchCriteria criteria =
                    SalesChannelCategorySearchCriteria.of(null, null, null, null, queryContext);

            given(repository.countByCriteria(criteria)).willReturn(0L);

            // when
            long result = queryAdapter.countByCriteria(criteria);

            // then
            assertThat(result).isZero();
            then(repository).should().countByCriteria(criteria);
        }
    }

    // ========================================================================
    // 4. existsBySalesChannelIdAndExternalCode 테스트
    // ========================================================================

    @Nested
    @DisplayName("existsBySalesChannelIdAndExternalCode 메서드 테스트")
    class ExistsBySalesChannelIdAndExternalCodeTest {

        @Test
        @DisplayName("SalesChannelId와 ExternalCode로 존재 여부를 확인합니다")
        void existsBySalesChannelIdAndExternalCode_WhenExists_ReturnsTrue() {
            // given
            Long salesChannelId = 1L;
            String externalCategoryCode = "CAT001";

            given(repository.existsBySalesChannelIdAndExternalCode(
                            salesChannelId, externalCategoryCode))
                    .willReturn(true);

            // when
            boolean result =
                    queryAdapter.existsBySalesChannelIdAndExternalCode(
                            salesChannelId, externalCategoryCode);

            // then
            assertThat(result).isTrue();
            then(repository)
                    .should()
                    .existsBySalesChannelIdAndExternalCode(salesChannelId, externalCategoryCode);
        }

        @Test
        @DisplayName("존재하지 않으면 false를 반환합니다")
        void existsBySalesChannelIdAndExternalCode_WhenNotExists_ReturnsFalse() {
            // given
            Long salesChannelId = 999L;
            String externalCategoryCode = "CAT999";

            given(repository.existsBySalesChannelIdAndExternalCode(
                            salesChannelId, externalCategoryCode))
                    .willReturn(false);

            // when
            boolean result =
                    queryAdapter.existsBySalesChannelIdAndExternalCode(
                            salesChannelId, externalCategoryCode);

            // then
            assertThat(result).isFalse();
            then(repository)
                    .should()
                    .existsBySalesChannelIdAndExternalCode(salesChannelId, externalCategoryCode);
        }
    }
}
