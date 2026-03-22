package com.ryuqq.marketplace.domain.qna.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("QnaStatus 단위 테스트")
class QnaStatusTest {

    @Nested
    @DisplayName("canAnswer() 테스트")
    class CanAnswerTest {

        @Test
        @DisplayName("PENDING 상태에서만 canAnswer()가 true이다")
        void onlyPendingCanAnswer() {
            assertThat(QnaStatus.PENDING.canAnswer()).isTrue();
            assertThat(QnaStatus.ANSWERED.canAnswer()).isFalse();
            assertThat(QnaStatus.CLOSED.canAnswer()).isFalse();
        }
    }

    @Nested
    @DisplayName("canClose() 테스트")
    class CanCloseTest {

        @Test
        @DisplayName("ANSWERED 상태에서만 canClose()가 true이다")
        void onlyAnsweredCanClose() {
            assertThat(QnaStatus.ANSWERED.canClose()).isTrue();
            assertThat(QnaStatus.PENDING.canClose()).isFalse();
            assertThat(QnaStatus.CLOSED.canClose()).isFalse();
        }
    }

    @Nested
    @DisplayName("isTerminal() 테스트")
    class IsTerminalTest {

        @Test
        @DisplayName("CLOSED만 isTerminal()이 true이다")
        void onlyClosedIsTerminal() {
            assertThat(QnaStatus.CLOSED.isTerminal()).isTrue();
            assertThat(QnaStatus.PENDING.isTerminal()).isFalse();
            assertThat(QnaStatus.ANSWERED.isTerminal()).isFalse();
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("3개의 상태가 정의되어 있다")
        void hasThreeStatuses() {
            assertThat(QnaStatus.values()).hasSize(3);
        }

        @Test
        @DisplayName("모든 상태 값이 존재한다")
        void allStatusesExist() {
            assertThat(QnaStatus.values())
                    .containsExactly(QnaStatus.PENDING, QnaStatus.ANSWERED, QnaStatus.CLOSED);
        }
    }
}
