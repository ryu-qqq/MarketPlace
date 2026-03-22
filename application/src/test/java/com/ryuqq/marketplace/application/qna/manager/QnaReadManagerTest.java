package com.ryuqq.marketplace.application.qna.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.qna.QnaQueryFixtures;
import com.ryuqq.marketplace.application.qna.dto.query.QnaSearchCondition;
import com.ryuqq.marketplace.application.qna.port.out.query.QnaQueryPort;
import com.ryuqq.marketplace.domain.qna.QnaFixtures;
import com.ryuqq.marketplace.domain.qna.aggregate.Qna;
import com.ryuqq.marketplace.domain.qna.exception.QnaException;
import com.ryuqq.marketplace.domain.qna.vo.QnaStatus;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("QnaReadManager 단위 테스트")
class QnaReadManagerTest {

    @InjectMocks private QnaReadManager sut;

    @Mock private QnaQueryPort queryPort;

    @Nested
    @DisplayName("getById() - ID로 Qna 단건 조회")
    class GetByIdTest {

        @Test
        @DisplayName("존재하는 ID로 Qna를 조회한다")
        void getById_ExistingId_ReturnsQna() {
            // given
            Qna qna = QnaFixtures.pendingQna();
            given(queryPort.findById(qna.idValue())).willReturn(Optional.of(qna));

            // when
            Qna result = sut.getById(qna.idValue());

            // then
            assertThat(result).isEqualTo(qna);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 QnaException이 발생한다")
        void getById_NonExistentId_ThrowsQnaException() {
            // given
            long nonExistentId = 999L;
            given(queryPort.findById(nonExistentId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(nonExistentId))
                    .isInstanceOf(QnaException.class);
        }
    }

    @Nested
    @DisplayName("findBySellerId() - 셀러 ID로 Qna 목록 조회")
    class FindBySellerIdTest {

        @Test
        @DisplayName("셀러 ID와 상태로 Qna 목록을 조회한다")
        void findBySellerId_ValidParams_ReturnsQnaList() {
            // given
            long sellerId = 1L;
            Qna qna = QnaFixtures.pendingQna();
            given(queryPort.findBySellerId(sellerId, QnaStatus.PENDING, 0, 10))
                    .willReturn(List.of(qna));

            // when
            List<Qna> result = sut.findBySellerId(sellerId, QnaStatus.PENDING, 0, 10);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(qna);
        }

        @Test
        @DisplayName("조회 결과가 없으면 빈 목록을 반환한다")
        void findBySellerId_NoResults_ReturnsEmptyList() {
            // given
            long sellerId = 999L;
            given(queryPort.findBySellerId(sellerId, QnaStatus.PENDING, 0, 10))
                    .willReturn(List.of());

            // when
            List<Qna> result = sut.findBySellerId(sellerId, QnaStatus.PENDING, 0, 10);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("countBySellerId() - 셀러 ID로 Qna 개수 조회")
    class CountBySellerIdTest {

        @Test
        @DisplayName("셀러 ID와 상태로 Qna 개수를 반환한다")
        void countBySellerId_ValidParams_ReturnsCount() {
            // given
            long sellerId = 1L;
            given(queryPort.countBySellerId(sellerId, QnaStatus.PENDING)).willReturn(5L);

            // when
            long count = sut.countBySellerId(sellerId, QnaStatus.PENDING);

            // then
            assertThat(count).isEqualTo(5L);
        }
    }

    @Nested
    @DisplayName("search() - 검색 조건으로 Qna 목록 조회")
    class SearchTest {

        @Test
        @DisplayName("검색 조건으로 Qna 목록을 조회한다")
        void search_ValidCondition_ReturnsQnaList() {
            // given
            QnaSearchCondition condition = QnaQueryFixtures.searchCondition();
            Qna qna = QnaFixtures.pendingQna();
            given(queryPort.search(condition)).willReturn(List.of(qna));

            // when
            List<Qna> result = sut.search(condition);

            // then
            assertThat(result).hasSize(1);
            then(queryPort).should().search(condition);
        }
    }

    @Nested
    @DisplayName("countByCondition() - 검색 조건으로 Qna 개수 조회")
    class CountByConditionTest {

        @Test
        @DisplayName("검색 조건에 맞는 Qna 개수를 반환한다")
        void countByCondition_ValidCondition_ReturnsCount() {
            // given
            QnaSearchCondition condition = QnaQueryFixtures.searchCondition();
            given(queryPort.countByCondition(condition)).willReturn(3L);

            // when
            long count = sut.countByCondition(condition);

            // then
            assertThat(count).isEqualTo(3L);
        }
    }
}
