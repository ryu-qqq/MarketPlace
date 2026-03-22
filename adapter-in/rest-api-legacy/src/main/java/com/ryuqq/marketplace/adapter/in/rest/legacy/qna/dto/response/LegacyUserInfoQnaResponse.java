package com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/** 세토프 호환 QnA 사용자 정보 응답 DTO. */
public record LegacyUserInfoQnaResponse(
        String userType,
        @JsonInclude(JsonInclude.Include.NON_NULL) Long userId,
        String userName,
        @JsonInclude(JsonInclude.Include.NON_DEFAULT) String phoneNumber,
        @JsonInclude(JsonInclude.Include.NON_DEFAULT) String email,
        @JsonInclude(JsonInclude.Include.NON_NULL) String gender) {}
