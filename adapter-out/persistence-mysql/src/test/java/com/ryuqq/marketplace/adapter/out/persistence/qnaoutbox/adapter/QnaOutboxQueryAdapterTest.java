package com.ryuqq.marketplace.adapter.out.persistence.qnaoutbox.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.qnaoutbox.QnaOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.qnaoutbox.entity.QnaOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.qnaoutbox.mapper.QnaOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.qnaoutbox.repository.QnaOutboxQueryDslRepository;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.qna.id.QnaId;
import com.ryuqq.marketplace.domain.qna.outbox.aggregate.QnaOutbox;
import com.ryuqq.marketplace.domain.qna.outbox.id.QnaOutboxId;
import com.ryuqq.marketplace.domain.qna.outbox.vo.QnaOutboxStatus;
import com.ryuqq.marketplace.domain.qna.outbox.vo.QnaOutboxType;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * QnaOutboxQueryAdapterTest - QnaOutbox Query Adapter 단위 테스트.
 *
 * <p>PER-ADP-004: QueryAdapter는 QueryDslRepository 사용.
 *
 * <p>PER-ADP-005: Entity -> Domain 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("QnaOutboxQueryAdapter 단위 테스트")
class QnaOutboxQueryAdapterTest {

    @Mock private QnaOutboxQueryDslRepository queryDslRepository;

    @Mock private QnaOutboxJpaEntityMapper mapper;

    @InjectMocks private QnaOutboxQueryAdapter queryAdapter;

    private static QnaOutbox pendingDomain() {
        return QnaOutbox.reconstitute(
                QnaOutboxId.of(QnaOutboxJpaEntityFixtures.DEFAULT_ID),
                QnaId.of(QnaOutboxJpaEntityFixtures.DEFAULT_QNA_ID),
                QnaOutboxJpaEntityFixtures.DEFAULT_SALES_CHANNEL_ID,
                QnaOutboxJpaEntityFixtures.DEFAULT_EXTERNAL_QNA_ID,
                QnaOutboxType.ANSWER,
                QnaOutboxStatus.PENDING,
                QnaOutboxJpaEntityFixtures.DEFAULT_PAYLOAD,
                0,
                3,
                CommonVoFixtures.now(),
                CommonVoFixtures.now(),
                null,
                null,
                0L,
                QnaOutboxJpaEntityFixtures.DEFAULT_IDEMPOTENCY_KEY);
    }

    private static QnaOutbox processingDomain(Long id) {
        return QnaOutbox.reconstitute(
                QnaOutboxId.of(id),
                QnaId.of(QnaOutboxJpaEntityFixtures.DEFAULT_QNA_ID),
                QnaOutboxJpaEntityFixtures.DEFAULT_SALES_CHANNEL_ID,
                QnaOutboxJpaEntityFixtures.DEFAULT_EXTERNAL_QNA_ID,
                QnaOutboxType.ANSWER,
                QnaOutboxStatus.PROCESSING,
                QnaOutboxJpaEntityFixtures.DEFAULT_PAYLOAD,
                0,
                3,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday(),
                null,
                null,
                0L,
                QnaOutboxJpaEntityFixtures.DEFAULT_IDEMPOTENCY_KEY + "_" + id);
    }

    // ========================================================================
    // 1. findById 테스트
    // ========================================================================

    @Nested
    @DisplayName("findById 메서드 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("존재하는 ID로 조회 시 Domain을 반환합니다")
        void findById_WithExistingId_ReturnsDomain() {
            // given
            long id = QnaOutboxJpaEntityFixtures.DEFAULT_ID;
            QnaOutboxJpaEntity entity = QnaOutboxJpaEntityFixtures.pendingEntity(id);
            QnaOutbox domain = pendingDomain();

            given(queryDslRepository.findById(id)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<QnaOutbox> result = queryAdapter.findById(id);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistingId_ReturnsEmpty() {
            // given
            long id = 9999L;
            given(queryDslRepository.findById(id)).willReturn(Optional.empty());

            // when
            Optional<QnaOutbox> result = queryAdapter.findById(id);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Entity가 존재할 때 Mapper가 호출됩니다")
        void findById_WhenEntityExists_CallsMapper() {
            // given
            long id = QnaOutboxJpaEntityFixtures.DEFAULT_ID;
            QnaOutboxJpaEntity entity = QnaOutboxJpaEntityFixtures.pendingEntity(id);
            QnaOutbox domain = pendingDomain();

            given(queryDslRepository.findById(id)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            queryAdapter.findById(id);

            // then
            then(mapper).should().toDomain(entity);
        }

        @Test
        @DisplayName("Entity가 없을 때 Mapper는 호출되지 않습니다")
        void findById_WhenEntityNotExists_DoesNotCallMapper() {
            // given
            long id = 9999L;
            given(queryDslRepository.findById(id)).willReturn(Optional.empty());

            // when
            queryAdapter.findById(id);

            // then
            then(mapper).shouldHaveNoInteractions();
        }
    }

    // ========================================================================
    // 2. findPendingOutboxes 테스트
    // ========================================================================

    @Nested
    @DisplayName("findPendingOutboxes 메서드 테스트")
    class FindPendingOutboxesTest {

        @Test
        @DisplayName("beforeTime 이전에 생성된 PENDING Outbox 목록을 반환합니다")
        void findPendingOutboxes_WithBeforeTime_ReturnsPendingList() {
            // given
            Instant beforeTime = Instant.now();
            int limit = 10;
            QnaOutboxJpaEntity entity1 = QnaOutboxJpaEntityFixtures.pendingEntity(1L);
            QnaOutboxJpaEntity entity2 = QnaOutboxJpaEntityFixtures.pendingEntity(2L);
            QnaOutbox domain1 = pendingDomain();
            QnaOutbox domain2 = pendingDomain();

            given(queryDslRepository.findPendingOutboxes(beforeTime, limit))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<QnaOutbox> result = queryAdapter.findPendingOutboxes(beforeTime, limit);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("PENDING 데이터가 없을 때 빈 목록을 반환합니다")
        void findPendingOutboxes_WhenNoPending_ReturnsEmptyList() {
            // given
            Instant beforeTime = Instant.now();
            int limit = 10;

            given(queryDslRepository.findPendingOutboxes(beforeTime, limit))
                    .willReturn(List.of());

            // when
            List<QnaOutbox> result = queryAdapter.findPendingOutboxes(beforeTime, limit);

            // then
            assertThat(result).isEmpty();
            then(mapper).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("Mapper가 각 Entity마다 호출됩니다")
        void findPendingOutboxes_CallsMapperForEachEntity() {
            // given
            Instant beforeTime = Instant.now();
            int limit = 10;
            QnaOutboxJpaEntity entity1 = QnaOutboxJpaEntityFixtures.pendingEntity(1L);
            QnaOutboxJpaEntity entity2 = QnaOutboxJpaEntityFixtures.pendingEntity(2L);

            given(queryDslRepository.findPendingOutboxes(beforeTime, limit))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(pendingDomain());
            given(mapper.toDomain(entity2)).willReturn(pendingDomain());

            // when
            queryAdapter.findPendingOutboxes(beforeTime, limit);

            // then
            then(mapper).should().toDomain(entity1);
            then(mapper).should().toDomain(entity2);
        }

        @Test
        @DisplayName("Repository에 올바른 파라미터를 전달합니다")
        void findPendingOutboxes_PassesCorrectParamsToRepository() {
            // given
            Instant beforeTime = Instant.now().minusSeconds(300);
            int limit = 5;

            given(queryDslRepository.findPendingOutboxes(beforeTime, limit))
                    .willReturn(List.of());

            // when
            queryAdapter.findPendingOutboxes(beforeTime, limit);

            // then
            then(queryDslRepository).should().findPendingOutboxes(beforeTime, limit);
        }
    }

    // ========================================================================
    // 3. findProcessingTimeoutOutboxes 테스트
    // ========================================================================

    @Nested
    @DisplayName("findProcessingTimeoutOutboxes 메서드 테스트")
    class FindProcessingTimeoutOutboxesTest {

        @Test
        @DisplayName("타임아웃된 PROCESSING Outbox 목록을 반환합니다")
        void findProcessingTimeoutOutboxes_WithTimeoutBefore_ReturnsTimeoutList() {
            // given
            Instant timeoutBefore = Instant.now().minusSeconds(300);
            int limit = 10;
            QnaOutboxJpaEntity entity = QnaOutboxJpaEntityFixtures.processingEntity();
            QnaOutbox domain = processingDomain(QnaOutboxJpaEntityFixtures.DEFAULT_ID);

            given(queryDslRepository.findProcessingTimeoutOutboxes(timeoutBefore, limit))
                    .willReturn(List.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            List<QnaOutbox> result =
                    queryAdapter.findProcessingTimeoutOutboxes(timeoutBefore, limit);

            // then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("타임아웃 데이터가 없을 때 빈 목록을 반환합니다")
        void findProcessingTimeoutOutboxes_WhenNoTimeout_ReturnsEmptyList() {
            // given
            Instant timeoutBefore = Instant.now().minusSeconds(300);
            int limit = 10;

            given(queryDslRepository.findProcessingTimeoutOutboxes(timeoutBefore, limit))
                    .willReturn(List.of());

            // when
            List<QnaOutbox> result =
                    queryAdapter.findProcessingTimeoutOutboxes(timeoutBefore, limit);

            // then
            assertThat(result).isEmpty();
            then(mapper).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("Repository에 올바른 파라미터를 전달합니다")
        void findProcessingTimeoutOutboxes_PassesCorrectParamsToRepository() {
            // given
            Instant timeoutBefore = Instant.now().minusSeconds(600);
            int limit = 3;

            given(queryDslRepository.findProcessingTimeoutOutboxes(timeoutBefore, limit))
                    .willReturn(List.of());

            // when
            queryAdapter.findProcessingTimeoutOutboxes(timeoutBefore, limit);

            // then
            then(queryDslRepository)
                    .should()
                    .findProcessingTimeoutOutboxes(timeoutBefore, limit);
        }
    }
}
