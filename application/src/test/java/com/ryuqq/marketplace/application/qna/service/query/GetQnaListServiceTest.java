package com.ryuqq.marketplace.application.qna.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.qna.QnaQueryFixtures;
import com.ryuqq.marketplace.application.qna.assembler.QnaAssembler;
import com.ryuqq.marketplace.application.qna.dto.query.QnaSearchCondition;
import com.ryuqq.marketplace.application.qna.dto.result.QnaListResult;
import com.ryuqq.marketplace.application.qna.manager.QnaReadManager;
import com.ryuqq.marketplace.domain.qna.QnaFixtures;
import com.ryuqq.marketplace.domain.qna.aggregate.Qna;
import com.ryuqq.marketplace.domain.qna.vo.QnaStatus;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("GetQnaListService 단위 테스트")
class GetQnaListServiceTest {

    @InjectMocks private GetQnaListService sut;

    @Mock private QnaReadManager readManager;
    @Spy private QnaAssembler assembler;

    @Nested
    @DisplayName("execute(sellerId, status, offset, limit) - 셀러 기준 QnA 목록 조회")
    class ExecuteBySellerTest {

        @Test
        @DisplayName("셀러별 QnA 목록 조회 시 items와 totalCount를 포함한 QnaListResult를 반환한다")
        void execute_BySeller_ReturnsQnaListResult() {
            // given
            long sellerId = 1L;
            Qna qna = QnaFixtures.pendingQna();
            given(readManager.findBySellerId(sellerId, QnaStatus.PENDING, 0, 10))
                    .willReturn(List.of(qna));
            given(readManager.countBySellerId(sellerId, QnaStatus.PENDING)).willReturn(1L);

            // when
            QnaListResult result = sut.execute(sellerId, QnaStatus.PENDING, 0, 10);

            // then
            assertThat(result.items()).hasSize(1);
            assertThat(result.totalCount()).isEqualTo(1L);
            assertThat(result.offset()).isZero();
            assertThat(result.limit()).isEqualTo(10);
        }

        @Test
        @DisplayName("조회 결과가 없으면 빈 items와 0 totalCount를 반환한다")
        void execute_BySeller_NoResults_ReturnsEmptyResult() {
            // given
            long sellerId = 999L;
            given(readManager.findBySellerId(sellerId, QnaStatus.PENDING, 0, 10))
                    .willReturn(List.of());
            given(readManager.countBySellerId(sellerId, QnaStatus.PENDING)).willReturn(0L);

            // when
            QnaListResult result = sut.execute(sellerId, QnaStatus.PENDING, 0, 10);

            // then
            assertThat(result.items()).isEmpty();
            assertThat(result.totalCount()).isZero();
        }
    }

    @Nested
    @DisplayName("execute(condition) - 검색 조건 기반 QnA 목록 조회")
    class ExecuteByConditionTest {

        @Test
        @DisplayName("검색 조건으로 QnA 목록 조회 시 QnaListResult를 반환한다")
        void execute_ByCondition_ReturnsQnaListResult() {
            // given
            QnaSearchCondition condition = QnaQueryFixtures.searchCondition();
            Qna qna = QnaFixtures.pendingQna();
            given(readManager.search(condition)).willReturn(List.of(qna));
            given(readManager.countByCondition(condition)).willReturn(1L);

            // when
            QnaListResult result = sut.execute(condition);

            // then
            assertThat(result.items()).hasSize(1);
            assertThat(result.totalCount()).isEqualTo(1L);
        }

        @Test
        @DisplayName("검색 조건으로 조회 시 size를 limit으로 사용한다")
        void execute_ByCondition_UsesConditionSizeAsLimit() {
            // given
            QnaSearchCondition condition =
                    QnaQueryFixtures.searchCondition(1L, QnaStatus.PENDING, 15);
            given(readManager.search(condition)).willReturn(List.of());
            given(readManager.countByCondition(condition)).willReturn(0L);

            // when
            QnaListResult result = sut.execute(condition);

            // then
            assertThat(result.limit()).isEqualTo(15);
        }

        @Test
        @DisplayName("검색 조건으로 조회 시 readManager의 search와 countByCondition을 모두 호출한다")
        void execute_ByCondition_InvokesBothSearchAndCount() {
            // given
            QnaSearchCondition condition = QnaQueryFixtures.searchCondition();
            given(readManager.search(condition)).willReturn(List.of());
            given(readManager.countByCondition(condition)).willReturn(0L);

            // when
            sut.execute(condition);

            // then
            then(readManager).should().search(condition);
            then(readManager).should().countByCondition(condition);
        }
    }
}
