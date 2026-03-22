package com.ryuqq.marketplace.application.common.util;

import java.util.Map;

/**
 * Outbox Payload JSON 안전 생성 유틸리티.
 *
 * <p>Map 기반으로 JSON을 생성하여 이스케이프 누락을 방지합니다. 문자열 직접 연결 방식 대신 이 유틸을 사용하세요.
 */
public final class OutboxPayloadUtils {

    private OutboxPayloadUtils() {}

    /**
     * Map을 JSON 문자열로 변환합니다.
     *
     * <p>String 값은 이스케이프 처리 후 따옴표로 감싸고, 숫자 값은 따옴표 없이 출력합니다.
     *
     * @param map key-value 쌍
     * @return JSON 문자열
     */
    public static String mapToJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) {
                sb.append(",");
            }
            sb.append("\"").append(escapeJson(entry.getKey())).append("\":");
            Object value = entry.getValue();
            if (value instanceof String s) {
                sb.append("\"").append(escapeJson(s)).append("\"");
            } else {
                sb.append(value);
            }
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * JSON 문자열 이스케이프 처리.
     *
     * @param value 원본 문자열
     * @return 이스케이프된 문자열
     */
    public static String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
