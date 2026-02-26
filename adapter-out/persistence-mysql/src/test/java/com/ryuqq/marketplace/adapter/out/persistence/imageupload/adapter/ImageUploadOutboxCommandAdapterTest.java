package com.ryuqq.marketplace.adapter.out.persistence.imageupload.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.imageupload.ImageUploadOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.imageupload.entity.ImageUploadOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.imageupload.mapper.ImageUploadOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.imageupload.repository.ImageUploadOutboxJpaRepository;
import com.ryuqq.marketplace.domain.imageupload.ImageUploadFixtures;
import com.ryuqq.marketplace.domain.imageupload.aggregate.ImageUploadOutbox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ImageUploadOutboxCommandAdapter 단위 테스트")
class ImageUploadOutboxCommandAdapterTest {

    @InjectMocks private ImageUploadOutboxCommandAdapter sut;

    @Mock private ImageUploadOutboxJpaRepository repository;
    @Mock private ImageUploadOutboxJpaEntityMapper mapper;

    @Nested
    @DisplayName("persist() - Outbox 저장")
    class PersistTest {

        @Test
        @DisplayName("새 ImageUploadOutbox를 저장하고 ID를 반환한다")
        void persist_NewOutbox_ReturnsId() {
            // given
            ImageUploadOutbox domain = ImageUploadFixtures.newPendingOutbox();
            ImageUploadOutboxJpaEntity entity =
                    ImageUploadOutboxJpaEntityFixtures.newPendingEntity();
            ImageUploadOutboxJpaEntity savedEntity =
                    ImageUploadOutboxJpaEntityFixtures.pendingEntity();
            Long expectedId = ImageUploadOutboxJpaEntityFixtures.DEFAULT_ID;

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(savedEntity);

            // when
            Long result = sut.persist(domain);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(mapper).should().toEntity(domain);
            then(repository).should().save(entity);
        }

        @Test
        @DisplayName("기존 ID가 있는 ImageUploadOutbox를 저장하고 ID를 반환한다")
        void persist_ExistingOutbox_ReturnsId() {
            // given
            ImageUploadOutbox domain = ImageUploadFixtures.pendingOutbox();
            ImageUploadOutboxJpaEntity entity = ImageUploadOutboxJpaEntityFixtures.pendingEntity();
            Long expectedId = ImageUploadOutboxJpaEntityFixtures.DEFAULT_ID;

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            Long result = sut.persist(domain);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(mapper).should().toEntity(domain);
            then(repository).should().save(entity);
        }

        @Test
        @DisplayName("PROCESSING 상태 Outbox를 저장한다")
        void persist_ProcessingOutbox() {
            // given
            ImageUploadOutbox domain = ImageUploadFixtures.processingOutbox();
            ImageUploadOutboxJpaEntity entity =
                    ImageUploadOutboxJpaEntityFixtures.processingEntity();
            Long expectedId = ImageUploadOutboxJpaEntityFixtures.DEFAULT_ID;

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            Long result = sut.persist(domain);

            // then
            assertThat(result).isEqualTo(expectedId);
        }

        @Test
        @DisplayName("COMPLETED 상태 Outbox를 저장한다")
        void persist_CompletedOutbox() {
            // given
            ImageUploadOutbox domain = ImageUploadFixtures.completedOutbox();
            ImageUploadOutboxJpaEntity entity =
                    ImageUploadOutboxJpaEntityFixtures.completedEntity();
            Long expectedId = ImageUploadOutboxJpaEntityFixtures.DEFAULT_ID;

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            Long result = sut.persist(domain);

            // then
            assertThat(result).isEqualTo(expectedId);
        }

        @Test
        @DisplayName("FAILED 상태 Outbox를 저장한다")
        void persist_FailedOutbox() {
            // given
            ImageUploadOutbox domain = ImageUploadFixtures.failedOutbox();
            ImageUploadOutboxJpaEntity entity = ImageUploadOutboxJpaEntityFixtures.failedEntity();
            Long expectedId = ImageUploadOutboxJpaEntityFixtures.DEFAULT_ID;

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            Long result = sut.persist(domain);

            // then
            assertThat(result).isEqualTo(expectedId);
        }
    }
}
