package com.ryuqq.marketplace.application.legacyproduct.port.out.command;

import java.util.List;

/**
 * 세토프 DB 상품그룹 커맨드 Port.
 *
 * <p>LEGACY_IMPORTED 폴백 경로에서 세토프 DB에 직접 수정하거나, OutboundSync 세토프 채널에서 상품 동기화 시 사용합니다.
 *
 * <p>전환기: persistence-mysql-legacy 모듈이 구현 (세토프 DB 직접 쓰기)
 *
 * <p>최종: setof-client 모듈이 구현 (HTTP API)
 */
public interface SetofProductGroupCommandPort {

    /** 가격 수정. */
    void updatePrice(long productGroupId, long regularPrice, long currentPrice, long salePrice);

    /** 진열 상태 수정. */
    void updateDisplayYn(long productGroupId, String displayYn);

    /** 품절 상태 수정. */
    void updateSoldOutYn(long productGroupId, String soldOutYn);

    /** 고시정보 수정. */
    void updateNotice(
            long productGroupId,
            String material,
            String color,
            String size,
            String maker,
            String origin,
            String washingMethod,
            String yearMonthDay,
            String assuranceStandard,
            String asPhone);

    /** 이미지 전체 교체. */
    void replaceImages(long productGroupId, List<ImageUpdateData> images);

    /** 상세설명 수정. */
    void updateDetailDescription(long productGroupId, String imageUrl);

    /** 상품 재고 수정. */
    void updateStock(long productId, int quantity);

    /** 상품 품절 처리. */
    void markProductSoldOut(long productId);

    record ImageUpdateData(String imageType, String imageUrl) {}
}
