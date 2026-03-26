package com.ryuqq.marketplace.domain.qna.outbox.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.qna.QnaOutboxFixtures;
import com.ryuqq.marketplace.domain.qna.id.QnaId;
import com.ryuqq.marketplace.domain.qna.outbox.vo.QnaOutboxStatus;
import com.ryuqq.marketplace.domain.qna.outbox.vo.QnaOutboxType;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("QnaOutbox Aggregate 단위 테스트")
class QnaOutboxTest {

    @Nested
    @DisplayName("forNew() - 신규 생성")
    class ForNewTest {

        @Test
        @DisplayName("신규 아웃박스를 생성하면 PENDING 상태로 초기화된다")
        void createNewOutboxIsPending() {
            // given
            QnaId qnaId = QnaOutboxFixtures.defaultQnaId();
            Instant now = CommonVoFixtures.now();

            // when
            QnaOutbox outbox = QnaOutbox.forNew(
                    qnaId,
                    QnaOutboxFixtures.DEFAULT_SALES_CHANNEL_ID,
                    QnaOutboxFixtures.DEFAULT_EXTERNAL_QNA_ID,
                    QnaOutboxType.ANSWER,
                    QnaOutboxFixtures.DEFAULT_PAYLOAD,
                    now);

            // then
            assertThat(outbox.status()).isEqualTo(QnaOutboxStatus.PENDING);
            assertThat(outbox.retryCount()).isEqualTo(0);
            assertThat(outbox.maxRetry()).isEqualTo(3);
            assertThat(outbox.processedAt()).isNull();
            assertThat(outbox.errorMessage()).isNull();
            assertThat(outbox.payload()).isEqualTo(QnaOutboxFixtures.DEFAULT_PAYLOAD);
            assertThat(outbox.outboxType()).isEqualTo(QnaOutboxType.ANSWER);
        }

        @Test
        @DisplayName("신규 생성 시 ID는 새 ID(value=null)이다")
        void createNewOutboxHasNewId() {
            // when
            QnaOutbox outbox = QnaOutboxFixtures.newQnaOutbox();

            // then
            assertThat(outbox.isNew()).isTrue();
            assertThat(outbox.idValue()).isNull();
        }

        @Test
        @DisplayName("신규 생성 시 멱등성 키가 QNBO: 접두사로 자동 생성된다")
        void createNewOutboxHasIdempotencyKeyWithPrefix() {
            // when
            QnaOutbox outbox = QnaOutboxFixtures.newQnaOutbox();

            // then
            assertThat(outbox.idempotencyKeyValue()).isNotBlank();
            assertThat(outbox.idempotencyKeyValue()).startsWith("QNBO:");
        }

        @Test
        @DisplayName("신규 생성 시 isPending()은 true이다")
        void newOutboxIsPending() {
            // when
            QnaOutbox outbox = QnaOutboxFixtures.newQnaOutbox();

            // then
            assertThat(outbox.isPending()).isTrue();
            assertThat(outbox.isProcessing()).isFalse();
        }

        @Test
        @DisplayName("신규 생성 시 qnaId와 salesChannelId가 올바르게 저장된다")
        void createNewOutboxHasCorrectQnaIdAndSalesChannelId() {
            // given
            QnaId qnaId = QnaOutboxFixtures.defaultQnaId();

            // when
            QnaOutbox outbox = QnaOutboxFixtures.newQnaOutbox(qnaId);

            // then
            assertThat(outbox.qnaId()).isEqualTo(qnaId);
            assertThat(outbox.qnaIdValue()).isEqualTo(QnaOutboxFixtures.DEFAULT_QNA_ID);
            assertThat(outbox.salesChannelId()).isEqualTo(QnaOutboxFixtures.DEFAULT_SALES_CHANNEL_ID);
            assertThat(outbox.externalQnaId()).isEqualTo(QnaOutboxFixtures.DEFAULT_EXTERNAL_QNA_ID);
        }
    }

    @Nested
    @DisplayName("reconstitute() - 영속성 복원")
    class ReconstituteTest {

        @Test
        @DisplayName("PENDING 상태로 복원한다")
        void reconstitutePendingOutbox() {
            // when
            QnaOutbox outbox = QnaOutboxFixtures.pendingQnaOutbox();

            // then
            assertThat(outbox.status()).isEqualTo(QnaOutboxStatus.PENDING);
            assertThat(outbox.isPending()).isTrue();
            assertThat(outbox.isNew()).isFalse();
        }

        @Test
        @DisplayName("PROCESSING 상태로 복원한다")
        void reconstituteProcessingOutbox() {
            // when
            QnaOutbox outbox = QnaOutboxFixtures.processingQnaOutbox();

            // then
            assertThat(outbox.status()).isEqualTo(QnaOutboxStatus.PROCESSING);
            assertThat(outbox.isProcessing()).isTrue();
        }

        @Test
        @DisplayName("FAILED 상태로 복원하면 status가 FAILED이다")
        void reconstituteFailedOutbox() {
            // when
            QnaOutbox outbox = QnaOutboxFixtures.failedQnaOutbox();

            // then
            assertThat(outbox.status()).isEqualTo(QnaOutboxStatus.FAILED);
            assertThat(outbox.status().isFailed()).isTrue();
            assertThat(outbox.retryCount()).isEqualTo(3);
            assertThat(outbox.errorMessage()).isEqualTo("외부 API 호출 실패");
        }

        @Test
        @DisplayName("COMPLETED 상태로 복원하면 status가 COMPLETED이다")
        void reconstituteCompletedOutbox() {
            // when
            QnaOutbox outbox = QnaOutboxFixtures.completedQnaOutbox();

            // then
            assertThat(outbox.status()).isEqualTo(QnaOutboxStatus.COMPLETED);
            assertThat(outbox.status().isCompleted()).isTrue();
        }
    }

    @Nested
    @DisplayName("startProcessing() - 처리 시작")
    class StartProcessingTest {

        @Test
        @DisplayName("PENDING 상태에서 처리를 시작하면 PROCESSING이 된다")
        void startProcessingFromPending() {
            // given
            QnaOutbox outbox = QnaOutboxFixtures.pendingQnaOutbox();
            Instant now = CommonVoFixtures.now();

            // when
            outbox.startProcessing(now);

            // then
            assertThat(outbox.status()).isEqualTo(QnaOutboxStatus.PROCESSING);
            assertThat(outbox.isProcessing()).isTrue();
            assertThat(outbox.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("PENDING이 아닌 상태에서 처리 시작하면 예외가 발생한다")
        void startProcessingFromNonPending_ThrowsException() {
            // given
            QnaOutbox outbox = QnaOutboxFixtures.processingQnaOutbox();

            // when & then
            assertThatThrownBy(() -> outbox.startProcessing(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("PENDING 상태에서만 처리를 시작할 수 있습니다");
        }

        @Test
        @DisplayName("COMPLETED 상태에서 처리 시작하면 예외가 발생한다")
        void startProcessingFromCompleted_ThrowsException() {
            // given
            QnaOutbox outbox = QnaOutboxFixtures.completedQnaOutbox();

            // when & then
            assertThatThrownBy(() -> outbox.startProcessing(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("FAILED 상태에서 처리 시작하면 예외가 발생한다")
        void startProcessingFromFailed_ThrowsException() {
            // given
            QnaOutbox outbox = QnaOutboxFixtures.failedQnaOutbox();

            // when & then
            assertThatThrownBy(() -> outbox.startProcessing(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("complete() - 처리 완료")
    class CompleteTest {

        @Test
        @DisplayName("PROCESSING 상태에서 완료 처리하면 COMPLETED가 된다")
        void completeFromProcessing() {
            // given
            QnaOutbox outbox = QnaOutboxFixtures.processingQnaOutbox();
            Instant now = CommonVoFixtures.now();

            // when
            outbox.complete(now);

            // then
            assertThat(outbox.status()).isEqualTo(QnaOutboxStatus.COMPLETED);
            assertThat(outbox.status().isCompleted()).isTrue();
            assertThat(outbox.processedAt()).isEqualTo(now);
            assertThat(outbox.errorMessage()).isNull();
        }

        @Test
        @DisplayName("PENDING 상태에서 완료 처리하면 예외가 발생한다")
        void completeFromPending_ThrowsException() {
            // given
            QnaOutbox outbox = QnaOutboxFixtures.pendingQnaOutbox();

            // when & then
            assertThatThrownBy(() -> outbox.complete(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("PROCESSING 상태에서만 완료할 수 있습니다");
        }

        @Test
        @DisplayName("FAILED 상태에서 완료 처리하면 예외가 발생한다")
        void completeFromFailed_ThrowsException() {
            // given
            QnaOutbox outbox = QnaOutboxFixtures.failedQnaOutbox();

            // when & then
            assertThatThrownBy(() -> outbox.complete(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("failAndRetry() - 실패 후 재시도")
    class FailAndRetryTest {

        @Test
        @DisplayName("재시도 횟수가 maxRetry 미만이면 PENDING으로 돌아간다")
        void failAndRetryBelowMaxRetry() {
            // given
            QnaOutbox outbox = QnaOutboxFixtures.processingQnaOutbox();
            String errorMsg = "네트워크 오류";

            // when
            outbox.failAndRetry(errorMsg, CommonVoFixtures.now());

            // then
            assertThat(outbox.status()).isEqualTo(QnaOutboxStatus.PENDING);
            assertThat(outbox.retryCount()).isEqualTo(1);
            assertThat(outbox.errorMessage()).isEqualTo(errorMsg);
        }

        @Test
        @DisplayName("재시도 횟수가 maxRetry에 도달하면 FAILED 상태가 된다")
        void failAndRetryAtMaxRetry() {
            // given
            QnaOutbox outbox = QnaOutboxFixtures.processingQnaOutboxWithRetry(2);

            // when
            outbox.failAndRetry("최종 실패", CommonVoFixtures.now());

            // then
            assertThat(outbox.status()).isEqualTo(QnaOutboxStatus.FAILED);
            assertThat(outbox.retryCount()).isEqualTo(3);
            assertThat(outbox.status().isFailed()).isTrue();
        }

        @Test
        @DisplayName("PENDING 상태에서 failAndRetry 호출하면 예외가 발생한다")
        void failAndRetryFromPending_ThrowsException() {
            // given
            QnaOutbox outbox = QnaOutboxFixtures.pendingQnaOutbox();

            // when & then
            assertThatThrownBy(() -> outbox.failAndRetry("오류", CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("PROCESSING 상태에서만 실패 처리할 수 있습니다");
        }
    }

    @Nested
    @DisplayName("fail() - 즉시 실패 처리")
    class FailTest {

        @Test
        @DisplayName("PROCESSING 상태에서 즉시 실패 처리하면 FAILED가 된다")
        void failFromProcessing() {
            // given
            QnaOutbox outbox = QnaOutboxFixtures.processingQnaOutbox();
            String errorMsg = "복구 불가 오류";
            Instant now = CommonVoFixtures.now();

            // when
            outbox.fail(errorMsg, now);

            // then
            assertThat(outbox.status()).isEqualTo(QnaOutboxStatus.FAILED);
            assertThat(outbox.status().isFailed()).isTrue();
            assertThat(outbox.errorMessage()).isEqualTo(errorMsg);
            assertThat(outbox.processedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("PENDING 상태에서 fail 호출하면 예외가 발생한다")
        void failFromPending_ThrowsException() {
            // given
            QnaOutbox outbox = QnaOutboxFixtures.pendingQnaOutbox();

            // when & then
            assertThatThrownBy(() -> outbox.fail("오류", CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("recordFailure() - 외부 API 실패 결과 반영")
    class RecordFailureTest {

        @Test
        @DisplayName("canRetry가 true이면 failAndRetry를 수행한다")
        void recordFailureWithRetry() {
            // given
            QnaOutbox outbox = QnaOutboxFixtures.processingQnaOutbox();

            // when
            outbox.recordFailure(true, "일시적 오류", CommonVoFixtures.now());

            // then
            assertThat(outbox.status()).isEqualTo(QnaOutboxStatus.PENDING);
            assertThat(outbox.retryCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("canRetry가 false이면 fail을 수행한다")
        void recordFailureWithoutRetry() {
            // given
            QnaOutbox outbox = QnaOutboxFixtures.processingQnaOutbox();

            // when
            outbox.recordFailure(false, "영구 오류", CommonVoFixtures.now());

            // then
            assertThat(outbox.status()).isEqualTo(QnaOutboxStatus.FAILED);
            assertThat(outbox.retryCount()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("recoverFromTimeout() - 타임아웃 복구")
    class RecoverFromTimeoutTest {

        @Test
        @DisplayName("PROCESSING 상태에서 타임아웃 복구하면 PENDING이 된다")
        void recoverFromTimeoutInProcessing() {
            // given
            QnaOutbox outbox = QnaOutboxFixtures.processingQnaOutbox();
            Instant now = CommonVoFixtures.now();

            // when
            outbox.recoverFromTimeout(now);

            // then
            assertThat(outbox.status()).isEqualTo(QnaOutboxStatus.PENDING);
            assertThat(outbox.errorMessage()).isEqualTo("타임아웃으로 인한 복구");
            assertThat(outbox.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("PENDING 상태에서 타임아웃 복구 호출하면 예외가 발생한다")
        void recoverFromTimeoutInPending_ThrowsException() {
            // given
            QnaOutbox outbox = QnaOutboxFixtures.pendingQnaOutbox();

            // when & then
            assertThatThrownBy(() -> outbox.recoverFromTimeout(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("타임아웃 복구는 PROCESSING 상태에서만 가능합니다");
        }

        @Test
        @DisplayName("FAILED 상태에서 타임아웃 복구 호출하면 예외가 발생한다")
        void recoverFromTimeoutInFailed_ThrowsException() {
            // given
            QnaOutbox outbox = QnaOutboxFixtures.failedQnaOutbox();

            // when & then
            assertThatThrownBy(() -> outbox.recoverFromTimeout(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("retry() - 수동 재처리")
    class RetryTest {

        @Test
        @DisplayName("FAILED 상태에서 retry하면 PENDING 상태로 복구된다")
        void retryFromFailed() {
            // given
            QnaOutbox outbox = QnaOutboxFixtures.failedQnaOutbox();
            Instant now = CommonVoFixtures.now();

            // when
            outbox.retry(now);

            // then
            assertThat(outbox.status()).isEqualTo(QnaOutboxStatus.PENDING);
            assertThat(outbox.retryCount()).isEqualTo(0);
            assertThat(outbox.errorMessage()).isNull();
            assertThat(outbox.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("PENDING 상태에서 retry 호출하면 예외가 발생한다")
        void retryFromPending_ThrowsException() {
            // given
            QnaOutbox outbox = QnaOutboxFixtures.pendingQnaOutbox();

            // when & then
            assertThatThrownBy(() -> outbox.retry(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("FAILED 상태에서만 재처리할 수 있습니다");
        }

        @Test
        @DisplayName("PROCESSING 상태에서 retry 호출하면 예외가 발생한다")
        void retryFromProcessing_ThrowsException() {
            // given
            QnaOutbox outbox = QnaOutboxFixtures.processingQnaOutbox();

            // when & then
            assertThatThrownBy(() -> outbox.retry(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("refreshVersion() - 버전 관리")
    class VersionTest {

        @Test
        @DisplayName("refreshVersion()으로 버전을 갱신한다")
        void refreshVersionUpdatesVersion() {
            // given
            QnaOutbox outbox = QnaOutboxFixtures.pendingQnaOutbox();

            // when
            outbox.refreshVersion(5L);

            // then
            assertThat(outbox.version()).isEqualTo(5L);
        }
    }

    @Nested
    @DisplayName("전체 상태 전이 흐름 테스트")
    class FullFlowTest {

        @Test
        @DisplayName("PENDING → PROCESSING → COMPLETED 정상 흐름")
        void normalFlow() {
            // given
            QnaOutbox outbox = QnaOutboxFixtures.newQnaOutbox();

            // when
            outbox.startProcessing(CommonVoFixtures.now());
            assertThat(outbox.isProcessing()).isTrue();

            outbox.complete(CommonVoFixtures.now());

            // then
            assertThat(outbox.status().isCompleted()).isTrue();
            assertThat(outbox.processedAt()).isNotNull();
        }

        @Test
        @DisplayName("PENDING → PROCESSING → FAILED → PENDING(수동 재처리) → PROCESSING → COMPLETED")
        void retryAndSuccessFlow() {
            // given
            QnaOutbox outbox = QnaOutboxFixtures.newQnaOutbox();

            // when: 최대 재시도 도달로 FAILED
            outbox.startProcessing(CommonVoFixtures.now());
            outbox.failAndRetry("1차 실패", CommonVoFixtures.now());
            outbox.startProcessing(CommonVoFixtures.now());
            outbox.failAndRetry("2차 실패", CommonVoFixtures.now());
            outbox.startProcessing(CommonVoFixtures.now());
            outbox.failAndRetry("3차 실패 - FAILED", CommonVoFixtures.now());

            assertThat(outbox.status().isFailed()).isTrue();

            // then: 수동 재처리 후 성공
            outbox.retry(CommonVoFixtures.now());
            assertThat(outbox.isPending()).isTrue();
            assertThat(outbox.retryCount()).isEqualTo(0);

            outbox.startProcessing(CommonVoFixtures.now());
            outbox.complete(CommonVoFixtures.now());
            assertThat(outbox.status().isCompleted()).isTrue();
        }

        @Test
        @DisplayName("PROCESSING → 타임아웃 복구 → PENDING → PROCESSING → COMPLETED")
        void timeoutRecoveryAndSuccessFlow() {
            // given
            QnaOutbox outbox = QnaOutboxFixtures.processingQnaOutbox();

            // when
            outbox.recoverFromTimeout(CommonVoFixtures.now());
            assertThat(outbox.isPending()).isTrue();

            outbox.startProcessing(CommonVoFixtures.now());
            outbox.complete(CommonVoFixtures.now());

            // then
            assertThat(outbox.status().isCompleted()).isTrue();
        }
    }
}
