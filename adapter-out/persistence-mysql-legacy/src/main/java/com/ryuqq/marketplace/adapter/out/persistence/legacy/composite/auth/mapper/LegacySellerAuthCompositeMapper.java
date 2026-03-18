package com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.auth.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.auth.dto.LegacySellerAuthQueryDto;
import com.ryuqq.marketplace.application.legacyauth.dto.result.LegacySellerAuthResult;
import org.springframework.stereotype.Component;

/**
 * 레거시 셀러 인증 Composite Mapper.
 *
 * <p>4테이블 조인의 QueryDto를 Application DTO로 변환합니다.
 */
@Component
public class LegacySellerAuthCompositeMapper {

    public LegacySellerAuthResult toResult(LegacySellerAuthQueryDto dto) {
        return new LegacySellerAuthResult(
                dto.sellerId(),
                dto.email(),
                dto.passwordHash(),
                dto.authGroupType(),
                dto.approvalStatus());
    }
}
