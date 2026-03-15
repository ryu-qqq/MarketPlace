package com.ryuqq.marketplace.domain.imagetransform.vo;

/**
 * 이미지 변환 콜백 결과 VO.
 *
 * <p>외부 시스템(FileFlow)의 콜백 데이터를 도메인 언어로 표현합니다. 완료/실패 판단과 재시도 가능 여부를 도메인 내부에서 결정합니다.
 */
@SuppressWarnings({"PMD.DataClass", "PMD.DomainTooManyMethods"})
public class ImageTransformCallbackResult {

    private static final String STATUS_COMPLETED = "COMPLETED";
    private static final String HTTP_400 = "400";
    private static final String HTTP_403 = "403";
    private static final String HTTP_404 = "404";
    private static final String HTTP_410 = "410";

    private final boolean completed;
    private final String resultAssetId;
    private final String resultCdnUrl;
    private final Integer width;
    private final Integer height;
    private final String errorMessage;

    private ImageTransformCallbackResult(
            boolean completed,
            String resultAssetId,
            String resultCdnUrl,
            Integer width,
            Integer height,
            String errorMessage) {
        this.completed = completed;
        this.resultAssetId = resultAssetId;
        this.resultCdnUrl = resultCdnUrl;
        this.width = width;
        this.height = height;
        this.errorMessage = errorMessage;
    }

    /**
     * 콜백 상태 문자열과 결과 데이터로부터 도메인 객체를 생성합니다.
     *
     * @param status 콜백 상태 문자열 (COMPLETED, FAILED 등)
     * @param resultAssetId 결과 에셋 ID (완료 시)
     * @param resultCdnUrl 결과 CDN URL (완료 시, 외부에서 resolve)
     * @param width 결과 너비 (완료 시)
     * @param height 결과 높이 (완료 시)
     * @param errorMessage 에러 메시지 (실패 시)
     * @return 콜백 결과 도메인 객체
     */
    public static ImageTransformCallbackResult of(
            String status,
            String resultAssetId,
            String resultCdnUrl,
            Integer width,
            Integer height,
            String errorMessage) {
        boolean isCompleted = STATUS_COMPLETED.equals(status);
        return new ImageTransformCallbackResult(
                isCompleted, resultAssetId, resultCdnUrl, width, height, errorMessage);
    }

    /** 변환 성공 여부. */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * 재시도 가능한 실패인지 판단합니다.
     *
     * <p>4xx 클라이언트 에러(400, 403, 404, 410)는 재시도해도 동일하게 실패하므로 재시도 불가합니다. 5xx 서버 에러, 타임아웃 등은 재시도
     * 가능합니다.
     */
    public boolean isRetryableFailure() {
        if (errorMessage == null) {
            return true;
        }
        return !errorMessage.contains(HTTP_400)
                && !errorMessage.contains(HTTP_403)
                && !errorMessage.contains(HTTP_404)
                && !errorMessage.contains(HTTP_410);
    }

    public String resultAssetId() {
        return resultAssetId;
    }

    public String resultCdnUrl() {
        return resultCdnUrl;
    }

    public Integer width() {
        return width;
    }

    public Integer height() {
        return height;
    }

    public String errorMessage() {
        return errorMessage;
    }
}
