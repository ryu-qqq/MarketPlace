package com.ryuqq.marketplace.adapter.out.persistence.canceloutbox.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.canceloutbox.CancelOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.canceloutbox.entity.CancelOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.canceloutbox.mapper.CancelOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.canceloutbox.repository.CancelOutboxJpaRepository;
import com.ryuqq.marketplace.domain.cancel.CancelFixtures;
import com.ryuqq.marketplace.domain.cancel.outbox.aggregate.CancelOutbox;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CancelOutboxCommandAdapter 단위 테스트.
 *
 * <p>PER-ADP-003: CommandAdapter는 JpaRepository만 사용.
 *
 * <p>PER-ADP-006: Mapper를 통해 Domain -> Entity 변환 후 저장.
 *
 * <p>CancelOutbox 특이사항: persist 후 version을 Domain에 반영합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("CancelOutboxCommandAdapter 단위 테스트")
class CancelOutboxCommandAdapterTest {

    @Mock private CancelOutboxJpaRepository repository;
    @Mock private CancelOutboxJpaEntityMapper mapper;

    @InjectMocks private CancelOutboxCommandAdapter commandAdapter;

    // ========================================================================
    // 1. persist 테스트
    // ========================================================================

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("Mapper를 통해 Entity로 변환한 후 repository.save를 호출합니다")
        void persist_CallsMapperAndRepository() {
            // given
            CancelOutbox outbox = CancelFixtures.pendingCancelOutbox();
            CancelOutboxJpaEntity entity = CancelOutboxJpaEntityFixtures.pendingEntity();
            CancelOutboxJpaEntity savedEntity = CancelOutboxJpaEntityFixtures.pendingEntity();

            given(mapper.toEntity(outbox)).willReturn(entity);
            given(repository.save(entity)).willReturn(savedEntity);

            // when
            Long result = commandAdapter.persist(outbox);

            // then
            then(mapper).should().toEntity(outbox);
            then(repository).should().save(entity);
            assertThat(result).isEqualTo(savedEntity.getId());
        }

        @Test
        @DisplayName("persist 후 저장된 Entity의 id를 반환합니다")
        void persist_ReturnsIdOfSavedEntity() {
            // given
            CancelOutbox outbox = CancelFixtures.pendingCancelOutbox();
            CancelOutboxJpaEntity entity = CancelOutboxJpaEntityFixtures.pendingEntity();
            CancelOutboxJpaEntity savedEntity = CancelOutboxJpaEntityFixtures.pendingEntity();

            given(mapper.toEntity(outbox)).willReturn(entity);
            given(repository.save(entity)).willReturn(savedEntity);

            // when
            Long result = commandAdapter.persist(outbox);

            // then
            assertThat(result).isEqualTo(CancelOutboxJpaEntityFixtures.DEFAULT_ID);
        }

        @Test
        @DisplayName("persist 후 Domain의 version이 저장된 Entity의 version으로 갱신됩니다")
        void persist_RefreshesDomainVersion() {
            // given
            CancelOutbox outbox = CancelFixtures.pendingCancelOutbox();
            CancelOutboxJpaEntity entity = CancelOutboxJpaEntityFixtures.pendingEntity();
            CancelOutboxJpaEntity savedEntity = CancelOutboxJpaEntityFixtures.completedEntity();

            given(mapper.toEntity(outbox)).willReturn(entity);
            given(repository.save(entity)).willReturn(savedEntity);

            long versionBefore = outbox.version();

            // when
            commandAdapter.persist(outbox);

            // then
            assertThat(outbox.version()).isEqualTo(savedEntity.getVersion());
            assertThat(outbox.version()).isNotEqualTo(versionBefore);
        }

        @Test
        @DisplayName("Mapper가 정확히 한 번만 호출됩니다")
        void persist_CallsMapperExactlyOnce() {
            // given
            CancelOutbox outbox = CancelFixtures.pendingCancelOutbox();
            CancelOutboxJpaEntity entity = CancelOutboxJpaEntityFixtures.pendingEntity();

            given(mapper.toEntity(outbox)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            commandAdapter.persist(outbox);

            // then
            then(mapper).should().toEntity(outbox);
            then(mapper).shouldHaveNoMoreInteractions();
        }
    }

    // ========================================================================
    // 2. persistAll 테스트
    // ========================================================================

    @Nested
    @DisplayName("persistAll 메서드 테스트")
    class PersistAllTest {

        @Test
        @DisplayName("CancelOutbox 목록을 Entity 목록으로 변환한 후 saveAll을 호출합니다")
        void persistAll_CallsMapperAndRepositoryForEachOutbox() {
            // given
            CancelOutbox outbox1 = CancelFixtures.pendingCancelOutbox();
            CancelOutbox outbox2 = CancelFixtures.pendingCancelOutbox();
            CancelOutboxJpaEntity entity1 = CancelOutboxJpaEntityFixtures.pendingEntity(1L, 1001L);
            CancelOutboxJpaEntity entity2 = CancelOutboxJpaEntityFixtures.pendingEntity(2L, 1002L);
            List<CancelOutbox> outboxes = List.of(outbox1, outbox2);

            given(mapper.toEntity(outbox1)).willReturn(entity1);
            given(mapper.toEntity(outbox2)).willReturn(entity2);
            given(repository.saveAll(List.of(entity1, entity2)))
                    .willReturn(List.of(entity1, entity2));

            // when
            commandAdapter.persistAll(outboxes);

            // then
            then(mapper).should().toEntity(outbox1);
            then(mapper).should().toEntity(outbox2);
            then(repository).should().saveAll(List.of(entity1, entity2));
        }

        @Test
        @DisplayName("persistAll 후 각 Domain의 version이 저장된 Entity의 version으로 갱신됩니다")
        void persistAll_RefreshesVersionForEachDomain() {
            // given
            CancelOutbox outbox1 = CancelFixtures.pendingCancelOutbox();
            CancelOutbox outbox2 = CancelFixtures.pendingCancelOutbox();
            CancelOutboxJpaEntity entity1 = CancelOutboxJpaEntityFixtures.pendingEntity(1L, 1001L);
            CancelOutboxJpaEntity entity2 = CancelOutboxJpaEntityFixtures.pendingEntity(2L, 1002L);
            CancelOutboxJpaEntity savedEntity1 = CancelOutboxJpaEntityFixtures.completedEntity();
            CancelOutboxJpaEntity savedEntity2 = CancelOutboxJpaEntityFixtures.completedEntity();
            List<CancelOutbox> outboxes = List.of(outbox1, outbox2);

            given(mapper.toEntity(outbox1)).willReturn(entity1);
            given(mapper.toEntity(outbox2)).willReturn(entity2);
            given(repository.saveAll(List.of(entity1, entity2)))
                    .willReturn(List.of(savedEntity1, savedEntity2));

            // when
            commandAdapter.persistAll(outboxes);

            // then
            assertThat(outbox1.version()).isEqualTo(savedEntity1.getVersion());
            assertThat(outbox2.version()).isEqualTo(savedEntity2.getVersion());
        }

        @Test
        @DisplayName("빈 목록 입력 시 saveAll을 빈 목록으로 호출합니다")
        void persistAll_WithEmptyList_CallsSaveAllWithEmptyList() {
            // given
            given(repository.saveAll(List.of())).willReturn(List.of());

            // when
            commandAdapter.persistAll(List.of());

            // then
            then(repository).should().saveAll(List.of());
            then(mapper).shouldHaveNoInteractions();
        }
    }
}
