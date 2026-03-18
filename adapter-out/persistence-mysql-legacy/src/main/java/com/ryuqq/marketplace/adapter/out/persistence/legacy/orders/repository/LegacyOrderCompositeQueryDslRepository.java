package com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity.QLegacyExternalOrderEntity.legacyExternalOrderEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity.QLegacyInterlockingOrderEntity.legacyInterlockingOrderEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity.QLegacyOrderEntity.legacyOrderEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity.QLegacyOrderSnapshotOptionDetailEntity.legacyOrderSnapshotOptionDetailEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity.QLegacyOrderSnapshotProductGroupEntity.legacyOrderSnapshotProductGroupEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity.QLegacyOrderSnapshotProductGroupImageEntity.legacyOrderSnapshotProductGroupImageEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity.QLegacyPaymentSnapshotShippingAddressEntity.legacyPaymentSnapshotShippingAddressEntity;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.dto.LegacyOrderCompositeQueryDto;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

/**
 * 레거시 주문 복합 조회 QueryDSL Repository.
 *
 * <p>orders + order_snapshot_product_group + external_order + interlocking_order +
 * payment_snapshot_shipping_address 조인으로 flat DTO를 조회합니다.
 * order_snapshot_option_detail은 별도 메서드로 조회합니다.
 * order_snapshot_product_group_image는 첫 번째 이미지를 별도 메서드로 조회합니다.
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
     * <p>orders + order_snapshot_product_group + external_order (LEFT) + interlocking_order (LEFT) +
     * payment_snapshot_shipping_address (LEFT) 조인.
     * mainImageUrl은 별도 쿼리({@link #fetchMainImageUrl}) 결과로 채웁니다.
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
                                        legacyPaymentSnapshotShippingAddressEntity.deliveryRequest))
                        .from(legacyOrderEntity)
                        .innerJoin(legacyOrderSnapshotProductGroupEntity)
                        .on(legacyOrderSnapshotProductGroupEntity.orderId.eq(legacyOrderEntity.id))
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
}
