package com.ryuqq.marketplace.adapter.out.persistence.seller.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerAuthOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerAuthOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.seller.mapper.SellerAuthOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerAuthOutboxJpaRepository;
import com.ryuqq.marketplace.domain.seller.SellerFixtures;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerAuthOutbox;
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
@DisplayName("SellerAuthOutboxCommandAdapter 단위 테스트")
class SellerAuthOutboxCommandAdapterTest {

    @InjectMocks private SellerAuthOutboxCommandAdapter sut;

    @Mock private SellerAuthOutboxJpaRepository repository;
    @Mock private SellerAuthOutboxJpaEntityMapper mapper;

    @Nested
    @DisplayName("persist() - Outbox 저장")
    class PersistTest {

        @Test
        @DisplayName("새 SellerAuthOutbox를 저장하고 ID를 반환한다")
        void persist_NewOutbox_ReturnsId() {
            // given
            SellerAuthOutbox domain = SellerFixtures.newSellerAuthOutbox();
            SellerAuthOutboxJpaEntity entity = SellerAuthOutboxJpaEntityFixtures.newPendingEntity();
            SellerAuthOutboxJpaEntity savedEntity =
                    SellerAuthOutboxJpaEntityFixtures.pendingEntity();
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
        @DisplayName("기존 SellerAuthOutbox를 업데이트하고 ID를 반환한다")
        void persist_ExistingOutbox_ReturnsId() {
            // given
            SellerAuthOutbox domain = SellerFixtures.pendingSellerAuthOutboxWithId();
            SellerAuthOutboxJpaEntity entity = SellerAuthOutboxJpaEntityFixtures.pendingEntity();
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
            SellerAuthOutbox domain = SellerFixtures.processingSellerAuthOutbox();
            SellerAuthOutboxJpaEntity entity = SellerAuthOutboxJpaEntityFixtures.processingEntity();
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
            SellerAuthOutbox domain = SellerFixtures.completedSellerAuthOutbox();
            SellerAuthOutboxJpaEntity entity = SellerAuthOutboxJpaEntityFixtures.completedEntity();
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
            SellerAuthOutbox domain = SellerFixtures.failedSellerAuthOutbox();
            SellerAuthOutboxJpaEntity entity = SellerAuthOutboxJpaEntityFixtures.failedEntity();
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
