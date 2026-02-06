package com.ryuqq.marketplace.adapter.in.rest.selleradmin.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.query.SearchSellerAdminApplicationsApiRequest;
import com.ryuqq.marketplace.application.selleradmin.dto.query.GetSellerAdminApplicationQuery;
import com.ryuqq.marketplace.application.selleradmin.dto.query.SellerAdminApplicationSearchParams;
import com.ryuqq.marketplace.domain.selleradmin.vo.SellerAdminStatus;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SellerAdminApplicationQueryApiMapper 단위 테스트")
class SellerAdminApplicationQueryApiMapperTest {

    private SellerAdminApplicationQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SellerAdminApplicationQueryApiMapper();
    }

    @Nested
    @DisplayName("toGetQuery() - 상세 조회 Query 변환")
    class ToGetQueryTest {

        @Test
        @DisplayName("sellerAdminId를 GetQuery로 변환한다")
        void toGetQuery_ConvertsId_ReturnsGetQuery() {
            // given
            String sellerAdminId = "01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f60";

            // when
            GetSellerAdminApplicationQuery result = mapper.toGetQuery(sellerAdminId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.sellerAdminId()).isEqualTo(sellerAdminId);
        }
    }

    @Nested
    @DisplayName("toSearchParams() - 검색 요청 변환")
    class ToSearchParamsTest {

        @Test
        @DisplayName("SearchApiRequest를 SearchParams로 변환한다")
        void toSearchParams_ConvertsRequest_ReturnsSearchParams() {
            // given
            SearchSellerAdminApplicationsApiRequest request =
                    new SearchSellerAdminApplicationsApiRequest(
                            List.of(1L, 2L),
                            List.of("PENDING_APPROVAL", "ACTIVE"),
                            "loginId",
                            "admin@example.com",
                            "createdAt",
                            "DESC",
                            "2025-01-01",
                            "2025-12-31",
                            0,
                            20);

            // when
            SellerAdminApplicationSearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.sellerIds()).containsExactly(1L, 2L);
            assertThat(result.status())
                    .containsExactly(SellerAdminStatus.PENDING_APPROVAL, SellerAdminStatus.ACTIVE);
            assertThat(result.searchField()).isEqualTo("loginId");
            assertThat(result.searchWord()).isEqualTo("admin@example.com");
            assertThat(result.dateRange()).isNotNull();
            assertThat(result.dateRange().startDate()).isEqualTo(LocalDate.of(2025, 1, 1));
            assertThat(result.dateRange().endDate()).isEqualTo(LocalDate.of(2025, 12, 31));
            assertThat(result.commonSearchParams().sortKey()).isEqualTo("createdAt");
            assertThat(result.commonSearchParams().sortDirection()).isEqualTo("DESC");
            assertThat(result.commonSearchParams().page()).isZero();
            assertThat(result.commonSearchParams().size()).isEqualTo(20);
        }

        @Test
        @DisplayName("null 값이 있으면 기본값으로 변환한다")
        void toSearchParams_NullValues_UsesDefaults() {
            // given
            SearchSellerAdminApplicationsApiRequest request =
                    new SearchSellerAdminApplicationsApiRequest(
                            null, null, null, null, null, null, null, null, null, null);

            // when
            SellerAdminApplicationSearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.sellerIds()).isNull();
            assertThat(result.status()).isEmpty();
            assertThat(result.searchField()).isNull();
            assertThat(result.searchWord()).isNull();
            assertThat(result.dateRange()).isNull();
            assertThat(result.commonSearchParams().sortKey()).isEqualTo("createdAt");
            assertThat(result.commonSearchParams().sortDirection()).isEqualTo("DESC");
            assertThat(result.commonSearchParams().page()).isZero();
            assertThat(result.commonSearchParams().size()).isEqualTo(20);
        }

        @Test
        @DisplayName("날짜 범위가 부분적으로만 있어도 변환된다")
        void toSearchParams_PartialDateRange_ConvertsSuccessfully() {
            // given - startDate만 있는 경우
            SearchSellerAdminApplicationsApiRequest request1 =
                    new SearchSellerAdminApplicationsApiRequest(
                            null, null, null, null, null, null, "2025-01-01", null, null, null);

            // when
            SellerAdminApplicationSearchParams result1 = mapper.toSearchParams(request1);

            // then
            assertThat(result1.dateRange()).isNotNull();
            assertThat(result1.dateRange().startDate()).isEqualTo(LocalDate.of(2025, 1, 1));
            assertThat(result1.dateRange().endDate()).isNull();

            // given - endDate만 있는 경우
            SearchSellerAdminApplicationsApiRequest request2 =
                    new SearchSellerAdminApplicationsApiRequest(
                            null, null, null, null, null, null, null, "2025-12-31", null, null);

            // when
            SellerAdminApplicationSearchParams result2 = mapper.toSearchParams(request2);

            // then
            assertThat(result2.dateRange()).isNotNull();
            assertThat(result2.dateRange().startDate()).isNull();
            assertThat(result2.dateRange().endDate()).isEqualTo(LocalDate.of(2025, 12, 31));
        }

        @Test
        @DisplayName("잘못된 날짜 형식은 무시된다")
        void toSearchParams_InvalidDateFormat_IgnoresDate() {
            // given
            SearchSellerAdminApplicationsApiRequest request =
                    new SearchSellerAdminApplicationsApiRequest(
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            "invalid-date",
                            "2025/12/31",
                            null,
                            null);

            // when
            SellerAdminApplicationSearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.dateRange()).isNull();
        }

        @Test
        @DisplayName("잘못된 status는 무시된다")
        void toSearchParams_InvalidStatus_IgnoresInvalidValues() {
            // given
            SearchSellerAdminApplicationsApiRequest request =
                    new SearchSellerAdminApplicationsApiRequest(
                            null,
                            List.of("PENDING_APPROVAL", "INVALID_STATUS", "ACTIVE"),
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null);

            // when
            SellerAdminApplicationSearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.status())
                    .containsExactlyInAnyOrder(
                            SellerAdminStatus.PENDING_APPROVAL, SellerAdminStatus.ACTIVE);
        }

        @Test
        @DisplayName("빈 문자열 status는 무시된다")
        void toSearchParams_BlankStatus_IgnoresBlankValues() {
            // given
            SearchSellerAdminApplicationsApiRequest request =
                    new SearchSellerAdminApplicationsApiRequest(
                            null,
                            List.of("PENDING_APPROVAL", "", "   ", "ACTIVE"),
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null);

            // when
            SellerAdminApplicationSearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.status())
                    .containsExactlyInAnyOrder(
                            SellerAdminStatus.PENDING_APPROVAL, SellerAdminStatus.ACTIVE);
        }
    }
}
