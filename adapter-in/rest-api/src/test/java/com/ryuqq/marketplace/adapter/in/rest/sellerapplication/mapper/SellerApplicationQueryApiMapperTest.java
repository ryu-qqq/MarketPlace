package com.ryuqq.marketplace.adapter.in.rest.sellerapplication.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.sellerapplication.SellerApplicationApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.sellerapplication.dto.query.SearchSellerApplicationsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.sellerapplication.dto.response.SellerApplicationApiResponse;
import com.ryuqq.marketplace.application.sellerapplication.dto.query.SellerApplicationSearchParams;
import com.ryuqq.marketplace.application.sellerapplication.dto.response.SellerApplicationPageResult;
import com.ryuqq.marketplace.application.sellerapplication.dto.response.SellerApplicationResult;
import com.ryuqq.marketplace.domain.sellerapplication.vo.ApplicationStatus;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SellerApplicationQueryApiMapper 단위 테스트")
class SellerApplicationQueryApiMapperTest {

    private SellerApplicationQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SellerApplicationQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams() - 검색 요청 변환")
    class ToSearchParamsTest {

        @Test
        @DisplayName("SearchSellerApplicationsApiRequest를 SellerApplicationSearchParams로 변환한다")
        void toSearchParams_ConvertsRequest_ReturnsSearchParams() {
            // given
            SearchSellerApplicationsApiRequest request =
                    SellerApplicationApiFixtures.searchRequest(
                            List.of("PENDING"), "sellerName", "테스트", 0, 20);

            // when
            SellerApplicationSearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.status()).containsExactly(ApplicationStatus.PENDING);
            assertThat(result.searchField()).isEqualTo("sellerName");
            assertThat(result.searchWord()).isEqualTo("테스트");
            assertThat(result.page()).isZero();
            assertThat(result.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("null 필드에 기본값을 적용한다")
        void toSearchParams_NullFields_AppliesDefaults() {
            // given
            SearchSellerApplicationsApiRequest request =
                    SellerApplicationApiFixtures.searchRequest();

            // when
            SellerApplicationSearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.status()).isEmpty();
            assertThat(result.searchField()).isNull();
            assertThat(result.searchWord()).isNull();
            assertThat(result.page()).isZero();
            assertThat(result.size()).isEqualTo(20);
            assertThat(result.sortKey()).isEqualTo("appliedAt");
            assertThat(result.sortDirection()).isEqualTo("DESC");
        }

        @Test
        @DisplayName("여러 상태를 파싱한다")
        void toSearchParams_MultipleStatuses_ParsesAll() {
            // given
            SearchSellerApplicationsApiRequest request =
                    SellerApplicationApiFixtures.searchRequestWithStatus(
                            List.of("PENDING", "APPROVED", "REJECTED"));

            // when
            SellerApplicationSearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.status())
                    .containsExactly(
                            ApplicationStatus.PENDING,
                            ApplicationStatus.APPROVED,
                            ApplicationStatus.REJECTED);
        }

        @Test
        @DisplayName("유효하지 않은 상태 문자열은 무시한다")
        void toSearchParams_InvalidStatus_FiltersOut() {
            // given
            SearchSellerApplicationsApiRequest request =
                    SellerApplicationApiFixtures.searchRequestWithStatus(
                            List.of("PENDING", "INVALID_STATUS", "APPROVED"));

            // when
            SellerApplicationSearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.status())
                    .containsExactly(ApplicationStatus.PENDING, ApplicationStatus.APPROVED);
        }

        @Test
        @DisplayName("빈 상태 목록은 빈 리스트를 반환한다")
        void toSearchParams_EmptyStatusList_ReturnsEmptyList() {
            // given
            SearchSellerApplicationsApiRequest request =
                    SellerApplicationApiFixtures.searchRequestWithStatus(List.of());

            // when
            SellerApplicationSearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.status()).isEmpty();
        }
    }

    @Nested
    @DisplayName("toResponse() - 단일 결과 변환")
    class ToResponseTest {

        @Test
        @DisplayName("SellerApplicationResult를 SellerApplicationApiResponse로 변환한다")
        void toResponse_ConvertsResult_ReturnsApiResponse() {
            // given
            SellerApplicationResult result = SellerApplicationApiFixtures.applicationResult(1L);

            // when
            SellerApplicationApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.status()).isEqualTo("PENDING");
            assertThat(response.sellerInfo().sellerName())
                    .isEqualTo(SellerApplicationApiFixtures.DEFAULT_SELLER_NAME);
            assertThat(response.sellerInfo().displayName())
                    .isEqualTo(SellerApplicationApiFixtures.DEFAULT_DISPLAY_NAME);
            assertThat(response.businessInfo().registrationNumber())
                    .isEqualTo(SellerApplicationApiFixtures.DEFAULT_REGISTRATION_NUMBER);
            assertThat(response.csContact().phone())
                    .isEqualTo(SellerApplicationApiFixtures.DEFAULT_PHONE);
        }

        @Test
        @DisplayName("날짜를 ISO 8601 형식으로 변환한다")
        void toResponse_ConvertsDate_ReturnsIso8601Format() {
            // given
            SellerApplicationResult result = SellerApplicationApiFixtures.applicationResult(1L);

            // when
            SellerApplicationApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.appliedAt()).isNotNull();
            assertThat(response.appliedAt()).contains("2025-01-23");
            assertThat(response.appliedAt()).contains("+09:00");
        }

        @Test
        @DisplayName("processedAt이 null이면 null을 반환한다")
        void toResponse_NullProcessedAt_ReturnsNull() {
            // given
            SellerApplicationResult result = SellerApplicationApiFixtures.applicationResult(1L);

            // when
            SellerApplicationApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.processedAt()).isNull();
        }

        @Test
        @DisplayName("승인 결과를 변환한다")
        void toResponse_ApprovedResult_ConvertsCorrectly() {
            // given
            SellerApplicationResult result = SellerApplicationApiFixtures.approvedResult(1L, 100L);

            // when
            SellerApplicationApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.status()).isEqualTo("APPROVED");
            assertThat(response.approvedSellerId()).isEqualTo(100L);
            assertThat(response.processedBy()).isEqualTo("admin@example.com");
            assertThat(response.processedAt()).isNotNull();
            assertThat(response.rejectionReason()).isNull();
        }

        @Test
        @DisplayName("거절 결과를 변환한다")
        void toResponse_RejectedResult_ConvertsCorrectly() {
            // given
            SellerApplicationResult result = SellerApplicationApiFixtures.rejectedResult(1L);

            // when
            SellerApplicationApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.status()).isEqualTo("REJECTED");
            assertThat(response.rejectionReason())
                    .isEqualTo(SellerApplicationApiFixtures.DEFAULT_REJECTION_REASON);
            assertThat(response.processedBy()).isEqualTo("admin@example.com");
            assertThat(response.approvedSellerId()).isNull();
        }

        @Test
        @DisplayName("하위 객체가 null이면 null을 반환한다")
        void toResponse_NullSubObjects_ReturnsNulls() {
            // given
            SellerApplicationResult result =
                    SellerApplicationApiFixtures.resultWithNullSubObjects(1L);

            // when
            SellerApplicationApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.sellerInfo()).isNull();
            assertThat(response.businessInfo()).isNull();
            assertThat(response.csContact()).isNull();
            assertThat(response.agreement()).isNull();
        }

        @Test
        @DisplayName("동의 정보의 날짜를 ISO 8601로 변환한다")
        void toResponse_AgreementDate_ConvertsToIso8601() {
            // given
            SellerApplicationResult result = SellerApplicationApiFixtures.applicationResult(1L);

            // when
            SellerApplicationApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.agreement()).isNotNull();
            assertThat(response.agreement().agreedAt()).contains("2025-01-23");
            assertThat(response.agreement().termsAgreed()).isTrue();
            assertThat(response.agreement().privacyAgreed()).isTrue();
        }
    }

    @Nested
    @DisplayName("toResponses() - 목록 변환")
    class ToResponsesTest {

        @Test
        @DisplayName("결과 목록을 응답 목록으로 변환한다")
        void toResponses_ConvertsResults_ReturnsApiResponses() {
            // given
            List<SellerApplicationResult> results =
                    SellerApplicationApiFixtures.applicationResults(3);

            // when
            List<SellerApplicationApiResponse> responses = mapper.toResponses(results);

            // then
            assertThat(responses).hasSize(3);
            assertThat(responses.get(0).id()).isEqualTo(1L);
            assertThat(responses.get(1).id()).isEqualTo(2L);
            assertThat(responses.get(2).id()).isEqualTo(3L);
        }

        @Test
        @DisplayName("빈 목록은 빈 응답 목록을 반환한다")
        void toResponses_EmptyList_ReturnsEmptyList() {
            // given
            List<SellerApplicationResult> results = List.of();

            // when
            List<SellerApplicationApiResponse> responses = mapper.toResponses(results);

            // then
            assertThat(responses).isEmpty();
        }
    }

    @Nested
    @DisplayName("toPageResponse() - 페이지 변환")
    class ToPageResponseTest {

        @Test
        @DisplayName("SellerApplicationPageResult를 PageApiResponse로 변환한다")
        void toPageResponse_ConvertsPageResult_ReturnsPageApiResponse() {
            // given
            SellerApplicationPageResult pageResult =
                    SellerApplicationApiFixtures.pageResult(5, 0, 20);

            // when
            PageApiResponse<SellerApplicationApiResponse> response =
                    mapper.toPageResponse(pageResult);

            // then
            assertThat(response.content()).hasSize(5);
            assertThat(response.page()).isZero();
            assertThat(response.size()).isEqualTo(20);
            assertThat(response.totalElements()).isEqualTo(5);
        }

        @Test
        @DisplayName("빈 페이지 결과를 변환한다")
        void toPageResponse_EmptyPageResult_ReturnsEmptyPage() {
            // given
            SellerApplicationPageResult pageResult = SellerApplicationApiFixtures.emptyPageResult();

            // when
            PageApiResponse<SellerApplicationApiResponse> response =
                    mapper.toPageResponse(pageResult);

            // then
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isZero();
        }
    }
}
