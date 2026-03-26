package com.ryuqq.marketplace.application.shipment.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.application.shipment.ShipmentQueryFixtures;
import com.ryuqq.marketplace.application.shipment.dto.query.ShipmentSearchParams;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.shipment.query.ShipmentSearchCriteria;
import com.ryuqq.marketplace.domain.shipment.query.ShipmentSortKey;
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
@DisplayName("ShipmentQueryFactory 단위 테스트")
class ShipmentQueryFactoryTest {

    @InjectMocks private ShipmentQueryFactory sut;

    @Mock private CommonVoFactory commonVoFactory;

    @Nested
    @DisplayName("createCriteria() - ShipmentSearchParams → ShipmentSearchCriteria 변환")
    class CreateCriteriaTest {

        @Test
        @DisplayName("기본 검색 파라미터로 ShipmentSearchCriteria를 생성한다")
        void createCriteria_DefaultParams_ReturnsShipmentSearchCriteria() {
            // given
            ShipmentSearchParams params = ShipmentQueryFixtures.searchParams();

            given(commonVoFactory.parseSortDirection(any())).willReturn(SortDirection.DESC);
            given(commonVoFactory.createPageRequest(0, 20)).willReturn(PageRequest.of(0, 20));
            given(commonVoFactory.createQueryContext(any(), any(), any(), anyBoolean()))
                    .willReturn(
                            new QueryContext<>(
                                    ShipmentSortKey.CREATED_AT,
                                    SortDirection.DESC,
                                    PageRequest.of(0, 20),
                                    false));
            given(commonVoFactory.createDateRange(any(), any())).willReturn(null);

            // when
            ShipmentSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.statuses()).isEmpty();
        }

        @Test
        @DisplayName("상태 필터를 포함한 검색 파라미터로 ShipmentSearchCriteria를 생성한다")
        void createCriteria_WithStatusFilter_ReturnsCriteriaWithStatuses() {
            // given
            ShipmentSearchParams params = ShipmentQueryFixtures.searchParamsByStatus("SHIPPED");

            given(commonVoFactory.parseSortDirection(any())).willReturn(SortDirection.DESC);
            given(commonVoFactory.createPageRequest(0, 20)).willReturn(PageRequest.of(0, 20));
            given(commonVoFactory.createQueryContext(any(), any(), any(), anyBoolean()))
                    .willReturn(
                            new QueryContext<>(
                                    ShipmentSortKey.CREATED_AT,
                                    SortDirection.DESC,
                                    PageRequest.of(0, 20),
                                    false));
            given(commonVoFactory.createDateRange(any(), any())).willReturn(null);

            // when
            ShipmentSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result.statuses()).hasSize(1);
        }

        @Test
        @DisplayName("셀러 ID 필터를 포함한 검색 파라미터로 ShipmentSearchCriteria를 생성한다")
        void createCriteria_WithSellerIdFilter_ReturnsCriteriaWithSellerIds() {
            // given
            ShipmentSearchParams params = ShipmentQueryFixtures.searchParamsBySeller(1L);

            given(commonVoFactory.parseSortDirection(any())).willReturn(SortDirection.DESC);
            given(commonVoFactory.createPageRequest(0, 20)).willReturn(PageRequest.of(0, 20));
            given(commonVoFactory.createQueryContext(any(), any(), any(), anyBoolean()))
                    .willReturn(
                            new QueryContext<>(
                                    ShipmentSortKey.CREATED_AT,
                                    SortDirection.DESC,
                                    PageRequest.of(0, 20),
                                    false));
            given(commonVoFactory.createDateRange(any(), any())).willReturn(null);

            // when
            ShipmentSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result.sellerIds()).containsExactly(1L);
        }
    }
}
