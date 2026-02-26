package com.ryuqq.marketplace.adapter.out.persistence.seller.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerSettlementJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerSettlementJpaEntity.SettlementCycleJpaValue;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerSettlement;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.seller.id.SellerSettlementId;
import com.ryuqq.marketplace.domain.seller.vo.BankAccount;
import com.ryuqq.marketplace.domain.seller.vo.SettlementCycle;
import org.springframework.stereotype.Component;

/**
 * SellerSettlementJpaEntityMapper - 셀러 정산 정보 Entity-Domain 매퍼.
 *
 * <p>Entity ↔ Domain 변환을 담당합니다.
 *
 * <p>PER-MAP-001: Mapper는 @Component로 등록.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 */
@Component
public class SellerSettlementJpaEntityMapper {

    public SellerSettlementJpaEntity toEntity(SellerSettlement domain) {
        return SellerSettlementJpaEntity.create(
                domain.idValue(),
                domain.sellerIdValue(),
                domain.bankCode(),
                domain.bankName(),
                domain.accountNumber(),
                domain.accountHolderName(),
                toJpaCycle(domain.settlementCycle()),
                domain.settlementDay(),
                domain.isVerified(),
                domain.verifiedAt(),
                domain.createdAt(),
                domain.updatedAt(),
                null);
    }

    public SellerSettlement toDomain(SellerSettlementJpaEntity entity) {
        BankAccount bankAccount = null;
        if (entity.getBankName() != null && entity.getAccountNumber() != null) {
            bankAccount =
                    BankAccount.of(
                            entity.getBankCode(),
                            entity.getBankName(),
                            entity.getAccountNumber(),
                            entity.getAccountHolderName());
        }

        var id =
                entity.getId() != null
                        ? SellerSettlementId.of(entity.getId())
                        : SellerSettlementId.forNew();
        return SellerSettlement.reconstitute(
                id,
                SellerId.of(entity.getSellerId()),
                bankAccount,
                toDomainCycle(entity.getSettlementCycle()),
                entity.getSettlementDay(),
                entity.isVerified(),
                entity.getVerifiedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    private SettlementCycleJpaValue toJpaCycle(SettlementCycle cycle) {
        return switch (cycle) {
            case WEEKLY -> SettlementCycleJpaValue.WEEKLY;
            case BIWEEKLY -> SettlementCycleJpaValue.BIWEEKLY;
            case MONTHLY -> SettlementCycleJpaValue.MONTHLY;
        };
    }

    private SettlementCycle toDomainCycle(SettlementCycleJpaValue cycle) {
        return switch (cycle) {
            case WEEKLY -> SettlementCycle.WEEKLY;
            case BIWEEKLY -> SettlementCycle.BIWEEKLY;
            case MONTHLY -> SettlementCycle.MONTHLY;
        };
    }
}
