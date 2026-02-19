package com.ryuqq.marketplace.adapter.out.persistence.channeloptionmapping.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.adapter.out.persistence.channeloptionmapping.ChannelOptionMappingJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.channeloptionmapping.entity.ChannelOptionMappingJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.channeloptionmapping.mapper.ChannelOptionMappingJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.channeloptionmapping.repository.ChannelOptionMappingQueryDslRepository;
import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionValueId;
import com.ryuqq.marketplace.domain.channeloptionmapping.ChannelOptionMappingFixtures;
import com.ryuqq.marketplace.domain.channeloptionmapping.aggregate.ChannelOptionMapping;
import com.ryuqq.marketplace.domain.channeloptionmapping.id.ChannelOptionMappingId;
import com.ryuqq.marketplace.domain.channeloptionmapping.query.ChannelOptionMappingSearchCriteria;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
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
 * ChannelOptionMappingQueryAdapterTest - ChannelOptionMapping 조회 어댑터 단위 테스트.
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
@DisplayName("ChannelOptionMappingQueryAdapter 단위 테스트")
class ChannelOptionMappingQueryAdapterTest {

    @Mock private ChannelOptionMappingQueryDslRepository queryDslRepository;

    @Mock private ChannelOptionMappingJpaEntityMapper mapper;

    @InjectMocks private ChannelOptionMappingQueryAdapter adapter;

    // ========================================================================
    // 1. findById 테스트
    // ========================================================================

    @Nested
    @DisplayName("findById 메서드 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("ID로 ChannelOptionMapping을 조회하여 Domain으로 반환합니다")
        void findById_WithValidId_ReturnsDomain() {
            // given
            Long id = 1L;
            ChannelOptionMappingId mappingId = ChannelOptionMappingId.of(id);
            ChannelOptionMappingJpaEntity entity = ChannelOptionMappingJpaEntityFixtures.entity(id);
            ChannelOptionMapping domain =
                    ChannelOptionMappingFixtures.existingChannelOptionMapping(id);

            given(queryDslRepository.findById(id)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<ChannelOptionMapping> result = adapter.findById(mappingId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
            then(queryDslRepository).should(times(1)).findById(id);
            then(mapper).should(times(1)).toDomain(entity);
        }

        @Test
        @DisplayName("존재하지 않는 ID 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistentId_ReturnsEmpty() {
            // given
            Long id = 999L;
            ChannelOptionMappingId mappingId = ChannelOptionMappingId.of(id);

            given(queryDslRepository.findById(id)).willReturn(Optional.empty());

            // when
            Optional<ChannelOptionMapping> result = adapter.findById(mappingId);

            // then
            assertThat(result).isEmpty();
            then(queryDslRepository).should(times(1)).findById(id);
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
        @DisplayName("검색 조건으로 ChannelOptionMapping 목록을 조회하여 반환합니다")
        void findByCriteria_WithCriteria_ReturnsDomainList() {
            // given
            ChannelOptionMappingSearchCriteria criteria =
                    ChannelOptionMappingSearchCriteria.defaultCriteria();
            ChannelOptionMappingJpaEntity entity1 =
                    ChannelOptionMappingJpaEntityFixtures.entity(1L);
            ChannelOptionMappingJpaEntity entity2 =
                    ChannelOptionMappingJpaEntityFixtures.entity(2L);
            List<ChannelOptionMappingJpaEntity> entities = List.of(entity1, entity2);

            ChannelOptionMapping domain1 =
                    ChannelOptionMappingFixtures.existingChannelOptionMapping(1L);
            ChannelOptionMapping domain2 =
                    ChannelOptionMappingFixtures.existingChannelOptionMapping(2L);

            given(queryDslRepository.findByCriteria(criteria)).willReturn(entities);
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<ChannelOptionMapping> result = adapter.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(domain1, domain2);
            then(queryDslRepository).should(times(1)).findByCriteria(criteria);
            then(mapper).should(times(2)).toDomain(org.mockito.ArgumentMatchers.any());
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 목록을 반환합니다")
        void findByCriteria_WithNoResults_ReturnsEmptyList() {
            // given
            ChannelOptionMappingSearchCriteria criteria =
                    ChannelOptionMappingSearchCriteria.defaultCriteria();

            given(queryDslRepository.findByCriteria(criteria)).willReturn(List.of());

            // when
            List<ChannelOptionMapping> result = adapter.findByCriteria(criteria);

            // then
            assertThat(result).isEmpty();
            then(queryDslRepository).should(times(1)).findByCriteria(criteria);
            then(mapper).should(times(0)).toDomain(org.mockito.ArgumentMatchers.any());
        }

        @Test
        @DisplayName("판매채널 필터 조건으로 조회합니다")
        void findByCriteria_WithSalesChannelFilter_ReturnsDomainList() {
            // given
            ChannelOptionMappingSearchCriteria criteria =
                    ChannelOptionMappingSearchCriteria.of(
                            1L,
                            null,
                            ChannelOptionMappingSearchCriteria.defaultCriteria().queryContext());
            ChannelOptionMappingJpaEntity entity =
                    ChannelOptionMappingJpaEntityFixtures.entityWithSalesChannelId(1L);
            ChannelOptionMapping domain =
                    ChannelOptionMappingFixtures.existingChannelOptionMapping(
                            1L, 1L, 100L, "CODE-001");

            given(queryDslRepository.findByCriteria(criteria)).willReturn(List.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            List<ChannelOptionMapping> result = adapter.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            then(queryDslRepository).should(times(1)).findByCriteria(criteria);
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
            ChannelOptionMappingSearchCriteria criteria =
                    ChannelOptionMappingSearchCriteria.defaultCriteria();
            long expectedCount = 5L;

            given(queryDslRepository.countByCriteria(criteria)).willReturn(expectedCount);

            // when
            long result = adapter.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(expectedCount);
            then(queryDslRepository).should(times(1)).countByCriteria(criteria);
        }

        @Test
        @DisplayName("검색 결과가 없으면 0을 반환합니다")
        void countByCriteria_WithNoResults_ReturnsZero() {
            // given
            ChannelOptionMappingSearchCriteria criteria =
                    ChannelOptionMappingSearchCriteria.defaultCriteria();

            given(queryDslRepository.countByCriteria(criteria)).willReturn(0L);

            // when
            long result = adapter.countByCriteria(criteria);

            // then
            assertThat(result).isZero();
            then(queryDslRepository).should(times(1)).countByCriteria(criteria);
        }
    }

    // ========================================================================
    // 4. existsBySalesChannelIdAndCanonicalOptionValueId 테스트
    // ========================================================================

    @Nested
    @DisplayName("existsBySalesChannelIdAndCanonicalOptionValueId 메서드 테스트")
    class ExistsBySalesChannelIdAndCanonicalOptionValueIdTest {

        @Test
        @DisplayName("salesChannelId와 canonicalOptionValueId 조합이 존재하면 true를 반환합니다")
        void exists_WhenExists_ReturnsTrue() {
            // given
            SalesChannelId salesChannelId = SalesChannelId.of(1L);
            CanonicalOptionValueId canonicalOptionValueId = CanonicalOptionValueId.of(100L);

            given(queryDslRepository.existsBySalesChannelIdAndCanonicalOptionValueId(1L, 100L))
                    .willReturn(true);

            // when
            boolean result =
                    adapter.existsBySalesChannelIdAndCanonicalOptionValueId(
                            salesChannelId, canonicalOptionValueId);

            // then
            assertThat(result).isTrue();
            then(queryDslRepository)
                    .should(times(1))
                    .existsBySalesChannelIdAndCanonicalOptionValueId(1L, 100L);
        }

        @Test
        @DisplayName("salesChannelId와 canonicalOptionValueId 조합이 존재하지 않으면 false를 반환합니다")
        void exists_WhenNotExists_ReturnsFalse() {
            // given
            SalesChannelId salesChannelId = SalesChannelId.of(999L);
            CanonicalOptionValueId canonicalOptionValueId = CanonicalOptionValueId.of(999L);

            given(queryDslRepository.existsBySalesChannelIdAndCanonicalOptionValueId(999L, 999L))
                    .willReturn(false);

            // when
            boolean result =
                    adapter.existsBySalesChannelIdAndCanonicalOptionValueId(
                            salesChannelId, canonicalOptionValueId);

            // then
            assertThat(result).isFalse();
            then(queryDslRepository)
                    .should(times(1))
                    .existsBySalesChannelIdAndCanonicalOptionValueId(999L, 999L);
        }

        @Test
        @DisplayName("ID 값이 올바르게 unwrap되어 Repository에 전달됩니다")
        void exists_UnwrapsIdValues_PassesToRepository() {
            // given
            Long salesChannelIdValue = 5L;
            Long canonicalOptionValueIdValue = 200L;
            SalesChannelId salesChannelId = SalesChannelId.of(salesChannelIdValue);
            CanonicalOptionValueId canonicalOptionValueId =
                    CanonicalOptionValueId.of(canonicalOptionValueIdValue);

            given(
                            queryDslRepository.existsBySalesChannelIdAndCanonicalOptionValueId(
                                    salesChannelIdValue, canonicalOptionValueIdValue))
                    .willReturn(true);

            // when
            adapter.existsBySalesChannelIdAndCanonicalOptionValueId(
                    salesChannelId, canonicalOptionValueId);

            // then
            then(queryDslRepository)
                    .should()
                    .existsBySalesChannelIdAndCanonicalOptionValueId(
                            salesChannelIdValue, canonicalOptionValueIdValue);
        }
    }
}
