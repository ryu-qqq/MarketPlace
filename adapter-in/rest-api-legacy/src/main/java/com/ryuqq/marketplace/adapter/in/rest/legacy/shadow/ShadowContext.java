package com.ryuqq.marketplace.adapter.in.rest.legacy.shadow;

/**
 * Shadow 모드 상태를 ThreadLocal로 관리하는 컨텍스트.
 *
 * <p>X-Shadow-Mode: verify 헤더가 감지된 쓰기 요청에서 활성화됩니다.
 */
public final class ShadowContext {

    private static final ThreadLocal<ShadowState> CONTEXT = new ThreadLocal<>();

    private ShadowContext() {}

    public static void activate(String correlationId, String httpMethod, String requestPath) {
        CONTEXT.set(new ShadowState(correlationId, httpMethod, requestPath));
    }

    public static boolean isActive() {
        return CONTEXT.get() != null;
    }

    public static void clear() {
        CONTEXT.remove();
    }

    public record ShadowState(String correlationId, String httpMethod, String requestPath) {}
}
