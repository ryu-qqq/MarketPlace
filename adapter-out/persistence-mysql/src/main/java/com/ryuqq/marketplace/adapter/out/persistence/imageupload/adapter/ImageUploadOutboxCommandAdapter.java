package com.ryuqq.marketplace.adapter.out.persistence.imageupload.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.imageupload.entity.ImageUploadOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.imageupload.mapper.ImageUploadOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.imageupload.repository.ImageUploadOutboxJpaRepository;
import com.ryuqq.marketplace.application.imageupload.port.out.command.ImageUploadOutboxCommandPort;
import com.ryuqq.marketplace.domain.imageupload.aggregate.ImageUploadOutbox;
import org.springframework.stereotype.Component;

/**
 * ImageUploadOutboxCommandAdapter - 이미지 업로드 Outbox 명령 어댑터.
 *
 * <p>ImageUploadOutboxCommandPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 */
@Component
public class ImageUploadOutboxCommandAdapter implements ImageUploadOutboxCommandPort {

    private final ImageUploadOutboxJpaRepository repository;
    private final ImageUploadOutboxJpaEntityMapper mapper;

    public ImageUploadOutboxCommandAdapter(
            ImageUploadOutboxJpaRepository repository, ImageUploadOutboxJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(ImageUploadOutbox outbox) {
        ImageUploadOutboxJpaEntity entity = mapper.toEntity(outbox);
        ImageUploadOutboxJpaEntity saved = repository.save(entity);
        outbox.refreshVersion(saved.getVersion());
        return saved.getId();
    }
}
