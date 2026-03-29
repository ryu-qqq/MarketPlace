package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.LegacyOrderIdMappingJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.LegacyOrderIdMappingJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.mapper.LegacyOrderIdMappingJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.repository.LegacyOrderIdMappingQueryDslRepository;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderIdMapping;
import com.ryuqq.marketplace.domain.legacyconversion.id.LegacyOrderIdMappingId;
import java.time.Instant;
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
 * LegacyOrderIdMappingQueryAdapterTest - 주문 ID 매핑 Query Adapter 단위 테스트.
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
@DisplayName("LegacyOrderIdMappingQueryAdapter 단위 테스트")
class LegacyOrderIdMappingQueryAdapterTest {

    @Mock private LegacyOrderIdMappingQueryDslRepository queryDslRepository;

    @Mock private LegacyOrderIdMappingJpaEntityMapper mapper;

    @InjectMocks private LegacyOrderIdMappingQueryAdapter queryAdapter;

    private LegacyOrderIdMapping domainMapping() {
        return LegacyOrderIdMapping.reconstitute(
                LegacyOrderIdMappingId.of(1L),
                LegacyOrderIdMappingJpaEntityFixtures.DEFAULT_LEGACY_ORDER_ID,
                LegacyOrderIdMappingJpaEntityFixtures.DEFAULT_LEGACY_PAYMENT_ID,
                LegacyOrderIdMappingJpaEntityFixtures.DEFAULT_INTERNAL_ORDER_ID,
                1001L,
                LegacyOrderIdMappingJpaEntityFixtures.DEFAULT_SALES_CHANNEL_ID,
                LegacyOrderIdMappingJpaEntityFixtures.DEFAULT_CHANNEL_NAME,
                Instant.now());
    }

    // ========================================================================
    // 1. findByLegacyOrderId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByLegacyOrderId 메서드 테스트")
    class FindByLegacyOrderIdTest {

        @Test
        @DisplayName("존재하는 legacyOrderId로 조회 시 Domain을 반환합니다")
        void findByLegacyOrderId_WithExistingId_ReturnsDomain() {
            // given
            long legacyOrderId = 10001L;
            LegacyOrderIdMappingJpaEntity entity = LegacyOrderIdMappingJpaEntityFixtures.entity();
            LegacyOrderIdMapping domain = domainMapping();

            given(queryDslRepository.findByLegacyOrderId(legacyOrderId))
                    .willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<LegacyOrderIdMapping> result = queryAdapter.findByLegacyOrderId(legacyOrderId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
            then(queryDslRepository).should().findByLegacyOrderId(legacyOrderId);
        }

        @Test
        @DisplayName("존재하지 않는 legacyOrderId로 조회 시 빈 Optional을 반환합니다")
        void findByLegacyOrderId_WithNonExistingId_ReturnsEmpty() {
            // given
            long legacyOrderId = 99999L;
            given(queryDslRepository.findByLegacyOrderId(legacyOrderId))
                    .willReturn(Optional.empty());

            // when
            Optional<LegacyOrderIdMapping> result = queryAdapter.findByLegacyOrderId(legacyOrderId);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 2. existsByLegacyOrderId 테스트
    // ========================================================================

    @Nested
    @DisplayName("existsByLegacyOrderId 메서드 테스트")
    class ExistsByLegacyOrderIdTest {

        @Test
        @DisplayName("매핑이 존재하면 true를 반환합니다")
        void existsByLegacyOrderId_WithExistingMapping_ReturnsTrue() {
            // given
            long legacyOrderId = 10001L;
            given(queryDslRepository.existsByLegacyOrderId(legacyOrderId)).willReturn(true);

            // when
            boolean result = queryAdapter.existsByLegacyOrderId(legacyOrderId);

            // then
            assertThat(result).isTrue();
            then(queryDslRepository).should().existsByLegacyOrderId(legacyOrderId);
        }

        @Test
        @DisplayName("매핑이 없으면 false를 반환합니다")
        void existsByLegacyOrderId_WithNoMapping_ReturnsFalse() {
            // given
            long legacyOrderId = 99999L;
            given(queryDslRepository.existsByLegacyOrderId(legacyOrderId)).willReturn(false);

            // when
            boolean result = queryAdapter.existsByLegacyOrderId(legacyOrderId);

            // then
            assertThat(result).isFalse();
        }
    }
}
