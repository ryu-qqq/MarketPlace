package com.ryuqq.marketplace.adapter.out.persistence.qnaoutbox.adapter;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.qnaoutbox.QnaOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.qnaoutbox.entity.QnaOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.qnaoutbox.mapper.QnaOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.qnaoutbox.repository.QnaOutboxJpaRepository;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.qna.id.QnaId;
import com.ryuqq.marketplace.domain.qna.outbox.aggregate.QnaOutbox;
import com.ryuqq.marketplace.domain.qna.outbox.id.QnaOutboxId;
import com.ryuqq.marketplace.domain.qna.outbox.vo.QnaOutboxStatus;
import com.ryuqq.marketplace.domain.qna.outbox.vo.QnaOutboxType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * QnaOutboxCommandAdapterTest - QnaOutbox Command Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * <p>PER-ADP-005: Domain -> Entity 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("QnaOutboxCommandAdapter 단위 테스트")
class QnaOutboxCommandAdapterTest {

    @Mock private QnaOutboxJpaRepository repository;

    @Mock private QnaOutboxJpaEntityMapper mapper;

    @InjectMocks private QnaOutboxCommandAdapter commandAdapter;

    private static QnaOutbox pendingDomain() {
        return QnaOutbox.reconstitute(
                QnaOutboxId.of(1L),
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

    private static QnaOutbox newDomain() {
        return QnaOutbox.forNew(
                QnaId.of(QnaOutboxJpaEntityFixtures.DEFAULT_QNA_ID),
                QnaOutboxJpaEntityFixtures.DEFAULT_SALES_CHANNEL_ID,
                QnaOutboxJpaEntityFixtures.DEFAULT_EXTERNAL_QNA_ID,
                QnaOutboxType.ANSWER,
                QnaOutboxJpaEntityFixtures.DEFAULT_PAYLOAD,
                CommonVoFixtures.now());
    }

    // ========================================================================
    // 1. persist 테스트
    // ========================================================================

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("Domain을 Entity로 변환 후 저장합니다")
        void persist_WithValidDomain_SavesEntity() {
            // given
            QnaOutbox domain = pendingDomain();
            QnaOutboxJpaEntity entityToSave = QnaOutboxJpaEntityFixtures.pendingEntity();
            QnaOutboxJpaEntity savedEntity = QnaOutboxJpaEntityFixtures.pendingEntity(1L);

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);

            // when
            commandAdapter.persist(domain);

            // then
            then(mapper).should().toEntity(domain);
            then(repository).should().save(entityToSave);
        }

        @Test
        @DisplayName("저장 후 version이 갱신됩니다")
        void persist_AfterSave_RefreshesVersion() {
            // given
            QnaOutbox domain = pendingDomain();
            QnaOutboxJpaEntity entityToSave = QnaOutboxJpaEntityFixtures.pendingEntity();
            QnaOutboxJpaEntity savedEntity = QnaOutboxJpaEntityFixtures.completedEntity();

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);

            long versionBefore = domain.version();

            // when
            commandAdapter.persist(domain);

            // then - 저장 후 Domain의 version이 savedEntity의 version으로 갱신됩니다
            assertThat(domain.version()).isEqualTo(savedEntity.getVersion());
            assertThat(domain.version()).isNotEqualTo(versionBefore);
        }

        @Test
        @DisplayName("신규 Domain도 저장합니다")
        void persist_WithNewDomain_Saves() {
            // given
            QnaOutbox domain = newDomain();
            QnaOutboxJpaEntity entityToSave = QnaOutboxJpaEntityFixtures.newPendingEntity();
            QnaOutboxJpaEntity savedEntity = QnaOutboxJpaEntityFixtures.pendingEntity(100L);

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);

            // when
            commandAdapter.persist(domain);

            // then
            then(repository).should().save(entityToSave);
        }

        @Test
        @DisplayName("Mapper가 정확히 한 번 호출됩니다")
        void persist_CallsMapperOnce() {
            // given
            QnaOutbox domain = pendingDomain();
            QnaOutboxJpaEntity entity = QnaOutboxJpaEntityFixtures.pendingEntity();

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
            then(mapper).should().toEntity(domain);
        }
    }
}
