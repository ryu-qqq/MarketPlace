package com.ryuqq.marketplace.domain.productgroup.vo;

/** 상품 그룹 옵션 유형. */
public enum OptionType {
    NONE("옵션 없음"),
    SINGLE("단일 옵션"),
    COMBINATION("조합 옵션"),
    FREE_INPUT("자유 입력");

    private final String displayName;

    OptionType(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }

    /** 옵션 그룹이 필요한 유형인지 확인. */
    public boolean requiresOptionGroup() {
        return this == SINGLE || this == COMBINATION;
    }

    /** 예상되는 옵션 그룹 수 반환. NONE/FREE_INPUT은 0, SINGLE은 1, COMBINATION은 2. */
    public int expectedOptionGroupCount() {
        return switch (this) {
            case NONE, FREE_INPUT -> 0;
            case SINGLE -> 1;
            case COMBINATION -> 2;
        };
    }
}
