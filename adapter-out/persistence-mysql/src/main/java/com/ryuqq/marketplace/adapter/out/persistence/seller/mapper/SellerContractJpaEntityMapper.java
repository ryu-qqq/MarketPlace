package com.ryuqq.marketplace.adapter.out.persistence.seller.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerContractJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerContractJpaEntity.ContractStatusJpaValue;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerContract;
import com.ryuqq.marketplace.domain.seller.id.SellerContractId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.seller.vo.CommissionRate;
import com.ryuqq.marketplace.domain.seller.vo.ContractStatus;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

/**
 * SellerContractJpaEntityMapper - 셀러 계약 정보 Entity-Domain 매퍼.
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
public class SellerContractJpaEntityMapper {

    public SellerContractJpaEntity toEntity(SellerContract domain) {
        return SellerContractJpaEntity.create(
                domain.idValue(),
                domain.sellerIdValue(),
                BigDecimal.valueOf(domain.commissionRateValue()),
                domain.contractStartDate(),
                domain.contractEndDate(),
                toJpaStatus(domain.status()),
                domain.specialTerms(),
                domain.createdAt(),
                domain.updatedAt(),
                null);
    }

    public SellerContract toDomain(SellerContractJpaEntity entity) {
        var id =
                entity.getId() != null
                        ? SellerContractId.of(entity.getId())
                        : SellerContractId.forNew();
        return SellerContract.reconstitute(
                id,
                SellerId.of(entity.getSellerId()),
                CommissionRate.of(entity.getCommissionRate().doubleValue()),
                entity.getContractStartDate(),
                entity.getContractEndDate(),
                toDomainStatus(entity.getStatus()),
                entity.getSpecialTerms(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    private ContractStatusJpaValue toJpaStatus(ContractStatus status) {
        return switch (status) {
            case ACTIVE -> ContractStatusJpaValue.ACTIVE;
            case EXPIRED -> ContractStatusJpaValue.EXPIRED;
            case TERMINATED -> ContractStatusJpaValue.TERMINATED;
        };
    }

    private ContractStatus toDomainStatus(ContractStatusJpaValue status) {
        return switch (status) {
            case ACTIVE -> ContractStatus.ACTIVE;
            case EXPIRED -> ContractStatus.EXPIRED;
            case TERMINATED -> ContractStatus.TERMINATED;
        };
    }
}
