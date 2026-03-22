package com.ryuqq.marketplace.integration.qna;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.adapter.out.persistence.qna.QnaJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.qna.entity.QnaJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.qna.repository.QnaJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.qna.repository.QnaReplyJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.qnaoutbox.entity.QnaOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.qnaoutbox.repository.QnaOutboxJpaRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * QnA Command E2E 테스트.
 *
 * <p>테스트 대상:
 *
 * <ul>
 *   <li>C1~C7: POST /qnas/{qnaId}/answers - QnA 답변 등록
 *   <li>C8~C12: POST /qnas/{qnaId}/close - QnA 종결
 * </ul>
 */
@Tag("e2e")
@Tag("qna")
@Tag("command")
@DisplayName("QnA Command E2E 테스트")
class QnaCommandE2ETest extends E2ETestBase {

    private static final String QNA_ANSWER = "/qnas/{qnaId}/answers";
    private static final String QNA_CLOSE = "/qnas/{qnaId}/close";
    private static final String QNA_DETAIL = "/qnas/{qnaId}";

    @Autowired private QnaJpaRepository qnaRepository;
    @Autowired private QnaReplyJpaRepository qnaReplyRepository;
    @Autowired private QnaOutboxJpaRepository qnaOutboxRepository;

    @BeforeEach
    void setUp() {
        qnaReplyRepository.deleteAll();
        qnaOutboxRepository.deleteAll();
        qnaRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        qnaReplyRepository.deleteAll();
        qnaOutboxRepository.deleteAll();
        qnaRepository.deleteAll();
    }

    @Nested
    @DisplayName("C1: POST /qnas/{qnaId}/answers - PENDING → 답변 등록 → ANSWERED + reply 생성")
    class AnswerQna_PendingToAnsweredTest {

        @Test
        @Tag("P0")
        @DisplayName("[C1] PENDING 상태 QnA에 답변 등록 → 201 반환, QnA ANSWERED 전이, reply 생성 확인")
        void answerQna_PendingQna_Returns201AndStatusChangedToAnswered() {
            var qna = qnaRepository.save(QnaJpaEntityFixtures.pendingEntity());

            given().spec(givenSuperAdmin())
                    .body(createAnswerRequest("해당 상품은 Free 사이즈입니다.", "판매자A"))
                    .when()
                    .post(QNA_ANSWER, qna.getId())
                    .then()
                    .statusCode(HttpStatus.CREATED.value());

            QnaJpaEntity updated = qnaRepository.findById(qna.getId()).orElseThrow();
            assertThat(updated.getStatus()).isEqualTo(QnaJpaEntity.Status.ANSWERED);

            assertThat(qnaReplyRepository.findByQnaId(qna.getId())).hasSize(1);
        }
    }

    @Nested
    @DisplayName("C2: POST /qnas/{qnaId}/answers - 답변 등록 시 QnaOutbox 생성 확인")
    class AnswerQna_OutboxCreatedTest {

        @Test
        @Tag("P0")
        @DisplayName("[C2] 답변 등록 후 qna_outboxes에 status=PENDING, outboxType=ANSWER 레코드 생성 확인")
        void answerQna_AfterAnswer_OutboxCreatedWithPendingStatus() {
            var qna = qnaRepository.save(QnaJpaEntityFixtures.pendingEntity());

            given().spec(givenSuperAdmin())
                    .body(createAnswerRequest("답변 내용입니다.", "판매자A"))
                    .when()
                    .post(QNA_ANSWER, qna.getId())
                    .then()
                    .statusCode(HttpStatus.CREATED.value());

            List<QnaOutboxJpaEntity> outboxes = qnaOutboxRepository.findAll();
            assertThat(outboxes).hasSize(1);

            QnaOutboxJpaEntity outbox = outboxes.get(0);
            assertThat(outbox.getStatus()).isEqualTo(QnaOutboxJpaEntity.Status.PENDING);
            assertThat(outbox.getOutboxType()).isEqualTo("ANSWER");
            assertThat(outbox.getQnaId()).isEqualTo(qna.getId());
        }
    }

    @Nested
    @DisplayName("C3: POST /qnas/{qnaId}/answers - 답변 등록 시 대댓글 (parentReplyId 지정)")
    class AnswerQna_WithParentReplyIdTest {

        @Test
        @Tag("P1")
        @DisplayName("[C3] PENDING QnA에 parentReplyId 지정하여 답변 등록 → 201 반환, reply parentReplyId 설정 확인")
        void answerQna_WithParentReplyId_Returns201AndReplyHasParentReplyId() {
            // PENDING 상태의 QnA를 사용 (answer는 PENDING에서만 가능)
            var qna = qnaRepository.save(QnaJpaEntityFixtures.pendingEntity());
            // 이전 대화 맥락용 reply를 DB에 직접 시딩 (id=null로 자동 생성)
            var parentReply = qnaReplyRepository.save(
                    QnaJpaEntityFixtures.sellerReplyEntity(null, qna.getId()));

            given().spec(givenSuperAdmin())
                    .body(createAnswerRequestWithParent(
                            "추가 답변입니다.", "판매자A", parentReply.getId()))
                    .when()
                    .post(QNA_ANSWER, qna.getId())
                    .then()
                    .statusCode(HttpStatus.CREATED.value());

            var replies = qnaReplyRepository.findByQnaId(qna.getId());
            assertThat(replies).hasSizeGreaterThanOrEqualTo(2);

            var childReply = replies.stream()
                    .filter(r -> r.getParentReplyId() != null)
                    .findFirst()
                    .orElseThrow();
            assertThat(childReply.getParentReplyId()).isEqualTo(parentReply.getId());
        }
    }

    @Nested
    @DisplayName("C4: POST /qnas/{qnaId}/answers - ANSWERED 상태에서 답변 시도 → 409")
    class AnswerQna_AlreadyAnsweredTest {

        @Test
        @Tag("P0")
        @DisplayName("[C4] ANSWERED 상태 QnA에 답변 시도 → 409")
        void answerQna_AlreadyAnswered_Returns409() {
            var qna = qnaRepository.save(QnaJpaEntityFixtures.answeredEntity());

            given().spec(givenSuperAdmin())
                    .body(createAnswerRequest("재답변 시도", "판매자A"))
                    .when()
                    .post(QNA_ANSWER, qna.getId())
                    .then()
                    .statusCode(HttpStatus.CONFLICT.value());
        }
    }

    @Nested
    @DisplayName("C5: POST /qnas/{qnaId}/answers - CLOSED 상태에서 답변 시도 → 409")
    class AnswerQna_ClosedQnaTest {

        @Test
        @Tag("P0")
        @DisplayName("[C5] CLOSED 상태 QnA에 답변 시도 → 409")
        void answerQna_ClosedQna_Returns409() {
            var qna = qnaRepository.save(QnaJpaEntityFixtures.closedEntity(null));

            given().spec(givenSuperAdmin())
                    .body(createAnswerRequest("닫힌 QnA에 답변", "판매자A"))
                    .when()
                    .post(QNA_ANSWER, qna.getId())
                    .then()
                    .statusCode(HttpStatus.CONFLICT.value());
        }
    }

    @Nested
    @DisplayName("C6: POST /qnas/{qnaId}/answers - 존재하지 않는 QnA에 답변 → 404")
    class AnswerQna_NonExistentQnaTest {

        @Test
        @Tag("P0")
        @DisplayName("[C6] 존재하지 않는 QnA ID에 답변 등록 → 404")
        void answerQna_NonExistentQna_Returns404() {
            given().spec(givenSuperAdmin())
                    .body(createAnswerRequest("답변 내용", "판매자A"))
                    .when()
                    .post(QNA_ANSWER, 999999L)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }
    }

    @Nested
    @DisplayName("C7: POST /qnas/{qnaId}/answers - content 빈 문자열로 답변 → 400 (Validation)")
    class AnswerQna_BlankContentTest {

        @Test
        @Tag("P0")
        @DisplayName("[C7] content 빈 문자열로 답변 등록 → 400 Validation 오류")
        void answerQna_BlankContent_Returns400() {
            var qna = qnaRepository.save(QnaJpaEntityFixtures.pendingEntity());

            given().spec(givenSuperAdmin())
                    .body(createAnswerRequest("", "판매자A"))
                    .when()
                    .post(QNA_ANSWER, qna.getId())
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    @Nested
    @DisplayName("C8: POST /qnas/{qnaId}/close - ANSWERED → close → CLOSED")
    class CloseQna_AnsweredToClosedTest {

        @Test
        @Tag("P0")
        @DisplayName("[C8] ANSWERED 상태 QnA 종결 → 204 No Content 반환, 상태 CLOSED 전이 확인")
        void closeQna_AnsweredQna_Returns204AndStatusChangedToClosed() {
            var qna = qnaRepository.save(QnaJpaEntityFixtures.answeredEntity());

            given().spec(givenSuperAdmin())
                    .when()
                    .post(QNA_CLOSE, qna.getId())
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            QnaJpaEntity updated = qnaRepository.findById(qna.getId()).orElseThrow();
            assertThat(updated.getStatus()).isEqualTo(QnaJpaEntity.Status.CLOSED);
        }
    }

    @Nested
    @DisplayName("C9: POST /qnas/{qnaId}/close - PENDING → close 시도 → 409")
    class CloseQna_PendingQnaTest {

        @Test
        @Tag("P0")
        @DisplayName("[C9] PENDING 상태 QnA 종결 시도 → 409")
        void closeQna_PendingQna_Returns409() {
            var qna = qnaRepository.save(QnaJpaEntityFixtures.pendingEntity());

            given().spec(givenSuperAdmin())
                    .when()
                    .post(QNA_CLOSE, qna.getId())
                    .then()
                    .statusCode(HttpStatus.CONFLICT.value());
        }
    }

    @Nested
    @DisplayName("C10: POST /qnas/{qnaId}/close - CLOSED → close 시도 → 409")
    class CloseQna_AlreadyClosedTest {

        @Test
        @Tag("P0")
        @DisplayName("[C10] 이미 CLOSED 상태 QnA 재종결 시도 → 409")
        void closeQna_AlreadyClosedQna_Returns409() {
            var qna = qnaRepository.save(QnaJpaEntityFixtures.closedEntity(null));

            given().spec(givenSuperAdmin())
                    .when()
                    .post(QNA_CLOSE, qna.getId())
                    .then()
                    .statusCode(HttpStatus.CONFLICT.value());
        }
    }

    @Nested
    @DisplayName("C11: POST /qnas/{qnaId}/close - 존재하지 않는 QnA close → 404")
    class CloseQna_NonExistentQnaTest {

        @Test
        @Tag("P0")
        @DisplayName("[C11] 존재하지 않는 QnA ID 종결 시도 → 404")
        void closeQna_NonExistentQna_Returns404() {
            given().spec(givenSuperAdmin())
                    .when()
                    .post(QNA_CLOSE, 999999L)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }
    }

    @Nested
    @DisplayName("C12: POST /qnas/{qnaId}/answers - 답변 등록 후 상세 조회로 replies 확인")
    class AnswerQna_ThenVerifyRepliesInDetailTest {

        @Test
        @Tag("P0")
        @DisplayName("[C12] 답변 등록 후 상세 조회 시 replies 목록에 새 답변 포함 확인")
        void answerQna_ThenGetDetail_RepliesContainNewAnswer() {
            var qna = qnaRepository.save(QnaJpaEntityFixtures.pendingEntity());

            given().spec(givenSuperAdmin())
                    .body(createAnswerRequest("답변 내용입니다.", "판매자A"))
                    .when()
                    .post(QNA_ANSWER, qna.getId())
                    .then()
                    .statusCode(HttpStatus.CREATED.value());

            given().spec(givenSuperAdmin())
                    .when()
                    .get(QNA_DETAIL, qna.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.status", equalTo("ANSWERED"))
                    .body("data.replies", hasSize(1))
                    .body("data.replies[0].content", equalTo("답변 내용입니다."))
                    .body("data.replies[0].authorName", equalTo("판매자A"));
        }
    }

    // ===== Helper 메서드 =====

    private Map<String, Object> createAnswerRequest(String content, String authorName) {
        return Map.of(
                "content", content,
                "authorName", authorName);
    }

    private Map<String, Object> createAnswerRequestWithParent(
            String content, String authorName, Long parentReplyId) {
        return Map.of(
                "content", content,
                "authorName", authorName,
                "parentReplyId", parentReplyId);
    }
}
