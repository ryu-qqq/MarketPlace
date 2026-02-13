package com.ryuqq.marketplace.application.imageupload.internal;

import com.ryuqq.marketplace.application.common.dto.command.ExternalDownloadRequest;
import com.ryuqq.marketplace.domain.imageupload.aggregate.ImageUploadOutbox;
import java.time.Instant;

/**
 * 이미지 업로드 처리 Bundle DTO.
 *
 * <p>Outbox 처리에 필요한 정보를 하나로 묶어 전달합니다.
 *
 * @param outbox 처리 대상 Outbox
 * @param downloadRequest 외부 다운로드 요청 정보
 * @param processedAt 처리 시각
 */
public record ImageUploadProcessBundle(
        ImageUploadOutbox outbox, ExternalDownloadRequest downloadRequest, Instant processedAt) {}
