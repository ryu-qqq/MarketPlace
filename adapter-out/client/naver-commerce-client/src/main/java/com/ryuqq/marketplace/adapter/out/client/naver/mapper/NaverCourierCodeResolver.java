package com.ryuqq.marketplace.adapter.out.client.naver.mapper;

import java.util.Map;

/** 네이버 택배사 코드 → 택배사명 변환 유틸리티. */
public final class NaverCourierCodeResolver {

    private NaverCourierCodeResolver() {}

    /** 네이버 deliveryCompanyCode → 택배사 한글명 매핑. */
    private static final Map<String, String> NAVER_CODE_TO_NAME =
            Map.ofEntries(
                    Map.entry("CJGLS", "CJ대한통운"),
                    Map.entry("KGB", "로젠택배"),
                    Map.entry("HANJIN", "한진택배"),
                    Map.entry("EPOST", "우체국택배"),
                    Map.entry("HYUNDAI", "롯데택배"),
                    Map.entry("KDEXP", "경동택배"),
                    Map.entry("ILYANG", "일양로지스"),
                    Map.entry("CHUNIL", "천일택배"),
                    Map.entry("DAESIN", "대신택배"),
                    Map.entry("HDEXP", "합동택배"),
                    Map.entry("GSPOSTBOX", "GS Postbox 택배"),
                    Map.entry("KUNYOUNG", "건영택배"),
                    Map.entry("HANDEX", "한덱스"),
                    Map.entry("SLX", "SLX택배"),
                    Map.entry("HONAM", "호남택배"),
                    Map.entry("IPARCEL", "아이파셀"),
                    Map.entry("SEDEX", "세덱스"),
                    Map.entry("ACI", "ACI Express"),
                    Map.entry("REGISTPOST", "우편등기"),
                    Map.entry("DHLDE", "DHL"),
                    Map.entry("FEDEX", "FedEx"),
                    Map.entry("UPS", "UPS"),
                    Map.entry("USPS", "USPS"),
                    Map.entry("EMS", "EMS"),
                    Map.entry("TNT", "TNT"),
                    Map.entry("SAGAWA", "사가와(Sagawa)"),
                    Map.entry("YAMATO", "야마토(Yamato)"),
                    Map.entry("YUUPACK", "유팩(YuuPack)"));

    /**
     * 네이버 택배사 코드를 택배사명으로 변환합니다.
     *
     * @param naverCode 네이버 deliveryCompanyCode (예: "CJGLS", "HANJIN")
     * @return 택배사명 (매핑이 없으면 코드를 그대로 반환)
     */
    public static String toDisplayName(String naverCode) {
        if (naverCode == null || naverCode.isBlank()) {
            return naverCode;
        }
        return NAVER_CODE_TO_NAME.getOrDefault(naverCode, naverCode);
    }
}
