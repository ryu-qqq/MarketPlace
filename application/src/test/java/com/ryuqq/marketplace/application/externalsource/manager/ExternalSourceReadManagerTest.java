package com.ryuqq.marketplace.application.externalsource.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.externalsource.port.out.query.ExternalSourceQueryPort;
import com.ryuqq.marketplace.domain.externalsource.ExternalSourceFixtures;
import com.ryuqq.marketplace.domain.externalsource.aggregate.ExternalSource;
import com.ryuqq.marketplace.domain.externalsource.exception.ExternalSourceNotFoundException;
import com.ryuqq.marketplace.domain.externalsource.id.ExternalSourceId;
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
@DisplayName("ExternalSourceReadManager 단위 테스트")
class ExternalSourceReadManagerTest {

    @InjectMocks private ExternalSourceReadManager sut;

    @Mock private ExternalSourceQueryPort queryPort;

    @Nested
    @DisplayName("getById() - ID로 조회")
    class GetByIdTest {

        @Test
        @DisplayName("존재하는 ID로 ExternalSource를 조회한다")
        void getById_Exists_ReturnsExternalSource() {
            // given
            long id = 1L;
            ExternalSourceId externalSourceId = ExternalSourceId.of(id);
            ExternalSource expected = ExternalSourceFixtures.activeExternalSource(id);

            given(queryPort.findById(externalSourceId)).willReturn(Optional.of(expected));

            // when
            ExternalSource result = sut.getById(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findById(externalSourceId);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 예외가 발생한다")
        void getById_NotExists_ThrowsException() {
            // given
            long id = 999L;
            ExternalSourceId externalSourceId = ExternalSourceId.of(id);

            given(queryPort.findById(externalSourceId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(id))
                    .isInstanceOf(ExternalSourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findByCode() - 코드로 조회")
    class FindByCodeTest {

        @Test
        @DisplayName("존재하는 코드로 ExternalSource를 조회한다")
        void findByCode_Exists_ReturnsExternalSource() {
            // given
            String code = "SETOF";
            ExternalSource expected = ExternalSourceFixtures.activeExternalSource();

            given(queryPort.findByCode(code)).willReturn(Optional.of(expected));

            // when
            ExternalSource result = sut.findByCode(code);

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
                    .isInstanceOf(ExternalSourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findByCriteria() - 검색 조건으로 조회")
    class FindByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 ExternalSource 목록을 조회한다")
        void findByCriteria_ReturnsSources() {
            // given
            var criteria =
                    Mockito.mock(
                            com.ryuqq.marketplace.domain.externalsource.query
                                    .ExternalSourceSearchCriteria.class);
            List<ExternalSource> expected = List.of(ExternalSourceFixtures.activeExternalSource());

            given(queryPort.findByCriteria(criteria)).willReturn(expected);

            // when
            List<ExternalSource> result = sut.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            then(queryPort).should().findByCriteria(criteria);
        }
    }

    @Nested
    @DisplayName("countByCriteria() - 검색 조건으로 카운트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 ExternalSource 수를 반환한다")
        void countByCriteria_ReturnsCount() {
            // given
            var criteria =
                    Mockito.mock(
                            com.ryuqq.marketplace.domain.externalsource.query
                                    .ExternalSourceSearchCriteria.class);
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
