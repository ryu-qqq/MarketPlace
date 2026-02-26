package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.LegacyProductIdMappingJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.LegacyProductIdMappingJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.mapper.LegacyProductIdMappingJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.repository.LegacyProductIdMappingQueryDslRepository;
import com.ryuqq.marketplace.domain.legacyconversion.LegacyConversionFixtures;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyProductIdMapping;
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
 * LegacyProductIdMappingQueryAdapterTest - ID 매핑 Query Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-005: Entity -> Domain 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyProductIdMappingQueryAdapter 단위 테스트")
class LegacyProductIdMappingQueryAdapterTest {

    @Mock private LegacyProductIdMappingQueryDslRepository queryDslRepository;

    @Mock private LegacyProductIdMappingJpaEntityMapper mapper;

    @InjectMocks private LegacyProductIdMappingQueryAdapter queryAdapter;

    // ========================================================================
    // 1. findByLegacyProductId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByLegacyProductId 메서드 테스트")
    class FindByLegacyProductIdTest {

        @Test
        @DisplayName("존재하는 legacyProductId로 조회 시 Domain을 반환합니다")
        void findByLegacyProductId_WithExistingId_ReturnsDomain() {
            // given
            long legacyProductId = 200L;
            LegacyProductIdMappingJpaEntity entity =
                    LegacyProductIdMappingJpaEntityFixtures.entity();
            LegacyProductIdMapping domain = LegacyConversionFixtures.mapping();

            given(queryDslRepository.findByLegacyProductId(legacyProductId))
                    .willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<LegacyProductIdMapping> result =
                    queryAdapter.findByLegacyProductId(legacyProductId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("존재하지 않는 legacyProductId로 조회 시 빈 Optional을 반환합니다")
        void findByLegacyProductId_WithNonExistingId_ReturnsEmpty() {
            // given
            long legacyProductId = 9999L;
            given(queryDslRepository.findByLegacyProductId(legacyProductId))
                    .willReturn(Optional.empty());

            // when
            Optional<LegacyProductIdMapping> result =
                    queryAdapter.findByLegacyProductId(legacyProductId);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 2. findByInternalProductId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByInternalProductId 메서드 테스트")
    class FindByInternalProductIdTest {

        @Test
        @DisplayName("존재하는 internalProductId로 조회 시 Domain을 반환합니다")
        void findByInternalProductId_WithExistingId_ReturnsDomain() {
            // given
            long internalProductId = 300L;
            LegacyProductIdMappingJpaEntity entity =
                    LegacyProductIdMappingJpaEntityFixtures.entity();
            LegacyProductIdMapping domain = LegacyConversionFixtures.mapping();

            given(queryDslRepository.findByInternalProductId(internalProductId))
                    .willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<LegacyProductIdMapping> result =
                    queryAdapter.findByInternalProductId(internalProductId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("존재하지 않는 internalProductId로 조회 시 빈 Optional을 반환합니다")
        void findByInternalProductId_WithNonExistingId_ReturnsEmpty() {
            // given
            long internalProductId = 9999L;
            given(queryDslRepository.findByInternalProductId(internalProductId))
                    .willReturn(Optional.empty());

            // when
            Optional<LegacyProductIdMapping> result =
                    queryAdapter.findByInternalProductId(internalProductId);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 3. findByLegacyProductGroupId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByLegacyProductGroupId 메서드 테스트")
    class FindByLegacyProductGroupIdTest {

        @Test
        @DisplayName("legacyProductGroupId로 매핑 목록을 반환합니다")
        void findByLegacyProductGroupId_WithExistingGroupId_ReturnsDomainList() {
            // given
            long legacyProductGroupId = 100L;
            LegacyProductIdMappingJpaEntity entity1 =
                    LegacyProductIdMappingJpaEntityFixtures.entity(1L);
            LegacyProductIdMappingJpaEntity entity2 =
                    LegacyProductIdMappingJpaEntityFixtures.entity(2L);
            LegacyProductIdMapping domain1 = LegacyConversionFixtures.mapping(1L);
            LegacyProductIdMapping domain2 = LegacyConversionFixtures.mapping(2L);

            given(queryDslRepository.findByLegacyProductGroupId(legacyProductGroupId))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<LegacyProductIdMapping> result =
                    queryAdapter.findByLegacyProductGroupId(legacyProductGroupId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(domain1, domain2);
            then(queryDslRepository).should().findByLegacyProductGroupId(legacyProductGroupId);
        }

        @Test
        @DisplayName("결과가 없으면 빈 리스트를 반환합니다")
        void findByLegacyProductGroupId_WithNoMappings_ReturnsEmptyList() {
            // given
            long legacyProductGroupId = 9999L;
            given(queryDslRepository.findByLegacyProductGroupId(legacyProductGroupId))
                    .willReturn(List.of());

            // when
            List<LegacyProductIdMapping> result =
                    queryAdapter.findByLegacyProductGroupId(legacyProductGroupId);

            // then
            assertThat(result).isEmpty();
        }
    }
}
