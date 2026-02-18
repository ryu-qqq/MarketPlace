package com.ryuqq.marketplace.adapter.out.persistence.selleradmin.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.SellerAdminAuthOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.entity.SellerAdminAuthOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.mapper.SellerAdminAuthOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.repository.SellerAdminAuthOutboxQueryDslRepository;
import com.ryuqq.marketplace.domain.selleradmin.SellerAdminFixtures;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdminAuthOutbox;
import com.ryuqq.marketplace.domain.selleradmin.id.SellerAdminId;
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
 * SellerAdminAuthOutboxQueryAdapterTest - 셀러 관리자 인증 Outbox 조회 어댑터 단위 테스트.
 *
 * <p>PER-ADP-004: QueryAdapter는 QueryDslRepository만 사용.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SellerAdminAuthOutboxQueryAdapter 단위 테스트")
class SellerAdminAuthOutboxQueryAdapterTest {

    @InjectMocks private SellerAdminAuthOutboxQueryAdapter sut;

    @Mock private SellerAdminAuthOutboxQueryDslRepository queryDslRepository;
    @Mock private SellerAdminAuthOutboxJpaEntityMapper mapper;

    @Nested
    @DisplayName("findPendingBySellerAdminId() - SellerAdminId로 PENDING Outbox 조회")
    class FindPendingBySellerAdminIdTest {

        @Test
        @DisplayName("PENDING Outbox가 존재하면 Domain을 반환한다")
        void findPendingBySellerAdminId_WithPendingOutbox_ReturnsDomain() {
            // given
            SellerAdminId sellerAdminId =
                    SellerAdminId.of(SellerAdminFixtures.DEFAULT_SELLER_ADMIN_ID);
            SellerAdminAuthOutboxJpaEntity entity =
                    SellerAdminAuthOutboxJpaEntityFixtures.pendingEntity();
            SellerAdminAuthOutbox domain = SellerAdminFixtures.pendingSellerAdminAuthOutbox();

            given(queryDslRepository.findPendingBySellerAdminId(sellerAdminId.value()))
                    .willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<SellerAdminAuthOutbox> result = sut.findPendingBySellerAdminId(sellerAdminId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
            then(queryDslRepository).should().findPendingBySellerAdminId(sellerAdminId.value());
            then(mapper).should().toDomain(entity);
        }

        @Test
        @DisplayName("PENDING Outbox가 없으면 Optional.empty()를 반환한다")
        void findPendingBySellerAdminId_WithNoOutbox_ReturnsEmpty() {
            // given
            SellerAdminId sellerAdminId =
                    SellerAdminId.of(SellerAdminFixtures.DEFAULT_SELLER_ADMIN_ID);

            given(queryDslRepository.findPendingBySellerAdminId(sellerAdminId.value()))
                    .willReturn(Optional.empty());

            // when
            Optional<SellerAdminAuthOutbox> result = sut.findPendingBySellerAdminId(sellerAdminId);

            // then
            assertThat(result).isEmpty();
            then(queryDslRepository).should().findPendingBySellerAdminId(sellerAdminId.value());
            then(mapper).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("findPendingOutboxesForRetry() - 재시도 대상 Outbox 목록 조회")
    class FindPendingOutboxesForRetryTest {

        @Test
        @DisplayName("재시도 대상 Outbox 목록을 반환한다")
        void findPendingOutboxesForRetry_WithOutboxes_ReturnsDomains() {
            // given
            Instant beforeTime = Instant.now().minusSeconds(60);
            int limit = 10;
            List<SellerAdminAuthOutboxJpaEntity> entities =
                    List.of(
                            SellerAdminAuthOutboxJpaEntityFixtures.pendingEntity(),
                            SellerAdminAuthOutboxJpaEntityFixtures.retriedPendingEntity(1));
            SellerAdminAuthOutbox domain1 = SellerAdminFixtures.pendingSellerAdminAuthOutbox();
            SellerAdminAuthOutbox domain2 = SellerAdminFixtures.retriableSellerAdminAuthOutbox();

            given(queryDslRepository.findPendingOutboxesForRetry(beforeTime, limit))
                    .willReturn(entities);
            given(mapper.toDomain(entities.get(0))).willReturn(domain1);
            given(mapper.toDomain(entities.get(1))).willReturn(domain2);

            // when
            List<SellerAdminAuthOutbox> result = sut.findPendingOutboxesForRetry(beforeTime, limit);

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
            List<SellerAdminAuthOutbox> result = sut.findPendingOutboxesForRetry(beforeTime, limit);

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
            List<SellerAdminAuthOutbox> result = sut.findPendingOutboxesForRetry(beforeTime, limit);

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
            List<SellerAdminAuthOutboxJpaEntity> entities =
                    List.of(
                            SellerAdminAuthOutboxJpaEntityFixtures.processingTimeoutEntity(400),
                            SellerAdminAuthOutboxJpaEntityFixtures.processingTimeoutEntity(500));
            SellerAdminAuthOutbox domain1 =
                    SellerAdminFixtures.processingTimeoutSellerAdminAuthOutbox(400);
            SellerAdminAuthOutbox domain2 =
                    SellerAdminFixtures.processingTimeoutSellerAdminAuthOutbox(500);

            given(queryDslRepository.findProcessingTimeoutOutboxes(timeoutThreshold, limit))
                    .willReturn(entities);
            given(mapper.toDomain(entities.get(0))).willReturn(domain1);
            given(mapper.toDomain(entities.get(1))).willReturn(domain2);

            // when
            List<SellerAdminAuthOutbox> result =
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
            List<SellerAdminAuthOutbox> result =
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
            List<SellerAdminAuthOutbox> result =
                    sut.findProcessingTimeoutOutboxes(timeoutThreshold, limit);

            // then
            assertThat(result).isEmpty();
        }
    }
}
