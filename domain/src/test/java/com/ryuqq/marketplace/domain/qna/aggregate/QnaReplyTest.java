package com.ryuqq.marketplace.domain.qna.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.qna.id.QnaReplyId;
import com.ryuqq.marketplace.domain.qna.vo.QnaReplyType;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("QnaReply 단위 테스트")
class QnaReplyTest {

    @Nested
    @DisplayName("forNew() - 신규 생성")
    class ForNewTest {

        @Test
        @DisplayName("최상위 답변 생성 시 parentReplyId가 null")
        void shouldCreateTopLevelReply() {
            Instant now = CommonVoFixtures.now();
            QnaReply reply = QnaReply.forNew(null, "답변 내용", "판매자A", QnaReplyType.SELLER_ANSWER, now);

            assertThat(reply.isTopLevel()).isTrue();
            assertThat(reply.parentReplyId()).isNull();
            assertThat(reply.isSellerAnswer()).isTrue();
            assertThat(reply.id().isNew()).isTrue();
        }

        @Test
        @DisplayName("대댓글 생성 시 parentReplyId 설정")
        void shouldCreateNestedReply() {
            Instant now = CommonVoFixtures.now();
            QnaReply reply = QnaReply.forNew(1L, "추가 질문", "구매자A", QnaReplyType.BUYER_FOLLOW_UP, now);

            assertThat(reply.isTopLevel()).isFalse();
            assertThat(reply.parentReplyId()).isEqualTo(1L);
            assertThat(reply.isSellerAnswer()).isFalse();
        }
    }

    @Nested
    @DisplayName("reconstitute() - 영속성 복원")
    class ReconstituteTest {

        @Test
        @DisplayName("영속성에서 복원 시 모든 필드가 올바르게 설정된다")
        void shouldReconstituteWithAllFields() {
            Instant now = CommonVoFixtures.now();
            QnaReplyId id = QnaReplyId.of(10L);

            QnaReply reply = QnaReply.reconstitute(
                    id, 5L, "복원된 답변", "판매자B", QnaReplyType.SELLER_ANSWER, now);

            assertThat(reply.idValue()).isEqualTo(10L);
            assertThat(reply.parentReplyId()).isEqualTo(5L);
            assertThat(reply.content()).isEqualTo("복원된 답변");
            assertThat(reply.authorName()).isEqualTo("판매자B");
            assertThat(reply.replyType()).isEqualTo(QnaReplyType.SELLER_ANSWER);
            assertThat(reply.createdAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("복원된 reply의 isNew()는 false이다")
        void reconstitutedReplyIsNotNew() {
            QnaReply reply = QnaReply.reconstitute(
                    QnaReplyId.of(1L), null, "내용", "작성자",
                    QnaReplyType.SELLER_ANSWER, CommonVoFixtures.now());

            assertThat(reply.id().isNew()).isFalse();
        }
    }

    @Nested
    @DisplayName("updateContent() - 답변 내용 수정")
    class UpdateContentTest {

        @Test
        @DisplayName("판매자 답변의 내용을 수정한다")
        void shouldUpdateSellerAnswerContent() {
            QnaReply reply = QnaReply.reconstitute(
                    QnaReplyId.of(1L), null, "원본 답변", "판매자A",
                    QnaReplyType.SELLER_ANSWER, CommonVoFixtures.now());

            reply.updateContent("수정된 답변");

            assertThat(reply.content()).isEqualTo("수정된 답변");
        }

        @Test
        @DisplayName("구매자 추가 질문은 수정할 수 없다")
        void shouldThrowWhenUpdatingBuyerFollowUp() {
            QnaReply reply = QnaReply.reconstitute(
                    QnaReplyId.of(1L), null, "원본 질문", "구매자A",
                    QnaReplyType.BUYER_FOLLOW_UP, CommonVoFixtures.now());

            assertThatThrownBy(() -> reply.updateContent("수정 시도"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("판매자 답변만 수정할 수 있습니다");
        }
    }

    @Nested
    @DisplayName("isTopLevel() / isSellerAnswer() 테스트")
    class HelperMethodsTest {

        @Test
        @DisplayName("parentReplyId가 null이면 최상위 답변이다")
        void topLevelWhenParentIsNull() {
            QnaReply reply = QnaReply.forNew(null, "내용", "작성자", QnaReplyType.SELLER_ANSWER, CommonVoFixtures.now());
            assertThat(reply.isTopLevel()).isTrue();
        }

        @Test
        @DisplayName("parentReplyId가 있으면 최상위가 아니다")
        void notTopLevelWhenParentExists() {
            QnaReply reply = QnaReply.forNew(1L, "내용", "작성자", QnaReplyType.BUYER_FOLLOW_UP, CommonVoFixtures.now());
            assertThat(reply.isTopLevel()).isFalse();
        }

        @Test
        @DisplayName("SELLER_ANSWER 타입이면 isSellerAnswer()가 true이다")
        void sellerAnswerIsTrue() {
            QnaReply reply = QnaReply.forNew(null, "내용", "판매자", QnaReplyType.SELLER_ANSWER, CommonVoFixtures.now());
            assertThat(reply.isSellerAnswer()).isTrue();
        }

        @Test
        @DisplayName("BUYER_FOLLOW_UP 타입이면 isSellerAnswer()가 false이다")
        void buyerFollowUpIsNotSellerAnswer() {
            QnaReply reply = QnaReply.forNew(null, "내용", "구매자", QnaReplyType.BUYER_FOLLOW_UP, CommonVoFixtures.now());
            assertThat(reply.isSellerAnswer()).isFalse();
        }
    }
}
