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

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.dto.LegacyOrderCompositeQueryDto;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.dto.LegacyOrderHistoryQueryDto;
import com.ryuqq.marketplace.application.legacy.order.dto.query.LegacyOrderSearchParams;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

/**
 * 레거시 주문 API용 QueryDSL Repository.
 *
 * <p>단건 조회(기존 패턴 재사용), 목록 조회(커서+필터), 카운트, 옵션값 batch, 히스토리 batch.
 */
@Repository
public class LegacyOrderApiQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public LegacyOrderApiQueryDslRepository(
            @Qualifier("legacyJpaQueryFactory") JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /** 주문 ID로 주문 복합 flat DTO를 조회합니다. */
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

    /** 커서 기반 주문 목록 조회. */
    public List<LegacyOrderCompositeQueryDto> fetchOrderList(LegacyOrderSearchParams params) {
        BooleanBuilder where = buildWhereCondition(params);

        return queryFactory
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
                .on(legacyOrderSnapshotProductGroupImageEntity.orderId.eq(legacyOrderEntity.id))
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
                .where(where)
                .orderBy(legacyOrderEntity.id.desc())
                .limit(params.size())
                .fetch();
    }

    /** 검색 조건에 맞는 주문 건수 조회. */
    public long countOrders(LegacyOrderSearchParams params) {
        BooleanBuilder where = buildWhereCondition(params);

        Long count =
                queryFactory
                        .select(legacyOrderEntity.id.count())
                        .from(legacyOrderEntity)
                        .where(where)
                        .fetchOne();

        return count != null ? count : 0L;
    }

    /** 주문 ID 목록으로 옵션값 batch 조회. */
    public List<String> fetchOptionValues(long orderId) {
        return queryFactory
                .select(legacyOrderSnapshotOptionDetailEntity.optionValue)
                .from(legacyOrderSnapshotOptionDetailEntity)
                .where(
                        legacyOrderSnapshotOptionDetailEntity.orderId.eq(orderId),
                        legacyOrderSnapshotOptionDetailEntity.deleteYn.eq("N"))
                .fetch();
    }

    /** 주문 ID 목록으로 히스토리 batch 조회. */
    public List<LegacyOrderHistoryQueryDto> fetchOrderHistories(List<Long> orderIds) {
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
                        legacyOrderHistoryEntity.orderId.in(orderIds),
                        legacyOrderHistoryEntity.deleteYn.eq("N"))
                .orderBy(legacyOrderHistoryEntity.id.desc())
                .fetch();
    }

    private BooleanBuilder buildWhereCondition(LegacyOrderSearchParams params) {
        BooleanBuilder where = new BooleanBuilder();

        if (params.lastDomainId() != null) {
            where.and(legacyOrderEntity.id.lt(params.lastDomainId()));
        }

        if (params.orderStatusList() != null && !params.orderStatusList().isEmpty()) {
            where.and(legacyOrderEntity.orderStatus.in(params.orderStatusList()));
        }

        if (params.sellerId() != null) {
            where.and(legacyOrderEntity.sellerId.eq(params.sellerId()));
        }

        if (params.startDate() != null) {
            where.and(legacyOrderEntity.insertDate.goe(params.startDate()));
        }

        if (params.endDate() != null) {
            where.and(legacyOrderEntity.insertDate.loe(params.endDate()));
        }

        return where;
    }
}
