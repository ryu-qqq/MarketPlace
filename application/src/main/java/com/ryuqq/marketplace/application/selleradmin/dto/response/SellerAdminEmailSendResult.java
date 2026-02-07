package com.ryuqq.marketplace.application.selleradmin.dto.response;

/**
 * 셀러 관리자 이메일 발송 결과.
 *
 * <p>외부 이메일 서비스(SES 등)에 발송 요청 결과를 담습니다.
 *
 * @param success 성공 여부
 * @param messageId 발송된 메시지 ID (성공 시)
 * @param errorCode 에러 코드 (실패 시)
 * @param errorMessage 에러 메시지 (실패 시)
 * @param retryable 재시도 가능 여부 (실패 시)
 */
public record SellerAdminEmailSendResult(
        boolean success,
        String messageId,
        String errorCode,
        String errorMessage,
        boolean retryable) {

    public static SellerAdminEmailSendResult success(String messageId) {
        return new SellerAdminEmailSendResult(true, messageId, null, null, false);
    }

    public static SellerAdminEmailSendResult retryableFailure(
            String errorCode, String errorMessage) {
        return new SellerAdminEmailSendResult(false, null, errorCode, errorMessage, true);
    }

    public static SellerAdminEmailSendResult permanentFailure(
            String errorCode, String errorMessage) {
        return new SellerAdminEmailSendResult(false, null, errorCode, errorMessage, false);
    }
}
