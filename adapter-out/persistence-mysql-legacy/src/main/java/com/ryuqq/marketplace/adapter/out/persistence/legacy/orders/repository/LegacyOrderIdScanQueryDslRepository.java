package com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity.QLegacyOrderEntity.legacyOrderEntity;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.application.legacyconversion.port.out.query.LegacyOrderScanEntry;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

/**
 * 레거시 주문 ID 커서 기반 스캔 QueryDSL Repository.
 *
 * <p>orders 테이블에서 활성 주문 ID와 결제 ID를 커서 기반으로 페이징 조회합니다.
 * ORDER_FAILED 상태 제외, delete_yn='N' 조건 적용.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Repository
public class LegacyOrderIdScanQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public LegacyOrderIdScanQueryDslRepository(
            @Qualifier("legacyJpaQueryFactory") JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * 지정된 ID 이후의 활성 주문 엔트리(orderId + paymentId) 목록을 조회합니다.
     *
     * <p>delete_yn='N' AND ORDER_STATUS != 'ORDER_FAILED' AND order_id > afterId 조건으로
     * order_id 오름차순 조회합니다.
     *
     * @param afterId 이 ID 이후부터 조회 (exclusive)
     * @param limit 최대 조회 개수
     * @return 활성 주문 스캔 엔트리 목록 (orderId 오름차순)
     */
    public List<LegacyOrderScanEntry> findActiveOrderEntries(long afterId, int limit) {
        return queryFactory
                .select(
                        Projections.constructor(
                                LegacyOrderScanEntry.class,
                                legacyOrderEntity.id,
                                legacyOrderEntity.paymentId))
                .from(legacyOrderEntity)
                .where(
                        legacyOrderEntity.deleteYn.eq("N"),
                        legacyOrderEntity.orderStatus.ne("ORDER_FAILED"),
                        legacyOrderEntity.id.gt(afterId))
                .orderBy(legacyOrderEntity.id.asc())
                .limit(limit)
                .fetch();
    }
}
