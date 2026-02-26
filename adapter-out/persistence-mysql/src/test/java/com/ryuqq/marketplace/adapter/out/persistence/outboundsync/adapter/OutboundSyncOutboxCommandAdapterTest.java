package com.ryuqq.marketplace.adapter.out.persistence.outboundsync.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.OutboundSyncOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.entity.OutboundSyncOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.mapper.OutboundSyncOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.repository.OutboundSyncOutboxJpaRepository;
import com.ryuqq.marketplace.domain.outboundsync.OutboundSyncOutboxFixtures;
import com.ryuqq.marketplace.domain.outboundsync.aggregate.OutboundSyncOutbox;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * OutboundSyncOutboxCommandAdapter 단위 테스트.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * <p>PER-ADP-002: Domain → Entity 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("OutboundSyncOutboxCommandAdapter 단위 테스트")
class OutboundSyncOutboxCommandAdapterTest {

    @InjectMocks private OutboundSyncOutboxCommandAdapter sut;

    @Mock private OutboundSyncOutboxJpaRepository repository;
    @Mock private OutboundSyncOutboxJpaEntityMapper mapper;

    // ========================================================================
    // 1. persist() 테스트
    // ========================================================================

    @Nested
    @DisplayName("persist() - 단건 저장")
    class PersistTest {

        @Test
        @DisplayName("새 OutboundSyncOutbox를 저장하고 ID를 반환한다")
        void persist_NewOutbox_ReturnsId() {
            // given
            OutboundSyncOutbox domain = OutboundSyncOutboxFixtures.newPendingOutbox();
            OutboundSyncOutboxJpaEntity entity =
                    OutboundSyncOutboxJpaEntityFixtures.newPendingEntity();
            OutboundSyncOutboxJpaEntity savedEntity =
                    OutboundSyncOutboxJpaEntityFixtures.pendingEntity();
            Long expectedId = OutboundSyncOutboxJpaEntityFixtures.DEFAULT_ID;

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
        @DisplayName("기존 OutboundSyncOutbox를 업데이트하고 ID를 반환한다")
        void persist_ExistingOutbox_ReturnsId() {
            // given
            OutboundSyncOutbox domain = OutboundSyncOutboxFixtures.pendingOutbox();
            OutboundSyncOutboxJpaEntity entity =
                    OutboundSyncOutboxJpaEntityFixtures.pendingEntity();
            Long expectedId = OutboundSyncOutboxJpaEntityFixtures.DEFAULT_ID;

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
            OutboundSyncOutbox domain = OutboundSyncOutboxFixtures.processingOutbox();
            OutboundSyncOutboxJpaEntity entity =
                    OutboundSyncOutboxJpaEntityFixtures.processingEntity();
            Long expectedId = OutboundSyncOutboxJpaEntityFixtures.DEFAULT_ID;

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
            OutboundSyncOutbox domain = OutboundSyncOutboxFixtures.completedOutbox();
            OutboundSyncOutboxJpaEntity entity =
                    OutboundSyncOutboxJpaEntityFixtures.completedEntity();
            Long expectedId = OutboundSyncOutboxJpaEntityFixtures.DEFAULT_ID;

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
            OutboundSyncOutbox domain = OutboundSyncOutboxFixtures.failedOutbox();
            OutboundSyncOutboxJpaEntity entity = OutboundSyncOutboxJpaEntityFixtures.failedEntity();
            Long expectedId = OutboundSyncOutboxJpaEntityFixtures.DEFAULT_ID;

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            Long result = sut.persist(domain);

            // then
            assertThat(result).isEqualTo(expectedId);
        }
    }

    // ========================================================================
    // 2. persistAll() 테스트
    // ========================================================================

    @Nested
    @DisplayName("persistAll() - 다건 저장")
    class PersistAllTest {

        @Test
        @DisplayName("여러 OutboundSyncOutbox를 일괄 저장한다")
        void persistAll_MultipleOutboxes_SavesAll() {
            // given
            OutboundSyncOutbox domain1 = OutboundSyncOutboxFixtures.newPendingOutbox();
            OutboundSyncOutbox domain2 = OutboundSyncOutboxFixtures.newPendingUpdateOutbox();
            OutboundSyncOutbox domain3 = OutboundSyncOutboxFixtures.newPendingDeleteOutbox();
            List<OutboundSyncOutbox> domains = List.of(domain1, domain2, domain3);

            OutboundSyncOutboxJpaEntity entity1 =
                    OutboundSyncOutboxJpaEntityFixtures.newPendingEntity();
            OutboundSyncOutboxJpaEntity entity2 =
                    OutboundSyncOutboxJpaEntityFixtures.newPendingEntity();
            OutboundSyncOutboxJpaEntity entity3 =
                    OutboundSyncOutboxJpaEntityFixtures.newPendingEntity();

            given(mapper.toEntity(domain1)).willReturn(entity1);
            given(mapper.toEntity(domain2)).willReturn(entity2);
            given(mapper.toEntity(domain3)).willReturn(entity3);
            given(repository.saveAll(List.of(entity1, entity2, entity3)))
                    .willReturn(List.of(entity1, entity2, entity3));

            // when
            sut.persistAll(domains);

            // then
            then(mapper)
                    .should(Mockito.times(3))
                    .toEntity(ArgumentMatchers.any(OutboundSyncOutbox.class));
            then(repository).should().saveAll(List.of(entity1, entity2, entity3));
        }

        @Test
        @DisplayName("빈 리스트 입력 시 saveAll을 빈 리스트로 호출한다")
        void persistAll_EmptyList_CallsSaveAllWithEmptyList() {
            // given
            List<OutboundSyncOutbox> domains = List.of();
            given(repository.saveAll(List.of())).willReturn(List.of());

            // when
            sut.persistAll(domains);

            // then
            then(repository).should().saveAll(List.of());
            then(mapper).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("단건 리스트도 saveAll로 처리한다")
        void persistAll_SingleItem_CallsSaveAll() {
            // given
            OutboundSyncOutbox domain = OutboundSyncOutboxFixtures.newPendingOutbox();
            OutboundSyncOutboxJpaEntity entity =
                    OutboundSyncOutboxJpaEntityFixtures.newPendingEntity();
            List<OutboundSyncOutbox> domains = List.of(domain);

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.saveAll(List.of(entity))).willReturn(List.of(entity));

            // when
            sut.persistAll(domains);

            // then
            then(mapper).should().toEntity(domain);
            then(repository).should().saveAll(List.of(entity));
        }
    }
}
