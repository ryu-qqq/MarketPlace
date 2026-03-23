package com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.legacy.brand.entity.QLegacyBrandEntity.legacyBrandEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity.QLegacyExternalOrderEntity.legacyExternalOrderEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity.QLegacyInterlockingOrderEntity.legacyInterlockingOrderEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity.QLegacyOrderEntity.legacyOrderEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity.QLegacyOrderHistoryEntity.legacyOrderHistoryEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity.QLegacyOrderSnapshotOptionDetailEntity.legacyOrderSnapshotOptionDetailEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity.QLegacyOrderSnapshotProductGroupEntity.legacyOrderSnapshotProductGroupEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity.QLegacyOrderSnapshotProductGroupImageEntity.legacyOrderSnapshotProductGroupImageEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity.QLegacyPaymentSnapshotShippingAddressEntity.legacyPaymentSnapshotShippingAddressEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity.QLegacyShipmentEntity.legacyShipmentEntity;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.dto.LegacyOrderCompositeQueryDto;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.dto.LegacyOrderHistoryQueryDto;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

/**
 * 레거시 주문 복합 조회 QueryDSL Repository.
 *
 * <p>orders + order_snapshot_product_group + external_order + interlocking_order +
 * payment_snapshot_shipping_address + shipment 조인으로 flat DTO를 조회합니다.
 * order_snapshot_option_detail과 orders_history는 별도 메서드로 조회합니다.
 *
 * <p>레거시 구조: 1 order = 1 product (orders.PRODUCT_ID). 스냅샷도 order_id 단위.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Repository
public class LegacyOrderCompositeQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public LegacyOrderCompositeQueryDslRepository(
            @Qualifier("legacyJpaQueryFactory") JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * 주문 ID로 주문 복합 flat DTO를 조회합니다.
     *
     * <p>orders + order_snapshot_product_group + external_order (LEFT) + interlocking_order (LEFT)
     * + payment_snapshot_shipping_address (LEFT) + shipment (LEFT) 조인.
     *
     * @param orderId 레거시 주문 ID
     * @return 주문 복합 flat DTO Optional
     */
    public Optional<LegacyOrderCompositeQueryDto> fetchOrderComposite(long orderId) {
        LegacyOrderCompositeQueryDto result =
                queryFactory
                        .select(
                                Projections.constructor(
                                        LegacyOrderCompositeQueryDto.class,
                                        legacyOrderEntity.id,
                                        legacyOrderEntity.paymentId,
                                        legacyOrderEntity.productId,
                                        legacyOrderEntity.sellerId,
                                        legacyOrderEntity.userId,
                                        legacyOrderEntity.orderAmount,
                                        legacyOrderEntity.orderStatus,
                                        legacyOrderEntity.quantity,
                                        legacyOrderEntity.insertDate,
                                        legacyOrderSnapshotProductGroupEntity.productGroupId,
                                        legacyOrderSnapshotProductGroupEntity.productGroupName,
                                        legacyOrderSnapshotProductGroupEntity.brandId,
                                        legacyBrandEntity.brandName,
                                        legacyOrderSnapshotProductGroupEntity.categoryId,
                                        legacyOrderSnapshotProductGroupEntity.regularPrice,
                                        legacyOrderSnapshotProductGroupEntity.currentPrice,
                                        legacyOrderSnapshotProductGroupEntity.commissionRate,
                                        legacyOrderSnapshotProductGroupEntity.shareRatio,
                                        legacyOrderSnapshotProductGroupImageEntity.imageUrl,
                                        legacyExternalOrderEntity.externalOrderPkId,
                                        legacyExternalOrderEntity.siteId,
                                        legacyInterlockingOrderEntity.siteName,
                                        legacyPaymentSnapshotShippingAddressEntity.receiverName,
                                        legacyPaymentSnapshotShippingAddressEntity.phoneNumber,
                                        legacyPaymentSnapshotShippingAddressEntity.zipCode,
                                        legacyPaymentSnapshotShippingAddressEntity.addressLine1,
                                        legacyPaymentSnapshotShippingAddressEntity.addressLine2,
                                        legacyPaymentSnapshotShippingAddressEntity.deliveryRequest,
                                        legacyShipmentEntity.invoiceNo,
                                        legacyShipmentEntity.companyCode,
                                        legacyShipmentEntity.insertDate))
                        .from(legacyOrderEntity)
                        .innerJoin(legacyOrderSnapshotProductGroupEntity)
                        .on(legacyOrderSnapshotProductGroupEntity.orderId.eq(legacyOrderEntity.id))
                        .leftJoin(legacyBrandEntity)
                        .on(legacyBrandEntity.id.eq(legacyOrderSnapshotProductGroupEntity.brandId))
                        .leftJoin(legacyOrderSnapshotProductGroupImageEntity)
                        .on(
                                legacyOrderSnapshotProductGroupImageEntity.orderId.eq(
                                        legacyOrderEntity.id))
                        .leftJoin(legacyExternalOrderEntity)
                        .on(legacyExternalOrderEntity.orderId.eq(legacyOrderEntity.id))
                        .leftJoin(legacyInterlockingOrderEntity)
                        .on(legacyInterlockingOrderEntity.orderId.eq(legacyOrderEntity.id))
                        .leftJoin(legacyPaymentSnapshotShippingAddressEntity)
                        .on(
                                legacyPaymentSnapshotShippingAddressEntity.paymentId.eq(
                                        legacyOrderEntity.paymentId))
                        .leftJoin(legacyShipmentEntity)
                        .on(legacyShipmentEntity.orderId.eq(legacyOrderEntity.id))
                        .where(legacyOrderEntity.id.eq(orderId))
                        .orderBy(legacyOrderSnapshotProductGroupImageEntity.id.asc())
                        .limit(1)
                        .fetchOne();

        return Optional.ofNullable(result);
    }

    /**
     * 주문 ID로 선택된 옵션값 목록을 조회합니다.
     *
     * @param orderId 레거시 주문 ID
     * @return 옵션값 목록
     */
    public List<String> fetchOptionValues(long orderId) {
        return queryFactory
                .select(legacyOrderSnapshotOptionDetailEntity.optionValue)
                .from(legacyOrderSnapshotOptionDetailEntity)
                .where(
                        legacyOrderSnapshotOptionDetailEntity.orderId.eq(orderId),
                        legacyOrderSnapshotOptionDetailEntity.deleteYn.eq("N"))
                .fetch();
    }

    /**
     * 주문 ID로 상태 변경 이력을 조회합니다.
     *
     * <p>시간순 정렬 (오래된 것부터). 컨버전 시 타임스탬프/사유 추출에 사용됩니다.
     *
     * @param orderId 레거시 주문 ID
     * @return 주문 이력 목록
     */
    public List<LegacyOrderHistoryQueryDto> fetchOrderHistories(long orderId) {
        return queryFactory
                .select(
                        Projections.constructor(
                                LegacyOrderHistoryQueryDto.class,
                                legacyOrderHistoryEntity.id,
                                legacyOrderHistoryEntity.orderId,
                                legacyOrderHistoryEntity.orderStatus,
                                legacyOrderHistoryEntity.changeReason,
                                legacyOrderHistoryEntity.changeDetailReason,
                                legacyOrderHistoryEntity.insertDate))
                .from(legacyOrderHistoryEntity)
                .where(
                        legacyOrderHistoryEntity.orderId.eq(orderId),
                        legacyOrderHistoryEntity.deleteYn.eq("N"))
                .orderBy(legacyOrderHistoryEntity.id.asc())
                .fetch();
    }
}
