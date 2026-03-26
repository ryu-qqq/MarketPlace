package com.ryuqq.marketplace.application.qna.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.qna.dto.result.QnaResult;
import com.ryuqq.marketplace.domain.qna.QnaFixtures;
import com.ryuqq.marketplace.domain.qna.aggregate.Qna;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("QnaAssembler 단위 테스트")
class QnaAssemblerTest {

    private QnaAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new QnaAssembler();
    }

    @Nested
    @DisplayName("toResult() - Qna → QnaResult 변환")
    class ToResultTest {

        @Test
        @DisplayName("PENDING 상태 Qna를 QnaResult로 변환한다")
        void toResult_PendingQna_ReturnsQnaResult() {
            // given
            Qna qna = QnaFixtures.pendingQna();

            // when
            QnaResult result = sut.toResult(qna);

            // then
            assertThat(result).isNotNull();
            assertThat(result.qnaId()).isEqualTo(qna.idValue());
            assertThat(result.sellerId()).isEqualTo(qna.sellerId());
            assertThat(result.productGroupId()).isEqualTo(qna.productGroupId());
            assertThat(result.qnaType()).isEqualTo(qna.qnaType());
            assertThat(result.source()).isEqualTo(qna.source());
            assertThat(result.questionContent()).isEqualTo(qna.questionContent());
            assertThat(result.questionAuthor()).isEqualTo(qna.questionAuthor());
            assertThat(result.status()).isEqualTo(qna.status());
            assertThat(result.createdAt()).isEqualTo(qna.createdAt());
            assertThat(result.updatedAt()).isEqualTo(qna.updatedAt());
        }

        @Test
        @DisplayName("PENDING 상태 Qna 변환 시 replies가 빈 목록이다")
        void toResult_PendingQnaWithoutReplies_ReturnsEmptyReplies() {
            // given
            Qna qna = QnaFixtures.pendingQna();

            // when
            QnaResult result = sut.toResult(qna);

            // then
            assertThat(result.replies()).isEmpty();
        }

        @Test
        @DisplayName("ANSWERED 상태 Qna 변환 시 replies가 포함된다")
        void toResult_AnsweredQnaWithReply_ReturnsResultWithReplies() {
            // given
            Qna qna = QnaFixtures.answeredQna();

            // when
            QnaResult result = sut.toResult(qna);

            // then
            assertThat(result.replies()).hasSize(1);
            assertThat(result.replies().get(0).content())
                    .isEqualTo(QnaFixtures.DEFAULT_ANSWER_CONTENT);
            assertThat(result.replies().get(0).authorName())
                    .isEqualTo(QnaFixtures.DEFAULT_ANSWER_AUTHOR);
        }

        @Test
        @DisplayName("변환된 QnaResult의 source 필드가 원본 Qna와 동일하다")
        void toResult_QnaWithSource_MapsSourceCorrectly() {
            // given
            Qna qna = QnaFixtures.pendingQna();

            // when
            QnaResult result = sut.toResult(qna);

            // then
            assertThat(result.source().salesChannelId())
                    .isEqualTo(QnaFixtures.DEFAULT_SALES_CHANNEL_ID);
            assertThat(result.source().externalQnaId())
                    .isEqualTo(QnaFixtures.DEFAULT_EXTERNAL_QNA_ID);
        }
    }

    @Nested
    @DisplayName("toResults() - Qna 목록 → QnaResult 목록 변환")
    class ToResultsTest {

        @Test
        @DisplayName("Qna 목록을 QnaResult 목록으로 변환한다")
        void toResults_QnaList_ReturnsQnaResultList() {
            // given
            Qna pendingQna = QnaFixtures.pendingQna(1L);
            Qna answeredQna = QnaFixtures.answeredQna(2L);

            // when
            List<QnaResult> results = sut.toResults(List.of(pendingQna, answeredQna));

            // then
            assertThat(results).hasSize(2);
            assertThat(results.get(0).qnaId()).isEqualTo(1L);
            assertThat(results.get(1).qnaId()).isEqualTo(2L);
        }

        @Test
        @DisplayName("빈 목록을 변환하면 빈 결과 목록을 반환한다")
        void toResults_EmptyList_ReturnsEmptyList() {
            // when
            List<QnaResult> results = sut.toResults(List.of());

            // then
            assertThat(results).isEmpty();
        }

        @Test
        @DisplayName("단일 Qna 목록을 변환하면 단일 결과 목록을 반환한다")
        void toResults_SingleQna_ReturnsSingleResult() {
            // given
            Qna qna = QnaFixtures.pendingQna(5L);

            // when
            List<QnaResult> results = sut.toResults(List.of(qna));

            // then
            assertThat(results).hasSize(1);
            assertThat(results.get(0).qnaId()).isEqualTo(5L);
        }
    }
}
