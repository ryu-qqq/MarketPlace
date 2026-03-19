package com.ryuqq.marketplace.adapter.out.persistence.refund.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.refund.RefundClaimJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.refund.entity.RefundClaimJpaEntity;
import com.ryuqq.marketplace.domain.refund.RefundFixtures;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import com.ryuqq.marketplace.domain.refund.vo.RefundStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * RefundPersistenceMapperTest - 환불 클레임 Entity-Domain 매퍼 단위 테스트.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@DisplayName("RefundPersistenceMapper 단위 테스트")
class RefundPersistenceMapperTest {

    private RefundPersistenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new RefundPersistenceMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("REQUESTED 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithRequestedClaim_ConvertsCorrectly() {
            // given
            RefundClaim domain = RefundFixtures.requestedRefundClaim();

            // when
            RefundClaimJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getClaimNumber()).isEqualTo(domain.claimNumberValue());
            assertThat(entity.getOrderItemId()).isEqualTo(domain.orderItemIdValue());
            assertThat(entity.getSellerId()).isEqualTo(domain.sellerId());
            assertThat(entity.getRefundQty()).isEqualTo(domain.refundQty());
            assertThat(entity.getRefundStatus()).isEqualTo(RefundStatus.REQUESTED.name());
        }

        @Test
        @DisplayName("COMPLETED 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithCompletedClaim_ConvertsCorrectly() {
            // given
            RefundClaim domain = RefundFixtures.completedRefundClaim();

            // when
            RefundClaimJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getRefundStatus()).isEqualTo(RefundStatus.COMPLETED.name());
            assertThat(entity.getCompletedAt()).isNotNull();
        }

        @Test
        @DisplayName("refundInfo가 있는 COMPLETED Domain을 Entity로 변환 시 금액 필드가 설정됩니다")
        void toEntity_WithRefundInfo_ConvertsAmountFields() {
            // given
            RefundClaim domain = RefundFixtures.completedRefundClaim();

            // when
            RefundClaimJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getOriginalAmount()).isNotNull();
            assertThat(entity.getFinalAmount()).isNotNull();
            assertThat(entity.getRefundMethod()).isNotNull();
        }

        @Test
        @DisplayName("refundInfo가 null인 REQUESTED Domain을 Entity로 변환 시 금액 필드가 null입니다")
        void toEntity_WithNullRefundInfo_ConvertsAmountFieldsToNull() {
            // given
            RefundClaim domain = RefundFixtures.requestedRefundClaim();

            // when
            RefundClaimJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getOriginalAmount()).isNull();
            assertThat(entity.getFinalAmount()).isNull();
            assertThat(entity.getDeductionAmount()).isNull();
        }

        @Test
        @DisplayName("환불 사유가 Entity의 reasonType, reasonDetail로 변환됩니다")
        void toEntity_WithRefundReason_ConvertsReasonFields() {
            // given
            RefundClaim domain = RefundFixtures.requestedRefundClaim();

            // when
            RefundClaimJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getReasonType()).isEqualTo(domain.reason().reasonType().name());
            assertThat(entity.getReasonDetail()).isEqualTo(domain.reason().reasonDetail());
        }
    }

    // ========================================================================
    // 2. toDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain 메서드 테스트")
    class ToDomainTest {

        @Test
        @DisplayName("REQUESTED Entity를 Domain으로 변환합니다")
        void toDomain_WithRequestedEntity_ConvertsCorrectly() {
            // given
            RefundClaimJpaEntity entity = RefundClaimJpaEntityFixtures.requestedEntity();

            // when
            RefundClaim domain = mapper.toDomain(entity, null);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.claimNumberValue()).isEqualTo(entity.getClaimNumber());
            assertThat(domain.orderItemIdValue()).isEqualTo(entity.getOrderItemId());
            assertThat(domain.sellerId()).isEqualTo(entity.getSellerId());
            assertThat(domain.status()).isEqualTo(RefundStatus.REQUESTED);
        }

        @Test
        @DisplayName("COMPLETED Entity를 Domain으로 변환합니다")
        void toDomain_WithCompletedEntity_ConvertsCorrectly() {
            // given
            RefundClaimJpaEntity entity =
                    RefundClaimJpaEntityFixtures.completedEntity(
                            RefundClaimJpaEntityFixtures.DEFAULT_ID);

            // when
            RefundClaim domain = mapper.toDomain(entity, null);

            // then
            assertThat(domain.status()).isEqualTo(RefundStatus.COMPLETED);
            assertThat(domain.completedAt()).isNotNull();
        }

        @Test
        @DisplayName("금액 필드가 모두 null인 Entity는 refundInfo가 null입니다")
        void toDomain_WithNullAmountFields_ReturnsNullRefundInfo() {
            // given
            RefundClaimJpaEntity entity = RefundClaimJpaEntityFixtures.requestedEntity();

            // when
            RefundClaim domain = mapper.toDomain(entity, null);

            // then
            assertThat(domain.refundInfo()).isNull();
        }

        @Test
        @DisplayName("금액 필드가 있는 Entity는 refundInfo가 설정됩니다")
        void toDomain_WithAmountFields_ReturnsRefundInfo() {
            // given
            RefundClaimJpaEntity entity =
                    RefundClaimJpaEntityFixtures.completedEntity(
                            RefundClaimJpaEntityFixtures.DEFAULT_ID);

            // when
            RefundClaim domain = mapper.toDomain(entity, null);

            // then
            assertThat(domain.refundInfo()).isNotNull();
            assertThat(domain.refundInfo().originalAmount().value())
                    .isEqualTo(entity.getOriginalAmount());
        }

        @Test
        @DisplayName("holdReason, holdAt이 있는 Entity는 holdInfo가 설정됩니다")
        void toDomain_WithHoldInfo_ReturnsHoldInfo() {
            // given
            RefundClaimJpaEntity entity =
                    RefundClaimJpaEntityFixtures.heldEntity(
                            RefundClaimJpaEntityFixtures.DEFAULT_ID, "추가 확인 필요");

            // when
            RefundClaim domain = mapper.toDomain(entity, null);

            // then
            assertThat(domain.holdInfo()).isNotNull();
            assertThat(domain.holdInfo().holdReason()).isEqualTo("추가 확인 필요");
        }

        @Test
        @DisplayName("holdReason이 null인 Entity는 holdInfo가 null입니다")
        void toDomain_WithNullHoldReason_ReturnsNullHoldInfo() {
            // given
            RefundClaimJpaEntity entity = RefundClaimJpaEntityFixtures.requestedEntity();

            // when
            RefundClaim domain = mapper.toDomain(entity, null);

            // then
            assertThat(domain.holdInfo()).isNull();
        }
    }

    // ========================================================================
    // 3. 양방향 변환 테스트
    // ========================================================================

    @Nested
    @DisplayName("양방향 변환 테스트")
    class BidirectionalConversionTest {

        @Test
        @DisplayName("Domain -> Entity -> Domain 변환 시 핵심 데이터가 보존됩니다")
        void roundTrip_DomainToEntityToDomain_PreservesCoreData() {
            // given
            RefundClaim original = RefundFixtures.requestedRefundClaim();

            // when
            RefundClaimJpaEntity entity = mapper.toEntity(original);
            RefundClaim converted = mapper.toDomain(entity, null);

            // then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.claimNumberValue()).isEqualTo(original.claimNumberValue());
            assertThat(converted.orderItemIdValue()).isEqualTo(original.orderItemIdValue());
            assertThat(converted.sellerId()).isEqualTo(original.sellerId());
            assertThat(converted.status()).isEqualTo(original.status());
            assertThat(converted.refundQty()).isEqualTo(original.refundQty());
        }

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 핵심 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesCoreData() {
            // given
            RefundClaimJpaEntity original = RefundClaimJpaEntityFixtures.requestedEntity();

            // when
            RefundClaim domain = mapper.toDomain(original, null);
            RefundClaimJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getClaimNumber()).isEqualTo(original.getClaimNumber());
            assertThat(converted.getOrderItemId()).isEqualTo(original.getOrderItemId());
            assertThat(converted.getRefundStatus()).isEqualTo(original.getRefundStatus());
            assertThat(converted.getSellerId()).isEqualTo(original.getSellerId());
        }
    }
}
