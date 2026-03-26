package com.ryuqq.marketplace.adapter.out.persistence.settlement.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.settlement.entity.SettlementJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.settlement.aggregate.Settlement;
import com.ryuqq.marketplace.domain.settlement.id.SettlementId;
import com.ryuqq.marketplace.domain.settlement.vo.HoldInfo;
import com.ryuqq.marketplace.domain.settlement.vo.SettlementAmounts;
import com.ryuqq.marketplace.domain.settlement.vo.SettlementCycle;
import com.ryuqq.marketplace.domain.settlement.vo.SettlementPeriod;
import com.ryuqq.marketplace.domain.settlement.vo.SettlementStatus;
import org.springframework.stereotype.Component;

/** 정산 JPA Entity Mapper. */
@Component
public class SettlementJpaEntityMapper {

    public SettlementJpaEntity toEntity(Settlement domain) {
        return SettlementJpaEntity.create(
                domain.idValue(),
                domain.sellerId(),
                domain.status().name(),
                domain.period().startDate(),
                domain.period().endDate(),
                domain.period().cycle().name(),
                domain.amounts().totalSalesAmount().value(),
                domain.amounts().totalCommissionAmount().value(),
                domain.amounts().totalReversalAmount().value(),
                domain.amounts().netSettlementAmount().value(),
                domain.entryCount(),
                domain.holdInfo() != null ? domain.holdInfo().holdReason() : null,
                domain.holdInfo() != null ? domain.holdInfo().holdAt() : null,
                domain.expectedSettlementDay(),
                domain.settlementDay(),
                domain.createdAt(),
                domain.updatedAt());
    }

    public Settlement toDomain(SettlementJpaEntity entity) {
        SettlementPeriod period =
                SettlementPeriod.of(
                        entity.getPeriodStartDate(),
                        entity.getPeriodEndDate(),
                        SettlementCycle.valueOf(entity.getSettlementCycle()));

        SettlementAmounts amounts =
                SettlementAmounts.of(
                        Money.of(entity.getTotalSalesAmount()),
                        Money.of(entity.getTotalCommissionAmount()),
                        Money.of(entity.getTotalReversalAmount()),
                        Money.of(entity.getNetSettlementAmount()));

        HoldInfo holdInfo =
                entity.getHoldReason() != null
                        ? HoldInfo.of(entity.getHoldReason(), entity.getHoldAt())
                        : null;

        return Settlement.reconstitute(
                SettlementId.of(entity.getId()),
                entity.getSellerId(),
                SettlementStatus.valueOf(entity.getSettlementStatus()),
                period,
                amounts,
                entity.getEntryCount(),
                holdInfo,
                entity.getExpectedSettlementDay(),
                entity.getSettlementDay(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
