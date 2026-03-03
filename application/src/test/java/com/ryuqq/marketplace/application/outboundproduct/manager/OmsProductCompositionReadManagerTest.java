package com.ryuqq.marketplace.application.outboundproduct.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.outboundproduct.OmsProductQueryFixtures;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.OmsProductListResult;
import com.ryuqq.marketplace.application.outboundproduct.port.out.query.OmsProductCompositionQueryPort;
import com.ryuqq.marketplace.domain.outboundproduct.query.OmsProductSearchCriteria;
import java.util.Collections;
import java.util.List;
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
@DisplayName("OmsProductCompositionReadManager 단위 테스트")
class OmsProductCompositionReadManagerTest {

    @InjectMocks private OmsProductCompositionReadManager sut;

    @Mock private OmsProductCompositionQueryPort compositionQueryPort;

    @Nested
    @DisplayName("findByCriteria() - 검색 조건으로 OMS 상품 목록 조회")
    class FindByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 OMS 상품 목록을 반환한다")
        void findByCriteria_ReturnsProducts() {
            // given
            OmsProductSearchCriteria criteria = OmsProductSearchCriteria.defaultCriteria();
            List<OmsProductListResult> expected =
                    List.of(
                            OmsProductQueryFixtures.omsProductListResult(1L),
                            OmsProductQueryFixtures.omsProductListResult(2L));

            given(compositionQueryPort.findByCriteria(criteria)).willReturn(expected);

            // when
            List<OmsProductListResult> result = sut.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).isEqualTo(expected);
            then(compositionQueryPort).should().findByCriteria(criteria);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 목록을 반환한다")
        void findByCriteria_NoResults_ReturnsEmptyList() {
            // given
            OmsProductSearchCriteria criteria = OmsProductSearchCriteria.defaultCriteria();

            given(compositionQueryPort.findByCriteria(criteria))
                    .willReturn(Collections.emptyList());

            // when
            List<OmsProductListResult> result = sut.findByCriteria(criteria);

            // then
            assertThat(result).isEmpty();
            then(compositionQueryPort).should().findByCriteria(criteria);
        }

        @Test
        @DisplayName("Port에 조회를 위임하고 결과를 그대로 반환한다")
        void findByCriteria_DelegatesToPort() {
            // given
            OmsProductSearchCriteria criteria = OmsProductSearchCriteria.defaultCriteria();
            List<OmsProductListResult> expected =
                    List.of(OmsProductQueryFixtures.omsProductListResult(1L));

            given(compositionQueryPort.findByCriteria(criteria)).willReturn(expected);

            // when
            sut.findByCriteria(criteria);

            // then
            then(compositionQueryPort).should().findByCriteria(criteria);
            then(compositionQueryPort).shouldHaveNoMoreInteractions();
        }
    }

    @Nested
    @DisplayName("countByCriteria() - 검색 조건으로 OMS 상품 개수 조회")
    class CountByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 OMS 상품 개수를 반환한다")
        void countByCriteria_ReturnsCount() {
            // given
            OmsProductSearchCriteria criteria = OmsProductSearchCriteria.defaultCriteria();
            long expected = 42L;

            given(compositionQueryPort.countByCriteria(criteria)).willReturn(expected);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(expected);
            then(compositionQueryPort).should().countByCriteria(criteria);
        }

        @Test
        @DisplayName("검색 결과가 없으면 0을 반환한다")
        void countByCriteria_NoResults_ReturnsZero() {
            // given
            OmsProductSearchCriteria criteria = OmsProductSearchCriteria.defaultCriteria();

            given(compositionQueryPort.countByCriteria(criteria)).willReturn(0L);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isZero();
            then(compositionQueryPort).should().countByCriteria(criteria);
        }

        @Test
        @DisplayName("Port에 카운트 조회를 위임하고 결과를 그대로 반환한다")
        void countByCriteria_DelegatesToPort() {
            // given
            OmsProductSearchCriteria criteria = OmsProductSearchCriteria.defaultCriteria();
            long expected = 100L;

            given(compositionQueryPort.countByCriteria(criteria)).willReturn(expected);

            // when
            sut.countByCriteria(criteria);

            // then
            then(compositionQueryPort).should().countByCriteria(criteria);
            then(compositionQueryPort).shouldHaveNoMoreInteractions();
        }
    }
}
