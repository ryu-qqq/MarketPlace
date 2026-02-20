package com.ryuqq.marketplace.domain.externalproductsync.vo;

/**
 * 외부 상품 연동 타입.
 *
 * <p>외부 판매채널로의 상품 연동 유형을 나타냅니다.
 */
@SuppressWarnings("PMD.DataClass")
public enum SyncType {

    /** 상품 신규 등록. */
    CREATE("등록"),

    /** 상품 수정. */
    UPDATE("수정"),

    /** 상품 삭제. */
    DELETE("삭제");

    private final String description;

    SyncType(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }

    public boolean isCreate() {
        return this == CREATE;
    }

    public boolean isUpdate() {
        return this == UPDATE;
    }

    public boolean isDelete() {
        return this == DELETE;
    }
}
