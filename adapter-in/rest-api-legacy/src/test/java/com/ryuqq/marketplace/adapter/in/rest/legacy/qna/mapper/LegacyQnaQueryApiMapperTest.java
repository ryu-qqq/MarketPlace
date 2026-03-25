package com.ryuqq.marketplace.adapter.in.rest.legacy.qna.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.LegacyQnAApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.request.LegacyQnaSearchRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response.LegacyDetailQnaResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response.LegacyFetchQnaResponse;
import com.ryuqq.marketplace.application.legacy.qna.dto.query.LegacyQnaSearchParams;
import com.ryuqq.marketplace.application.legacy.qna.dto.result.LegacyQnaAnswerResult;
import com.ryuqq.marketplace.application.legacy.qna.dto.result.LegacyQnaDetailResult;
import java.time.LocalDateTime;
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

    // ===== LegacyQnaDetailResult 픽스처 헬퍼 =====

    private LegacyQnaDetailResult detailResult() {
        return detailResult(LegacyQnAApiFixtures.DEFAULT_QNA_ID);
    }

    private LegacyQnaDetailResult detailResult(long qnaId) {
        return new LegacyQnaDetailResult(
                qnaId,
                LegacyQnAApiFixtures.DEFAULT_TITLE,
                LegacyQnAApiFixtures.DEFAULT_CONTENT,
                "N",
                "PENDING",
                "PRODUCT",
                "GENERAL",
                2L,
                1L,
                "MEMBERS",
                "홍길동",
                LocalDateTime.of(2025, 2, 10, 10, 30, 0),
                LocalDateTime.of(2025, 2, 10, 11, 0, 0),
                100L,
                null,
                List.of(answerResult()),
                List.of());
    }

    private LegacyQnaDetailResult detailResultWithOrderId(long orderId) {
        return new LegacyQnaDetailResult(
                LegacyQnAApiFixtures.DEFAULT_QNA_ID,
                LegacyQnAApiFixtures.DEFAULT_TITLE,
                LegacyQnAApiFixtures.DEFAULT_CONTENT,
                "N",
                "PENDING",
                "ORDER",
                "GENERAL",
                2L,
                1L,
                "MEMBERS",
                "홍길동",
                LocalDateTime.of(2025, 2, 10, 10, 30, 0),
                LocalDateTime.of(2025, 2, 10, 11, 0, 0),
                100L,
                orderId,
                List.of(),
                List.of());
    }

    private LegacyQnaAnswerResult answerResult() {
        return new LegacyQnaAnswerResult(
                LegacyQnAApiFixtures.DEFAULT_QNA_ANSWER_ID,
                null,
                "SELLER",
                "",
                "답변 내용입니다.",
                "SELLER",
                "SELLER",
                LocalDateTime.of(2025, 2, 10, 10, 30, 0),
                LocalDateTime.of(2025, 2, 10, 10, 30, 0),
                List.of());
    }

    @Nested
    @DisplayName("toSearchParams - 검색 요청을 LegacyQnaSearchParams로 변환")
    class ToSearchParamsTest {

        @Test
        @DisplayName("검색 요청의 모든 필드가 LegacyQnaSearchParams에 올바르게 매핑된다")
        void toSearchParams_AllFields_MappedCorrectly() {
            // given
            LegacyQnaSearchRequest request = LegacyQnAApiFixtures.searchRequest();
            int size = 20;

            // when
            LegacyQnaSearchParams params = mapper.toSearchParams(request, size);

            // then
            assertThat(params.qnaStatus()).isEqualTo("PENDING");
            assertThat(params.qnaType()).isEqualTo("PRODUCT");
            assertThat(params.sellerId()).isEqualTo(1L);
            assertThat(params.lastDomainId()).isNull();
            assertThat(params.searchKeyword()).isNull();
            assertThat(params.startDate()).isNull();
            assertThat(params.endDate()).isNull();
            assertThat(params.size()).isEqualTo(size);
        }

        @Test
        @DisplayName("size 파라미터가 LegacyQnaSearchParams에 올바르게 매핑된다")
        void toSearchParams_Size_MappedCorrectly() {
            // given
            LegacyQnaSearchRequest request = LegacyQnAApiFixtures.searchRequest();

            // when
            LegacyQnaSearchParams params = mapper.toSearchParams(request, 50);

            // then
            assertThat(params.size()).isEqualTo(50);
        }

        @Test
        @DisplayName("qnaStatus가 null이면 status는 null로 매핑된다")
        void toSearchParams_NullStatus_MapsToNull() {
            // given
            LegacyQnaSearchRequest request =
                    new LegacyQnaSearchRequest(
                            null, "PRODUCT", null, null, null, 1L, null, null, null);

            // when
            LegacyQnaSearchParams params = mapper.toSearchParams(request, 20);

            // then
            assertThat(params.qnaStatus()).isNull();
        }
    }

    @Nested
    @DisplayName("toDetailResponse - LegacyQnaDetailResult를 LegacyDetailQnaResponse로 변환")
    class ToDetailResponseTest {

        @Test
        @DisplayName("LegacyQnaDetailResult를 LegacyDetailQnaResponse로 변환한다")
        void toDetailResponse_ConvertsResult_ReturnsResponse() {
            // given
            LegacyQnaDetailResult result = detailResult();

            // when
            LegacyDetailQnaResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response).isNotNull();
            assertThat(response.qna().qnaId()).isEqualTo(LegacyQnAApiFixtures.DEFAULT_QNA_ID);
        }

        @Test
        @DisplayName("답변이 answerQnas에 포함된다")
        void toDetailResponse_Answers_InAnswerQnas() {
            // given
            LegacyQnaDetailResult result = detailResult();

            // when
            LegacyDetailQnaResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.answerQnas()).isNotEmpty();
        }

        @Test
        @DisplayName("QnA 질문 본문이 올바르게 매핑된다")
        void toDetailResponse_QnaContents_MappedCorrectly() {
            // given
            LegacyQnaDetailResult result = detailResult();

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
            LegacyQnaDetailResult result =
                    new LegacyQnaDetailResult(
                            LegacyQnAApiFixtures.DEFAULT_QNA_ID,
                            null, null, "N", "PENDING", "PRODUCT", "GENERAL",
                            2L, 1L, "MEMBERS", null, null, null, 100L, null,
                            List.of(), List.of());

            // when
            LegacyDetailQnaResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.qna().qnaContents().title()).isEqualTo("");
            assertThat(response.qna().qnaContents().content()).isEqualTo("");
        }
    }

    @Nested
    @DisplayName("toFetchResponse - LegacyQnaDetailResult를 LegacyFetchQnaResponse로 변환")
    class ToFetchResponseTest {

        @Test
        @DisplayName("LegacyQnaDetailResult를 LegacyFetchQnaResponse로 변환한다")
        void toFetchResponse_ConvertsResult_ReturnsResponse() {
            // given
            LegacyQnaDetailResult result = detailResult();

            // when
            LegacyFetchQnaResponse response = mapper.toFetchResponse(result);

            // then
            assertThat(response.qnaId()).isEqualTo(LegacyQnAApiFixtures.DEFAULT_QNA_ID);
            assertThat(response.qnaStatus()).isEqualTo("PENDING");
            assertThat(response.qnaType()).isEqualTo("PRODUCT");
        }

        @Test
        @DisplayName("insertOperator가 userName으로 매핑된다")
        void toFetchResponse_InsertOperator_MappedToUserName() {
            // given
            LegacyQnaDetailResult result = detailResult();

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
            LegacyQnaDetailResult result = detailResult();

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
            LegacyQnaDetailResult result = detailResultWithOrderId(3001L);

            // when
            LegacyFetchQnaResponse response = mapper.toFetchResponse(result);

            // then
            assertThat(response.qnaTarget()).isNotNull();
            assertThat(response.qnaTarget().orderId()).isEqualTo(3001L);
        }

        @Test
        @DisplayName("insertOperator가 null이면 userName은 빈 문자열로 변환된다")
        void toFetchResponse_NullInsertOperator_ReturnsEmptyUserName() {
            // given
            LegacyQnaDetailResult result =
                    new LegacyQnaDetailResult(
                            LegacyQnAApiFixtures.DEFAULT_QNA_ID,
                            LegacyQnAApiFixtures.DEFAULT_TITLE,
                            LegacyQnAApiFixtures.DEFAULT_CONTENT,
                            "N", "PENDING", "PRODUCT", "GENERAL",
                            2L, 1L, "MEMBERS", null, null, null, 100L, null,
                            List.of(), List.of());

            // when
            LegacyFetchQnaResponse response = mapper.toFetchResponse(result);

            // then
            assertThat(response.userInfo().userName()).isEqualTo("");
        }
    }

    @Nested
    @DisplayName("toFetchResponses - LegacyQnaDetailResult 리스트를 응답 DTO 리스트로 변환")
    class ToFetchResponsesTest {

        @Test
        @DisplayName("결과 리스트를 응답 DTO 리스트로 변환한다")
        void toFetchResponses_ListConverted_Correctly() {
            // given
            List<LegacyQnaDetailResult> results = List.of(detailResult(101L), detailResult(102L));

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
