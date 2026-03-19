package com.ryuqq.marketplace.adapter.out.persistence.settlement.entry.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.settlement.entry.entity.SettlementEntryJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.settlement.entry.aggregate.SettlementEntry;
import com.ryuqq.marketplace.domain.settlement.entry.id.SettlementEntryId;
import com.ryuqq.marketplace.domain.settlement.entry.vo.EntryAmounts;
import com.ryuqq.marketplace.domain.settlement.entry.vo.EntrySourceReference;
import com.ryuqq.marketplace.domain.settlement.entry.vo.EntryStatus;
import com.ryuqq.marketplace.domain.settlement.entry.vo.EntryType;
import com.ryuqq.marketplace.domain.settlement.id.SettlementId;
import org.springframework.stereotype.Component;

/** 정산 원장 JPA Entity Mapper. */
@Component
public class SettlementEntryJpaEntityMapper {

    public SettlementEntryJpaEntity toEntity(SettlementEntry domain) {
        return SettlementEntryJpaEntity.create(
                domain.idValue(),
                domain.sellerId(),
                domain.entryType().name(),
                domain.status().name(),
                domain.amounts().salesAmount().value(),
                domain.amounts().commissionRate(),
                domain.amounts().commissionAmount().value(),
                domain.amounts().settlementAmount().value(),
                domain.source().orderItemId(),
                domain.source().claimId(),
                domain.source().claimType(),
                domain.reversalOfEntryId() != null ? domain.reversalOfEntryId().value() : null,
                domain.settlementId() != null ? domain.settlementId().value() : null,
                domain.eligibleAt(),
                domain.createdAt(),
                domain.updatedAt());
    }

    public SettlementEntry toDomain(SettlementEntryJpaEntity entity) {
        EntryAmounts amounts =
                EntryAmounts.of(
                        Money.of(entity.getSalesAmount()),
                        entity.getCommissionRate(),
                        Money.of(entity.getCommissionAmount()),
                        Money.of(entity.getSettlementAmount()));

        EntrySourceReference source =
                new EntrySourceReference(
                        entity.getOrderItemId(), entity.getClaimId(), entity.getClaimType());

        return SettlementEntry.reconstitute(
                SettlementEntryId.of(entity.getId()),
                entity.getSellerId(),
                EntryType.valueOf(entity.getEntryType()),
                EntryStatus.valueOf(entity.getEntryStatus()),
                amounts,
                source,
                entity.getReversalOfEntryId() != null
                        ? SettlementEntryId.of(entity.getReversalOfEntryId())
                        : null,
                entity.getSettlementId() != null ? SettlementId.of(entity.getSettlementId()) : null,
                entity.getEligibleAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
