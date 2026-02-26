package com.ryuqq.marketplace.adapter.out.persistence.selleradmin.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.SellerAdminEmailOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.entity.SellerAdminEmailOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.mapper.SellerAdminEmailOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.repository.SellerAdminEmailOutboxJpaRepository;
import com.ryuqq.marketplace.domain.selleradmin.SellerAdminFixtures;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdminEmailOutbox;
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
@DisplayName("SellerAdminEmailOutboxCommandAdapter 단위 테스트")
class SellerAdminEmailOutboxCommandAdapterTest {

    @InjectMocks private SellerAdminEmailOutboxCommandAdapter sut;

    @Mock private SellerAdminEmailOutboxJpaRepository repository;
    @Mock private SellerAdminEmailOutboxJpaEntityMapper mapper;

    @Nested
    @DisplayName("persist() - Outbox 저장")
    class PersistTest {

        @Test
        @DisplayName("새 SellerAdminEmailOutbox를 저장하고 ID를 반환한다")
        void persist_NewOutbox_ReturnsId() {
            // given
            SellerAdminEmailOutbox domain = SellerAdminFixtures.newSellerAdminEmailOutbox();
            SellerAdminEmailOutboxJpaEntity entity =
                    SellerAdminEmailOutboxJpaEntityFixtures.newPendingEntity();
            SellerAdminEmailOutboxJpaEntity savedEntity =
                    SellerAdminEmailOutboxJpaEntityFixtures.pendingEntity();
            Long expectedId = 1L;

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
        @DisplayName("기존 SellerAdminEmailOutbox를 업데이트하고 ID를 반환한다")
        void persist_ExistingOutbox_ReturnsId() {
            // given
            SellerAdminEmailOutbox domain =
                    SellerAdminFixtures.pendingSellerAdminEmailOutboxWithId();
            SellerAdminEmailOutboxJpaEntity entity =
                    SellerAdminEmailOutboxJpaEntityFixtures.pendingEntity();
            Long expectedId = 1L;

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
            SellerAdminEmailOutbox domain = SellerAdminFixtures.processingSellerAdminEmailOutbox();
            SellerAdminEmailOutboxJpaEntity entity =
                    SellerAdminEmailOutboxJpaEntityFixtures.processingEntity();
            Long expectedId = 1L;

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
            SellerAdminEmailOutbox domain = SellerAdminFixtures.completedSellerAdminEmailOutbox();
            SellerAdminEmailOutboxJpaEntity entity =
                    SellerAdminEmailOutboxJpaEntityFixtures.completedEntity();
            Long expectedId = 1L;

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
            SellerAdminEmailOutbox domain = SellerAdminFixtures.failedSellerAdminEmailOutbox();
            SellerAdminEmailOutboxJpaEntity entity =
                    SellerAdminEmailOutboxJpaEntityFixtures.failedEntity();
            Long expectedId = 1L;

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            Long result = sut.persist(domain);

            // then
            assertThat(result).isEqualTo(expectedId);
        }
    }
}
