package com.ryuqq.marketplace.application.seller.dto.response;

import com.ryuqq.marketplace.domain.seller.aggregate.Seller;
import java.time.Instant;

/**
 * 셀러 기본 조회 결과.
 *
 * @param id 셀러 ID
 * @param sellerName 셀러명
 * @param displayName 표시명
 * @param logoUrl 로고 URL
 * @param description 설명
 * @param active 활성화 여부
 * @param createdAt 생성일시
 * @param updatedAt 수정일시
 */
public record SellerResult(
        Long id,
        String sellerName,
        String displayName,
        String logoUrl,
        String description,
        boolean active,
        Instant createdAt,
        Instant updatedAt) {

    public static SellerResult from(Seller seller) {
        return new SellerResult(
                seller.idValue(),
                seller.sellerNameValue(),
                seller.displayNameValue(),
                seller.logoUrlValue(),
                seller.descriptionValue(),
                seller.isActive(),
                seller.createdAt(),
                seller.updatedAt());
    }
}
