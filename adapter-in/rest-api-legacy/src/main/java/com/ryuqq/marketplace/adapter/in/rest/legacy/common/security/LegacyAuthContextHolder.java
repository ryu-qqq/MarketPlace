package com.ryuqq.marketplace.adapter.in.rest.legacy.common.security;

/**
 * 레거시 인증 컨텍스트 ThreadLocal 홀더.
 *
 * <p>{@link LegacyJwtAuthenticationFilter}가 JWT claims를 파싱하여 세팅하고,
 * Controller에서 {@code LegacyAuthContextHolder.getContext()}로 조회합니다.
 *
 * <p>표준 API의 {@code UserContextHolder}와 동일한 패턴입니다.
 */
public final class LegacyAuthContextHolder {

    private static final ThreadLocal<LegacyAuthContext> CONTEXT = new ThreadLocal<>();

    private LegacyAuthContextHolder() {}

    public static void setContext(LegacyAuthContext context) {
        CONTEXT.set(context);
    }

    public static LegacyAuthContext getContext() {
        LegacyAuthContext context = CONTEXT.get();
        if (context == null) {
            throw new IllegalStateException("LegacyAuthContext가 세팅되지 않았습니다. 인증되지 않은 요청입니다.");
        }
        return context;
    }

    public static long getSellerId() {
        return getContext().sellerId();
    }

    public static String getEmail() {
        return getContext().email();
    }

    public static String getRoleType() {
        return getContext().roleType();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
