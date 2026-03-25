package com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.seller.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.legacy.seller.entity.QLegacySellerBusinessInfoEntity.legacySellerBusinessInfoEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.seller.entity.QLegacySellerEntity.legacySellerEntity;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.seller.dto.LegacySellerCompositeQueryDto;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * 레거시 셀러 복합 조회 QueryDSL Repository.
 *
 * <p>luxurydb seller + seller_business_info 조인 조회.
 */
@Repository
public class LegacySellerCompositeQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public LegacySellerCompositeQueryDslRepository(
            @org.springframework.beans.factory.annotation.Qualifier("legacyJpaQueryFactory")
                    JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * 셀러 ID로 셀러 복합 정보 조회.
     *
     * @param sellerId 레거시 셀러 ID
     * @return LegacySellerCompositeQueryDto Optional
     */
    public Optional<LegacySellerCompositeQueryDto> findById(long sellerId) {
        LegacySellerCompositeQueryDto result =
                queryFactory
                        .select(
                                Projections.constructor(
                                        LegacySellerCompositeQueryDto.class,
                                        legacySellerEntity.id,
                                        legacySellerEntity.sellerName,
                                        legacySellerEntity.sellerLogoUrl,
                                        legacySellerEntity.sellerDescription,
                                        legacySellerEntity.commissionRate,
                                        legacySellerBusinessInfoEntity.registrationNumber,
                                        legacySellerBusinessInfoEntity.companyName,
                                        legacySellerBusinessInfoEntity.representative,
                                        legacySellerBusinessInfoEntity.saleReportNumber,
                                        legacySellerBusinessInfoEntity.businessAddressZipCode,
                                        legacySellerBusinessInfoEntity.businessAddressLine1,
                                        legacySellerBusinessInfoEntity.businessAddressLine2,
                                        legacySellerBusinessInfoEntity.bankName,
                                        legacySellerBusinessInfoEntity.accountNumber,
                                        legacySellerBusinessInfoEntity.accountHolderName,
                                        legacySellerBusinessInfoEntity.csNumber,
                                        legacySellerBusinessInfoEntity.csPhoneNumber,
                                        legacySellerBusinessInfoEntity.csEmail))
                        .from(legacySellerEntity)
                        .innerJoin(legacySellerBusinessInfoEntity)
                        .on(legacySellerBusinessInfoEntity.sellerId.eq(legacySellerEntity.id))
                        .where(legacySellerEntity.id.eq(sellerId))
                        .fetchOne();

        return Optional.ofNullable(result);
    }
}
