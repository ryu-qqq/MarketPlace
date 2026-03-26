package com.ryuqq.marketplace.application.qna.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.qna.port.out.query.QnaOutboxQueryPort;
import com.ryuqq.marketplace.domain.qna.QnaOutboxFixtures;
import com.ryuqq.marketplace.domain.qna.exception.QnaException;
import com.ryuqq.marketplace.domain.qna.outbox.aggregate.QnaOutbox;
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

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("QnaOutboxReadManager 단위 테스트")
class QnaOutboxReadManagerTest {

    @InjectMocks private QnaOutboxReadManager sut;

    @Mock private QnaOutboxQueryPort queryPort;

    @Nested
    @DisplayName("getById() - ID로 QnaOutbox 단건 조회")
    class GetByIdTest {

        @Test
        @DisplayName("존재하는 ID로 QnaOutbox를 조회한다")
        void getById_ExistingId_ReturnsOutbox() {
            // given
            QnaOutbox outbox = QnaOutboxFixtures.pendingQnaOutbox();
            given(queryPort.findById(outbox.idValue())).willReturn(Optional.of(outbox));

            // when
            QnaOutbox result = sut.getById(outbox.idValue());

            // then
            assertThat(result).isEqualTo(outbox);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 QnaException이 발생한다")
        void getById_NonExistentId_ThrowsQnaException() {
            // given
            long nonExistentId = 999L;
            given(queryPort.findById(nonExistentId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(nonExistentId))
                    .isInstanceOf(QnaException.class);
        }
    }

    @Nested
    @DisplayName("findPendingOutboxes() - PENDING 상태 아웃박스 목록 조회")
    class FindPendingOutboxesTest {

        @Test
        @DisplayName("기준 시각과 limit으로 PENDING 아웃박스 목록을 조회한다")
        void findPendingOutboxes_ValidParams_ReturnsOutboxList() {
            // given
            Instant beforeTime = Instant.now();
            int limit = 10;
            QnaOutbox outbox = QnaOutboxFixtures.pendingQnaOutbox();
            given(queryPort.findPendingOutboxes(beforeTime, limit)).willReturn(List.of(outbox));

            // when
            List<QnaOutbox> result = sut.findPendingOutboxes(beforeTime, limit);

            // then
            assertThat(result).hasSize(1);
            then(queryPort).should().findPendingOutboxes(beforeTime, limit);
        }

        @Test
        @DisplayName("PENDING 아웃박스가 없으면 빈 목록을 반환한다")
        void findPendingOutboxes_NoResults_ReturnsEmptyList() {
            // given
            Instant beforeTime = Instant.now();
            int limit = 10;
            given(queryPort.findPendingOutboxes(beforeTime, limit)).willReturn(List.of());

            // when
            List<QnaOutbox> result = sut.findPendingOutboxes(beforeTime, limit);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findProcessingTimeoutOutboxes() - PROCESSING 타임아웃 아웃박스 목록 조회")
    class FindProcessingTimeoutOutboxesTest {

        @Test
        @DisplayName("타임아웃 기준 시각과 limit으로 PROCESSING 타임아웃 아웃박스 목록을 조회한다")
        void findProcessingTimeoutOutboxes_ValidParams_ReturnsOutboxList() {
            // given
            Instant timeoutBefore = Instant.now().minusSeconds(300);
            int limit = 50;
            QnaOutbox outbox = QnaOutboxFixtures.processingQnaOutbox();
            given(queryPort.findProcessingTimeoutOutboxes(timeoutBefore, limit))
                    .willReturn(List.of(outbox));

            // when
            List<QnaOutbox> result = sut.findProcessingTimeoutOutboxes(timeoutBefore, limit);

            // then
            assertThat(result).hasSize(1);
            then(queryPort).should().findProcessingTimeoutOutboxes(timeoutBefore, limit);
        }

        @Test
        @DisplayName("타임아웃 아웃박스가 없으면 빈 목록을 반환한다")
        void findProcessingTimeoutOutboxes_NoResults_ReturnsEmptyList() {
            // given
            Instant timeoutBefore = Instant.now().minusSeconds(300);
            int limit = 50;
            given(queryPort.findProcessingTimeoutOutboxes(timeoutBefore, limit))
                    .willReturn(List.of());

            // when
            List<QnaOutbox> result = sut.findProcessingTimeoutOutboxes(timeoutBefore, limit);

            // then
            assertThat(result).isEmpty();
        }
    }
}
