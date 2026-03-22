package com.ryuqq.marketplace.integration.container.qna;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import com.ryuqq.marketplace.adapter.out.persistence.qna.QnaJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.qna.entity.QnaJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.qna.repository.QnaJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.qna.repository.QnaReplyJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.qnaoutbox.entity.QnaOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.qnaoutbox.repository.QnaOutboxJpaRepository;
import com.ryuqq.marketplace.integration.container.ContainerE2ETestBase;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * QnA Command Testcontainers E2E 테스트.
 *
 * <p>MySQL 실제 컨테이너 기반으로 QnA 커맨드 API를 검증합니다.
 * H2 기반 QnaCommandE2ETest의 Testcontainers 전환 버전입니다.
 *
 * <p>테스트 대상:
 * <ul>
 *   <li>C1~C7: POST /qnas/{qnaId}/answers - QnA 답변 등록</li>
 *   <li>C8~C12: POST /qnas/{qnaId}/close - QnA 종결</li>
 * </ul>
 */
@Tag("e2e")
@Tag("container")
@Tag("qna")
@Tag("command")
@DisplayName("QnA Command Container E2E 테스트")
class QnaContainerCommandE2ETest extends ContainerE2ETestBase {

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

    @Nested
    @DisplayName("C1: POST /qnas/{qnaId}/answers - PENDING → 답변 등록 → ANSWERED + reply 생성")
    class AnswerQna_PendingToAnsweredTest {

        @Test
        @Tag("P0")
        @DisplayName("[C1] PENDING QnA에 답변 등록 → 201 반환, QnA ANSWERED 전이, reply 생성 확인")
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
            assertThat(outboxes.get(0).getStatus()).isEqualTo(QnaOutboxJpaEntity.Status.PENDING);
            assertThat(outboxes.get(0).getOutboxType()).isEqualTo("ANSWER");
            assertThat(outboxes.get(0).getQnaId()).isEqualTo(qna.getId());
        }
    }

    @Nested
    @DisplayName("C3: POST /qnas/{qnaId}/answers - 대댓글 (parentReplyId 지정)")
    class AnswerQna_WithParentReplyIdTest {

        @Test
        @Tag("P1")
        @DisplayName("[C3] parentReplyId 지정하여 답변 등록 → 201 반환, reply parentReplyId 설정 확인")
        void answerQna_WithParentReplyId_Returns201AndReplyHasParentReplyId() {
            var qna = qnaRepository.save(QnaJpaEntityFixtures.pendingEntity());
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
    @DisplayName("C7: POST /qnas/{qnaId}/answers - content 빈 문자열 → 400 (Validation)")
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
        @DisplayName("[C8] ANSWERED QnA 종결 → 204 No Content, 상태 CLOSED 전이 확인")
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
        @DisplayName("[C9] PENDING QnA 종결 시도 → 409")
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
    @DisplayName("C10: POST /qnas/{qnaId}/close - CLOSED → close 재시도 → 409")
    class CloseQna_AlreadyClosedTest {

        @Test
        @Tag("P0")
        @DisplayName("[C10] 이미 CLOSED QnA 재종결 시도 → 409")
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
