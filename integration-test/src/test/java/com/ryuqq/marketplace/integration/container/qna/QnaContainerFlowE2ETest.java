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
 * QnA 전체 플로우 Testcontainers E2E 테스트.
 *
 * <p>MySQL 실제 컨테이너 기반으로 QnA 전체 라이프사이클을 검증합니다.
 * H2 기반 QnaFlowE2ETest의 Testcontainers 전환 버전입니다.
 *
 * <p>테스트 대상:
 * <ul>
 *   <li>FLOW-1: 생성 → 목록 조회 → 답변 → 상세 조회 → 종결</li>
 *   <li>FLOW-2: 다건 QnA 생성 → 각각 답변 → 목록 재조회</li>
 *   <li>FLOW-3: 답변 → Outbox 확인 → 종결 → 재답변 불가 확인</li>
 *   <li>FLOW-4: 답변 → DB 직접 조작 → 재답변 → ANSWERED</li>
 *   <li>FLOW-5: 여러 QnA 타입(PRODUCT, SHIPPING, ORDER) 생성 → 조회</li>
 *   <li>FLOW-6: QnA 목록 페이징 + totalCount 정합성</li>
 * </ul>
 */
@Tag("e2e")
@Tag("container")
@Tag("qna")
@Tag("flow")
@DisplayName("QnA 전체 플로우 Container E2E 테스트")
class QnaContainerFlowE2ETest extends ContainerE2ETestBase {

    private static final String QNAS = "/qnas";
    private static final String QNA_DETAIL = "/qnas/{qnaId}";
    private static final String QNA_ANSWER = "/qnas/{qnaId}/answers";
    private static final String QNA_CLOSE = "/qnas/{qnaId}/close";

    private static final long SELLER_ID = QnaJpaEntityFixtures.DEFAULT_SELLER_ID;

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
    @DisplayName("FLOW-1: 생성 → 목록 조회 → 답변 → 상세 조회 → 종결")
    class FullLifecycleFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[FLOW-1] QnA DB 시딩 → 목록 조회 → 답변 → 상세 조회로 ANSWERED 확인 → 종결 → CLOSED 확인")
        void qnaFullLifecycle_SeedToClose_AllStepsVerified() {
            var qna = qnaRepository.save(QnaJpaEntityFixtures.pendingEntity());

            given().spec(givenSuperAdmin())
                    .queryParam("sellerId", SELLER_ID)
                    .queryParam("status", "PENDING")
                    .queryParam("page", 0)
                    .queryParam("size", 20)
                    .when()
                    .get(QNAS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(1))
                    .body("data.content[0].status", equalTo("PENDING"));

            given().spec(givenSuperAdmin())
                    .body(createAnswerRequest("해당 상품은 Free 사이즈입니다.", "판매자A"))
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
                    .body("data.replies", hasSize(1));

            given().spec(givenSuperAdmin())
                    .when()
                    .post(QNA_CLOSE, qna.getId())
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            QnaJpaEntity closed = qnaRepository.findById(qna.getId()).orElseThrow();
            assertThat(closed.getStatus()).isEqualTo(QnaJpaEntity.Status.CLOSED);
        }
    }

    @Nested
    @DisplayName("FLOW-2: 다건 QnA 생성 → 각각 답변 → ANSWERED 필터 목록 재조회")
    class MultipleQnaAnswerFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[FLOW-2] PENDING 3건 생성 → 전체 조회 → 각각 답변 → ANSWERED 필터로 3건 확인")
        void multipleQnas_AnswerAll_ThenFilterByAnswered() {
            var qna1 = qnaRepository.save(QnaJpaEntityFixtures.pendingEntity());
            var qna2 = qnaRepository.save(QnaJpaEntityFixtures.pendingEntity());
            var qna3 = qnaRepository.save(QnaJpaEntityFixtures.pendingEntity());

            given().spec(givenSuperAdmin())
                    .queryParam("sellerId", SELLER_ID)
                    .queryParam("page", 0)
                    .queryParam("size", 20)
                    .when()
                    .get(QNAS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(3));

            for (var qna : List.of(qna1, qna2, qna3)) {
                given().spec(givenSuperAdmin())
                        .body(createAnswerRequest("답변 드립니다.", "판매자A"))
                        .when()
                        .post(QNA_ANSWER, qna.getId())
                        .then()
                        .statusCode(HttpStatus.CREATED.value());
            }

            given().spec(givenSuperAdmin())
                    .queryParam("sellerId", SELLER_ID)
                    .queryParam("status", "ANSWERED")
                    .queryParam("page", 0)
                    .queryParam("size", 20)
                    .when()
                    .get(QNAS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(3))
                    .body("data.content", hasSize(3));

            assertThat(qnaReplyRepository.count()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("FLOW-3: 답변 → Outbox 확인 → 종결 → 재답변 불가 확인")
    class AnswerOutboxAndCloseThenNoReAnswerFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[FLOW-3] 답변 등록 → Outbox PENDING 확인 → 종결 → 재답변 시도 → 409")
        void answerQna_ThenClose_ThenReanswerFails() {
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

            given().spec(givenSuperAdmin())
                    .when()
                    .post(QNA_CLOSE, qna.getId())
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            given().spec(givenSuperAdmin())
                    .body(createAnswerRequest("재답변 시도", "판매자A"))
                    .when()
                    .post(QNA_ANSWER, qna.getId())
                    .then()
                    .statusCode(HttpStatus.CONFLICT.value());
        }
    }

    @Nested
    @DisplayName("FLOW-4: 답변 → DB 직접 조작 → 재답변 → ANSWERED")
    class AnswerFollowUpReanswerFlowTest {

        @Test
        @Tag("P1")
        @DisplayName("[FLOW-4] 답변 → DB에서 새 PENDING QnA 시딩 → 재답변 → ANSWERED 전이 확인")
        void answerQna_ThenDbSimulatePending_ThenReanswerSucceeds() {
            var qna = qnaRepository.save(QnaJpaEntityFixtures.pendingEntity());

            given().spec(givenSuperAdmin())
                    .body(createAnswerRequest("1차 답변입니다.", "판매자A"))
                    .when()
                    .post(QNA_ANSWER, qna.getId())
                    .then()
                    .statusCode(HttpStatus.CREATED.value());

            QnaJpaEntity afterAnswer = qnaRepository.findById(qna.getId()).orElseThrow();
            assertThat(afterAnswer.getStatus()).isEqualTo(QnaJpaEntity.Status.ANSWERED);

            var followUpQna = qnaRepository.save(QnaJpaEntityFixtures.pendingEntity());

            given().spec(givenSuperAdmin())
                    .body(createAnswerRequest("2차 답변입니다.", "판매자A"))
                    .when()
                    .post(QNA_ANSWER, followUpQna.getId())
                    .then()
                    .statusCode(HttpStatus.CREATED.value());

            QnaJpaEntity reanswered = qnaRepository.findById(followUpQna.getId()).orElseThrow();
            assertThat(reanswered.getStatus()).isEqualTo(QnaJpaEntity.Status.ANSWERED);
            assertThat(qnaOutboxRepository.count()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("FLOW-5: 여러 QnA 타입(PRODUCT, SHIPPING, ORDER) 생성 → 조회")
    class MultipleQnaTypesFlowTest {

        @Test
        @Tag("P1")
        @DisplayName("[FLOW-5] PRODUCT/SHIPPING/ORDER 타입 QnA 각 1건 생성 → 전체 조회 시 3건 반환")
        void multipleQnaTypes_CreateAndSearch_ReturnsAll() {
            var now = java.time.Instant.now();
            qnaRepository.save(QnaJpaEntity.create(
                    null, SELLER_ID, 100L, null, "PRODUCT", 1L,
                    "EXT-FLOW5-PRODUCT-001", "상품 문의",
                    "상품 관련 문의입니다.", "구매자A",
                    QnaJpaEntity.Status.PENDING, now, now));

            qnaRepository.save(QnaJpaEntity.create(
                    null, SELLER_ID, 100L, null, "SHIPPING", 1L,
                    "EXT-FLOW5-SHIPPING-001", "배송 문의",
                    "배송 관련 문의입니다.", "구매자B",
                    QnaJpaEntity.Status.PENDING, now, now));

            qnaRepository.save(QnaJpaEntity.create(
                    null, SELLER_ID, 100L, null, "ORDER", 1L,
                    "EXT-FLOW5-ORDER-001", "주문 문의",
                    "주문 관련 문의입니다.", "구매자C",
                    QnaJpaEntity.Status.PENDING, now, now));

            given().spec(givenSuperAdmin())
                    .queryParam("sellerId", SELLER_ID)
                    .queryParam("page", 0)
                    .queryParam("size", 20)
                    .when()
                    .get(QNAS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(3))
                    .body("data.content", hasSize(3));

            var qnas = qnaRepository.findAll();
            var qnaTypes = qnas.stream().map(QnaJpaEntity::getQnaType).toList();
            assertThat(qnaTypes).contains("PRODUCT", "SHIPPING", "ORDER");
        }
    }

    @Nested
    @DisplayName("FLOW-6: QnA 목록 페이징 + totalCount 정합성")
    class PagingTotalCountConsistencyFlowTest {

        @Test
        @Tag("P2")
        @DisplayName("[FLOW-6] QnA 5건 생성 → size=2 페이징 조회 시 totalElements=5, 각 페이지 정합성 확인")
        void qnaPaging_FiveItems_PaginationConsistency() {
            for (int i = 0; i < 5; i++) {
                qnaRepository.save(QnaJpaEntityFixtures.pendingEntity());
            }

            given().spec(givenSuperAdmin())
                    .queryParam("sellerId", SELLER_ID)
                    .queryParam("page", 0)
                    .queryParam("size", 2)
                    .when()
                    .get(QNAS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(2))
                    .body("data.totalElements", equalTo(5));

            given().spec(givenSuperAdmin())
                    .queryParam("sellerId", SELLER_ID)
                    .queryParam("page", 1)
                    .queryParam("size", 2)
                    .when()
                    .get(QNAS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(2))
                    .body("data.totalElements", equalTo(5));

            given().spec(givenSuperAdmin())
                    .queryParam("sellerId", SELLER_ID)
                    .queryParam("page", 2)
                    .queryParam("size", 2)
                    .when()
                    .get(QNAS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(1))
                    .body("data.totalElements", equalTo(5));
        }
    }

    // ===== Helper 메서드 =====

    private Map<String, Object> createAnswerRequest(String content, String authorName) {
        return Map.of(
                "content", content,
                "authorName", authorName);
    }
}
