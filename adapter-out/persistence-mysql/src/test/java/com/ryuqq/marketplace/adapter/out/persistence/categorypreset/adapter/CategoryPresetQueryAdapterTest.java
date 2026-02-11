package com.ryuqq.marketplace.adapter.out.persistence.categorypreset.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.adapter.out.persistence.categorypreset.CategoryPresetJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.categorypreset.composite.CategoryPresetCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.categorypreset.entity.CategoryPresetJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.categorypreset.mapper.CategoryPresetJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.categorypreset.repository.CategoryPresetQueryDslRepository;
import com.ryuqq.marketplace.application.categorypreset.dto.response.CategoryPresetResult;
import com.ryuqq.marketplace.domain.categorypreset.CategoryPresetFixtures;
import com.ryuqq.marketplace.domain.categorypreset.aggregate.CategoryPreset;
import com.ryuqq.marketplace.domain.categorypreset.id.CategoryPresetId;
import com.ryuqq.marketplace.domain.categorypreset.query.CategoryPresetSearchCriteria;
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
 * CategoryPresetQueryAdapterTest - CategoryPreset 조회 어댑터 단위 테스트.
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
@DisplayName("CategoryPresetQueryAdapter 단위 테스트")
class CategoryPresetQueryAdapterTest {

    @Mock private CategoryPresetQueryDslRepository repository;

    @Mock private CategoryPresetJpaEntityMapper mapper;

    @InjectMocks private CategoryPresetQueryAdapter adapter;

    // ========================================================================
    // 1. findById 테스트
    // ========================================================================

    @Nested
    @DisplayName("findById 메서드 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("ID로 CategoryPreset을 조회하여 Domain으로 반환합니다")
        void findById_WithValidId_ReturnsDomain() {
            // given
            Long id = 1L;
            CategoryPresetId categoryPresetId = CategoryPresetId.of(id);
            CategoryPresetJpaEntity entity = CategoryPresetJpaEntityFixtures.activeEntity(id);
            CategoryPreset domain = CategoryPresetFixtures.activeCategoryPreset(id);

            given(repository.findById(id)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<CategoryPreset> result = adapter.findById(categoryPresetId);

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
            CategoryPresetId categoryPresetId = CategoryPresetId.of(id);

            given(repository.findById(id)).willReturn(Optional.empty());

            // when
            Optional<CategoryPreset> result = adapter.findById(categoryPresetId);

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
        @DisplayName("검색 조건으로 CategoryPreset 목록을 조회하여 Result로 반환합니다")
        void findByCriteria_WithCriteria_ReturnsResultList() {
            // given
            CategoryPresetSearchCriteria criteria = CategoryPresetFixtures.defaultSearchCriteria();
            CategoryPresetCompositeDto dto1 = createCompositeDto(1L);
            CategoryPresetCompositeDto dto2 = createCompositeDto(2L);
            List<CategoryPresetCompositeDto> dtos = List.of(dto1, dto2);

            CategoryPresetResult result1 = createResult(1L);
            CategoryPresetResult result2 = createResult(2L);

            given(repository.findByCriteria(criteria)).willReturn(dtos);
            given(mapper.toResult(dto1)).willReturn(result1);
            given(mapper.toResult(dto2)).willReturn(result2);

            // when
            List<CategoryPresetResult> results = adapter.findByCriteria(criteria);

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
            CategoryPresetSearchCriteria criteria = CategoryPresetFixtures.defaultSearchCriteria();

            given(repository.findByCriteria(criteria)).willReturn(List.of());

            // when
            List<CategoryPresetResult> results = adapter.findByCriteria(criteria);

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
            CategoryPresetSearchCriteria criteria = CategoryPresetFixtures.defaultSearchCriteria();
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
            CategoryPresetSearchCriteria criteria = CategoryPresetFixtures.defaultSearchCriteria();

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
        @DisplayName("ID 목록으로 CategoryPreset 목록을 조회하여 Domain 목록으로 반환합니다")
        void findAllByIds_WithValidIds_ReturnsDomainList() {
            // given
            List<Long> ids = List.of(1L, 2L, 3L);
            CategoryPresetJpaEntity entity1 = CategoryPresetJpaEntityFixtures.activeEntity(1L);
            CategoryPresetJpaEntity entity2 = CategoryPresetJpaEntityFixtures.activeEntity(2L);
            CategoryPresetJpaEntity entity3 = CategoryPresetJpaEntityFixtures.activeEntity(3L);
            List<CategoryPresetJpaEntity> entities = List.of(entity1, entity2, entity3);

            CategoryPreset domain1 = CategoryPresetFixtures.activeCategoryPreset(1L);
            CategoryPreset domain2 = CategoryPresetFixtures.activeCategoryPreset(2L);
            CategoryPreset domain3 = CategoryPresetFixtures.activeCategoryPreset(3L);

            given(repository.findAllByIds(ids)).willReturn(entities);
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);
            given(mapper.toDomain(entity3)).willReturn(domain3);

            // when
            List<CategoryPreset> results = adapter.findAllByIds(ids);

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
            List<CategoryPreset> results = adapter.findAllByIds(ids);

            // then
            assertThat(results).isEmpty();
            then(repository).should(times(1)).findAllByIds(ids);
        }
    }

    // ========================================================================
    // 5. findSalesChannelCategoryIdByCode 테스트
    // ========================================================================

    @Nested
    @DisplayName("findSalesChannelCategoryIdByCode 메서드 테스트")
    class FindSalesChannelCategoryIdByCodeTest {

        @Test
        @DisplayName("SalesChannelId와 CategoryCode로 SalesChannelCategoryId를 조회합니다")
        void findSalesChannelCategoryIdByCode_WithValidParameters_ReturnsCategoryId() {
            // given
            Long salesChannelId = 10L;
            String categoryCode = "CATE001";
            Long expectedCategoryId = 100L;

            given(repository.findSalesChannelCategoryIdByCode(salesChannelId, categoryCode))
                    .willReturn(Optional.of(expectedCategoryId));

            // when
            Optional<Long> result = adapter.findSalesChannelCategoryIdByCode(salesChannelId, categoryCode);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expectedCategoryId);
            then(repository).should(times(1)).findSalesChannelCategoryIdByCode(salesChannelId, categoryCode);
        }

        @Test
        @DisplayName("존재하지 않는 코드 조회 시 빈 Optional을 반환합니다")
        void findSalesChannelCategoryIdByCode_WithNonExistentCode_ReturnsEmpty() {
            // given
            Long salesChannelId = 10L;
            String categoryCode = "INVALID_CODE";

            given(repository.findSalesChannelCategoryIdByCode(salesChannelId, categoryCode))
                    .willReturn(Optional.empty());

            // when
            Optional<Long> result = adapter.findSalesChannelCategoryIdByCode(salesChannelId, categoryCode);

            // then
            assertThat(result).isEmpty();
            then(repository).should(times(1)).findSalesChannelCategoryIdByCode(salesChannelId, categoryCode);
        }
    }

    // ========================================================================
    // 테스트 헬퍼 메서드
    // ========================================================================

    private CategoryPresetCompositeDto createCompositeDto(Long id) {
        return new CategoryPresetCompositeDto(
                id,
                100L,
                "테스트샵",
                "account123",
                1L,
                "테스트채널",
                200L,
                "C123",
                "전자제품 > 컴퓨터",
                "테스트프리셋",
                "ACTIVE",
                Instant.now());
    }

    private CategoryPresetResult createResult(Long id) {
        return new CategoryPresetResult(
                id,
                100L,
                "테스트샵",
                1L,
                "테스트채널",
                "account123",
                "테스트프리셋",
                "전자제품 > 컴퓨터",
                "C123",
                Instant.now());
    }
}
