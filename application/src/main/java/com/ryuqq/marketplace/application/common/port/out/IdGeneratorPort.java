package com.ryuqq.marketplace.application.common.port.out;

/**
 * ID 생성 Port (Outbound)
 *
 * <p>도메인 Aggregate ID 생성을 위한 Port입니다. UUIDv7 기반의 시간 순서가 보장되는 고유 ID를 생성합니다.
 *
 * <p>구현체는 Adapter Layer에서 제공합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public interface IdGeneratorPort {

    /**
     * 새 ID 생성
     *
     * <p>UUIDv7 형식의 시간 순서가 보장되는 고유 ID를 생성합니다.
     *
     * @return 생성된 ID 문자열
     */
    String generate();

    /**
     * Long 타입 ID 생성
     *
     * <p>시간 순서가 보장되는 고유한 Long ID를 생성합니다. OrderItemId 등 Long 기반 ID에 사용됩니다.
     *
     * @return 생성된 Long ID
     */
    Long generateLong();
}
