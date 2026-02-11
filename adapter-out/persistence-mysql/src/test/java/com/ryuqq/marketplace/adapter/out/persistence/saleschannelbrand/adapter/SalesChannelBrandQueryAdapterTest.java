package com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.SalesChannelBrandJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.entity.SalesChannelBrandJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.mapper.SalesChannelBrandJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.repository.SalesChannelBrandQueryDslRepository;
import com.ryuqq.marketplace.domain.saleschannelbrand.SalesChannelBrandFixtures;
import com.ryuqq.marketplace.domain.saleschannelbrand.aggregate.SalesChannelBrand;
import com.ryuqq.marketplace.domain.saleschannelbrand.id.SalesChannelBrandId;
import com.ryuqq.marketplace.domain.saleschannelbrand.query.SalesChannelBrandSearchCriteria;
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
 * SalesChannelBrandQueryAdapterTest - SalesChannelBrand Query Adapter 단위 테스트.
 *
 * <p>PER-ADP-004: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-005: Entity -> Domain 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SalesChannelBrandQueryAdapter 단위 테스트")
class SalesChannelBrandQueryAdapterTest {

    @Mock private SalesChannelBrandQueryDslRepository repository;

    @Mock private SalesChannelBrandJpaEntityMapper mapper;

    @Mock private SalesChannelBrandSearchCriteria criteria;

    @InjectMocks private SalesChannelBrandQueryAdapter queryAdapter;

    // ========================================================================
    // 1. findById 테스트
    // ========================================================================

    @Nested
    @DisplayName("findById 메서드 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("존재하는 ID로 조회 시 Domain을 반환합니다")
        void findById_WithExistingId_ReturnsDomain() {
            // given
            SalesChannelBrandId id = SalesChannelBrandId.of(1L);
            SalesChannelBrandJpaEntity entity =
                    SalesChannelBrandJpaEntityFixtures.activeEntity(1L);
            SalesChannelBrand domain = SalesChannelBrandFixtures.activeSalesChannelBrand(1L);

            given(repository.findById(1L)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<SalesChannelBrand> result = queryAdapter.findById(id);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistingId_ReturnsEmpty() {
            // given
            SalesChannelBrandId id = SalesChannelBrandId.of(999L);
            given(repository.findById(999L)).willReturn(Optional.empty());

            // when
            Optional<SalesChannelBrand> result = queryAdapter.findById(id);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 2. findByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByCriteria 메서드 테스트")
    class FindByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 브랜드 목록을 조회합니다")
        void findByCriteria_WithValidCriteria_ReturnsDomainList() {
            // given
            SalesChannelBrandJpaEntity entity1 =
                    SalesChannelBrandJpaEntityFixtures.activeEntity(1L);
            SalesChannelBrandJpaEntity entity2 =
                    SalesChannelBrandJpaEntityFixtures.activeEntity(2L);
            SalesChannelBrand domain1 = SalesChannelBrandFixtures.activeSalesChannelBrand(1L);
            SalesChannelBrand domain2 = SalesChannelBrandFixtures.activeSalesChannelBrand(2L);

            given(repository.findByCriteria(criteria)).willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<SalesChannelBrand> result = queryAdapter.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            then(repository).should().findByCriteria(criteria);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 리스트를 반환합니다")
        void findByCriteria_WithNoResults_ReturnsEmptyList() {
            // given
            given(repository.findByCriteria(criteria)).willReturn(List.of());

            // when
            List<SalesChannelBrand> result = queryAdapter.findByCriteria(criteria);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("여러 salesChannelId로 검색한 결과를 반환합니다")
        void findByCriteria_WithMultipleSalesChannels_ReturnsList() {
            // given
            SalesChannelBrandJpaEntity entity1 =
                    SalesChannelBrandJpaEntityFixtures.activeEntityWithSalesChannel(1L);
            SalesChannelBrandJpaEntity entity2 =
                    SalesChannelBrandJpaEntityFixtures.activeEntityWithSalesChannel(2L);
            // Set IDs for entities
            entity1 =
                    SalesChannelBrandJpaEntity.create(
                            1L,
                            entity1.getSalesChannelId(),
                            entity1.getExternalBrandCode(),
                            entity1.getExternalBrandName(),
                            entity1.getStatus(),
                            entity1.getCreatedAt(),
                            entity1.getUpdatedAt());
            entity2 =
                    SalesChannelBrandJpaEntity.create(
                            2L,
                            entity2.getSalesChannelId(),
                            entity2.getExternalBrandCode(),
                            entity2.getExternalBrandName(),
                            entity2.getStatus(),
                            entity2.getCreatedAt(),
                            entity2.getUpdatedAt());

            SalesChannelBrand domain1 =
                    SalesChannelBrandFixtures.activeSalesChannelBrand(
                            1L, 1L, entity1.getExternalBrandCode());
            SalesChannelBrand domain2 =
                    SalesChannelBrandFixtures.activeSalesChannelBrand(
                            2L, 2L, entity2.getExternalBrandCode());

            given(repository.findByCriteria(criteria)).willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<SalesChannelBrand> result = queryAdapter.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
        }
    }

    // ========================================================================
    // 3. countByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("countByCriteria 메서드 테스트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 브랜드 개수를 반환합니다")
        void countByCriteria_WithValidCriteria_ReturnsCount() {
            // given
            given(repository.countByCriteria(criteria)).willReturn(5L);

            // when
            long result = queryAdapter.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(5L);
        }

        @Test
        @DisplayName("검색 결과가 없으면 0을 반환합니다")
        void countByCriteria_WithNoResults_ReturnsZero() {
            // given
            given(repository.countByCriteria(criteria)).willReturn(0L);

            // when
            long result = queryAdapter.countByCriteria(criteria);

            // then
            assertThat(result).isZero();
        }

        @Test
        @DisplayName("특정 salesChannelId로 검색한 개수를 반환합니다")
        void countByCriteria_WithSpecificSalesChannel_ReturnsCount() {
            // given
            given(repository.countByCriteria(criteria)).willReturn(3L);

            // when
            long result = queryAdapter.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(3L);
        }
    }

    // ========================================================================
    // 4. existsBySalesChannelIdAndExternalCode 테스트
    // ========================================================================

    @Nested
    @DisplayName("existsBySalesChannelIdAndExternalCode 메서드 테스트")
    class ExistsBySalesChannelIdAndExternalCodeTest {

        @Test
        @DisplayName("존재하는 salesChannelId와 externalCode로 조회 시 true를 반환합니다")
        void existsBySalesChannelIdAndExternalCode_WithExisting_ReturnsTrue() {
            // given
            Long salesChannelId = 1L;
            String externalCode = "BRAND-001";
            given(repository.existsBySalesChannelIdAndExternalCode(salesChannelId, externalCode))
                    .willReturn(true);

            // when
            boolean result =
                    queryAdapter.existsBySalesChannelIdAndExternalCode(
                            salesChannelId, externalCode);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 salesChannelId와 externalCode로 조회 시 false를 반환합니다")
        void existsBySalesChannelIdAndExternalCode_WithNonExisting_ReturnsFalse() {
            // given
            Long salesChannelId = 999L;
            String externalCode = "NON-EXISTING";
            given(repository.existsBySalesChannelIdAndExternalCode(salesChannelId, externalCode))
                    .willReturn(false);

            // when
            boolean result =
                    queryAdapter.existsBySalesChannelIdAndExternalCode(
                            salesChannelId, externalCode);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("같은 externalCode지만 다른 salesChannelId로 조회 시 false를 반환합니다")
        void existsBySalesChannelIdAndExternalCode_WithDifferentSalesChannel_ReturnsFalse() {
            // given
            Long salesChannelId = 2L;
            String externalCode = "BRAND-001";
            given(repository.existsBySalesChannelIdAndExternalCode(salesChannelId, externalCode))
                    .willReturn(false);

            // when
            boolean result =
                    queryAdapter.existsBySalesChannelIdAndExternalCode(
                            salesChannelId, externalCode);

            // then
            assertThat(result).isFalse();
        }
    }
}
