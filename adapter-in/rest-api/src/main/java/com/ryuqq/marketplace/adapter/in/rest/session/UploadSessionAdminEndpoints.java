package com.ryuqq.marketplace.adapter.in.rest.session;

/**
 * UploadSession Admin API 엔드포인트 상수.
 *
 * <p>API-END-001: Endpoints final class
 *
 * <p>API-END-002: static final 상수
 *
 * <p>API-END-003: Path Variable 상수
 */
public final class UploadSessionAdminEndpoints {

    private UploadSessionAdminEndpoints() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /** 업로드 세션 기본 경로 */
    public static final String UPLOAD_SESSIONS = "/api/v1/market/upload-sessions";

    /** Session ID Path Variable 이름 */
    public static final String PATH_SESSION_ID = "sessionId";

    /** 업로드 완료 처리 경로 */
    public static final String COMPLETE = "/{sessionId}/complete";

    /** 레거시 호환 Presigned URL 발급 경로 (프론트 OMS: /api/v1/image/presigned) */
    public static final String LEGACY_IMAGE_PRESIGNED = "/api/v1/image/presigned";
}
