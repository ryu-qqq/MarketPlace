package com.ryuqq.marketplace.adapter.in.rest.legacy.session.mapper;

import com.ryuqq.marketplace.adapter.in.rest.legacy.session.dto.request.LegacyPresignedUrlApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.session.dto.response.LegacyPresignedUrlApiResponse;
import com.ryuqq.marketplace.application.legacy.session.dto.command.LegacyGetPresignedUrlCommand;
import com.ryuqq.marketplace.application.legacy.session.dto.response.LegacyPresignedUrlResult;
import org.springframework.stereotype.Component;

/** 레거시 세션 API DTO 변환 매퍼. */
@Component
public class LegacySessionCommandApiMapper {

    public LegacyGetPresignedUrlCommand toCommand(LegacyPresignedUrlApiRequest request) {
        return new LegacyGetPresignedUrlCommand(
                request.fileName(), request.imagePath(), request.fileSize());
    }

    public LegacyPresignedUrlApiResponse toApiResponse(LegacyPresignedUrlResult result) {
        return new LegacyPresignedUrlApiResponse(
                result.sessionId(), result.preSignedUrl(), result.objectKey());
    }
}
