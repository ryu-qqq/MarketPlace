package com.ryuqq.marketplace.adapter.in.rest.legacy.qna.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.LegacyQnAApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.request.LegacyQnaSearchRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response.LegacyDetailQnaResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response.LegacyFetchQnaResponse;
import com.ryuqq.marketplace.application.qna.dto.query.QnaSearchCondition;
import com.ryuqq.marketplace.application.qna.dto.result.QnaReplyResult;
import com.ryuqq.marketplace.application.qna.dto.result.QnaResult;
import com.ryuqq.marketplace.domain.qna.vo.QnaReplyType;
import com.ryuqq.marketplace.domain.qna.vo.QnaStatus;
import com.ryuqq.marketplace.domain.qna.vo.QnaType;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("LegacyQnaQueryApiMapper 단위 테스트")
class LegacyQnaQueryApiMapperTest {

    private LegacyQnaQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LegacyQnaQueryApiMapper();
    }

    // ===== QnaResult 픽스처 헬퍼 =====

    private QnaResult qnaResult() {
        return qnaResult(LegacyQnAApiFixtures.DEFAULT_QNA_ID);
    }

    private QnaResult qnaResult(long qnaId) {
        return new QnaResult(
                qnaId,
                1L,
                100L,
                null,
                QnaType.PRODUCT,
                null,
                LegacyQnAApiFixtures.DEFAULT_TITLE,
                LegacyQnAApiFixtures.DEFAULT_CONTENT,
                "홍길동",
                QnaStatus.PENDING,
                List.of(qnaReplyResult()),
                Instant.parse("2025-02-10T01:30:00Z"),
                Instant.parse("2025-02-10T02:00:00Z"));
    }

    private QnaResult qnaResultWithOrderId(long orderId) {
        return new QnaResult(
                LegacyQnAApiFixtures.DEFAULT_QNA_ID,
                1L,
                100L,
                orderId,
                QnaType.ORDER,
                null,
                LegacyQnAApiFixtures.DEFAULT_TITLE,
                LegacyQnAApiFixtures.DEFAULT_CONTENT,
                "홍길동",
                QnaStatus.PENDING,
                List.of(),
                Instant.parse("2025-02-10T01:30:00Z"),
                Instant.parse("2025-02-10T02:00:00Z"));
    }

    private QnaReplyResult qnaReplyResult() {
        return new QnaReplyResult(
                LegacyQnAApiFixtures.DEFAULT_QNA_ANSWER_ID,
                null,
                "답변 내용입니다.",
                "SELLER",
                QnaReplyType.SELLER_ANSWER,
                Instant.parse("2025-02-10T01:30:00Z"));
    }

    @Nested
    @DisplayName("toSearchCondition - 검색 요청을 QnaSearchCondition으로 변환")
    class ToSearchConditionTest {

        @Test
        @DisplayName("검색 요청의 모든 필드가 QnaSearchCondition에 올바르게 매핑된다")
        void toSearchCondition_AllFields_MappedCorrectly() {
            // given
            LegacyQnaSearchRequest request = LegacyQnAApiFixtures.searchRequest();
            int size = 20;

            // when
            QnaSearchCondition condition = mapper.toSearchCondition(request, size);

            // then
            assertThat(condition.status()).isEqualTo(QnaStatus.PENDING);
            assertThat(condition.qnaType()).isEqualTo(QnaType.PRODUCT);
            assertThat(condition.sellerId()).isEqualTo(1L);
            assertThat(condition.cursorId()).isNull();
            assertThat(condition.keyword()).isNull();
            assertThat(condition.fromDate()).isNull();
            assertThat(condition.toDate()).isNull();
            assertThat(condition.size()).isEqualTo(size);
        }

        @Test
        @DisplayName("size 파라미터가 QnaSearchCondition에 올바르게 매핑된다")
        void toSearchCondition_Size_MappedCorrectly() {
            // given
            LegacyQnaSearchRequest request = LegacyQnAApiFixtures.searchRequest();

            // when
            QnaSearchCondition condition = mapper.toSearchCondition(request, 50);

            // then
            assertThat(condition.size()).isEqualTo(50);
        }

        @Test
        @DisplayName("qnaStatus가 null이면 status는 null로 매핑된다")
        void toSearchCondition_NullStatus_MapsToNull() {
            // given
            LegacyQnaSearchRequest request =
                    new LegacyQnaSearchRequest(
                            null, "PRODUCT", null, null, null, 1L, null, null, null);

            // when
            QnaSearchCondition condition = mapper.toSearchCondition(request, 20);

            // then
            assertThat(condition.status()).isNull();
        }
    }

    @Nested
    @DisplayName("toDetailResponse - QnaResult를 LegacyDetailQnaResponse로 변환")
    class ToDetailResponseTest {

        @Test
        @DisplayName("QnaResult를 LegacyDetailQnaResponse로 변환한다")
        void toDetailResponse_ConvertsResult_ReturnsResponse() {
            // given
            QnaResult result = qnaResult();

            // when
            LegacyDetailQnaResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response).isNotNull();
            assertThat(response.qna().qnaId()).isEqualTo(LegacyQnAApiFixtures.DEFAULT_QNA_ID);
        }

        @Test
        @DisplayName("SELLER_ANSWER 타입 답변만 answerQnas에 포함된다")
        void toDetailResponse_OnlySellerAnswers_InAnswerQnas() {
            // given
            QnaResult result = qnaResult();

            // when
            LegacyDetailQnaResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.answerQnas()).isNotEmpty();
        }

        @Test
        @DisplayName("QnA 질문 본문이 올바르게 매핑된다")
        void toDetailResponse_QnaContents_MappedCorrectly() {
            // given
            QnaResult result = qnaResult();

            // when
            LegacyDetailQnaResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.qna().qnaContents().title())
                    .isEqualTo(LegacyQnAApiFixtures.DEFAULT_TITLE);
            assertThat(response.qna().qnaContents().content())
                    .isEqualTo(LegacyQnAApiFixtures.DEFAULT_CONTENT);
        }

        @Test
        @DisplayName("null title/content는 빈 문자열로 변환된다")
        void toDetailResponse_NullContents_ConvertedToEmpty() {
            // given
            QnaResult result =
                    new QnaResult(
                            LegacyQnAApiFixtures.DEFAULT_QNA_ID,
                            1L,
                            100L,
                            null,
                            QnaType.PRODUCT,
                            null,
                            null,
                            null,
                            null,
                            QnaStatus.PENDING,
                            List.of(),
                            null,
                            null);

            // when
            LegacyDetailQnaResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.qna().qnaContents().title()).isEqualTo("");
            assertThat(response.qna().qnaContents().content()).isEqualTo("");
        }
    }

    @Nested
    @DisplayName("toFetchResponse - QnaResult를 LegacyFetchQnaResponse로 변환")
    class ToFetchResponseTest {

        @Test
        @DisplayName("QnaResult를 LegacyFetchQnaResponse로 변환한다")
        void toFetchResponse_ConvertsResult_ReturnsResponse() {
            // given
            QnaResult result = qnaResult();

            // when
            LegacyFetchQnaResponse response = mapper.toFetchResponse(result);

            // then
            assertThat(response.qnaId()).isEqualTo(LegacyQnAApiFixtures.DEFAULT_QNA_ID);
            assertThat(response.qnaStatus()).isEqualTo("PENDING");
            assertThat(response.qnaType()).isEqualTo("PRODUCT");
        }

        @Test
        @DisplayName("questionAuthor가 userName으로 매핑된다")
        void toFetchResponse_QuestionAuthor_MappedToUserName() {
            // given
            QnaResult result = qnaResult();

            // when
            LegacyFetchQnaResponse response = mapper.toFetchResponse(result);

            // then
            assertThat(response.userInfo()).isNotNull();
            assertThat(response.userInfo().userName()).isEqualTo("홍길동");
        }

        @Test
        @DisplayName("orderId가 없으면 product 타입 target으로 매핑된다")
        void toFetchResponse_NoOrderId_MapsToProductTarget() {
            // given
            QnaResult result = qnaResult();

            // when
            LegacyFetchQnaResponse response = mapper.toFetchResponse(result);

            // then
            assertThat(response.qnaTarget()).isNotNull();
            assertThat(response.qnaTarget().productGroupId()).isEqualTo(100L);
            assertThat(response.qnaTarget().orderId()).isNull();
        }

        @Test
        @DisplayName("orderId가 있으면 order 타입 target으로 매핑된다")
        void toFetchResponse_WithOrderId_MapsToOrderTarget() {
            // given
            QnaResult result = qnaResultWithOrderId(3001L);

            // when
            LegacyFetchQnaResponse response = mapper.toFetchResponse(result);

            // then
            assertThat(response.qnaTarget()).isNotNull();
            assertThat(response.qnaTarget().orderId()).isEqualTo(3001L);
        }

        @Test
        @DisplayName("questionAuthor가 null이면 userName은 빈 문자열로 변환된다")
        void toFetchResponse_NullAuthor_ReturnsEmptyUserName() {
            // given
            QnaResult result =
                    new QnaResult(
                            LegacyQnAApiFixtures.DEFAULT_QNA_ID,
                            1L,
                            100L,
                            null,
                            QnaType.PRODUCT,
                            null,
                            LegacyQnAApiFixtures.DEFAULT_TITLE,
                            LegacyQnAApiFixtures.DEFAULT_CONTENT,
                            null,
                            QnaStatus.PENDING,
                            List.of(),
                            null,
                            null);

            // when
            LegacyFetchQnaResponse response = mapper.toFetchResponse(result);

            // then
            assertThat(response.userInfo().userName()).isEqualTo("");
        }
    }

    @Nested
    @DisplayName("toFetchResponses - QnaResult 리스트를 응답 DTO 리스트로 변환")
    class ToFetchResponsesTest {

        @Test
        @DisplayName("결과 리스트를 응답 DTO 리스트로 변환한다")
        void toFetchResponses_ListConverted_Correctly() {
            // given
            List<QnaResult> results = List.of(qnaResult(101L), qnaResult(102L));

            // when
            List<LegacyFetchQnaResponse> responses = mapper.toFetchResponses(results);

            // then
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).qnaId()).isEqualTo(101L);
            assertThat(responses.get(1).qnaId()).isEqualTo(102L);
        }

        @Test
        @DisplayName("빈 리스트는 빈 응답 리스트로 변환된다")
        void toFetchResponses_EmptyList_ReturnsEmpty() {
            // when
            List<LegacyFetchQnaResponse> responses = mapper.toFetchResponses(List.of());

            // then
            assertThat(responses).isEmpty();
        }
    }
}
