package com.ryuqq.marketplace.domain.qna.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.qna.QnaFixtures;
import com.ryuqq.marketplace.domain.qna.event.QnaAnsweredEvent;
import com.ryuqq.marketplace.domain.qna.id.QnaId;
import com.ryuqq.marketplace.domain.qna.vo.QnaReplyType;
import com.ryuqq.marketplace.domain.qna.vo.QnaSource;
import com.ryuqq.marketplace.domain.qna.vo.QnaStatus;
import com.ryuqq.marketplace.domain.qna.vo.QnaType;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("Qna Aggregate 단위 테스트")
class QnaTest {

    @Nested
    @DisplayName("forNew() - 신규 Qna 생성")
    class ForNewTest {

        @Test
        @DisplayName("신규 Qna 생성 시 PENDING 상태로 초기화된다")
        void createNewQnaWithPendingStatus() {
            // given
            Instant now = CommonVoFixtures.now();

            // when
            Qna qna = Qna.forNew(
                    QnaFixtures.DEFAULT_SELLER_ID,
                    QnaFixtures.DEFAULT_PRODUCT_GROUP_ID,
                    QnaFixtures.DEFAULT_ORDER_ID,
                    QnaFixtures.DEFAULT_QNA_TYPE,
                    QnaFixtures.defaultSource(),
                    QnaFixtures.DEFAULT_QUESTION_TITLE,
                    QnaFixtures.DEFAULT_QUESTION_CONTENT,
                    QnaFixtures.DEFAULT_QUESTION_AUTHOR,
                    now);

            // then
            assertThat(qna.status()).isEqualTo(QnaStatus.PENDING);
            assertThat(qna.id().isNew()).isTrue();
            assertThat(qna.replies()).isEmpty();
            assertThat(qna.sellerId()).isEqualTo(QnaFixtures.DEFAULT_SELLER_ID);
            assertThat(qna.productGroupId()).isEqualTo(QnaFixtures.DEFAULT_PRODUCT_GROUP_ID);
            assertThat(qna.qnaType()).isEqualTo(QnaFixtures.DEFAULT_QNA_TYPE);
            assertThat(qna.createdAt()).isEqualTo(now);
            assertThat(qna.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("주문 연관 Qna 생성 시 orderId가 설정된다")
        void createOrderQnaWithOrderId() {
            // given
            Long orderId = 999L;
            Instant now = CommonVoFixtures.now();

            // when
            Qna qna = Qna.forNew(
                    QnaFixtures.DEFAULT_SELLER_ID,
                    QnaFixtures.DEFAULT_PRODUCT_GROUP_ID,
                    orderId,
                    QnaType.ORDER,
                    QnaFixtures.defaultSource(),
                    "주문 문의",
                    "주문 관련 내용",
                    QnaFixtures.DEFAULT_QUESTION_AUTHOR,
                    now);

            // then
            assertThat(qna.orderId()).isEqualTo(orderId);
            assertThat(qna.qnaType()).isEqualTo(QnaType.ORDER);
        }

        @Test
        @DisplayName("orderId가 null이어도 생성 가능하다")
        void createQnaWithNullOrderId() {
            // when
            Qna qna = QnaFixtures.newQna();

            // then
            assertThat(qna.orderId()).isNull();
        }
    }

    @Nested
    @DisplayName("reconstitute() - 영속성에서 복원")
    class ReconstituteTest {

        @Test
        @DisplayName("PENDING 상태의 Qna를 복원한다")
        void reconstitutePendingQna() {
            // given
            QnaId id = QnaId.of(1L);

            // when
            Qna qna = QnaFixtures.pendingQna(id.value());

            // then
            assertThat(qna.id()).isEqualTo(id);
            assertThat(qna.id().isNew()).isFalse();
            assertThat(qna.status()).isEqualTo(QnaStatus.PENDING);
            assertThat(qna.replies()).isEmpty();
        }

        @Test
        @DisplayName("ANSWERED 상태의 Qna를 복원하면 replies가 포함된다")
        void reconstituteAnsweredQna() {
            // when
            Qna qna = QnaFixtures.answeredQna();

            // then
            assertThat(qna.status()).isEqualTo(QnaStatus.ANSWERED);
            assertThat(qna.replies()).hasSize(1);
        }

        @Test
        @DisplayName("CLOSED 상태의 Qna를 복원한다")
        void reconstituteClosedQna() {
            // when
            Qna qna = QnaFixtures.closedQna();

            // then
            assertThat(qna.status()).isEqualTo(QnaStatus.CLOSED);
        }

        @Test
        @DisplayName("idValue()는 ID의 값을 반환한다")
        void idValueReturnsCorrectValue() {
            // when
            Qna qna = QnaFixtures.pendingQna(42L);

            // then
            assertThat(qna.idValue()).isEqualTo(42L);
        }
    }

    @Nested
    @DisplayName("answer() - 판매자 답변 등록")
    class AnswerTest {

        @Test
        @DisplayName("PENDING 상태에서 답변 등록 시 ANSWERED로 전이한다")
        void answerFromPendingTransitionsToAnswered() {
            // given
            Qna qna = QnaFixtures.pendingQna();
            Instant now = CommonVoFixtures.now();

            // when
            QnaReply reply = qna.answer("Free 사이즈입니다.", "판매자A", null, now);

            // then
            assertThat(qna.status()).isEqualTo(QnaStatus.ANSWERED);
            assertThat(qna.replies()).hasSize(1);
            assertThat(reply.content()).isEqualTo("Free 사이즈입니다.");
            assertThat(reply.replyType()).isEqualTo(QnaReplyType.SELLER_ANSWER);
            assertThat(reply.isTopLevel()).isTrue();
            assertThat(qna.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("답변 등록 시 QnaAnsweredEvent가 발행된다")
        void answerPublishesQnaAnsweredEvent() {
            // given
            Qna qna = QnaFixtures.pendingQna();
            Instant now = CommonVoFixtures.now();

            // when
            qna.answer("답변 내용", "판매자A", null, now);

            // then
            List<DomainEvent> events = qna.pollEvents();
            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(QnaAnsweredEvent.class);

            QnaAnsweredEvent event = (QnaAnsweredEvent) events.get(0);
            assertThat(event.qnaId()).isEqualTo(qna.id());
            assertThat(event.sellerId()).isEqualTo(qna.sellerId());
            assertThat(event.occurredAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("parentReplyId를 지정하면 대댓글로 등록된다")
        void answerWithParentReplyId() {
            // given
            Qna qna = QnaFixtures.pendingQna();

            // when
            QnaReply reply = qna.answer("대댓글 답변", "판매자A", 1L, CommonVoFixtures.now());

            // then
            assertThat(reply.parentReplyId()).isEqualTo(1L);
            assertThat(reply.isTopLevel()).isFalse();
        }

        @Test
        @DisplayName("ANSWERED 상태에서 답변 시 예외가 발생한다")
        void answerFromAnsweredThrowsException() {
            // given
            Qna qna = QnaFixtures.answeredQna();

            // when & then
            assertThatThrownBy(() -> qna.answer("중복 답변", "판매자A", null, CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("CLOSED 상태에서 답변 시 예외가 발생한다")
        void answerFromClosedThrowsException() {
            // given
            Qna qna = QnaFixtures.closedQna();

            // when & then
            assertThatThrownBy(() -> qna.answer("답변", "판매자A", null, CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("addFollowUp() - 구매자 추가 질문 등록")
    class AddFollowUpTest {

        @Test
        @DisplayName("ANSWERED 상태에서 추가 질문 시 PENDING으로 전환된다")
        void addFollowUpFromAnsweredTransitionsToPending() {
            // given
            Qna qna = QnaFixtures.answeredQna();
            Instant now = CommonVoFixtures.now();

            // when
            QnaReply followUp = qna.addFollowUp("90kg도 입을 수 있나요?", "구매자A", null, now);

            // then
            assertThat(qna.status()).isEqualTo(QnaStatus.PENDING);
            assertThat(followUp.replyType()).isEqualTo(QnaReplyType.BUYER_FOLLOW_UP);
            assertThat(qna.replies()).hasSize(2);
            assertThat(qna.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("PENDING 상태에서도 추가 질문이 가능하다")
        void addFollowUpFromPendingIsPossible() {
            // given
            Qna qna = QnaFixtures.pendingQna();

            // when
            QnaReply followUp = qna.addFollowUp("추가 질문", "구매자A", null, CommonVoFixtures.now());

            // then
            assertThat(qna.status()).isEqualTo(QnaStatus.PENDING);
            assertThat(followUp).isNotNull();
        }

        @Test
        @DisplayName("CLOSED 상태에서 추가 질문 시 예외가 발생한다")
        void addFollowUpFromClosedThrowsException() {
            // given
            Qna qna = QnaFixtures.closedQna();

            // when & then
            assertThatThrownBy(() -> qna.addFollowUp("추가 질문", "구매자A", null, CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("close() - QnA 종결")
    class CloseTest {

        @Test
        @DisplayName("ANSWERED 상태에서 종결 성공한다")
        void closeFromAnsweredSucceeds() {
            // given
            Qna qna = QnaFixtures.answeredQna();
            Instant now = CommonVoFixtures.now();

            // when
            qna.close(now);

            // then
            assertThat(qna.status()).isEqualTo(QnaStatus.CLOSED);
            assertThat(qna.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("PENDING 상태에서 종결 시 예외가 발생한다")
        void closeFromPendingThrowsException() {
            // given
            Qna qna = QnaFixtures.pendingQna();

            // when & then
            assertThatThrownBy(() -> qna.close(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("CLOSED 상태에서 종결 시 예외가 발생한다")
        void closeFromClosedThrowsException() {
            // given
            Qna qna = QnaFixtures.closedQna();

            // when & then
            assertThatThrownBy(() -> qna.close(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("updateReply() - 답변 내용 수정")
    class UpdateReplyTest {

        @Test
        @DisplayName("존재하는 replyId로 답변 내용을 수정한다")
        void updateExistingReply() {
            // given
            Qna qna = QnaFixtures.answeredQna();
            long replyId = qna.replies().get(0).idValue();
            Instant now = CommonVoFixtures.now();

            // when
            QnaReply updated = qna.updateReply(replyId, "수정된 답변", now);

            // then
            assertThat(updated.content()).isEqualTo("수정된 답변");
            assertThat(qna.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("존재하지 않는 replyId로 수정 시 예외가 발생한다")
        void updateNonExistentReplyThrowsException() {
            // given
            Qna qna = QnaFixtures.answeredQna();

            // when & then
            assertThatThrownBy(() -> qna.updateReply(99999L, "수정 시도", CommonVoFixtures.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("답변을 찾을 수 없습니다");
        }
    }

    @Nested
    @DisplayName("pollEvents() - 도메인 이벤트 수집")
    class PollEventsTest {

        @Test
        @DisplayName("pollEvents 호출 후 이벤트 리스트가 비워진다")
        void pollEventsClearsEvents() {
            // given
            Qna qna = QnaFixtures.pendingQna();
            qna.answer("답변", "판매자A", null, CommonVoFixtures.now());

            // when
            List<DomainEvent> firstPoll = qna.pollEvents();
            List<DomainEvent> secondPoll = qna.pollEvents();

            // then
            assertThat(firstPoll).hasSize(1);
            assertThat(secondPoll).isEmpty();
        }

        @Test
        @DisplayName("답변 미등록 시 이벤트 리스트가 비어있다")
        void noEventsWhenNoAnswerRegistered() {
            // given
            Qna qna = QnaFixtures.pendingQna();

            // when
            List<DomainEvent> events = qna.pollEvents();

            // then
            assertThat(events).isEmpty();
        }
    }

    @Nested
    @DisplayName("source() - 출처 정보")
    class SourceTest {

        @Test
        @DisplayName("QnaSource 정보를 올바르게 반환한다")
        void sourceReturnsCorrectValues() {
            // given
            QnaSource expectedSource = QnaFixtures.defaultSource();
            Qna qna = QnaFixtures.pendingQna();

            // then
            assertThat(qna.source()).isEqualTo(expectedSource);
            assertThat(qna.source().salesChannelId()).isEqualTo(QnaFixtures.DEFAULT_SALES_CHANNEL_ID);
            assertThat(qna.source().externalQnaId()).isEqualTo(QnaFixtures.DEFAULT_EXTERNAL_QNA_ID);
        }
    }
}
