package com.ryuqq.marketplace.adapter.out.client.ses.adapter;

import com.ryuqq.marketplace.adapter.out.client.ses.config.SesProperties;
import com.ryuqq.marketplace.adapter.out.client.ses.dto.EmailPayload;
import com.ryuqq.marketplace.adapter.out.client.ses.mapper.SesEmailMapper;
import com.ryuqq.marketplace.application.selleradmin.dto.response.SellerAdminEmailSendResult;
import com.ryuqq.marketplace.application.selleradmin.port.out.client.SellerAdminEmailClient;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdminEmailOutbox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.AccountSuspendedException;
import software.amazon.awssdk.services.sesv2.model.MailFromDomainNotVerifiedException;
import software.amazon.awssdk.services.sesv2.model.MessageRejectedException;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;
import software.amazon.awssdk.services.sesv2.model.SendEmailResponse;
import software.amazon.awssdk.services.sesv2.model.SendingPausedException;
import software.amazon.awssdk.services.sesv2.model.SesV2Exception;

/**
 * AWS SES v2 이메일 클라이언트 어댑터.
 *
 * <p>SES v2 SDK를 사용하여 셀러 관리자 이메일을 발송합니다.
 *
 * <p>Outbox 패턴과 함께 사용되며, 멱등키를 통해 중복 요청을 방지합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
@ConditionalOnProperty(prefix = "ses", name = "sender-email")
public class SesEmailClientAdapter implements SellerAdminEmailClient {

    private static final Logger log = LoggerFactory.getLogger(SesEmailClientAdapter.class);

    private final SesV2Client sesV2Client;
    private final SesEmailMapper mapper;
    private final SesProperties properties;

    public SesEmailClientAdapter(
            SesV2Client sesV2Client, SesEmailMapper mapper, SesProperties properties) {
        this.sesV2Client = sesV2Client;
        this.mapper = mapper;
        this.properties = properties;
    }

    @Override
    public SellerAdminEmailSendResult sendEmail(SellerAdminEmailOutbox outbox) {
        try {
            EmailPayload payload = mapper.parsePayload(outbox.payload());
            String recipientEmail = resolveRecipientEmail(payload);

            SendEmailRequest request =
                    mapper.toSendEmailRequest(payload, recipientEmail, properties);

            log.info(
                    "SES 이메일 발송 요청: outboxId={}, emailType={}, recipient={}",
                    outbox.idValue(),
                    payload.emailType(),
                    recipientEmail);

            SendEmailResponse response = sesV2Client.sendEmail(request);

            log.info(
                    "SES 이메일 발송 성공: outboxId={}, messageId={}",
                    outbox.idValue(),
                    response.messageId());

            return mapper.toSuccessResult(response);

        } catch (MessageRejectedException e) {
            log.error(
                    "SES 메시지 거부 (영구 실패): outboxId={}, error={}", outbox.idValue(), e.getMessage());
            return mapper.toPermanentFailure("MESSAGE_REJECTED", e.getMessage());

        } catch (AccountSuspendedException e) {
            log.error("SES 계정 정지 (영구 실패): outboxId={}, error={}", outbox.idValue(), e.getMessage());
            return mapper.toPermanentFailure("ACCOUNT_SUSPENDED", e.getMessage());

        } catch (MailFromDomainNotVerifiedException e) {
            log.error(
                    "SES 도메인 미검증 (영구 실패): outboxId={}, error={}", outbox.idValue(), e.getMessage());
            return mapper.toPermanentFailure("DOMAIN_NOT_VERIFIED", e.getMessage());

        } catch (SendingPausedException e) {
            log.warn(
                    "SES 발송 일시중지 (재시도 가능): outboxId={}, error={}",
                    outbox.idValue(),
                    e.getMessage());
            return mapper.toRetryableFailure("SENDING_PAUSED", e.getMessage());

        } catch (SesV2Exception e) {
            boolean retryable = isRetryableError(e);
            if (retryable) {
                log.warn(
                        "SES 일시적 오류 (재시도 가능): outboxId={}, errorCode={}, error={}",
                        outbox.idValue(),
                        e.awsErrorDetails().errorCode(),
                        e.getMessage());
                return mapper.toRetryableFailure("SES_ERROR", e.getMessage());
            }
            log.error(
                    "SES 영구 오류: outboxId={}, errorCode={}, error={}",
                    outbox.idValue(),
                    e.awsErrorDetails().errorCode(),
                    e.getMessage());
            return mapper.toPermanentFailure("SES_ERROR", e.getMessage());

        } catch (IllegalArgumentException e) {
            log.error(
                    "이메일 페이로드 파싱 실패 (영구 실패): outboxId={}, error={}",
                    outbox.idValue(),
                    e.getMessage());
            return mapper.toPermanentFailure("PAYLOAD_PARSE_ERROR", e.getMessage());
        }
    }

    private String resolveRecipientEmail(EmailPayload payload) {
        String contactEmail = payload.contactEmail();
        if (contactEmail != null && !contactEmail.isBlank()) {
            return contactEmail;
        }

        String loginId = payload.loginId();
        if (loginId != null && !loginId.isBlank()) {
            return loginId;
        }

        throw new IllegalArgumentException("수신자 이메일을 결정할 수 없습니다. contactEmail, loginId 모두 없습니다.");
    }

    private boolean isRetryableError(SesV2Exception e) {
        if (e.awsErrorDetails() == null) {
            return true;
        }
        String errorCode = e.awsErrorDetails().errorCode();
        return "Throttling".equals(errorCode)
                || "TooManyRequestsException".equals(errorCode)
                || "ServiceUnavailable".equals(errorCode)
                || "InternalServiceError".equals(errorCode)
                || e.statusCode() >= 500;
    }
}
