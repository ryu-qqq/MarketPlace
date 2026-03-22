package com.ryuqq.marketplace.integration.container.qna;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.ryuqq.marketplace.adapter.out.persistence.qna.QnaJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.qna.entity.QnaJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.qna.repository.QnaJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.qna.repository.QnaReplyJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.qnaoutbox.entity.QnaOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.qnaoutbox.repository.QnaOutboxJpaRepository;
import com.ryuqq.marketplace.integration.container.ContainerLegacyE2ETestBase;
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
 * 레거시 QnA Command Testcontainers E2E 테스트.
 *
 * <p>MySQL 실제 컨테이너 기반으로 레거시 QnA 커맨드 API를 검증합니다. H2 기반 LegacyQnACommandE2ETest의 Testcontainers 전환
 * 버전입니다.
 *
 * <p>테스트 대상:
 *
 * <ul>
 *   <li>LC1~LC5: POST /api/v1/legacy/qna/reply - QnA 답변 등록
 *   <li>LC6~LC9: PUT /api/v1/legacy/qna/reply - QnA 답변 수정
 * </ul>
 */
@Tag("e2e")
@Tag("container")
@Tag("legacy")
@Tag("qna")
@Tag("command")
@DisplayName("레거시 QnA Command Container E2E 테스트")
class LegacyQnAContainerCommandE2ETest extends ContainerLegacyE2ETestBase {

    private static final String QNA_REPLY = "/api/v1/legacy/qna/reply";

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
    @DisplayName("LC1: POST /api/v1/legacy/qna/reply - PENDING QnA에 답변 등록 성공")
    class AnswerQna_PendingToAnsweredTest {

        @Test
        @Tag("P0")
        @DisplayName("[LC1] PENDING QnA에 답변 등록 → 200, QnA ANSWERED 전이, reply 생성, Outbox 생성")
        void answerQna_PendingQna_Returns200AndStatusChangedToAnswered() {
            var qna = qnaRepository.save(QnaJpaEntityFixtures.pendingEntity());

            givenLegacyAuth()
                    .body(createAnswerRequest(qna.getId(), "답변 제목", "해당 상품은 Free 사이즈입니다."))
                    .when()
                    .post(QNA_REPLY)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.qnaId", equalTo(qna.getId().intValue()))
                    .body("data.qnaAnswerId", notNullValue())
                    .body("data.qnaStatus", equalTo("CLOSED"));

            QnaJpaEntity updated = qnaRepository.findById(qna.getId()).orElseThrow();
            assertThat(updated.getStatus()).isEqualTo(QnaJpaEntity.Status.ANSWERED);
            assertThat(qnaReplyRepository.findByQnaId(qna.getId())).hasSize(1);

            List<QnaOutboxJpaEntity> outboxes = qnaOutboxRepository.findAll();
            assertThat(outboxes).hasSize(1);
            assertThat(outboxes.get(0).getOutboxType()).isEqualTo("ANSWER");
            assertThat(outboxes.get(0).getStatus()).isEqualTo(QnaOutboxJpaEntity.Status.PENDING);
        }
    }

    @Nested
    @DisplayName("LC2: POST /api/v1/legacy/qna/reply - ANSWERED 상태에서 답변 시도 → 409")
    class AnswerQna_AlreadyAnsweredTest {

        @Test
        @Tag("P0")
        @DisplayName("[LC2] ANSWERED QnA에 답변 시도 → 409")
        void answerQna_AlreadyAnswered_Returns409() {
            var qna = qnaRepository.save(QnaJpaEntityFixtures.answeredEntity());

            givenLegacyAuth()
                    .body(createAnswerRequest(qna.getId(), "재답변 제목", "재답변 시도"))
                    .when()
                    .post(QNA_REPLY)
                    .then()
                    .statusCode(HttpStatus.CONFLICT.value());
        }
    }

    @Nested
    @DisplayName("LC3: POST /api/v1/legacy/qna/reply - CLOSED 상태에서 답변 시도 → 409")
    class AnswerQna_ClosedQnaTest {

        @Test
        @Tag("P0")
        @DisplayName("[LC3] CLOSED QnA에 답변 시도 → 409")
        void answerQna_ClosedQna_Returns409() {
            var qna = qnaRepository.save(QnaJpaEntityFixtures.closedEntity(null));

            givenLegacyAuth()
                    .body(createAnswerRequest(qna.getId(), "답변 제목", "닫힌 QnA에 답변"))
                    .when()
                    .post(QNA_REPLY)
                    .then()
                    .statusCode(HttpStatus.CONFLICT.value());
        }
    }

    @Nested
    @DisplayName("LC4: POST /api/v1/legacy/qna/reply - 존재하지 않는 QnA에 답변 → 200 (레거시 NOT_FOUND)")
    class AnswerQna_NonExistentQnaTest {

        @Test
        @Tag("P0")
        @DisplayName("[LC4] 존재하지 않는 QnA ID에 답변 등록 → 200 (레거시 NOT_FOUND는 200 + body에 에러)")
        void answerQna_NonExistentQna_Returns200WithError() {
            givenLegacyAuth()
                    .body(createAnswerRequest(999999L, "답변 제목", "답변 내용"))
                    .when()
                    .post(QNA_REPLY)
                    .then()
                    .statusCode(HttpStatus.OK.value());
        }
    }

    @Nested
    @DisplayName("LC5: POST /api/v1/legacy/qna/reply - 인증 없는 요청 → 401")
    class AnswerQna_UnauthenticatedTest {

        @Test
        @Tag("P0")
        @DisplayName("[LC5] 인증 헤더 없이 답변 등록 → 401 Unauthorized")
        void answerQna_Unauthenticated_Returns401() {
            var qna = qnaRepository.save(QnaJpaEntityFixtures.pendingEntity());

            givenUnauthenticated()
                    .body(createAnswerRequest(qna.getId(), "답변 제목", "답변 내용"))
                    .when()
                    .post(QNA_REPLY)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }
    }

    @Nested
    @DisplayName("LC6: PUT /api/v1/legacy/qna/reply - 기존 답변 수정 성공")
    class UpdateReply_SuccessTest {

        @Test
        @Tag("P0")
        @DisplayName("[LC6] ANSWERED QnA 답변 수정 → 200, reply content 변경 확인")
        void updateReply_ExistingReply_Returns200AndContentUpdated() {
            var qna = qnaRepository.save(QnaJpaEntityFixtures.answeredEntity());
            var reply =
                    qnaReplyRepository.save(
                            QnaJpaEntityFixtures.sellerReplyEntity(null, qna.getId()));

            givenLegacyAuth()
                    .body(
                            createUpdateRequest(
                                    reply.getId(), qna.getId(), "수정된 답변 제목", "수정된 답변 내용입니다."))
                    .when()
                    .put(QNA_REPLY)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.qnaId", equalTo(qna.getId().intValue()))
                    .body("data.qnaAnswerId", notNullValue());

            var updatedReply =
                    qnaReplyRepository.findByQnaId(qna.getId()).stream()
                            .filter(r -> r.getId().equals(reply.getId()))
                            .findFirst()
                            .orElseThrow();
            assertThat(updatedReply.getContent()).isEqualTo("수정된 답변 내용입니다.");
        }
    }

    @Nested
    @DisplayName("LC7: PUT /api/v1/legacy/qna/reply - 존재하지 않는 QnA 답변 수정 → 200 (레거시 NOT_FOUND)")
    class UpdateReply_NonExistentQnaTest {

        @Test
        @Tag("P0")
        @DisplayName("[LC7] 존재하지 않는 QnA ID 답변 수정 → 200 (레거시 NOT_FOUND는 200 + body에 에러)")
        void updateReply_NonExistentQna_Returns200WithError() {
            givenLegacyAuth()
                    .body(createUpdateRequest(1L, 999999L, "수정 제목", "수정 내용"))
                    .when()
                    .put(QNA_REPLY)
                    .then()
                    .statusCode(HttpStatus.OK.value());
        }
    }

    @Nested
    @DisplayName("LC8: PUT /api/v1/legacy/qna/reply - 존재하지 않는 replyId로 수정 → 400")
    class UpdateReply_NonExistentReplyTest {

        @Test
        @Tag("P1")
        @DisplayName("[LC8] 존재하지 않는 replyId로 답변 수정 → 400")
        void updateReply_NonExistentReply_Returns400() {
            var qna = qnaRepository.save(QnaJpaEntityFixtures.answeredEntity());

            givenLegacyAuth()
                    .body(createUpdateRequest(999999L, qna.getId(), "수정 제목", "수정 내용"))
                    .when()
                    .put(QNA_REPLY)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    @Nested
    @DisplayName("LC9: PUT /api/v1/legacy/qna/reply - 인증 없는 요청 → 401")
    class UpdateReply_UnauthenticatedTest {

        @Test
        @Tag("P0")
        @DisplayName("[LC9] 인증 헤더 없이 답변 수정 → 401 Unauthorized")
        void updateReply_Unauthenticated_Returns401() {
            givenUnauthenticated()
                    .body(createUpdateRequest(1L, 1L, "수정 제목", "수정 내용"))
                    .when()
                    .put(QNA_REPLY)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }
    }

    // ===== Helper 메서드 =====

    private Map<String, Object> createAnswerRequest(long qnaId, String title, String content) {
        return Map.of(
                "qnaId", qnaId,
                "qnaContents",
                        Map.of(
                                "title", title,
                                "content", content),
                "qnaImages", List.of());
    }

    private Map<String, Object> createUpdateRequest(
            long qnaAnswerId, long qnaId, String title, String content) {
        return Map.of(
                "qnaAnswerId",
                qnaAnswerId,
                "qnaId",
                qnaId,
                "qnaContents",
                Map.of(
                        "title", title,
                        "content", content),
                "qnaImages",
                List.of());
    }
}
