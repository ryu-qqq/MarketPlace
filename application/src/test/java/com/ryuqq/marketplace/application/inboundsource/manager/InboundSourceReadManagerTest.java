package com.ryuqq.marketplace.application.inboundsource.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.inboundsource.port.out.query.InboundSourceQueryPort;
import com.ryuqq.marketplace.domain.inboundsource.InboundSourceFixtures;
import com.ryuqq.marketplace.domain.inboundsource.aggregate.InboundSource;
import com.ryuqq.marketplace.domain.inboundsource.exception.InboundSourceNotFoundException;
import com.ryuqq.marketplace.domain.inboundsource.id.InboundSourceId;
import com.ryuqq.marketplace.domain.inboundsource.query.InboundSourceSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("InboundSourceReadManager 단위 테스트")
class InboundSourceReadManagerTest {

    @InjectMocks private InboundSourceReadManager sut;

    @Mock private InboundSourceQueryPort queryPort;

    @Nested
    @DisplayName("getById() - ID로 조회")
    class GetByIdTest {

        @Test
        @DisplayName("존재하는 ID로 InboundSource를 조회한다")
        void getById_Exists_ReturnsInboundSource() {
            // given
            long id = 1L;
            InboundSourceId inboundSourceId = InboundSourceId.of(id);
            InboundSource expected = InboundSourceFixtures.activeInboundSource(id);

            given(queryPort.findById(inboundSourceId)).willReturn(Optional.of(expected));

            // when
            InboundSource result = sut.getById(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findById(inboundSourceId);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 예외가 발생한다")
        void getById_NotExists_ThrowsException() {
            // given
            long id = 999L;
            InboundSourceId inboundSourceId = InboundSourceId.of(id);

            given(queryPort.findById(inboundSourceId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(id))
                    .isInstanceOf(InboundSourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findByCode() - 코드로 조회")
    class FindByCodeTest {

        @Test
        @DisplayName("존재하는 코드로 InboundSource를 조회한다")
        void findByCode_Exists_ReturnsInboundSource() {
            // given
            String code = "SETOF";
            InboundSource expected = InboundSourceFixtures.activeInboundSource();

            given(queryPort.findByCode(code)).willReturn(Optional.of(expected));

            // when
            InboundSource result = sut.findByCode(code);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findByCode(code);
        }

        @Test
        @DisplayName("존재하지 않는 코드로 조회 시 예외가 발생한다")
        void findByCode_NotExists_ThrowsException() {
            // given
            String code = "NOT_EXIST";

            given(queryPort.findByCode(code)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.findByCode(code))
                    .isInstanceOf(InboundSourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findByCriteria() - 검색 조건으로 조회")
    class FindByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 InboundSource 목록을 조회한다")
        void findByCriteria_ReturnsSources() {
            // given
            var criteria = Mockito.mock(InboundSourceSearchCriteria.class);
            List<InboundSource> expected = List.of(InboundSourceFixtures.activeInboundSource());

            given(queryPort.findByCriteria(criteria)).willReturn(expected);

            // when
            List<InboundSource> result = sut.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            then(queryPort).should().findByCriteria(criteria);
        }
    }

    @Nested
    @DisplayName("countByCriteria() - 검색 조건으로 카운트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 InboundSource 수를 반환한다")
        void countByCriteria_ReturnsCount() {
            // given
            var criteria = Mockito.mock(InboundSourceSearchCriteria.class);
            long expected = 3L;

            given(queryPort.countByCriteria(criteria)).willReturn(expected);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().countByCriteria(criteria);
        }
    }
}
