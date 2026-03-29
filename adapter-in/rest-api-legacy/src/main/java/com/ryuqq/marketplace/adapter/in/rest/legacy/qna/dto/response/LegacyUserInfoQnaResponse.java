package com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/** 세토프 호환 QnA 사용자 정보 응답 DTO. */
@JsonInclude(JsonInclude.Include.ALWAYS)
public record LegacyUserInfoQnaResponse(
        String userType,
        Long userId,
        String userName,
        String phoneNumber,
        String email,
        String gender) {}
