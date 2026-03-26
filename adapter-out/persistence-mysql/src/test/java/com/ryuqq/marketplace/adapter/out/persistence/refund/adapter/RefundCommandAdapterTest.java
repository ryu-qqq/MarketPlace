package com.ryuqq.marketplace.adapter.out.persistence.refund.adapter;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.adapter.out.persistence.refund.RefundClaimJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.refund.entity.RefundClaimJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.refund.mapper.RefundPersistenceMapper;
import com.ryuqq.marketplace.adapter.out.persistence.refund.repository.RefundClaimJpaRepository;
import com.ryuqq.marketplace.domain.refund.RefundFixtures;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * RefundCommandAdapterTest - 환불 클레임 Command Adapter 단위 테스트.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("RefundCommandAdapter 단위 테스트")
class RefundCommandAdapterTest {

    @Mock private RefundClaimJpaRepository claimRepository;
    @Mock private RefundPersistenceMapper mapper;

    @InjectMocks private RefundCommandAdapter commandAdapter;

    // ========================================================================
    // 1. persist 테스트
    // ========================================================================

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("Domain을 Entity로 변환 후 저장합니다")
        void persist_WithValidDomain_SavesEntity() {
            // given
            RefundClaim domain = RefundFixtures.requestedRefundClaim();
            RefundClaimJpaEntity entity = RefundClaimJpaEntityFixtures.requestedEntity();

            given(mapper.toEntity(domain)).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
            then(mapper).should().toEntity(domain);
            then(claimRepository).should().save(entity);
        }

        @Test
        @DisplayName("REQUESTED 상태 환불을 저장합니다")
        void persist_WithRequestedClaim_Saves() {
            // given
            RefundClaim domain = RefundFixtures.requestedRefundClaim();
            RefundClaimJpaEntity entity = RefundClaimJpaEntityFixtures.requestedEntity();

            given(mapper.toEntity(domain)).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
            then(claimRepository).should().save(entity);
        }

        @Test
        @DisplayName("COMPLETED 상태 환불을 저장합니다")
        void persist_WithCompletedClaim_Saves() {
            // given
            RefundClaim domain = RefundFixtures.completedRefundClaim();
            RefundClaimJpaEntity entity =
                    RefundClaimJpaEntityFixtures.completedEntity(
                            RefundClaimJpaEntityFixtures.DEFAULT_ID);

            given(mapper.toEntity(domain)).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
            then(claimRepository).should().save(entity);
        }

        @Test
        @DisplayName("REJECTED 상태 환불을 저장합니다")
        void persist_WithRejectedClaim_Saves() {
            // given
            RefundClaim domain = RefundFixtures.rejectedRefundClaim();
            RefundClaimJpaEntity entity =
                    RefundClaimJpaEntityFixtures.rejectedEntity(
                            RefundClaimJpaEntityFixtures.DEFAULT_ID);

            given(mapper.toEntity(domain)).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
            then(claimRepository).should().save(entity);
        }

        @Test
        @DisplayName("Mapper가 정확히 한 번 호출됩니다")
        void persist_CallsMapperOnce() {
            // given
            RefundClaim domain = RefundFixtures.requestedRefundClaim();
            RefundClaimJpaEntity entity = RefundClaimJpaEntityFixtures.requestedEntity();

            given(mapper.toEntity(domain)).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
            then(mapper).should(times(1)).toEntity(domain);
        }
    }
}
