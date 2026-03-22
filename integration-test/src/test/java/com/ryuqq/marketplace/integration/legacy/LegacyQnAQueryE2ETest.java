package com.ryuqq.marketplace.integration.legacy;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import com.ryuqq.marketplace.adapter.out.persistence.qna.QnaJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.qna.repository.QnaJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.qna.repository.QnaReplyJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.qnaoutbox.repository.QnaOutboxJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * 레거시 QnA Query E2E 테스트.
 *
 * <p>테스트 대상:
 *
 * <ul>
 *   <li>LQ1~LQ5: GET /api/v1/legacy/qnas - QnA 목록 조회 (상태 필터, 페이징, 커서)
 *   <li>LQ6~LQ9: GET /api/v1/legacy/qna/{qnaId} - QnA 단건 상세 조회
 * </ul>
 */
@Tag("e2e")
@Tag("legacy")
@Tag("qna")
@Tag("query")
@DisplayName("레거시 QnA Query E2E 테스트")
class LegacyQnAQueryE2ETest extends LegacyE2ETestBase {

    private static final String QNAS = "/api/v1/legacy/qnas";
    private static final String QNA_DETAIL = "/api/v1/legacy/qna/{qnaId}";

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

    // ===== GET /api/v1/legacy/qnas =====

    @Nested
    @DisplayName("LQ1: GET /api/v1/legacy/qnas - PENDING 필터 조회")
    class SearchQnasByPendingStatusTest {

        @Test
        @Tag("P0")
        @DisplayName("[LQ1] PENDING 2건, ANSWERED 1건 존재 시 qnaStatus=PENDING 필터 -> 2건 반환")
        void searchQnas_PendingFilter_ReturnsPendingOnly() {
            qnaRepository.save(QnaJpaEntityFixtures.pendingEntity());
            qnaRepository.save(QnaJpaEntityFixtures.pendingEntity());
            qnaRepository.save(QnaJpaEntityFixtures.answeredEntity());

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
                    .body("data.content", hasSize(2))
                    .body("data.content[0].qnaStatus", equalTo("PENDING"));
        }
    }

    @Nested
    @DisplayName("LQ2: GET /api/v1/legacy/qnas - ANSWERED 필터 조회")
    class SearchQnasByAnsweredStatusTest {

        @Test
        @Tag("P0")
        @DisplayName("[LQ2] ANSWERED 1건, PENDING 2건 존재 시 qnaStatus=ANSWERED 필터 -> 1건 반환")
        void searchQnas_AnsweredFilter_ReturnsAnsweredOnly() {
            qnaRepository.save(QnaJpaEntityFixtures.pendingEntity());
            qnaRepository.save(QnaJpaEntityFixtures.pendingEntity());
            qnaRepository.save(QnaJpaEntityFixtures.answeredEntity());

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
                    .body("data.content", hasSize(1))
                    .body("data.content[0].qnaStatus", equalTo("ANSWERED"));
        }
    }

    @Nested
    @DisplayName("LQ3: GET /api/v1/legacy/qnas - 상태 필터 없이 전체 조회")
    class SearchQnasNoStatusFilterTest {

        @Test
        @Tag("P0")
        @DisplayName("[LQ3] 상태 필터 없이 조회 시 전체 3건 반환")
        void searchQnas_NoStatusFilter_ReturnsAll() {
            qnaRepository.save(QnaJpaEntityFixtures.pendingEntity());
            qnaRepository.save(QnaJpaEntityFixtures.answeredEntity());
            qnaRepository.save(QnaJpaEntityFixtures.closedEntity(null));

            givenLegacyAuth()
                    .queryParam("qnaType", "PRODUCT")
                    .queryParam("sellerId", SELLER_ID)
                    .queryParam("page", 0)
                    .queryParam("size", 20)
                    .when()
                    .get(QNAS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(3))
                    .body("data.totalElements", equalTo(3));
        }
    }

    @Nested
    @DisplayName("LQ4: GET /api/v1/legacy/qnas - 빈 결과")
    class SearchQnasEmptyResultTest {

        @Test
        @Tag("P0")
        @DisplayName("[LQ4] QnA 데이터 없을 때 조회 -> 빈 페이지 반환")
        void searchQnas_NoData_ReturnsEmptyPage() {
            givenLegacyAuth()
                    .queryParam("qnaType", "PRODUCT")
                    .queryParam("sellerId", SELLER_ID)
                    .queryParam("page", 0)
                    .queryParam("size", 20)
                    .when()
                    .get(QNAS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(0))
                    .body("data.totalElements", equalTo(0));
        }
    }

    @Nested
    @DisplayName("LQ5: GET /api/v1/legacy/qnas - 페이징 (size=1)")
    class SearchQnasPagingTest {

        @Test
        @Tag("P1")
        @DisplayName("[LQ5] QnA 3건 존재, size=1 요청 시 1건만 반환하고 totalElements=3 확인")
        void searchQnas_PageSize1_ReturnsOnlyOne() {
            qnaRepository.save(QnaJpaEntityFixtures.pendingEntity());
            qnaRepository.save(QnaJpaEntityFixtures.pendingEntity());
            qnaRepository.save(QnaJpaEntityFixtures.pendingEntity());

            givenLegacyAuth()
                    .queryParam("qnaType", "PRODUCT")
                    .queryParam("sellerId", SELLER_ID)
                    .queryParam("page", 0)
                    .queryParam("size", 1)
                    .when()
                    .get(QNAS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(1))
                    .body("data.totalElements", equalTo(3));
        }
    }

    // ===== GET /api/v1/legacy/qna/{qnaId} =====

    @Nested
    @DisplayName("LQ6: GET /api/v1/legacy/qna/{qnaId} - PENDING 상세 조회 (답변 없음)")
    class GetQnaDetailPendingTest {

        @Test
        @Tag("P0")
        @DisplayName("[LQ6] PENDING 상태 QnA 상세 조회 - qna 필드 존재, answerQnas 빈 Set")
        void getQnaDetail_PendingQna_ReturnsDetailWithNoAnswers() {
            var qna = qnaRepository.save(QnaJpaEntityFixtures.pendingEntity());

            givenLegacyAuth()
                    .when()
                    .get(QNA_DETAIL, qna.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.qna", notNullValue())
                    .body("data.qna.qnaId", equalTo(qna.getId().intValue()))
                    .body("data.qna.qnaStatus", equalTo("PENDING"))
                    .body("data.answerQnas", hasSize(0));
        }
    }

    @Nested
    @DisplayName("LQ7: GET /api/v1/legacy/qna/{qnaId} - ANSWERED 상세 조회 (답변 포함)")
    class GetQnaDetailAnsweredTest {

        @Test
        @Tag("P0")
        @DisplayName("[LQ7] ANSWERED QnA + 판매자 답변 1건 존재 시 상세 조회 - answerQnas 1건 포함")
        void getQnaDetail_AnsweredQnaWithReply_ReturnsDetailWithAnswers() {
            var qna = qnaRepository.save(QnaJpaEntityFixtures.answeredEntity());
            qnaReplyRepository.save(QnaJpaEntityFixtures.sellerReplyEntity(null, qna.getId()));

            givenLegacyAuth()
                    .when()
                    .get(QNA_DETAIL, qna.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.qna.qnaStatus", equalTo("ANSWERED"))
                    .body("data.answerQnas", hasSize(1));
        }
    }

    @Nested
    @DisplayName("LQ8: GET /api/v1/legacy/qna/{qnaId} - 존재하지 않는 ID -> 200 (레거시 NOT_FOUND)")
    class GetQnaDetailNotFoundTest {

        @Test
        @Tag("P0")
        @DisplayName("[LQ8] 존재하지 않는 QnA ID 상세 조회 -> 200 (레거시 NOT_FOUND는 200 + body에 에러)")
        void getQnaDetail_NonExistentId_Returns200WithError() {
            givenLegacyAuth()
                    .when()
                    .get(QNA_DETAIL, 999999L)
                    .then()
                    .statusCode(HttpStatus.OK.value());
        }
    }

    @Nested
    @DisplayName("LQ9: GET /api/v1/legacy/qna/{qnaId} - 인증 없는 요청 -> 401")
    class GetQnaDetailUnauthenticatedTest {

        @Test
        @Tag("P0")
        @DisplayName("[LQ9] 인증 헤더 없이 상세 조회 -> 401 Unauthorized")
        void getQnaDetail_Unauthenticated_Returns401() {
            var qna = qnaRepository.save(QnaJpaEntityFixtures.pendingEntity());

            givenUnauthenticated()
                    .when()
                    .get(QNA_DETAIL, qna.getId())
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }
    }
}
