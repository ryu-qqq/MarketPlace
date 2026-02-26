package com.ryuqq.marketplace.adapter.out.persistence.selleradmin.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.entity.SellerAdminJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.PhoneNumber;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdmin;
import com.ryuqq.marketplace.domain.selleradmin.id.SellerAdminId;
import com.ryuqq.marketplace.domain.selleradmin.vo.AdminName;
import com.ryuqq.marketplace.domain.selleradmin.vo.LoginId;
import org.springframework.stereotype.Component;

/**
 * SellerAdminJpaEntityMapper - 셀러 관리자 Entity-Domain 매퍼.
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
public class SellerAdminJpaEntityMapper {

    public SellerAdminJpaEntity toEntity(SellerAdmin domain) {
        return SellerAdminJpaEntity.create(
                domain.idValue(),
                domain.sellerIdValue(),
                domain.authUserId(),
                domain.loginIdValue(),
                domain.nameValue(),
                domain.phoneNumberValue(),
                domain.status(),
                domain.createdAt(),
                domain.updatedAt(),
                domain.deletedAt());
    }

    public SellerAdmin toDomain(SellerAdminJpaEntity entity) {
        return SellerAdmin.reconstitute(
                SellerAdminId.of(entity.getId()),
                entity.getSellerId() != null ? SellerId.of(entity.getSellerId()) : null,
                entity.getAuthUserId(),
                LoginId.of(entity.getLoginId()),
                AdminName.of(entity.getName()),
                entity.getPhoneNumber() != null ? PhoneNumber.of(entity.getPhoneNumber()) : null,
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt());
    }
}
