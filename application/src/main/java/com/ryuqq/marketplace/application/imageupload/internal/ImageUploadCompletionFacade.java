package com.ryuqq.marketplace.application.imageupload.internal;

import com.ryuqq.marketplace.application.imageupload.manager.ImageUploadOutboxCommandManager;
import com.ryuqq.marketplace.application.imageupload.port.out.command.ImageUploadedUrlUpdatePort;
import com.ryuqq.marketplace.domain.imageupload.aggregate.ImageUploadOutbox;
import java.time.Instant;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 이미지 업로드 완료 Facade.
 *
 * <p>Outbox 완료와 이미지 uploaded_url 업데이트를 하나의 트랜잭션으로 묶어 원자성을 보장합니다.
 *
 * <p>두 작업이 별도 트랜잭션으로 수행되면 데이터 불일치가 발생할 수 있습니다:
 *
 * <ul>
 *   <li>Outbox는 COMPLETED인데 이미지의 uploaded_url이 null인 상태
 *   <li>이 경우 재처리도 불가능하고 수동 복구가 필요
 * </ul>
 */
@Component
public class ImageUploadCompletionFacade {

    private final ImageUploadOutboxCommandManager outboxCommandManager;
    private final ImageUploadedUrlUpdatePort imageUploadedUrlUpdatePort;

    public ImageUploadCompletionFacade(
            ImageUploadOutboxCommandManager outboxCommandManager,
            ImageUploadedUrlUpdatePort imageUploadedUrlUpdatePort) {
        this.outboxCommandManager = outboxCommandManager;
        this.imageUploadedUrlUpdatePort = imageUploadedUrlUpdatePort;
    }

    /**
     * 이미지 업로드 완료 처리를 원자적으로 수행합니다.
     *
     * <p>같은 트랜잭션에서 다음을 수행합니다:
     *
     * <ol>
     *   <li>이미지의 uploaded_url 업데이트 (sourceType별 라우팅)
     *   <li>Outbox 상태를 COMPLETED로 변경
     * </ol>
     *
     * @param outbox 처리할 Outbox
     * @param newCdnUrl 업로드된 CDN URL
     * @param now 완료 시각
     */
    @Transactional
    public void complete(ImageUploadOutbox outbox, String newCdnUrl, Instant now) {
        imageUploadedUrlUpdatePort.updateUploadedUrl(
                outbox.sourceType(), outbox.sourceId(), newCdnUrl);
        outbox.complete(now);
        outboxCommandManager.persist(outbox);
    }
}
