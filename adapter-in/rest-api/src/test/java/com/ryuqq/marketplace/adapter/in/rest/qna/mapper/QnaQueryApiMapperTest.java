package com.ryuqq.marketplace.adapter.in.rest.qna.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.qna.QnaApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.qna.dto.response.QnaApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.qna.dto.response.QnaReplyApiResponse;
import com.ryuqq.marketplace.application.qna.dto.result.QnaListResult;
import com.ryuqq.marketplace.application.qna.dto.result.QnaResult;
import com.ryuqq.marketplace.domain.qna.vo.QnaReplyType;
import com.ryuqq.marketplace.domain.qna.vo.QnaStatus;
import com.ryuqq.marketplace.domain.qna.vo.QnaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("QnaQueryApiMapper 단위 테스트")
class QnaQueryApiMapperTest {

    private QnaQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new QnaQueryApiMapper();
    }

    @Nested
    @DisplayName("toResponse() - 단일 QnA 결과 변환")
    class ToResponseTest {

        @Test
        @DisplayName("QnaResult를 QnaApiResponse로 변환한다")
        void toResponse_ConvertsResult_ReturnsApiResponse() {
            // given
            QnaResult result = QnaApiFixtures.qnaResult(QnaApiFixtures.DEFAULT_QNA_ID);

            // when
            QnaApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.qnaId()).isEqualTo(QnaApiFixtures.DEFAULT_QNA_ID);
            assertThat(response.sellerId()).isEqualTo(QnaApiFixtures.DEFAULT_SELLER_ID);
            assertThat(response.productGroupId()).isEqualTo(QnaApiFixtures.DEFAULT_PRODUCT_GROUP_ID);
            assertThat(response.qnaType()).isEqualTo(QnaType.PRODUCT.name());
            assertThat(response.salesChannelId()).isEqualTo(QnaApiFixtures.DEFAULT_SALES_CHANNEL_ID);
            assertThat(response.externalQnaId()).isEqualTo(QnaApiFixtures.DEFAULT_EXTERNAL_QNA_ID);
            assertThat(response.questionContent()).isEqualTo(QnaApiFixtures.DEFAULT_QUESTION_CONTENT);
            assertThat(response.questionAuthor()).isEqualTo(QnaApiFixtures.DEFAULT_QUESTION_AUTHOR);
            assertThat(response.status()).isEqualTo(QnaStatus.PENDING.name());
            assertThat(response.replies()).isEmpty();
        }

        @Test
        @DisplayName("답변 목록이 포함된 QnaResult를 변환한다")
        void toResponse_WithReplies_ConvertsReplies() {
            // given
            QnaResult result = QnaApiFixtures.qnaResultWithReplies(QnaApiFixtures.DEFAULT_QNA_ID);

            // when
            QnaApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.status()).isEqualTo(QnaStatus.ANSWERED.name());
            assertThat(response.replies()).hasSize(2);

            QnaReplyApiResponse firstReply = response.replies().get(0);
            assertThat(firstReply.replyId()).isEqualTo(1L);
            assertThat(firstReply.parentReplyId()).isNull();
            assertThat(firstReply.content()).isEqualTo(QnaApiFixtures.DEFAULT_ANSWER_CONTENT);
            assertThat(firstReply.authorName()).isEqualTo(QnaApiFixtures.DEFAULT_AUTHOR_NAME);
            assertThat(firstReply.replyType()).isEqualTo(QnaReplyType.SELLER_ANSWER.name());

            QnaReplyApiResponse secondReply = response.replies().get(1);
            assertThat(secondReply.replyId()).isEqualTo(2L);
            assertThat(secondReply.parentReplyId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("QnaType이 enum name으로 변환된다")
        void toResponse_QnaType_IsConvertedToName() {
            // given
            QnaResult result = QnaApiFixtures.qnaResult(1L, QnaStatus.PENDING, QnaType.SHIPPING);

            // when
            QnaApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.qnaType()).isEqualTo("SHIPPING");
        }

        @Test
        @DisplayName("QnaStatus가 enum name으로 변환된다")
        void toResponse_QnaStatus_IsConvertedToName() {
            // given
            QnaResult result = QnaApiFixtures.qnaResult(1L, QnaStatus.ANSWERED, QnaType.PRODUCT);

            // when
            QnaApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.status()).isEqualTo("ANSWERED");
        }

        @Test
        @DisplayName("createdAt과 updatedAt이 문자열로 변환된다")
        void toResponse_Timestamps_AreConvertedToString() {
            // given
            QnaResult result = QnaApiFixtures.qnaResult(QnaApiFixtures.DEFAULT_QNA_ID);

            // when
            QnaApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.createdAt()).isNotBlank();
            assertThat(response.updatedAt()).isNotBlank();
        }

        @Test
        @DisplayName("QnaSource의 salesChannelId와 externalQnaId가 변환된다")
        void toResponse_QnaSource_IsFlattened() {
            // given
            QnaResult result = QnaApiFixtures.qnaResult(QnaApiFixtures.DEFAULT_QNA_ID);

            // when
            QnaApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.salesChannelId()).isEqualTo(QnaApiFixtures.DEFAULT_SALES_CHANNEL_ID);
            assertThat(response.externalQnaId()).isEqualTo(QnaApiFixtures.DEFAULT_EXTERNAL_QNA_ID);
        }
    }

    @Nested
    @DisplayName("toPageResponse() - 목록 페이지 결과 변환")
    class ToPageResponseTest {

        @Test
        @DisplayName("QnaListResult를 PageApiResponse로 변환한다")
        void toPageResponse_ConvertsListResult_ReturnsPageResponse() {
            // given
            QnaListResult listResult = QnaApiFixtures.listResult(3, 0, 20);

            // when
            PageApiResponse<QnaApiResponse> response = mapper.toPageResponse(listResult);

            // then
            assertThat(response.content()).hasSize(3);
            assertThat(response.page()).isZero();
            assertThat(response.size()).isEqualTo(20);
            assertThat(response.totalElements()).isEqualTo(3);
        }

        @Test
        @DisplayName("빈 결과이면 빈 페이지 응답을 반환한다")
        void toPageResponse_EmptyResult_ReturnsEmptyPage() {
            // given
            QnaListResult listResult = QnaApiFixtures.emptyListResult();

            // when
            PageApiResponse<QnaApiResponse> response = mapper.toPageResponse(listResult);

            // then
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isZero();
        }

        @Test
        @DisplayName("offset과 limit으로 page 번호를 계산한다")
        void toPageResponse_CalculatesPageFromOffsetAndLimit() {
            // given
            // page=2, size=10 이면 offset=20, limit=10
            QnaListResult listResult = QnaApiFixtures.listResult(5, 20, 10);

            // when
            PageApiResponse<QnaApiResponse> response = mapper.toPageResponse(listResult);

            // then
            assertThat(response.page()).isEqualTo(2);
            assertThat(response.size()).isEqualTo(10);
        }

        @Test
        @DisplayName("limit이 0이면 page는 0으로 처리된다")
        void toPageResponse_ZeroLimit_ReturnsPageZero() {
            // given
            QnaListResult listResult = new com.ryuqq.marketplace.application.qna.dto.result.QnaListResult(
                    java.util.List.of(), 0, 0, 0);

            // when
            PageApiResponse<QnaApiResponse> response = mapper.toPageResponse(listResult);

            // then
            assertThat(response.page()).isZero();
        }

        @Test
        @DisplayName("페이지 메타 정보가 정확히 변환된다")
        void toPageResponse_PageMeta_IsCorrect() {
            // given
            QnaListResult listResult = QnaApiFixtures.listResult(50, 40, 10);

            // when
            PageApiResponse<QnaApiResponse> response = mapper.toPageResponse(listResult);

            // then
            assertThat(response.page()).isEqualTo(4);
            assertThat(response.size()).isEqualTo(10);
            assertThat(response.totalElements()).isEqualTo(50);
        }
    }
}
