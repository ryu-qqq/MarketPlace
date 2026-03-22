package com.ryuqq.marketplace.adapter.out.persistence.qnaoutbox.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.qnaoutbox.QnaOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.qnaoutbox.entity.QnaOutboxJpaEntity;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.qna.id.QnaId;
import com.ryuqq.marketplace.domain.qna.outbox.aggregate.QnaOutbox;
import com.ryuqq.marketplace.domain.qna.outbox.id.QnaOutboxId;
import com.ryuqq.marketplace.domain.qna.outbox.vo.QnaOutboxStatus;
import com.ryuqq.marketplace.domain.qna.outbox.vo.QnaOutboxType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * QnaOutboxJpaEntityMapperTest - QnaOutbox Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("QnaOutboxJpaEntityMapper 단위 테스트")
class QnaOutboxJpaEntityMapperTest {

    private QnaOutboxJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new QnaOutboxJpaEntityMapper();
    }

    private static QnaOutbox pendingOutbox() {
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

    private static QnaOutbox newOutbox() {
        return QnaOutbox.forNew(
                QnaId.of(QnaOutboxJpaEntityFixtures.DEFAULT_QNA_ID),
                QnaOutboxJpaEntityFixtures.DEFAULT_SALES_CHANNEL_ID,
                QnaOutboxJpaEntityFixtures.DEFAULT_EXTERNAL_QNA_ID,
                QnaOutboxType.ANSWER,
                QnaOutboxJpaEntityFixtures.DEFAULT_PAYLOAD,
                CommonVoFixtures.now());
    }

    private static QnaOutbox completedOutbox() {
        return QnaOutbox.reconstitute(
                QnaOutboxId.of(1L),
                QnaId.of(QnaOutboxJpaEntityFixtures.DEFAULT_QNA_ID),
                QnaOutboxJpaEntityFixtures.DEFAULT_SALES_CHANNEL_ID,
                QnaOutboxJpaEntityFixtures.DEFAULT_EXTERNAL_QNA_ID,
                QnaOutboxType.ANSWER,
                QnaOutboxStatus.COMPLETED,
                QnaOutboxJpaEntityFixtures.DEFAULT_PAYLOAD,
                0,
                3,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.now(),
                CommonVoFixtures.now(),
                null,
                1L,
                QnaOutboxJpaEntityFixtures.DEFAULT_IDEMPOTENCY_KEY);
    }

    private static QnaOutbox failedOutbox() {
        return QnaOutbox.reconstitute(
                QnaOutboxId.of(1L),
                QnaId.of(QnaOutboxJpaEntityFixtures.DEFAULT_QNA_ID),
                QnaOutboxJpaEntityFixtures.DEFAULT_SALES_CHANNEL_ID,
                QnaOutboxJpaEntityFixtures.DEFAULT_EXTERNAL_QNA_ID,
                QnaOutboxType.ANSWER,
                QnaOutboxStatus.FAILED,
                QnaOutboxJpaEntityFixtures.DEFAULT_PAYLOAD,
                3,
                3,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.now(),
                CommonVoFixtures.now(),
                "외부 API 호출 실패",
                1L,
                QnaOutboxJpaEntityFixtures.DEFAULT_IDEMPOTENCY_KEY);
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("PENDING 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithPendingDomain_ConvertsCorrectly() {
            // given
            QnaOutbox domain = pendingOutbox();

            // when
            QnaOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getQnaId()).isEqualTo(domain.qnaIdValue());
            assertThat(entity.getSalesChannelId()).isEqualTo(domain.salesChannelId());
            assertThat(entity.getExternalQnaId()).isEqualTo(domain.externalQnaId());
            assertThat(entity.getOutboxType()).isEqualTo(domain.outboxType().name());
            assertThat(entity.getStatus().name()).isEqualTo(domain.status().name());
            assertThat(entity.getPayload()).isEqualTo(domain.payload());
            assertThat(entity.getRetryCount()).isEqualTo(domain.retryCount());
            assertThat(entity.getMaxRetry()).isEqualTo(domain.maxRetry());
            assertThat(entity.getProcessedAt()).isNull();
            assertThat(entity.getErrorMessage()).isNull();
            assertThat(entity.getIdempotencyKey()).isEqualTo(domain.idempotencyKeyValue());
        }

        @Test
        @DisplayName("신규 Domain(isNew=true)은 ID를 null로 변환합니다")
        void toEntity_WithNewDomain_ConvertsIdToNull() {
            // given
            QnaOutbox domain = newOutbox();

            // when
            QnaOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getStatus()).isEqualTo(QnaOutboxJpaEntity.Status.PENDING);
        }

        @Test
        @DisplayName("COMPLETED 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithCompletedDomain_ConvertsCorrectly() {
            // given
            QnaOutbox domain = completedOutbox();

            // when
            QnaOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo(QnaOutboxJpaEntity.Status.COMPLETED);
            assertThat(entity.getProcessedAt()).isNotNull();
            assertThat(entity.getErrorMessage()).isNull();
        }

        @Test
        @DisplayName("FAILED 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithFailedDomain_ConvertsCorrectly() {
            // given
            QnaOutbox domain = failedOutbox();

            // when
            QnaOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo(QnaOutboxJpaEntity.Status.FAILED);
            assertThat(entity.getRetryCount()).isEqualTo(3);
            assertThat(entity.getErrorMessage()).isEqualTo("외부 API 호출 실패");
        }
    }

    // ========================================================================
    // 2. toDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain 메서드 테스트")
    class ToDomainTest {

        @Test
        @DisplayName("PENDING 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithPendingEntity_ConvertsCorrectly() {
            // given
            QnaOutboxJpaEntity entity = QnaOutboxJpaEntityFixtures.pendingEntity();

            // when
            QnaOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.qnaIdValue()).isEqualTo(entity.getQnaId());
            assertThat(domain.salesChannelId()).isEqualTo(entity.getSalesChannelId());
            assertThat(domain.externalQnaId()).isEqualTo(entity.getExternalQnaId());
            assertThat(domain.outboxType().name()).isEqualTo(entity.getOutboxType());
            assertThat(domain.status().name()).isEqualTo(entity.getStatus().name());
            assertThat(domain.payload()).isEqualTo(entity.getPayload());
            assertThat(domain.retryCount()).isEqualTo(entity.getRetryCount());
            assertThat(domain.maxRetry()).isEqualTo(entity.getMaxRetry());
            assertThat(domain.processedAt()).isNull();
            assertThat(domain.errorMessage()).isNull();
            assertThat(domain.idempotencyKeyValue()).isEqualTo(entity.getIdempotencyKey());
        }

        @Test
        @DisplayName("COMPLETED 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithCompletedEntity_ConvertsCorrectly() {
            // given
            QnaOutboxJpaEntity entity = QnaOutboxJpaEntityFixtures.completedEntity();

            // when
            QnaOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(QnaOutboxStatus.COMPLETED);
            assertThat(domain.processedAt()).isNotNull();
        }

        @Test
        @DisplayName("FAILED 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithFailedEntity_ConvertsCorrectly() {
            // given
            QnaOutboxJpaEntity entity = QnaOutboxJpaEntityFixtures.failedEntity();

            // when
            QnaOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(QnaOutboxStatus.FAILED);
            assertThat(domain.retryCount()).isEqualTo(entity.getRetryCount());
            assertThat(domain.errorMessage()).isEqualTo(entity.getErrorMessage());
        }
    }

    // ========================================================================
    // 3. 양방향 변환 테스트
    // ========================================================================

    @Nested
    @DisplayName("양방향 변환 테스트")
    class BidirectionalConversionTest {

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesData() {
            // given
            QnaOutboxJpaEntity original = QnaOutboxJpaEntityFixtures.pendingEntity();

            // when
            QnaOutbox domain = mapper.toDomain(original);
            QnaOutboxJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getQnaId()).isEqualTo(original.getQnaId());
            assertThat(converted.getSalesChannelId()).isEqualTo(original.getSalesChannelId());
            assertThat(converted.getExternalQnaId()).isEqualTo(original.getExternalQnaId());
            assertThat(converted.getOutboxType()).isEqualTo(original.getOutboxType());
            assertThat(converted.getStatus()).isEqualTo(original.getStatus());
            assertThat(converted.getPayload()).isEqualTo(original.getPayload());
            assertThat(converted.getRetryCount()).isEqualTo(original.getRetryCount());
            assertThat(converted.getMaxRetry()).isEqualTo(original.getMaxRetry());
            assertThat(converted.getIdempotencyKey()).isEqualTo(original.getIdempotencyKey());
        }
    }
}
