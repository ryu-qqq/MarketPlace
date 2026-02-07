package com.ryuqq.marketplace.adapter.out.persistence.seller.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerAuthOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerAuthOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.seller.mapper.SellerAuthOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerAuthOutboxQueryDslRepository;
import com.ryuqq.marketplace.domain.seller.SellerFixtures;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerAuthOutbox;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
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
 * SellerAuthOutboxQueryAdapterTest - 셀러 인증 Outbox Query Adapter 단위 테스트.
 *
 * <p>PER-ADP-004: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-005: Entity -> Domain 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SellerAuthOutboxQueryAdapter 단위 테스트")
class SellerAuthOutboxQueryAdapterTest {

    @Mock private SellerAuthOutboxQueryDslRepository queryDslRepository;

    @Mock private SellerAuthOutboxJpaEntityMapper mapper;

    @InjectMocks private SellerAuthOutboxQueryAdapter queryAdapter;

    // ========================================================================
    // 1. findPendingBySellerId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findPendingBySellerId 메서드 테스트")
    class FindPendingBySellerIdTest {

        @Test
        @DisplayName("셀러 ID로 PENDING 상태 Outbox를 조회하여 Domain으로 변환합니다")
        void findPendingBySellerId_WithValidSellerId_ReturnsDomain() {
            // given
            SellerId sellerId = SellerId.of(1L);
            SellerAuthOutboxJpaEntity entity = SellerAuthOutboxJpaEntityFixtures.pendingEntity();
            SellerAuthOutbox domain = SellerFixtures.pendingSellerAuthOutbox();

            given(queryDslRepository.findPendingBySellerId(sellerId.value()))
                    .willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<SellerAuthOutbox> result = queryAdapter.findPendingBySellerId(sellerId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
            then(queryDslRepository).should().findPendingBySellerId(sellerId.value());
            then(mapper).should().toDomain(entity);
        }

        @Test
        @DisplayName("PENDING 상태가 없으면 Optional.empty()를 반환합니다")
        void findPendingBySellerId_WithNoPending_ReturnsEmpty() {
            // given
            SellerId sellerId = SellerId.of(999L);

            given(queryDslRepository.findPendingBySellerId(sellerId.value()))
                    .willReturn(Optional.empty());

            // when
            Optional<SellerAuthOutbox> result = queryAdapter.findPendingBySellerId(sellerId);

            // then
            assertThat(result).isEmpty();
            then(queryDslRepository).should().findPendingBySellerId(sellerId.value());
        }
    }

    // ========================================================================
    // 2. findPendingOutboxesForRetry 테스트
    // ========================================================================

    @Nested
    @DisplayName("findPendingOutboxesForRetry 메서드 테스트")
    class FindPendingOutboxesForRetryTest {

        @Test
        @DisplayName("재시도 대상 PENDING Outbox 목록을 조회하여 Domain 리스트로 변환합니다")
        void findPendingOutboxesForRetry_WithValidParams_ReturnsDomainList() {
            // given
            Instant beforeTime = Instant.now().minusSeconds(300);
            int limit = 10;

            SellerAuthOutboxJpaEntity entity1 =
                    SellerAuthOutboxJpaEntityFixtures.newPendingEntity();
            SellerAuthOutboxJpaEntity entity2 =
                    SellerAuthOutboxJpaEntityFixtures.newPendingEntityWithSellerId(2L);
            List<SellerAuthOutboxJpaEntity> entities = List.of(entity1, entity2);

            SellerAuthOutbox domain1 = SellerFixtures.pendingSellerAuthOutbox();
            SellerAuthOutbox domain2 = SellerFixtures.retriableSellerAuthOutbox();

            given(queryDslRepository.findPendingOutboxesForRetry(beforeTime, limit))
                    .willReturn(entities);
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<SellerAuthOutbox> result =
                    queryAdapter.findPendingOutboxesForRetry(beforeTime, limit);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).contains(domain1, domain2);
            then(queryDslRepository).should().findPendingOutboxesForRetry(beforeTime, limit);
        }

        @Test
        @DisplayName("재시도 대상이 없으면 빈 리스트를 반환합니다")
        void findPendingOutboxesForRetry_WithNoResults_ReturnsEmptyList() {
            // given
            Instant beforeTime = Instant.now().minusSeconds(300);
            int limit = 10;

            given(queryDslRepository.findPendingOutboxesForRetry(beforeTime, limit))
                    .willReturn(List.of());

            // when
            List<SellerAuthOutbox> result =
                    queryAdapter.findPendingOutboxesForRetry(beforeTime, limit);

            // then
            assertThat(result).isEmpty();
            then(queryDslRepository).should().findPendingOutboxesForRetry(beforeTime, limit);
        }
    }

    // ========================================================================
    // 3. findProcessingTimeoutOutboxes 테스트
    // ========================================================================

    @Nested
    @DisplayName("findProcessingTimeoutOutboxes 메서드 테스트")
    class FindProcessingTimeoutOutboxesTest {

        @Test
        @DisplayName("타임아웃된 PROCESSING Outbox 목록을 조회하여 Domain 리스트로 변환합니다")
        void findProcessingTimeoutOutboxes_WithValidParams_ReturnsDomainList() {
            // given
            Instant timeoutThreshold = Instant.now().minusSeconds(600);
            int limit = 10;

            SellerAuthOutboxJpaEntity entity1 =
                    SellerAuthOutboxJpaEntityFixtures.processingEntity();
            SellerAuthOutboxJpaEntity entity2 =
                    SellerAuthOutboxJpaEntityFixtures.processingTimeoutEntity(700L);
            List<SellerAuthOutboxJpaEntity> entities = List.of(entity1, entity2);

            SellerAuthOutbox domain1 = SellerFixtures.processingSellerAuthOutbox();
            SellerAuthOutbox domain2 = SellerFixtures.processingTimeoutSellerAuthOutbox(700L);

            given(queryDslRepository.findProcessingTimeoutOutboxes(timeoutThreshold, limit))
                    .willReturn(entities);
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<SellerAuthOutbox> result =
                    queryAdapter.findProcessingTimeoutOutboxes(timeoutThreshold, limit);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).contains(domain1, domain2);
            then(queryDslRepository)
                    .should()
                    .findProcessingTimeoutOutboxes(timeoutThreshold, limit);
        }

        @Test
        @DisplayName("타임아웃된 PROCESSING이 없으면 빈 리스트를 반환합니다")
        void findProcessingTimeoutOutboxes_WithNoResults_ReturnsEmptyList() {
            // given
            Instant timeoutThreshold = Instant.now().minusSeconds(600);
            int limit = 10;

            given(queryDslRepository.findProcessingTimeoutOutboxes(timeoutThreshold, limit))
                    .willReturn(List.of());

            // when
            List<SellerAuthOutbox> result =
                    queryAdapter.findProcessingTimeoutOutboxes(timeoutThreshold, limit);

            // then
            assertThat(result).isEmpty();
            then(queryDslRepository)
                    .should()
                    .findProcessingTimeoutOutboxes(timeoutThreshold, limit);
        }

        @Test
        @DisplayName("Mapper가 각 Entity에 대해 호출됩니다")
        void findProcessingTimeoutOutboxes_CallsMapperForEachEntity() {
            // given
            Instant timeoutThreshold = Instant.now().minusSeconds(600);
            int limit = 10;

            SellerAuthOutboxJpaEntity entity1 =
                    SellerAuthOutboxJpaEntityFixtures.processingEntity();
            SellerAuthOutboxJpaEntity entity2 =
                    SellerAuthOutboxJpaEntityFixtures.processingTimeoutEntity(700L);
            SellerAuthOutboxJpaEntity entity3 =
                    SellerAuthOutboxJpaEntityFixtures.processingTimeoutEntity(800L);
            List<SellerAuthOutboxJpaEntity> entities = List.of(entity1, entity2, entity3);

            SellerAuthOutbox domain = SellerFixtures.processingSellerAuthOutbox();
            given(queryDslRepository.findProcessingTimeoutOutboxes(timeoutThreshold, limit))
                    .willReturn(entities);
            given(mapper.toDomain(entity1)).willReturn(domain);
            given(mapper.toDomain(entity2)).willReturn(domain);
            given(mapper.toDomain(entity3)).willReturn(domain);

            // when
            queryAdapter.findProcessingTimeoutOutboxes(timeoutThreshold, limit);

            // then
            then(mapper)
                    .should(org.mockito.Mockito.times(3))
                    .toDomain(org.mockito.ArgumentMatchers.any(SellerAuthOutboxJpaEntity.class));
        }
    }
}
