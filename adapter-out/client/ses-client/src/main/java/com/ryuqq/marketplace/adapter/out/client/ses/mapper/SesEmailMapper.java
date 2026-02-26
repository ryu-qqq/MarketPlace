package com.ryuqq.marketplace.adapter.out.client.ses.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.marketplace.adapter.out.client.ses.config.SesProperties;
import com.ryuqq.marketplace.adapter.out.client.ses.dto.EmailPayload;
import com.ryuqq.marketplace.application.selleradmin.dto.response.SellerAdminEmailSendResult;
import com.ryuqq.marketplace.domain.selleradmin.vo.SellerAdminEmailType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Map;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sesv2.model.Destination;
import software.amazon.awssdk.services.sesv2.model.EmailContent;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;
import software.amazon.awssdk.services.sesv2.model.SendEmailResponse;
import software.amazon.awssdk.services.sesv2.model.Template;

/**
 * SES Email Mapper.
 *
 * <p>Outbox payload와 SES SDK 객체 간의 변환을 담당합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class SesEmailMapper {

    private final ObjectMapper objectMapper;

    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP2",
            justification = "Spring-managed singleton bean, immutable after injection")
    public SesEmailMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // ========== Request 변환 ==========

    /**
     * Outbox JSON 페이로드를 내부 DTO로 파싱합니다.
     *
     * @param jsonPayload JSON 페이로드
     * @return EmailPayload 내부 DTO
     * @throws IllegalArgumentException 페이로드 파싱 실패 시
     */
    public EmailPayload parsePayload(String jsonPayload) {
        try {
            return objectMapper.readValue(jsonPayload, EmailPayload.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                    "Failed to parse email outbox payload: " + e.getMessage(), e);
        }
    }

    /**
     * EmailPayload를 SES v2 템플릿 기반 SendEmailRequest로 변환합니다.
     *
     * @param payload 파싱된 이메일 페이로드
     * @param recipientEmail 수신자 이메일 주소
     * @param properties SES 설정
     * @return SES v2 SendEmailRequest
     */
    public SendEmailRequest toSendEmailRequest(
            EmailPayload payload, String recipientEmail, SesProperties properties) {
        SellerAdminEmailType emailType = SellerAdminEmailType.valueOf(payload.emailType());
        String templateName = resolveTemplateName(emailType, properties);
        String templateData = buildTemplateData(payload, properties);

        return SendEmailRequest.builder()
                .fromEmailAddress(properties.getSenderEmail())
                .destination(Destination.builder().toAddresses(recipientEmail).build())
                .content(
                        EmailContent.builder()
                                .template(
                                        Template.builder()
                                                .templateName(templateName)
                                                .templateData(templateData)
                                                .build())
                                .build())
                .build();
    }

    // ========== Response 변환 ==========

    /**
     * SES 응답을 성공 결과로 변환합니다.
     *
     * @param response SES SendEmail 응답
     * @return 이메일 발송 성공 결과
     */
    public SellerAdminEmailSendResult toSuccessResult(SendEmailResponse response) {
        return SellerAdminEmailSendResult.success(response.messageId());
    }

    /**
     * 재시도 가능한 실패 결과를 생성합니다.
     *
     * @param errorCode 에러 코드
     * @param errorMessage 에러 메시지
     * @return 재시도 가능 실패 결과
     */
    public SellerAdminEmailSendResult toRetryableFailure(String errorCode, String errorMessage) {
        return SellerAdminEmailSendResult.retryableFailure(errorCode, errorMessage);
    }

    /**
     * 영구 실패 결과를 생성합니다.
     *
     * @param errorCode 에러 코드
     * @param errorMessage 에러 메시지
     * @return 영구 실패 결과
     */
    public SellerAdminEmailSendResult toPermanentFailure(String errorCode, String errorMessage) {
        return SellerAdminEmailSendResult.permanentFailure(errorCode, errorMessage);
    }

    // ========== Private ==========

    private String resolveTemplateName(SellerAdminEmailType emailType, SesProperties properties) {
        String templateName = properties.getTemplates().get(emailType.name());
        if (templateName == null || templateName.isBlank()) {
            return emailType.templateName();
        }
        return templateName;
    }

    @SuppressWarnings("unchecked")
    private String buildTemplateData(EmailPayload payload, SesProperties properties) {
        try {
            Map<String, Object> data = objectMapper.convertValue(payload, Map.class);
            data.values().removeIf(v -> v == null);

            String signUpBaseUrl = properties.getSignUpBaseUrl();
            if (signUpBaseUrl != null && !signUpBaseUrl.isBlank() && payload.sellerId() != null) {
                data.put(
                        "signUpUrl",
                        signUpBaseUrl + "/auth/sign-user?sellerId=" + payload.sellerId());
            }

            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                    "Failed to serialize template data: " + e.getMessage(), e);
        }
    }
}
