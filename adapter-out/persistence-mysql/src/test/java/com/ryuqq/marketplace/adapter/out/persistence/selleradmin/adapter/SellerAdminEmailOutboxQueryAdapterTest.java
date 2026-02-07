package com.ryuqq.marketplace.adapter.out.persistence.selleradmin.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.SellerAdminEmailOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.entity.SellerAdminEmailOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.mapper.SellerAdminEmailOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.repository.SellerAdminEmailOutboxQueryDslRepository;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.selleradmin.SellerAdminFixtures;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdminEmailOutbox;
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

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SellerAdminEmailOutboxQueryAdapter 단위 테스트")
class SellerAdminEmailOutboxQueryAdapterTest {

    @InjectMocks private SellerAdminEmailOutboxQueryAdapter sut;

    @Mock private SellerAdminEmailOutboxQueryDslRepository queryDslRepository;
    @Mock private SellerAdminEmailOutboxJpaEntityMapper mapper;

    @Nested
    @DisplayName("findPendingBySellerId() - SellerId로 PENDING Outbox 조회")
    class FindPendingBySellerIdTest {

        @Test
        @DisplayName("PENDING Outbox가 존재하면 Domain을 반환한다")
        void findPendingBySellerId_WithPendingOutbox_ReturnsDomain() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            SellerAdminEmailOutboxJpaEntity entity =
                    SellerAdminEmailOutboxJpaEntityFixtures.pendingEntity();
            SellerAdminEmailOutbox domain = SellerAdminFixtures.pendingSellerAdminEmailOutbox();

            given(queryDslRepository.findPendingBySellerId(sellerId.value()))
                    .willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<SellerAdminEmailOutbox> result = sut.findPendingBySellerId(sellerId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
            then(queryDslRepository).should().findPendingBySellerId(sellerId.value());
            then(mapper).should().toDomain(entity);
        }

        @Test
        @DisplayName("PENDING Outbox가 없으면 Optional.empty()를 반환한다")
        void findPendingBySellerId_WithNoOutbox_ReturnsEmpty() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();

            given(queryDslRepository.findPendingBySellerId(sellerId.value()))
                    .willReturn(Optional.empty());

            // when
            Optional<SellerAdminEmailOutbox> result = sut.findPendingBySellerId(sellerId);

            // then
            assertThat(result).isEmpty();
            then(queryDslRepository).should().findPendingBySellerId(sellerId.value());
            then(mapper).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("findPendingOutboxesForRetry() - 재시도 대상 Outbox 목록 조회")
    class FindPendingOutboxesForRetryTest {

        @Test
        @DisplayName("재시도 대상 Outbox 목록을 반환한다")
        void findPendingOutboxesForRetry_WithOutboxes_ReturnsDomainsDecreaseDuplicatedWords() {
            // given
            Instant beforeTime = Instant.now().minusSeconds(60);
            int limit = 10;
            List<SellerAdminEmailOutboxJpaEntity> entities =
                    List.of(
                            SellerAdminEmailOutboxJpaEntityFixtures.pendingEntity(),
                            SellerAdminEmailOutboxJpaEntityFixtures.retriedPendingEntity(1));
            SellerAdminEmailOutbox domain1 = SellerAdminFixtures.pendingSellerAdminEmailOutbox();
            SellerAdminEmailOutbox domain2 = SellerAdminFixtures.retriableSellerAdminEmailOutbox();

            given(queryDslRepository.findPendingOutboxesForRetry(beforeTime, limit))
                    .willReturn(entities);
            given(mapper.toDomain(entities.get(0))).willReturn(domain1);
            given(mapper.toDomain(entities.get(1))).willReturn(domain2);

            // when
            List<SellerAdminEmailOutbox> result =
                    sut.findPendingOutboxesForRetry(beforeTime, limit);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(domain1, domain2);
            then(queryDslRepository).should().findPendingOutboxesForRetry(beforeTime, limit);
        }

        @Test
        @DisplayName("재시도 대상 Outbox가 없으면 빈 목록을 반환한다")
        void findPendingOutboxesForRetry_WithNoOutboxes_ReturnsEmptyList() {
            // given
            Instant beforeTime = Instant.now().minusSeconds(60);
            int limit = 10;

            given(queryDslRepository.findPendingOutboxesForRetry(beforeTime, limit))
                    .willReturn(List.of());

            // when
            List<SellerAdminEmailOutbox> result =
                    sut.findPendingOutboxesForRetry(beforeTime, limit);

            // then
            assertThat(result).isEmpty();
            then(queryDslRepository).should().findPendingOutboxesForRetry(beforeTime, limit);
            then(mapper).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("limit이 0이면 빈 목록을 반환한다")
        void findPendingOutboxesForRetry_WithZeroLimit_ReturnsEmptyList() {
            // given
            Instant beforeTime = Instant.now().minusSeconds(60);
            int limit = 0;

            given(queryDslRepository.findPendingOutboxesForRetry(beforeTime, limit))
                    .willReturn(List.of());

            // when
            List<SellerAdminEmailOutbox> result =
                    sut.findPendingOutboxesForRetry(beforeTime, limit);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findProcessingTimeoutOutboxes() - 타임아웃 Outbox 목록 조회")
    class FindProcessingTimeoutOutboxesTest {

        @Test
        @DisplayName("타임아웃된 Outbox 목록을 반환한다")
        void findProcessingTimeoutOutboxes_WithTimeoutOutboxes_ReturnsDomains() {
            // given
            Instant timeoutThreshold = Instant.now().minusSeconds(300);
            int limit = 10;
            List<SellerAdminEmailOutboxJpaEntity> entities =
                    List.of(
                            SellerAdminEmailOutboxJpaEntityFixtures.processingTimeoutEntity(400),
                            SellerAdminEmailOutboxJpaEntityFixtures.processingTimeoutEntity(500));
            SellerAdminEmailOutbox domain1 =
                    SellerAdminFixtures.processingTimeoutSellerAdminEmailOutbox(400);
            SellerAdminEmailOutbox domain2 =
                    SellerAdminFixtures.processingTimeoutSellerAdminEmailOutbox(500);

            given(queryDslRepository.findProcessingTimeoutOutboxes(timeoutThreshold, limit))
                    .willReturn(entities);
            given(mapper.toDomain(entities.get(0))).willReturn(domain1);
            given(mapper.toDomain(entities.get(1))).willReturn(domain2);

            // when
            List<SellerAdminEmailOutbox> result =
                    sut.findProcessingTimeoutOutboxes(timeoutThreshold, limit);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(domain1, domain2);
            then(queryDslRepository)
                    .should()
                    .findProcessingTimeoutOutboxes(timeoutThreshold, limit);
        }

        @Test
        @DisplayName("타임아웃된 Outbox가 없으면 빈 목록을 반환한다")
        void findProcessingTimeoutOutboxes_WithNoOutboxes_ReturnsEmptyList() {
            // given
            Instant timeoutThreshold = Instant.now().minusSeconds(300);
            int limit = 10;

            given(queryDslRepository.findProcessingTimeoutOutboxes(timeoutThreshold, limit))
                    .willReturn(List.of());

            // when
            List<SellerAdminEmailOutbox> result =
                    sut.findProcessingTimeoutOutboxes(timeoutThreshold, limit);

            // then
            assertThat(result).isEmpty();
            then(queryDslRepository)
                    .should()
                    .findProcessingTimeoutOutboxes(timeoutThreshold, limit);
            then(mapper).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("limit이 0이면 빈 목록을 반환한다")
        void findProcessingTimeoutOutboxes_WithZeroLimit_ReturnsEmptyList() {
            // given
            Instant timeoutThreshold = Instant.now().minusSeconds(300);
            int limit = 0;

            given(queryDslRepository.findProcessingTimeoutOutboxes(timeoutThreshold, limit))
                    .willReturn(List.of());

            // when
            List<SellerAdminEmailOutbox> result =
                    sut.findProcessingTimeoutOutboxes(timeoutThreshold, limit);

            // then
            assertThat(result).isEmpty();
        }
    }
}
