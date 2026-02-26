package com.ryuqq.marketplace.adapter.out.client.ses.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Outbox payload JSON 파싱 결과.
 *
 * <p>emailType에 따라 포함되는 필드가 다릅니다:
 *
 * <ul>
 *   <li>SELLER_APPROVAL_INVITE: sellerId, sellerName, contactEmail
 *   <li>SELLER_ADMIN_WELCOME: sellerAdminId, sellerId, authUserId, loginId, name
 * </ul>
 *
 * <p>미지원 필드는 무시합니다 ({@link JsonIgnoreProperties}).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record EmailPayload(
        @JsonProperty("emailType") String emailType,
        @JsonProperty("sellerId") Long sellerId,
        @JsonProperty("sellerName") String sellerName,
        @JsonProperty("contactEmail") String contactEmail,
        @JsonProperty("sellerAdminId") String sellerAdminId,
        @JsonProperty("authUserId") String authUserId,
        @JsonProperty("loginId") String loginId,
        @JsonProperty("name") String name) {}
