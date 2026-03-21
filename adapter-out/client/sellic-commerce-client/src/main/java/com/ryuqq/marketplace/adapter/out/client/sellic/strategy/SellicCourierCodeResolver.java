package com.ryuqq.marketplace.adapter.out.client.sellic.strategy;

import java.util.Map;

/**
 * 내부 택배사 코드 → 셀릭 택배사 코드 변환.
 *
 * <p>셀릭 API는 정수형 택배사 코드를 사용합니다.
 */
final class SellicCourierCodeResolver {

    private static final Map<String, Integer> COURIER_CODE_MAP =
            Map.ofEntries(
                    Map.entry("CJ", 1000),
                    Map.entry("CJLOGISTICS", 1000),
                    Map.entry("CJ대한통운", 1000),
                    Map.entry("HANJIN", 1001),
                    Map.entry("한진", 1001),
                    Map.entry("EPOST", 1002),
                    Map.entry("우체국", 1002),
                    Map.entry("LOTTE", 1003),
                    Map.entry("롯데", 1003),
                    Map.entry("LOGEN", 1004),
                    Map.entry("로젠", 1004),
                    Map.entry("KYUNGDONG", 1011),
                    Map.entry("경동", 1011),
                    Map.entry("DHL", 1028),
                    Map.entry("EMS", 1029),
                    Map.entry("FEDEX", 1030),
                    Map.entry("PANTOS", 1535),
                    Map.entry("판토스", 1535),
                    Map.entry("LOTTEGLOBAL", 1600),
                    Map.entry("롯데글로벌로지스", 1600));

    /** 기본 택배사: CJ대한통운. */
    private static final int DEFAULT_COURIER_CODE = 1000;

    private SellicCourierCodeResolver() {}

    /**
     * 내부 택배사 코드를 셀릭 택배사 코드로 변환.
     *
     * @param courierCode 내부 택배사 코드 (e.g. "CJ", "HANJIN")
     * @return 셀릭 택배사 코드 (정수)
     */
    static int resolve(String courierCode) {
        if (courierCode == null || courierCode.isBlank()) {
            return DEFAULT_COURIER_CODE;
        }
        return COURIER_CODE_MAP.getOrDefault(courierCode.toUpperCase(), DEFAULT_COURIER_CODE);
    }
}
