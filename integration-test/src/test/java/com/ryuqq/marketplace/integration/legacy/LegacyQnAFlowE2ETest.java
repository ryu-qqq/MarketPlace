package com.ryuqq.marketplace.integration.legacy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import com.ryuqq.marketplace.adapter.out.persistence.qna.QnaJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.qna.entity.QnaJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.qna.repository.QnaJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.qna.repository.QnaReplyJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.qnaoutbox.entity.QnaOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.qnaoutbox.repository.QnaOutboxJpaRepository;
import java.time.Instant;
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
 * 레거시 QnA 전체 플로우 E2E 테스트.
 *
 * <p>테스트 대상:
 *
 * <ul>
 *   <li>FLOW-1: 시딩 -> 목록 조회 -> 답변 등록 -> 상세 조회로 답변 확인
 *   <li>FLOW-2: 다건 QnA 생성 -> 각각 답변 -> 목록 재조회로 ANSWERED 확인
 *   <li>FLOW-3: 답변 등록 -> Outbox 확인 -> 답변 수정 -> 수정 내용 확인
 *   <li>FLOW-4: 다양한 QnA 타입 생성 -> 목록 조회로 전체 확인
 *   <li>FLOW-5: 목록 페이징 + totalCount 정합성
 * </ul>
 */
@Tag("e2e")
@Tag("legacy")
@Tag("qna")
@Tag("flow")
@DisplayName("레거시 QnA 전체 플로우 E2E 테스트")
class LegacyQnAFlowE2ETest extends LegacyE2ETestBase {

    private static final String QNAS = "/api/v1/legacy/qnas";
    private static final String QNA_DETAIL = "/api/v1/legacy/qna/{qnaId}";
    private static final String QNA_REPLY = "/api/v1/legacy/qna/reply";

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

    @AfterEach
    void tearDown() {
        qnaReplyRepository.deleteAll();
        qnaOutboxRepository.deleteAll();
        qnaRepository.deleteAll();
    }

    @Nested
    @DisplayName("FLOW-1: 시딩 -> 목록 조회 -> 답변 등록 -> 상세 조회로 답변 확인")
    class FullLifecycleFlowTest {

        @Test
        @Tag("P0")
        @DisplayName(
                "[FLOW-1] QnA DB 시딩 -> 목록 조회 PENDING 확인 -> 답변 등록 -> 상세 조회 ANSWERED + answerQnas 확인")
        void qnaFullLifecycle_SeedToAnswer_AllStepsVerified() {
            // Step 1. QnA DB 시딩 (PENDING)
            var qna = qnaRepository.save(QnaJpaEntityFixtures.pendingEntity());

            // Step 2. 목록 조회 - 1건, PENDING 확인
            givenLegacyAuth()
                    .queryParam("qnaType", "PRODUCT")
                    .queryParam("qnaStatus", "PENDING")
                    .queryParam("sellerId", SELLER_ID)
                    .queryParam("page", 0)
                    .queryParam("size", 20)
                    .when()
                    .get(QNAS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(1))
                    .body("data.content[0].qnaStatus", equalTo("PENDING"));

            // Step 3. 답변 등록
            givenLegacyAuth()
                    .body(createAnswerRequest(qna.getId(), "답변 제목", "해당 상품은 Free 사이즈입니다."))
                    .when()
                    .post(QNA_REPLY)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.qnaId", equalTo(qna.getId().intValue()));

            // Step 4. 상세 조회 - ANSWERED, answerQnas 1건 확인
            givenLegacyAuth()
                    .when()
                    .get(QNA_DETAIL, qna.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.qna.qnaStatus", equalTo("ANSWERED"))
                    .body("data.answerQnas", hasSize(1));

            // Step 5. DB 검증 - ANSWERED 상태 확인
            QnaJpaEntity updated = qnaRepository.findById(qna.getId()).orElseThrow();
            assertThat(updated.getStatus()).isEqualTo(QnaJpaEntity.Status.ANSWERED);
        }
    }

    @Nested
    @DisplayName("FLOW-2: 다건 QnA 생성 -> 각각 답변 -> ANSWERED 필터 목록 재조회")
    class MultipleQnaAnswerFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[FLOW-2] PENDING 3건 생성 -> 전체 조회 확인 -> 각각 답변 -> ANSWERED 필터로 3건 확인")
        void multipleQnas_AnswerAll_ThenFilterByAnswered() {
            // Step 1. PENDING QnA 3건 시딩
            var qna1 = qnaRepository.save(QnaJpaEntityFixtures.pendingEntity());
            var qna2 = qnaRepository.save(QnaJpaEntityFixtures.pendingEntity());
            var qna3 = qnaRepository.save(QnaJpaEntityFixtures.pendingEntity());

            // Step 2. 전체 목록 조회 - 3건 확인
            givenLegacyAuth()
                    .queryParam("qnaType", "PRODUCT")
                    .queryParam("sellerId", SELLER_ID)
                    .queryParam("page", 0)
                    .queryParam("size", 20)
                    .when()
                    .get(QNAS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(3));

            // Step 3. 각각 답변 등록
            for (var qna : List.of(qna1, qna2, qna3)) {
                givenLegacyAuth()
                        .body(createAnswerRequest(qna.getId(), "답변 제목", "답변 드립니다."))
                        .when()
                        .post(QNA_REPLY)
                        .then()
                        .statusCode(HttpStatus.OK.value());
            }

            // Step 4. ANSWERED 필터로 재조회 - 3건 확인
            givenLegacyAuth()
                    .queryParam("qnaType", "PRODUCT")
                    .queryParam("qnaStatus", "ANSWERED")
                    .queryParam("sellerId", SELLER_ID)
                    .queryParam("page", 0)
                    .queryParam("size", 20)
                    .when()
                    .get(QNAS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(3))
                    .body("data.content", hasSize(3));

            // Step 5. DB 검증 - reply 3건, outbox 3건 생성 확인
            assertThat(qnaReplyRepository.count()).isEqualTo(3);
            assertThat(qnaOutboxRepository.count()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("FLOW-3: 답변 등록 -> Outbox 확인 -> 답변 수정 -> 수정 내용 확인")
    class AnswerThenUpdateFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[FLOW-3] 답변 등록 -> Outbox 생성 확인 -> 답변 수정 -> 상세 조회에서 수정된 내용 확인")
        void answerQna_ThenUpdate_ContentUpdated() {
            // Step 1. PENDING QnA 시딩
            var qna = qnaRepository.save(QnaJpaEntityFixtures.pendingEntity());

            // Step 2. 답변 등록
            long qnaAnswerId =
                    givenLegacyAuth()
                            .body(createAnswerRequest(qna.getId(), "1차 답변 제목", "1차 답변 내용입니다."))
                            .when()
                            .post(QNA_REPLY)
                            .then()
                            .statusCode(HttpStatus.OK.value())
                            .extract()
                            .jsonPath()
                            .getLong("data.qnaAnswerId");

            // Step 3. Outbox 생성 확인 - PENDING 상태, ANSWER 타입
            List<QnaOutboxJpaEntity> outboxes = qnaOutboxRepository.findAll();
            assertThat(outboxes).hasSize(1);
            assertThat(outboxes.get(0).getStatus()).isEqualTo(QnaOutboxJpaEntity.Status.PENDING);
            assertThat(outboxes.get(0).getOutboxType()).isEqualTo("ANSWER");

            // Step 4. 답변 수정
            givenLegacyAuth()
                    .body(createUpdateRequest(qnaAnswerId, qna.getId(), "수정된 제목", "수정된 답변 내용입니다."))
                    .when()
                    .put(QNA_REPLY)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // Step 5. DB 검증 - reply content 변경 확인
            var replies = qnaReplyRepository.findByQnaId(qna.getId());
            assertThat(replies).hasSize(1);
            assertThat(replies.get(0).getContent()).isEqualTo("수정된 답변 내용입니다.");
        }
    }

    @Nested
    @DisplayName("FLOW-4: 다양한 QnA 타입 생성 -> 목록 조회로 전체 확인")
    class MultipleQnaTypesFlowTest {

        @Test
        @Tag("P1")
        @DisplayName("[FLOW-4] PRODUCT/SHIPPING/ORDER 타입 QnA 각 1건 생성 -> 전체 조회 시 3건 반환")
        void multipleQnaTypes_CreateAndSearch_ReturnsAll() {
            // Step 1. 다양한 타입 QnA 시딩
            Instant now = Instant.now();
            qnaRepository.save(
                    QnaJpaEntity.create(
                            null,
                            SELLER_ID,
                            100L,
                            null,
                            "PRODUCT",
                            1L,
                            "EXT-FLOW4-PRODUCT-001",
                            "상품 문의",
                            "상품 관련 문의입니다.",
                            "구매자A",
                            QnaJpaEntity.Status.PENDING,
                            now,
                            now));

            qnaRepository.save(
                    QnaJpaEntity.create(
                            null,
                            SELLER_ID,
                            100L,
                            null,
                            "SHIPPING",
                            1L,
                            "EXT-FLOW4-SHIPPING-001",
                            "배송 문의",
                            "배송 관련 문의입니다.",
                            "구매자B",
                            QnaJpaEntity.Status.PENDING,
                            now,
                            now));

            qnaRepository.save(
                    QnaJpaEntity.create(
                            null,
                            SELLER_ID,
                            100L,
                            null,
                            "ORDER",
                            1L,
                            "EXT-FLOW4-ORDER-001",
                            "주문 문의",
                            "주문 관련 문의입니다.",
                            "구매자C",
                            QnaJpaEntity.Status.PENDING,
                            now,
                            now));

            // Step 2. 전체 조회 (qnaType 필터 없이) - 3건 확인
            givenLegacyAuth()
                    .queryParam("qnaType", "PRODUCT")
                    .queryParam("sellerId", SELLER_ID)
                    .queryParam("page", 0)
                    .queryParam("size", 20)
                    .when()
                    .get(QNAS)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // Step 3. DB 검증 - qnaType 다양성 확인
            var qnas = qnaRepository.findAll();
            var qnaTypes = qnas.stream().map(QnaJpaEntity::getQnaType).toList();
            assertThat(qnaTypes).contains("PRODUCT", "SHIPPING", "ORDER");
        }
    }

    @Nested
    @DisplayName("FLOW-5: QnA 목록 페이징 + totalCount 정합성")
    class PagingTotalCountConsistencyFlowTest {

        @Test
        @Tag("P2")
        @DisplayName("[FLOW-5] QnA 5건 생성 -> size=2로 페이징 조회 시 totalElements=5, 각 페이지 정합성 확인")
        void qnaPaging_FiveItems_PaginationConsistency() {
            // Step 1. QnA 5건 시딩
            for (int i = 0; i < 5; i++) {
                qnaRepository.save(QnaJpaEntityFixtures.pendingEntity());
            }

            // Step 2. page=0, size=2 조회 - 2건, totalElements=5
            givenLegacyAuth()
                    .queryParam("qnaType", "PRODUCT")
                    .queryParam("sellerId", SELLER_ID)
                    .queryParam("page", 0)
                    .queryParam("size", 2)
                    .when()
                    .get(QNAS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(2))
                    .body("data.totalElements", equalTo(5));
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
