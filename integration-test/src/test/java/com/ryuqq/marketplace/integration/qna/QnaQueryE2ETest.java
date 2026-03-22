package com.ryuqq.marketplace.integration.qna;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.adapter.out.persistence.qna.QnaJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.qna.repository.QnaJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.qna.repository.QnaReplyJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.qnaoutbox.repository.QnaOutboxJpaRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * QnA Query E2E 테스트.
 *
 * <p>테스트 대상:
 *
 * <ul>
 *   <li>Q1~Q5: GET /qnas - QnA 목록 조회 (상태 필터, 페이징)
 *   <li>Q6~Q10: GET /qnas/{qnaId} - QnA 상세 조회
 * </ul>
 */
@Tag("e2e")
@Tag("qna")
@Tag("query")
@DisplayName("QnA Query E2E 테스트")
class QnaQueryE2ETest extends E2ETestBase {

    private static final String QNAS = "/qnas";
    private static final String QNA_DETAIL = "/qnas/{qnaId}";

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
    @DisplayName("Q1: GET /qnas - 셀러별 QnA 목록 조회 - PENDING 필터")
    class SearchQnasByPendingStatusTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q1] PENDING 상태 QnA 2건, ANSWERED 1건 존재 시 PENDING 필터 → 2건 반환")
        void searchQnas_PendingFilter_ReturnsPendingOnly() {
            qnaRepository.save(QnaJpaEntityFixtures.pendingEntity());
            qnaRepository.save(QnaJpaEntityFixtures.pendingEntity());
            qnaRepository.save(QnaJpaEntityFixtures.answeredEntity());

            given().spec(givenSuperAdmin())
                    .queryParam("sellerId", SELLER_ID)
                    .queryParam("status", "PENDING")
                    .queryParam("page", 0)
                    .queryParam("size", 20)
                    .when()
                    .get(QNAS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(2))
                    .body("data.content[0].status", equalTo("PENDING"));
        }
    }

    @Nested
    @DisplayName("Q2: GET /qnas - 셀러별 QnA 목록 조회 - ANSWERED 필터")
    class SearchQnasByAnsweredStatusTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q2] ANSWERED 상태 QnA 1건, PENDING 2건 존재 시 ANSWERED 필터 → 1건 반환")
        void searchQnas_AnsweredFilter_ReturnsAnsweredOnly() {
            qnaRepository.save(QnaJpaEntityFixtures.pendingEntity());
            qnaRepository.save(QnaJpaEntityFixtures.pendingEntity());
            qnaRepository.save(QnaJpaEntityFixtures.answeredEntity());

            given().spec(givenSuperAdmin())
                    .queryParam("sellerId", SELLER_ID)
                    .queryParam("status", "ANSWERED")
                    .queryParam("page", 0)
                    .queryParam("size", 20)
                    .when()
                    .get(QNAS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(1))
                    .body("data.content[0].status", equalTo("ANSWERED"));
        }
    }

    @Nested
    @DisplayName("Q3: GET /qnas - 셀러별 QnA 목록 조회 - 상태 필터 없음 (전체)")
    class SearchQnasByNoStatusFilterTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q3] 상태 필터 없이 조회 시 전체 3건 반환")
        void searchQnas_NoStatusFilter_ReturnsAll() {
            qnaRepository.save(QnaJpaEntityFixtures.pendingEntity());
            qnaRepository.save(QnaJpaEntityFixtures.answeredEntity());
            qnaRepository.save(QnaJpaEntityFixtures.closedEntity(null));

            given().spec(givenSuperAdmin())
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
    @DisplayName("Q4: GET /qnas - 셀러별 QnA 목록 조회 - 빈 결과")
    class SearchQnasByEmptyResultTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q4] QnA 데이터 없을 때 조회 → 빈 페이지 반환")
        void searchQnas_NoData_ReturnsEmptyPage() {
            given().spec(givenSuperAdmin())
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
    @DisplayName("Q5: GET /qnas - 셀러별 QnA 목록 조회 - 페이징 (page=0, size=1)")
    class SearchQnasByPagingTest {

        @Test
        @Tag("P1")
        @DisplayName("[Q5] QnA 3건 존재, size=1 요청 시 1건만 반환하고 totalElements=3 확인")
        void searchQnas_PageSize1_ReturnsOnlyOne() {
            qnaRepository.save(QnaJpaEntityFixtures.pendingEntity());
            qnaRepository.save(QnaJpaEntityFixtures.pendingEntity());
            qnaRepository.save(QnaJpaEntityFixtures.pendingEntity());

            given().spec(givenSuperAdmin())
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

    @Nested
    @DisplayName("Q6: GET /qnas/{qnaId} - QnA 상세 조회 - PENDING (답변 없음)")
    class GetQnaDetailPendingTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q6] PENDING 상태 QnA 상세 조회 - replies 빈 목록 반환")
        void getQnaDetail_PendingQna_ReturnsDetailWithEmptyReplies() {
            var qna = qnaRepository.save(QnaJpaEntityFixtures.pendingEntity());

            given().spec(givenSuperAdmin())
                    .when()
                    .get(QNA_DETAIL, qna.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue())
                    .body("data.qnaId", equalTo(qna.getId().intValue()))
                    .body("data.status", equalTo("PENDING"))
                    .body("data.replies", hasSize(0));
        }
    }

    @Nested
    @DisplayName("Q7: GET /qnas/{qnaId} - QnA 상세 조회 - ANSWERED (답변 포함)")
    class GetQnaDetailAnsweredTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q7] ANSWERED 상태 QnA + reply 1건 존재 시 상세 조회 - replies 포함 반환")
        void getQnaDetail_AnsweredQnaWithReply_ReturnsDetailWithReplies() {
            var qna = qnaRepository.save(QnaJpaEntityFixtures.answeredEntity());
            qnaReplyRepository.save(QnaJpaEntityFixtures.sellerReplyEntity(null, qna.getId()));

            given().spec(givenSuperAdmin())
                    .when()
                    .get(QNA_DETAIL, qna.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.status", equalTo("ANSWERED"))
                    .body("data.replies", hasSize(1))
                    .body("data.replies[0].replyType", notNullValue());
        }
    }

    @Nested
    @DisplayName("Q8: GET /qnas/{qnaId} - QnA 상세 조회 - 존재하지 않는 ID → 404")
    class GetQnaDetailNotFoundTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q8] 존재하지 않는 QnA ID 상세 조회 → 404")
        void getQnaDetail_NonExistentId_Returns404() {
            given().spec(givenSuperAdmin())
                    .when()
                    .get(QNA_DETAIL, 999999L)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }
    }

    @Nested
    @DisplayName("Q9: GET /qnas/{qnaId} - QnA 상세 조회 - replies 대댓글 포함")
    class GetQnaDetailWithNestedRepliesTest {

        @Test
        @Tag("P1")
        @DisplayName("[Q9] ANSWERED QnA + 판매자 답변 + 구매자 추가 질문 존재 시 replies 2건 반환")
        void getQnaDetail_WithNestedReplies_ReturnsBothReplies() {
            var qna = qnaRepository.save(QnaJpaEntityFixtures.answeredEntity());

            var sellerReply = qnaReplyRepository.save(
                    QnaJpaEntityFixtures.sellerReplyEntity(null, qna.getId()));
            qnaReplyRepository.save(
                    QnaJpaEntityFixtures.buyerFollowUpEntity(null, qna.getId(), sellerReply.getId()));

            given().spec(givenSuperAdmin())
                    .when()
                    .get(QNA_DETAIL, qna.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.replies", hasSize(2));
        }
    }

    @Nested
    @DisplayName("Q10: 목록 조회 후 상세 조회 일관성 검증")
    class SearchAndDetailConsistencyTest {

        @Test
        @Tag("P2")
        @DisplayName("[Q10] 목록 조회에서 반환된 qnaId로 상세 조회 시 동일한 데이터 반환")
        void searchQnas_ThenGetDetail_DataConsistent() {
            qnaRepository.save(QnaJpaEntityFixtures.pendingEntity());

            Long qnaId = given().spec(givenSuperAdmin())
                    .queryParam("sellerId", SELLER_ID)
                    .queryParam("page", 0)
                    .queryParam("size", 20)
                    .when()
                    .get(QNAS)
                    .jsonPath()
                    .getLong("data.content[0].qnaId");

            given().spec(givenSuperAdmin())
                    .when()
                    .get(QNA_DETAIL, qnaId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.qnaId", equalTo(qnaId.intValue()))
                    .body("data.status", equalTo("PENDING"));
        }
    }
}
