package com.ryuqq.marketplace.domain.outboundsync.vo;

/**
 * 상품그룹 변경 영역.
 *
 * <p>UPDATE Outbox 생성 시 어떤 영역이 변경되었는지를 나타냅니다. 채널별 UpdateStrategy에서 이 정보를 기반으로 전체 수정 또는 부분 수정을
 * 결정합니다.
 */
public enum ChangedArea {

    /** 판매가 (regularPrice, currentPrice). */
    PRICE,

    /** 재고 수량. */
    STOCK,

    /** 판매 상태 (ACTIVE, INACTIVE 등). */
    STATUS,

    /** 기본 정보 (상품명, 브랜드, 카테고리, 옵션타입 등). */
    BASIC_INFO,

    /** 상품 이미지 (대표이미지, 추가이미지). */
    IMAGE,

    /** 상세설명 (HTML). */
    DESCRIPTION,

    /** 옵션 구조 (옵션그룹, 옵션값, SKU 추가/삭제). */
    OPTION,

    /** 상품정보제공고시. */
    NOTICE;

    /** 모든 영역이 변경된 것으로 간주할 때 사용할 전체 집합 크기. */
    public static final int TOTAL_COUNT = values().length;
}
