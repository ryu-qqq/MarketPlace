package com.ryuqq.marketplace.application.saleschannelbrand.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import com.ryuqq.marketplace.application.saleschannelbrand.port.out.query.SalesChannelBrandQueryPort;
import com.ryuqq.marketplace.domain.saleschannelbrand.SalesChannelBrandFixtures;
import com.ryuqq.marketplace.domain.saleschannelbrand.aggregate.SalesChannelBrand;
import com.ryuqq.marketplace.domain.saleschannelbrand.exception.SalesChannelBrandNotFoundException;
import com.ryuqq.marketplace.domain.saleschannelbrand.id.SalesChannelBrandId;
import com.ryuqq.marketplace.domain.saleschannelbrand.query.SalesChannelBrandSearchCriteria;
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
@DisplayName("SalesChannelBrandReadManager 단위 테스트")
class SalesChannelBrandReadManagerTest {

    @InjectMocks private SalesChannelBrandReadManager sut;

    @Mock private SalesChannelBrandQueryPort queryPort;

    @Nested
    @DisplayName("getById() - ID로 조회")
    class GetByIdTest {

        @Test
        @DisplayName("존재하는 ID로 SalesChannelBrand를 조회한다")
        void getById_Exists_ReturnsBrand() {
            // given
            SalesChannelBrandId id = SalesChannelBrandId.of(1L);
            SalesChannelBrand expected = SalesChannelBrandFixtures.activeSalesChannelBrand();

            given(queryPort.findById(id)).willReturn(Optional.of(expected));

            // when
            SalesChannelBrand result = sut.getById(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findById(id);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 예외가 발생한다")
        void getById_NotExists_ThrowsException() {
            // given
            SalesChannelBrandId id = SalesChannelBrandId.of(999L);

            given(queryPort.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(id))
                    .isInstanceOf(SalesChannelBrandNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findByCriteria() - 검색 조건으로 조회")
    class FindByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 SalesChannelBrand 목록을 조회한다")
        void findByCriteria_ReturnsBrands() {
            // given
            SalesChannelBrandSearchCriteria criteria = mock(SalesChannelBrandSearchCriteria.class);
            List<SalesChannelBrand> expected =
                    List.of(SalesChannelBrandFixtures.activeSalesChannelBrand());

            given(queryPort.findByCriteria(criteria)).willReturn(expected);

            // when
            List<SalesChannelBrand> result = sut.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            then(queryPort).should().findByCriteria(criteria);
        }

        @Test
        @DisplayName("검색 조건에 해당하는 브랜드가 없으면 빈 목록을 반환한다")
        void findByCriteria_NoResults_ReturnsEmptyList() {
            // given
            SalesChannelBrandSearchCriteria criteria = mock(SalesChannelBrandSearchCriteria.class);
            List<SalesChannelBrand> expected = List.of();

            given(queryPort.findByCriteria(criteria)).willReturn(expected);

            // when
            List<SalesChannelBrand> result = sut.findByCriteria(criteria);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("countByCriteria() - 검색 조건으로 카운트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 SalesChannelBrand 수를 반환한다")
        void countByCriteria_ReturnsCount() {
            // given
            SalesChannelBrandSearchCriteria criteria = mock(SalesChannelBrandSearchCriteria.class);
            long expected = 10L;

            given(queryPort.countByCriteria(criteria)).willReturn(expected);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().countByCriteria(criteria);
        }

        @Test
        @DisplayName("검색 조건에 해당하는 브랜드가 없으면 0을 반환한다")
        void countByCriteria_NoResults_ReturnsZero() {
            // given
            SalesChannelBrandSearchCriteria criteria = mock(SalesChannelBrandSearchCriteria.class);
            long expected = 0L;

            given(queryPort.countByCriteria(criteria)).willReturn(expected);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isZero();
        }
    }

    @Nested
    @DisplayName("existsBySalesChannelIdAndExternalCode() - 외부 브랜드 코드 존재 여부 확인")
    class ExistsBySalesChannelIdAndExternalCodeTest {

        @Test
        @DisplayName("존재하는 외부 브랜드 코드면 true를 반환한다")
        void existsBySalesChannelIdAndExternalCode_Exists_ReturnsTrue() {
            // given
            Long salesChannelId = 1L;
            String externalBrandCode = "BRAND-001";

            given(queryPort.existsBySalesChannelIdAndExternalCode(
                            salesChannelId, externalBrandCode))
                    .willReturn(true);

            // when
            boolean result =
                    sut.existsBySalesChannelIdAndExternalCode(salesChannelId, externalBrandCode);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 외부 브랜드 코드면 false를 반환한다")
        void existsBySalesChannelIdAndExternalCode_NotExists_ReturnsFalse() {
            // given
            Long salesChannelId = 1L;
            String externalBrandCode = "BRAND-999";

            given(queryPort.existsBySalesChannelIdAndExternalCode(
                            salesChannelId, externalBrandCode))
                    .willReturn(false);

            // when
            boolean result =
                    sut.existsBySalesChannelIdAndExternalCode(salesChannelId, externalBrandCode);

            // then
            assertThat(result).isFalse();
        }
    }
}
