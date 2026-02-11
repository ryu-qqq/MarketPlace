package com.ryuqq.marketplace.adapter.out.persistence.brandpreset.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.adapter.out.persistence.brandpreset.BrandPresetJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.brandpreset.composite.BrandPresetCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.brandpreset.entity.BrandPresetJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.brandpreset.mapper.BrandPresetJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.brandpreset.repository.BrandPresetQueryDslRepository;
import com.ryuqq.marketplace.application.brandpreset.dto.response.BrandPresetResult;
import com.ryuqq.marketplace.domain.brandpreset.BrandPresetFixtures;
import com.ryuqq.marketplace.domain.brandpreset.aggregate.BrandPreset;
import com.ryuqq.marketplace.domain.brandpreset.id.BrandPresetId;
import com.ryuqq.marketplace.domain.brandpreset.query.BrandPresetSearchCriteria;
import java.time.Instant;
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
 * BrandPresetQueryAdapterTest - BrandPreset 조회 어댑터 단위 테스트.
 *
 * <p>PER-ADP-002: Port 인터페이스 구현 + Repository 호출.
 *
 * <p>PER-ADP-003: Mapper를 통한 변환 + 비즈니스 로직 없음.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("BrandPresetQueryAdapter 단위 테스트")
class BrandPresetQueryAdapterTest {

    @Mock private BrandPresetQueryDslRepository repository;

    @Mock private BrandPresetJpaEntityMapper mapper;

    @InjectMocks private BrandPresetQueryAdapter adapter;

    // ========================================================================
    // 1. findById 테스트
    // ========================================================================

    @Nested
    @DisplayName("findById 메서드 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("ID로 BrandPreset을 조회하여 Domain으로 반환합니다")
        void findById_WithValidId_ReturnsDomain() {
            // given
            Long id = 1L;
            BrandPresetId brandPresetId = BrandPresetId.of(id);
            BrandPresetJpaEntity entity = BrandPresetJpaEntityFixtures.activeEntity(id);
            BrandPreset domain = BrandPresetFixtures.activeBrandPreset(id);

            given(repository.findById(id)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<BrandPreset> result = adapter.findById(brandPresetId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
            then(repository).should(times(1)).findById(id);
            then(mapper).should(times(1)).toDomain(entity);
        }

        @Test
        @DisplayName("존재하지 않는 ID 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistentId_ReturnsEmpty() {
            // given
            Long id = 999L;
            BrandPresetId brandPresetId = BrandPresetId.of(id);

            given(repository.findById(id)).willReturn(Optional.empty());

            // when
            Optional<BrandPreset> result = adapter.findById(brandPresetId);

            // then
            assertThat(result).isEmpty();
            then(repository).should(times(1)).findById(id);
            then(mapper).should(times(0)).toDomain(org.mockito.ArgumentMatchers.any());
        }
    }

    // ========================================================================
    // 2. findByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByCriteria 메서드 테스트")
    class FindByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 BrandPreset 목록을 조회하여 Result로 반환합니다")
        void findByCriteria_WithCriteria_ReturnsResultList() {
            // given
            BrandPresetSearchCriteria criteria = BrandPresetFixtures.defaultSearchCriteria();
            BrandPresetCompositeDto dto1 = createCompositeDto(1L);
            BrandPresetCompositeDto dto2 = createCompositeDto(2L);
            List<BrandPresetCompositeDto> dtos = List.of(dto1, dto2);

            BrandPresetResult result1 = createResult(1L);
            BrandPresetResult result2 = createResult(2L);

            given(repository.findByCriteria(criteria)).willReturn(dtos);
            given(mapper.toResult(dto1)).willReturn(result1);
            given(mapper.toResult(dto2)).willReturn(result2);

            // when
            List<BrandPresetResult> results = adapter.findByCriteria(criteria);

            // then
            assertThat(results).hasSize(2);
            assertThat(results).containsExactly(result1, result2);
            then(repository).should(times(1)).findByCriteria(criteria);
            then(mapper).should(times(2)).toResult(org.mockito.ArgumentMatchers.any());
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 목록을 반환합니다")
        void findByCriteria_WithNoResults_ReturnsEmptyList() {
            // given
            BrandPresetSearchCriteria criteria = BrandPresetFixtures.defaultSearchCriteria();

            given(repository.findByCriteria(criteria)).willReturn(List.of());

            // when
            List<BrandPresetResult> results = adapter.findByCriteria(criteria);

            // then
            assertThat(results).isEmpty();
            then(repository).should(times(1)).findByCriteria(criteria);
        }
    }

    // ========================================================================
    // 3. countByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("countByCriteria 메서드 테스트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 개수를 조회합니다")
        void countByCriteria_WithCriteria_ReturnsCount() {
            // given
            BrandPresetSearchCriteria criteria = BrandPresetFixtures.defaultSearchCriteria();
            long expectedCount = 10L;

            given(repository.countByCriteria(criteria)).willReturn(expectedCount);

            // when
            long result = adapter.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(expectedCount);
            then(repository).should(times(1)).countByCriteria(criteria);
        }

        @Test
        @DisplayName("검색 결과가 없으면 0을 반환합니다")
        void countByCriteria_WithNoResults_ReturnsZero() {
            // given
            BrandPresetSearchCriteria criteria = BrandPresetFixtures.defaultSearchCriteria();

            given(repository.countByCriteria(criteria)).willReturn(0L);

            // when
            long result = adapter.countByCriteria(criteria);

            // then
            assertThat(result).isZero();
            then(repository).should(times(1)).countByCriteria(criteria);
        }
    }

    // ========================================================================
    // 4. findAllByIds 테스트
    // ========================================================================

    @Nested
    @DisplayName("findAllByIds 메서드 테스트")
    class FindAllByIdsTest {

        @Test
        @DisplayName("ID 목록으로 BrandPreset 목록을 조회하여 Domain 목록으로 반환합니다")
        void findAllByIds_WithValidIds_ReturnsDomainList() {
            // given
            List<Long> ids = List.of(1L, 2L, 3L);
            BrandPresetJpaEntity entity1 = BrandPresetJpaEntityFixtures.activeEntity(1L);
            BrandPresetJpaEntity entity2 = BrandPresetJpaEntityFixtures.activeEntity(2L);
            BrandPresetJpaEntity entity3 = BrandPresetJpaEntityFixtures.activeEntity(3L);
            List<BrandPresetJpaEntity> entities = List.of(entity1, entity2, entity3);

            BrandPreset domain1 = BrandPresetFixtures.activeBrandPreset(1L);
            BrandPreset domain2 = BrandPresetFixtures.activeBrandPreset(2L);
            BrandPreset domain3 = BrandPresetFixtures.activeBrandPreset(3L);

            given(repository.findAllByIds(ids)).willReturn(entities);
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);
            given(mapper.toDomain(entity3)).willReturn(domain3);

            // when
            List<BrandPreset> results = adapter.findAllByIds(ids);

            // then
            assertThat(results).hasSize(3);
            assertThat(results).containsExactly(domain1, domain2, domain3);
            then(repository).should(times(1)).findAllByIds(ids);
            then(mapper).should(times(3)).toDomain(org.mockito.ArgumentMatchers.any());
        }

        @Test
        @DisplayName("ID 목록이 비어있으면 빈 목록을 반환합니다")
        void findAllByIds_WithEmptyIds_ReturnsEmptyList() {
            // given
            List<Long> ids = List.of();

            given(repository.findAllByIds(ids)).willReturn(List.of());

            // when
            List<BrandPreset> results = adapter.findAllByIds(ids);

            // then
            assertThat(results).isEmpty();
            then(repository).should(times(1)).findAllByIds(ids);
        }
    }

    // ========================================================================
    // 5. findSalesChannelIdBySalesChannelBrandId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findSalesChannelIdBySalesChannelBrandId 메서드 테스트")
    class FindSalesChannelIdBySalesChannelBrandIdTest {

        @Test
        @DisplayName("SalesChannelBrandId로 SalesChannelId를 조회합니다")
        void findSalesChannelIdBySalesChannelBrandId_WithValidId_ReturnsSalesChannelId() {
            // given
            Long salesChannelBrandId = 100L;
            Long expectedSalesChannelId = 10L;

            given(repository.findSalesChannelIdBySalesChannelBrandId(salesChannelBrandId))
                    .willReturn(Optional.of(expectedSalesChannelId));

            // when
            Optional<Long> result =
                    adapter.findSalesChannelIdBySalesChannelBrandId(salesChannelBrandId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expectedSalesChannelId);
            then(repository)
                    .should(times(1))
                    .findSalesChannelIdBySalesChannelBrandId(salesChannelBrandId);
        }

        @Test
        @DisplayName("존재하지 않는 SalesChannelBrandId 조회 시 빈 Optional을 반환합니다")
        void findSalesChannelIdBySalesChannelBrandId_WithNonExistentId_ReturnsEmpty() {
            // given
            Long salesChannelBrandId = 999L;

            given(repository.findSalesChannelIdBySalesChannelBrandId(salesChannelBrandId))
                    .willReturn(Optional.empty());

            // when
            Optional<Long> result =
                    adapter.findSalesChannelIdBySalesChannelBrandId(salesChannelBrandId);

            // then
            assertThat(result).isEmpty();
            then(repository)
                    .should(times(1))
                    .findSalesChannelIdBySalesChannelBrandId(salesChannelBrandId);
        }
    }

    // ========================================================================
    // 테스트 헬퍼 메서드
    // ========================================================================

    private BrandPresetCompositeDto createCompositeDto(Long id) {
        return new BrandPresetCompositeDto(
                id,
                100L,
                "테스트샵",
                "account123",
                1L,
                "테스트채널",
                200L,
                "B123",
                "테스트브랜드",
                "테스트프리셋",
                "ACTIVE",
                Instant.now());
    }

    private BrandPresetResult createResult(Long id) {
        return new BrandPresetResult(
                id,
                100L,
                "테스트샵",
                1L,
                "테스트채널",
                "account123",
                "테스트프리셋",
                "테스트브랜드",
                "B123",
                Instant.now());
    }
}
