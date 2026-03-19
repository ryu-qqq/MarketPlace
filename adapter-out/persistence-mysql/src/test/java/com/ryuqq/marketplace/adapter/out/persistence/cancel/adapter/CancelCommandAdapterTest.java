package com.ryuqq.marketplace.adapter.out.persistence.cancel.adapter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.cancel.CancelJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.cancel.entity.CancelJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.cancel.mapper.CancelJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.cancel.repository.CancelJpaRepository;
import com.ryuqq.marketplace.domain.cancel.CancelFixtures;
import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
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
 * CancelCommandAdapter 단위 테스트.
 *
 * <p>PER-ADP-003: CommandAdapter는 JpaRepository만 사용.
 *
 * <p>PER-ADP-006: Mapper를 통해 Domain -> Entity 변환 후 저장.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("CancelCommandAdapter 단위 테스트")
class CancelCommandAdapterTest {

    @Mock private CancelJpaRepository cancelRepository;
    @Mock private CancelJpaEntityMapper mapper;

    @InjectMocks private CancelCommandAdapter commandAdapter;

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
            Cancel cancel = CancelFixtures.requestedCancel();
            CancelJpaEntity entity =
                    CancelJpaEntityFixtures.requestedEntity(
                            CancelJpaEntityFixtures.DEFAULT_ID,
                            CancelJpaEntityFixtures.DEFAULT_ORDER_ITEM_ID,
                            CancelJpaEntityFixtures.DEFAULT_SELLER_ID);

            given(mapper.toEntity(cancel)).willReturn(entity);

            // when
            commandAdapter.persist(cancel);

            // then
            then(mapper).should().toEntity(cancel);
            then(cancelRepository).should().save(entity);
        }

        @Test
        @DisplayName("REQUESTED 상태 Cancel을 저장합니다")
        void persist_WithRequestedCancel_SavesSuccessfully() {
            // given
            Cancel cancel = CancelFixtures.requestedCancel();
            CancelJpaEntity entity =
                    CancelJpaEntityFixtures.requestedEntity(
                            CancelJpaEntityFixtures.DEFAULT_ID,
                            CancelJpaEntityFixtures.DEFAULT_ORDER_ITEM_ID,
                            CancelJpaEntityFixtures.DEFAULT_SELLER_ID);

            given(mapper.toEntity(cancel)).willReturn(entity);

            // when
            commandAdapter.persist(cancel);

            // then
            then(cancelRepository).should().save(entity);
        }

        @Test
        @DisplayName("APPROVED 상태 Cancel을 저장합니다")
        void persist_WithApprovedCancel_SavesSuccessfully() {
            // given
            Cancel cancel = CancelFixtures.approvedCancel();
            CancelJpaEntity entity =
                    CancelJpaEntityFixtures.approvedEntity(
                            CancelJpaEntityFixtures.DEFAULT_ID,
                            CancelJpaEntityFixtures.DEFAULT_ORDER_ITEM_ID,
                            CancelJpaEntityFixtures.DEFAULT_SELLER_ID);

            given(mapper.toEntity(cancel)).willReturn(entity);

            // when
            commandAdapter.persist(cancel);

            // then
            then(cancelRepository).should().save(entity);
        }

        @Test
        @DisplayName("COMPLETED 상태 Cancel을 저장합니다")
        void persist_WithCompletedCancel_SavesSuccessfully() {
            // given
            Cancel cancel = CancelFixtures.completedCancel();
            CancelJpaEntity entity =
                    CancelJpaEntityFixtures.rejectedEntity(
                            CancelJpaEntityFixtures.DEFAULT_ID,
                            CancelJpaEntityFixtures.DEFAULT_ORDER_ITEM_ID,
                            CancelJpaEntityFixtures.DEFAULT_SELLER_ID);

            given(mapper.toEntity(cancel)).willReturn(entity);

            // when
            commandAdapter.persist(cancel);

            // then
            then(cancelRepository).should().save(entity);
        }

        @Test
        @DisplayName("Mapper가 정확히 한 번만 호출됩니다")
        void persist_CallsMapperExactlyOnce() {
            // given
            Cancel cancel = CancelFixtures.requestedCancel();
            CancelJpaEntity entity =
                    CancelJpaEntityFixtures.requestedEntity(
                            CancelJpaEntityFixtures.DEFAULT_ID,
                            CancelJpaEntityFixtures.DEFAULT_ORDER_ITEM_ID,
                            CancelJpaEntityFixtures.DEFAULT_SELLER_ID);

            given(mapper.toEntity(cancel)).willReturn(entity);

            // when
            commandAdapter.persist(cancel);

            // then
            then(mapper).should().toEntity(cancel);
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
        @DisplayName("Cancel 목록을 Entity 목록으로 변환한 후 saveAll을 호출합니다")
        void persistAll_CallsMapperAndRepositoryForEachCancel() {
            // given
            Cancel cancel1 = CancelFixtures.requestedCancel();
            Cancel cancel2 = CancelFixtures.approvedCancel();
            CancelJpaEntity entity1 =
                    CancelJpaEntityFixtures.requestedEntity(
                            "cancel-id-001",
                            CancelJpaEntityFixtures.DEFAULT_ORDER_ITEM_ID,
                            CancelJpaEntityFixtures.DEFAULT_SELLER_ID);
            CancelJpaEntity entity2 =
                    CancelJpaEntityFixtures.approvedEntity(
                            "cancel-id-002",
                            CancelJpaEntityFixtures.DEFAULT_ORDER_ITEM_ID,
                            CancelJpaEntityFixtures.DEFAULT_SELLER_ID);
            List<Cancel> cancels = List.of(cancel1, cancel2);

            given(mapper.toEntity(cancel1)).willReturn(entity1);
            given(mapper.toEntity(cancel2)).willReturn(entity2);

            // when
            commandAdapter.persistAll(cancels);

            // then
            then(mapper).should().toEntity(cancel1);
            then(mapper).should().toEntity(cancel2);
            then(cancelRepository).should().saveAll(List.of(entity1, entity2));
        }

        @Test
        @DisplayName("빈 목록 입력 시 빈 목록으로 saveAll을 호출합니다")
        void persistAll_WithEmptyList_CallsSaveAllWithEmptyList() {
            // when
            commandAdapter.persistAll(List.of());

            // then
            then(cancelRepository).should().saveAll(List.of());
            then(mapper).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("단일 Cancel 목록으로 persistAll 호출 시 saveAll이 한 번 호출됩니다")
        void persistAll_WithSingleCancel_CallsSaveAllOnce() {
            // given
            Cancel cancel = CancelFixtures.requestedCancel();
            CancelJpaEntity entity =
                    CancelJpaEntityFixtures.requestedEntity(
                            CancelJpaEntityFixtures.DEFAULT_ID,
                            CancelJpaEntityFixtures.DEFAULT_ORDER_ITEM_ID,
                            CancelJpaEntityFixtures.DEFAULT_SELLER_ID);

            given(mapper.toEntity(cancel)).willReturn(entity);

            // when
            commandAdapter.persistAll(List.of(cancel));

            // then
            then(cancelRepository).should().saveAll(any());
        }
    }
}
