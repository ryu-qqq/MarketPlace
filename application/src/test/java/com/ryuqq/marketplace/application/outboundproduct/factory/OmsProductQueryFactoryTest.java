package com.ryuqq.marketplace.application.outboundproduct.factory;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.outboundproduct.OmsProductQueryFixtures;
import com.ryuqq.marketplace.application.outboundproduct.dto.query.OmsProductSearchParams;
import com.ryuqq.marketplace.domain.outboundproduct.query.OmsProductSearchCriteria;
import com.ryuqq.marketplace.domain.outboundproduct.query.OmsProductSearchField;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncStatus;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupStatus;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("OmsProductQueryFactory 단위 테스트")
class OmsProductQueryFactoryTest {

    private OmsProductQueryFactory sut;

    @BeforeEach
    void setUp() {
        sut = new OmsProductQueryFactory();
    }

    @Nested
    @DisplayName("createCriteria() - OmsProductSearchCriteria 생성")
    class CreateCriteriaTest {

        @Test
        @DisplayName("기본 파라미터로 기본 Criteria를 생성한다")
        void createCriteria_DefaultParams_CreatesDefaultCriteria() {
            // given
            OmsProductSearchParams params = OmsProductQueryFixtures.omsProductSearchParams();

            // when
            OmsProductSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.statuses()).isEmpty();
            assertThat(result.syncStatuses()).isEmpty();
            assertThat(result.shopIds()).isEmpty();
            assertThat(result.partnerIds()).isEmpty();
            assertThat(result.searchField()).isNull();
            assertThat(result.searchWord()).isNull();
            assertThat(result.dateRange()).isNull();
            assertThat(result.queryContext()).isNotNull();
        }

        @Test
        @DisplayName("상품 상태 필터를 Criteria에 반영한다")
        void createCriteria_WithStatuses_ReflectsStatuses() {
            // given
            OmsProductSearchParams params =
                    OmsProductQueryFixtures.omsProductSearchParams(List.of("ACTIVE", "INACTIVE"));

            // when
            OmsProductSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result.statuses())
                    .containsExactlyInAnyOrder(
                            ProductGroupStatus.ACTIVE, ProductGroupStatus.INACTIVE);
        }

        @Test
        @DisplayName("유효하지 않은 상태 문자열은 무시한다")
        void createCriteria_WithInvalidStatus_IgnoresInvalidStatus() {
            // given
            OmsProductSearchParams params =
                    OmsProductQueryFixtures.omsProductSearchParams(
                            List.of("ACTIVE", "INVALID_STATUS"));

            // when
            OmsProductSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result.statuses()).containsExactly(ProductGroupStatus.ACTIVE);
        }

        @Test
        @DisplayName("연동 상태 필터를 Criteria에 반영한다")
        void createCriteria_WithSyncStatuses_ReflectsSyncStatuses() {
            // given
            OmsProductSearchParams params =
                    OmsProductQueryFixtures.omsProductSearchParams(
                            List.of("ACTIVE"), List.of("COMPLETED", "FAILED"));

            // when
            OmsProductSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result.syncStatuses())
                    .containsExactlyInAnyOrder(SyncStatus.COMPLETED, SyncStatus.FAILED);
        }

        @Test
        @DisplayName("유효하지 않은 연동 상태 문자열은 무시한다")
        void createCriteria_WithInvalidSyncStatus_IgnoresInvalidStatus() {
            // given
            OmsProductSearchParams params =
                    OmsProductQueryFixtures.omsProductSearchParams(
                            List.of(), List.of("COMPLETED", "UNKNOWN_STATUS"));

            // when
            OmsProductSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result.syncStatuses()).containsExactly(SyncStatus.COMPLETED);
        }

        @Test
        @DisplayName("검색 필드와 검색어를 Criteria에 반영한다")
        void createCriteria_WithSearchFieldAndWord_ReflectsSearch() {
            // given
            OmsProductSearchParams params =
                    OmsProductQueryFixtures.omsProductSearchParamsWithSearch(
                            "productName", "테스트 상품");

            // when
            OmsProductSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result.searchField()).isEqualTo(OmsProductSearchField.PRODUCT_NAME);
            assertThat(result.searchWord()).isEqualTo("테스트 상품");
        }

        @Test
        @DisplayName("PRODUCT_CODE 검색 필드를 변환한다")
        void createCriteria_WithProductCodeSearchField_ReflectsSearchField() {
            // given
            OmsProductSearchParams params =
                    OmsProductQueryFixtures.omsProductSearchParamsWithSearch(
                            "productCode", "PG-001");

            // when
            OmsProductSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result.searchField()).isEqualTo(OmsProductSearchField.PRODUCT_CODE);
            assertThat(result.searchWord()).isEqualTo("PG-001");
        }

        @Test
        @DisplayName("PARTNER_NAME 검색 필드를 변환한다")
        void createCriteria_WithPartnerNameSearchField_ReflectsSearchField() {
            // given
            OmsProductSearchParams params =
                    OmsProductQueryFixtures.omsProductSearchParamsWithSearch(
                            "partnerName", "테스트 파트너");

            // when
            OmsProductSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result.searchField()).isEqualTo(OmsProductSearchField.PARTNER_NAME);
        }

        @Test
        @DisplayName("알 수 없는 검색 필드는 null로 처리된다")
        void createCriteria_WithUnknownSearchField_ReturnsNullSearchField() {
            // given
            OmsProductSearchParams params =
                    OmsProductQueryFixtures.omsProductSearchParamsWithSearch("unknownField", "검색어");

            // when
            OmsProductSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result.searchField()).isNull();
            assertThat(result.searchWord()).isEqualTo("검색어");
        }

        @Test
        @DisplayName("쇼핑몰 ID 필터를 Criteria에 반영한다")
        void createCriteria_WithShopIds_ReflectsShopIds() {
            // given
            List<Long> shopIds = List.of(1L, 2L, 3L);
            OmsProductSearchParams params =
                    OmsProductQueryFixtures.omsProductSearchParamsWithShops(shopIds);

            // when
            OmsProductSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result.shopIds()).containsExactlyInAnyOrderElementsOf(shopIds);
        }

        @Test
        @DisplayName("상태 목록이 null이면 빈 리스트로 처리된다")
        void createCriteria_NullStatuses_ReturnsEmptyList() {
            // given
            OmsProductSearchParams params =
                    new OmsProductSearchParams(
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            OmsProductQueryFixtures.defaultCommonSearchParams());

            // when
            OmsProductSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result.statuses()).isEmpty();
            assertThat(result.syncStatuses()).isEmpty();
        }

        @Test
        @DisplayName("페이징 및 정렬 컨텍스트가 올바르게 생성된다")
        void createCriteria_PaginationAndSort_CreatesCorrectQueryContext() {
            // given
            OmsProductSearchParams params = OmsProductQueryFixtures.omsProductSearchParams(1, 10);

            // when
            OmsProductSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result.queryContext()).isNotNull();
            assertThat(result.queryContext().page()).isEqualTo(1);
            assertThat(result.queryContext().size()).isEqualTo(10);
        }
    }
}
