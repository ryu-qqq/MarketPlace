package com.ryuqq.marketplace.adapter.out.persistence.cancel.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.cancel.CancelJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.cancel.entity.CancelJpaEntity;
import com.ryuqq.marketplace.domain.cancel.CancelFixtures;
import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import com.ryuqq.marketplace.domain.cancel.vo.CancelStatus;
import com.ryuqq.marketplace.domain.cancel.vo.CancelType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * CancelJpaEntityMapper 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만 포함.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@DisplayName("CancelJpaEntityMapper 단위 테스트")
class CancelJpaEntityMapperTest {

    private CancelJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CancelJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("REQUESTED 상태 Cancel Domain을 Entity로 변환합니다")
        void toEntity_WithRequestedCancel_ConvertsCorrectly() {
            // given
            Cancel domain = CancelFixtures.requestedCancel();

            // when
            CancelJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getCancelNumber()).isEqualTo(domain.cancelNumberValue());
            assertThat(entity.getOrderItemId()).isEqualTo(domain.orderItemIdValue());
            assertThat(entity.getSellerId()).isEqualTo(domain.sellerId());
            assertThat(entity.getCancelQty()).isEqualTo(domain.cancelQty());
            assertThat(entity.getCancelType()).isEqualTo(CancelType.BUYER_CANCEL.name());
            assertThat(entity.getCancelStatus()).isEqualTo(CancelStatus.REQUESTED.name());
        }

        @Test
        @DisplayName("reasonType과 reasonDetail이 Entity에 올바르게 매핑됩니다")
        void toEntity_WithCancelReason_MapsReasonFieldsCorrectly() {
            // given
            Cancel domain = CancelFixtures.requestedCancel();

            // when
            CancelJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getReasonType()).isEqualTo(domain.reason().reasonType().name());
            assertThat(entity.getReasonDetail()).isEqualTo(domain.reason().reasonDetail());
        }

        @Test
        @DisplayName("refundInfo가 null이면 환불 관련 필드들이 null로 매핑됩니다")
        void toEntity_WithNullRefundInfo_MapsRefundFieldsAsNull() {
            // given
            Cancel domain = CancelFixtures.requestedCancel();

            // when
            CancelJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getRefundAmount()).isNull();
            assertThat(entity.getRefundMethod()).isNull();
            assertThat(entity.getRefundStatus()).isNull();
            assertThat(entity.getRefundedAt()).isNull();
            assertThat(entity.getPgRefundId()).isNull();
        }

        @Test
        @DisplayName("refundInfo가 있으면 환불 관련 필드들이 Entity에 매핑됩니다")
        void toEntity_WithRefundInfo_MapsRefundFieldsCorrectly() {
            // given
            Cancel domain = CancelFixtures.completedCancel();

            // when
            CancelJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getCancelStatus()).isEqualTo(CancelStatus.COMPLETED.name());
            assertThat(entity.getRefundAmount())
                    .isEqualTo(domain.refundInfo().refundAmount().value());
            assertThat(entity.getRefundMethod()).isEqualTo(domain.refundInfo().refundMethod());
            assertThat(entity.getRefundStatus()).isEqualTo(domain.refundInfo().refundStatus());
            assertThat(entity.getPgRefundId()).isEqualTo(domain.refundInfo().pgRefundId());
        }

        @Test
        @DisplayName("requestedBy, processedBy, requestedAt, processedAt이 Entity에 올바르게 매핑됩니다")
        void toEntity_WithTimestampFields_MapsTimestampFieldsCorrectly() {
            // given
            Cancel domain = CancelFixtures.approvedCancel();

            // when
            CancelJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getRequestedBy()).isEqualTo(domain.requestedBy());
            assertThat(entity.getProcessedBy()).isEqualTo(domain.processedBy());
            assertThat(entity.getRequestedAt()).isEqualTo(domain.requestedAt());
            assertThat(entity.getProcessedAt()).isEqualTo(domain.processedAt());
        }

        @Test
        @DisplayName("APPROVED 상태 Cancel Domain을 Entity로 변환합니다")
        void toEntity_WithApprovedCancel_ConvertsCorrectly() {
            // given
            Cancel domain = CancelFixtures.approvedCancel();

            // when
            CancelJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getCancelStatus()).isEqualTo(CancelStatus.APPROVED.name());
            assertThat(entity.getProcessedBy()).isEqualTo(domain.processedBy());
        }

        @Test
        @DisplayName("COMPLETED 상태 Cancel Domain을 Entity로 변환하면 completedAt이 매핑됩니다")
        void toEntity_WithCompletedCancel_MapsCompletedAtCorrectly() {
            // given
            Cancel domain = CancelFixtures.completedCancel();

            // when
            CancelJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getCancelStatus()).isEqualTo(CancelStatus.COMPLETED.name());
            assertThat(entity.getCompletedAt()).isEqualTo(domain.completedAt());
        }
    }

    // ========================================================================
    // 2. toDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain 메서드 테스트")
    class ToDomainTest {

        @Test
        @DisplayName("REQUESTED 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithRequestedEntity_ConvertsCorrectly() {
            // given
            CancelJpaEntity entity =
                    CancelJpaEntityFixtures.requestedEntity(
                            CancelJpaEntityFixtures.DEFAULT_ID,
                            CancelJpaEntityFixtures.DEFAULT_ORDER_ITEM_ID,
                            CancelJpaEntityFixtures.DEFAULT_SELLER_ID);

            // when
            Cancel domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.cancelNumberValue()).isEqualTo(entity.getCancelNumber());
            assertThat(domain.orderItemIdValue()).isEqualTo(entity.getOrderItemId());
            assertThat(domain.sellerId()).isEqualTo(entity.getSellerId());
            assertThat(domain.cancelQty()).isEqualTo(entity.getCancelQty());
            assertThat(domain.type()).isEqualTo(CancelType.valueOf(entity.getCancelType()));
            assertThat(domain.status()).isEqualTo(CancelStatus.valueOf(entity.getCancelStatus()));
        }

        @Test
        @DisplayName("reason 필드들이 Domain으로 올바르게 복원됩니다")
        void toDomain_WithReasonFields_RestoresReasonCorrectly() {
            // given
            CancelJpaEntity entity =
                    CancelJpaEntityFixtures.requestedEntity(
                            CancelJpaEntityFixtures.DEFAULT_ID,
                            CancelJpaEntityFixtures.DEFAULT_ORDER_ITEM_ID,
                            CancelJpaEntityFixtures.DEFAULT_SELLER_ID);

            // when
            Cancel domain = mapper.toDomain(entity);

            // then
            assertThat(domain.reason().reasonType().name()).isEqualTo(entity.getReasonType());
            assertThat(domain.reason().reasonDetail()).isEqualTo(entity.getReasonDetail());
        }

        @Test
        @DisplayName("refundAmount가 null인 Entity는 refundInfo가 null로 복원됩니다")
        void toDomain_WithNullRefundAmount_RestoresNullRefundInfo() {
            // given
            CancelJpaEntity entity =
                    CancelJpaEntityFixtures.requestedEntity(
                            CancelJpaEntityFixtures.DEFAULT_ID,
                            CancelJpaEntityFixtures.DEFAULT_ORDER_ITEM_ID,
                            CancelJpaEntityFixtures.DEFAULT_SELLER_ID);

            // when
            Cancel domain = mapper.toDomain(entity);

            // then
            assertThat(domain.refundInfo()).isNull();
        }

        @Test
        @DisplayName("APPROVED 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithApprovedEntity_ConvertsCorrectly() {
            // given
            CancelJpaEntity entity =
                    CancelJpaEntityFixtures.approvedEntity(
                            CancelJpaEntityFixtures.DEFAULT_ID,
                            CancelJpaEntityFixtures.DEFAULT_ORDER_ITEM_ID,
                            CancelJpaEntityFixtures.DEFAULT_SELLER_ID);

            // when
            Cancel domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(CancelStatus.APPROVED);
            assertThat(domain.processedBy()).isEqualTo(entity.getProcessedBy());
        }

        @Test
        @DisplayName("REJECTED 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithRejectedEntity_ConvertsCorrectly() {
            // given
            CancelJpaEntity entity =
                    CancelJpaEntityFixtures.rejectedEntity(
                            CancelJpaEntityFixtures.DEFAULT_ID,
                            CancelJpaEntityFixtures.DEFAULT_ORDER_ITEM_ID,
                            CancelJpaEntityFixtures.DEFAULT_SELLER_ID);

            // when
            Cancel domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(CancelStatus.REJECTED);
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
            Cancel original = CancelFixtures.requestedCancel();

            // when
            CancelJpaEntity entity = mapper.toEntity(original);
            Cancel converted = mapper.toDomain(entity);

            // then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.cancelNumberValue()).isEqualTo(original.cancelNumberValue());
            assertThat(converted.orderItemIdValue()).isEqualTo(original.orderItemIdValue());
            assertThat(converted.sellerId()).isEqualTo(original.sellerId());
            assertThat(converted.status()).isEqualTo(original.status());
            assertThat(converted.type()).isEqualTo(original.type());
        }

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 핵심 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesCoreData() {
            // given
            CancelJpaEntity original =
                    CancelJpaEntityFixtures.requestedEntity(
                            CancelJpaEntityFixtures.DEFAULT_ID,
                            CancelJpaEntityFixtures.DEFAULT_ORDER_ITEM_ID,
                            CancelJpaEntityFixtures.DEFAULT_SELLER_ID);

            // when
            Cancel domain = mapper.toDomain(original);
            CancelJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getCancelNumber()).isEqualTo(original.getCancelNumber());
            assertThat(converted.getOrderItemId()).isEqualTo(original.getOrderItemId());
            assertThat(converted.getSellerId()).isEqualTo(original.getSellerId());
            assertThat(converted.getCancelStatus()).isEqualTo(original.getCancelStatus());
        }

        @Test
        @DisplayName("refundInfo가 있는 Domain 양방향 변환 시 refundInfo가 보존됩니다")
        void roundTrip_CompletedCancel_PreservesRefundInfo() {
            // given
            Cancel original = CancelFixtures.completedCancel();

            // when
            CancelJpaEntity entity = mapper.toEntity(original);
            Cancel converted = mapper.toDomain(entity);

            // then
            assertThat(converted.refundInfo()).isNotNull();
            assertThat(converted.refundInfo().refundAmount().value())
                    .isEqualTo(original.refundInfo().refundAmount().value());
            assertThat(converted.refundInfo().refundMethod())
                    .isEqualTo(original.refundInfo().refundMethod());
        }
    }
}
