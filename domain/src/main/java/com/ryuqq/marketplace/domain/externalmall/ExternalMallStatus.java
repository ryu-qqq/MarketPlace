package com.ryuqq.marketplace.domain.externalmall;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

/**
 * 외부몰 상태 Enum
 *
 * <p>상태 전환 규칙:
 * <ul>
 *   <li>PENDING → ACTIVE: 관리자 수동 활성화
 *   <li>ACTIVE → INACTIVE: 관리자 수동 비활성화
 *   <li>ERROR: 검증 실패 시 (전환 불가 상태)
 * </ul>
 */
public enum ExternalMallStatus {
    /**
     * 대기 중 (등록 직후)
     */
    PENDING("PENDING"),

    /**
     * 활성화 (사용 가능)
     */
    ACTIVE("ACTIVE"),

    /**
     * 비활성화 (일시 정지)
     */
    INACTIVE("INACTIVE"),

    /**
     * 에러 (검증 실패)
     */
    ERROR("ERROR");

    private final String value;

    ExternalMallStatus(String value) {
        this.value = value;
    }

    /**
     * 외부몰 상태 값 반환
     *
     * @return 상태 값
     */
    public String getValue() {
        return value;
    }

    /**
     * 문자열 값으로 ExternalMallStatus Enum 반환
     *
     * <p>대소문자 구분 없이 변환합니다.
     *
     * @param value 외부몰 상태 문자열
     * @return ExternalMallStatus Enum
     * @throws IllegalArgumentException null, 빈 문자열, 또는 유효하지 않은 값인 경우
     */
    public static ExternalMallStatus fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("외부몰 상태는 null 또는 빈 문자열일 수 없습니다");
        }

        String upperValue = value.toUpperCase();
        return Arrays.stream(values())
                .filter(status -> status.value.equals(upperValue))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("유효하지 않은 외부몰 상태: %s", value)
                ));
    }

    /**
     * 특정 상태로의 전환이 허용되는지 검증
     *
     * <p>허용된 전환 규칙:
     * <ul>
     *   <li>PENDING → ACTIVE만 허용
     *   <li>ACTIVE → INACTIVE만 허용
     *   <li>ERROR 상태는 전환 불가
     *   <li>동일 상태로의 전환 불가
     * </ul>
     *
     * @param targetStatus 전환하려는 목표 상태
     * @return 전환 가능 여부
     */
    public boolean isTransitionAllowedTo(ExternalMallStatus targetStatus) {
        if (this == targetStatus) {
            return false; // 동일 상태로의 전환 불가
        }

        Set<ExternalMallStatus> allowedTransitions = getAllowedTransitions(this);
        return allowedTransitions.contains(targetStatus);
    }

    /**
     * 현재 상태에서 전환 가능한 상태 목록 반환
     *
     * @param from 현재 상태
     * @return 전환 가능한 상태 Set
     */
    private static Set<ExternalMallStatus> getAllowedTransitions(ExternalMallStatus from) {
        return switch (from) {
            case PENDING -> EnumSet.of(ACTIVE);     // PENDING → ACTIVE만 허용
            case ACTIVE -> EnumSet.of(INACTIVE);    // ACTIVE → INACTIVE만 허용
            case INACTIVE, ERROR -> EnumSet.noneOf(ExternalMallStatus.class); // 전환 불가
        };
    }
}
