package com.ryuqq.marketplace.domain.productgroupinspection.vo;

/** 검수 점수 항목. 각 항목의 가중치를 포함한다. */
public enum InspectionScoreType {

    /** 캐노니컬 옵션 매핑률. */
    CANONICAL_OPTION_MAPPING("캐노니컬 옵션 매핑", 50),

    /** 고시정보 완성도. */
    NOTICE_COMPLETENESS("고시정보 완성도", 30),

    /** 상세설명 품질. */
    DESCRIPTION_QUALITY("상세설명 품질", 20),

    /** 이미지 커버리지. */
    IMAGE_COVERAGE("이미지 커버리지", 30);

    private final String description;
    private final int weight;

    InspectionScoreType(String description, int weight) {
        this.description = description;
        this.weight = weight;
    }

    public String description() {
        return description;
    }

    public int weight() {
        return weight;
    }
}
