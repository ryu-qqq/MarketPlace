package com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.entity.QSellerSalesChannelJpaEntity.sellerSalesChannelJpaEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.condition.SellerSalesChannelConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.sellersaleschannel.entity.SellerSalesChannelJpaEntity;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * SellerSalesChannelQueryDslRepository - 셀러 판매채널 QueryDSL 레포지토리.
 *
 * <p>PER-REP-003: 모든 조회는 QueryDslRepository.
 *
 * <p>PER-CND-001: BooleanExpression은 ConditionBuilder로 분리.
 */
@Repository
public class SellerSalesChannelQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final SellerSalesChannelConditionBuilder conditionBuilder;

    public SellerSalesChannelQueryDslRepository(
            JPAQueryFactory queryFactory, SellerSalesChannelConditionBuilder conditionBuilder) {
        this.queryFactory = queryFactory;
        this.conditionBuilder = conditionBuilder;
    }

    /**
     * 셀러의 CONNECTED 상태 판매채널 목록 조회.
     *
     * @param sellerId 셀러 ID
     * @return CONNECTED 상태의 판매채널 엔티티 목록 (sellerId가 null이면 빈 리스트)
     */
    public List<SellerSalesChannelJpaEntity> findConnectedBySellerId(Long sellerId) {
        if (sellerId == null) {
            return Collections.emptyList();
        }
        return queryFactory
                .selectFrom(sellerSalesChannelJpaEntity)
                .where(
                        conditionBuilder.sellerIdEq(sellerId),
                        conditionBuilder.connectionStatusConnected())
                .fetch();
    }

    /**
     * 셀러 ID + 판매채널 ID로 단건 조회.
     *
     * @param sellerId 셀러 ID
     * @param salesChannelId 판매채널 ID
     * @return 일치하는 엔티티 (없으면 null)
     */
    public SellerSalesChannelJpaEntity findBySellerIdAndSalesChannelId(
            Long sellerId, Long salesChannelId) {
        return queryFactory
                .selectFrom(sellerSalesChannelJpaEntity)
                .where(
                        conditionBuilder.sellerIdEq(sellerId),
                        conditionBuilder.salesChannelIdEq(salesChannelId))
                .fetchOne();
    }

    /**
     * 채널 코드 기준 CONNECTED 상태 판매채널 목록 조회.
     *
     * @param channelCode 채널 코드
     * @return CONNECTED 상태의 판매채널 엔티티 목록
     */
    public List<SellerSalesChannelJpaEntity> findConnectedByChannelCode(String channelCode) {
        if (channelCode == null) {
            return Collections.emptyList();
        }
        return queryFactory
                .selectFrom(sellerSalesChannelJpaEntity)
                .where(
                        conditionBuilder.channelCodeEq(channelCode),
                        conditionBuilder.connectionStatusConnected())
                .fetch();
    }
}
