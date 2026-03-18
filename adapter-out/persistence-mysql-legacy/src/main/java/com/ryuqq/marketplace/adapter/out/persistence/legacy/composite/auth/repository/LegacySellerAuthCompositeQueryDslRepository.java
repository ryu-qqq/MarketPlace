package com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.auth.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.legacy.auth.entity.QLegacyAdminAuthGroupEntity.legacyAdminAuthGroupEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.auth.entity.QLegacyAdministratorEntity.legacyAdministratorEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.auth.entity.QLegacyAuthGroupEntity.legacyAuthGroupEntity;
import static com.ryuqq.marketplace.adapter.out.persistence.legacy.seller.entity.QLegacySellerEntity.legacySellerEntity;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.auth.dto.LegacySellerAuthQueryDto;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * 레거시 셀러 인증 정보 복합 조회 QueryDSL Repository.
 *
 * <p>administrators + admin_auth_group + auth_group + seller 4개 테이블 조인.
 */
@Repository
public class LegacySellerAuthCompositeQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public LegacySellerAuthCompositeQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * 이메일로 인증용 셀러 복합 정보 조회.
     *
     * @param email 관리자 이메일
     * @return LegacySellerAuthQueryDto Optional
     */
    public Optional<LegacySellerAuthQueryDto> findByEmail(String email) {
        LegacySellerAuthQueryDto result =
                queryFactory
                        .select(
                                Projections.constructor(
                                        LegacySellerAuthQueryDto.class,
                                        legacySellerEntity.id,
                                        legacyAdministratorEntity.email,
                                        legacyAdministratorEntity.passwordHash,
                                        legacyAuthGroupEntity.authGroupType,
                                        legacyAdministratorEntity.approvalStatus))
                        .from(legacyAdministratorEntity)
                        .innerJoin(legacyAdminAuthGroupEntity)
                        .on(legacyAdminAuthGroupEntity.adminId.eq(legacyAdministratorEntity.id))
                        .innerJoin(legacyAuthGroupEntity)
                        .on(legacyAuthGroupEntity.id.eq(legacyAdminAuthGroupEntity.authGroupId))
                        .innerJoin(legacySellerEntity)
                        .on(legacySellerEntity.id.eq(legacyAdministratorEntity.sellerId))
                        .where(legacyAdministratorEntity.email.eq(email))
                        .fetchOne();

        return Optional.ofNullable(result);
    }
}
