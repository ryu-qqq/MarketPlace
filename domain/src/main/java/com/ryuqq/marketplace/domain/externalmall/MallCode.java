package com.ryuqq.marketplace.domain.externalmall;

import java.util.Arrays;

/**
 * 외부몰 코드 Enum
 *
 * <p>지원하는 외부몰:
 * <ul>
 *   <li>OCO - OCO 외부몰
 *   <li>SELLIC - SELLIC 외부몰
 *   <li>LF - LF Mall
 *   <li>BUYMA - BUYMA
 * </ul>
 */
public enum MallCode {
    OCO("OCO"),
    SELLIC("SELLIC"),
    LF("LF"),
    BUYMA("BUYMA");

    private final String code;

    MallCode(String code) {
        this.code = code;
    }

    /**
     * 외부몰 코드 반환
     *
     * @return 외부몰 코드
     */
    public String getCode() {
        return code;
    }

    /**
     * 문자열 코드로 MallCode Enum 반환
     *
     * <p>대소문자 구분 없이 변환합니다.
     *
     * @param code 외부몰 코드 문자열
     * @return MallCode Enum
     * @throws IllegalArgumentException null, 빈 문자열, 또는 유효하지 않은 코드인 경우
     */
    public static MallCode fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("외부몰 코드는 null 또는 빈 문자열일 수 없습니다");
        }

        String upperCode = code.toUpperCase();
        return Arrays.stream(values())
                .filter(mallCode -> mallCode.code.equals(upperCode))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("유효하지 않은 외부몰 코드: %s", code)
                ));
    }
}
