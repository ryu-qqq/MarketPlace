package com.ryuqq.marketplace.domain.category.vo;

import java.util.Locale;

/**
 * 카테고리 그룹.
 *
 * <p>고시정보(notice_category) 및 속성 템플릿(category_attribute_template)과 연결되는 분류 그룹. 각 그룹별로 상품정보제공 고시에 따른
 * 필수 입력 항목이 다르게 적용됨.
 */
public enum CategoryGroup {

    /** 의류 - 소재, 색상, 치수, 세탁방법 등 */
    CLOTHING,

    /** 구두/신발 - 소재, 색상, 굽높이, 사이즈 등 */
    SHOES,

    /** 가방 - 소재, 색상, 크기 등 */
    BAGS,

    /** 패션잡화 - 소재, 색상 등 */
    ACCESSORIES,

    /** 화장품 - 용량, 주요성분, 사용기한 등 */
    COSMETICS,

    /** 귀금속/보석 - 소재, 중량, 순도 등 */
    JEWELRY,

    /** 시계 - 케이스 소재, 밴드 소재, 방수 등급 등 */
    WATCHES,

    /** 가구 - 소재, 크기, 배송/설치비용 등 */
    FURNITURE,

    /** 디지털/가전 - KC인증, 정격전압, 소비전력, A/S정보 등 */
    DIGITAL,

    /** 스포츠용품 - 소재, 크기/중량, 사용연령 등 */
    SPORTS,

    /** 영유아용품 - KC인증(필수), 사용연령, 크기/중량 등 */
    BABY_KIDS,

    /** 기타 재화 - 기본 고시정보 적용 */
    ETC;

    /**
     * 문자열로부터 CategoryGroup 변환.
     *
     * @param value 문자열 값
     * @return 해당하는 CategoryGroup, 없으면 ETC
     */
    public static CategoryGroup fromString(String value) {
        if (value == null || value.isBlank()) {
            return ETC;
        }
        try {
            return CategoryGroup.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return ETC;
        }
    }

    /**
     * 고시정보 연결이 필요한 그룹인지 확인.
     *
     * @return ETC가 아니면 true
     */
    public boolean requiresNoticeInfo() {
        return this != ETC;
    }
}
