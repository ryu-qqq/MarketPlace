package com.ryuqq.marketplace.adapter.in.rest.legacy.product.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.legacy.product.LegacyProductApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreatePriceRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyUpdateProductStockRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.response.LegacyProductFetchResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.LegacyProductGroupApiFixtures;
import com.ryuqq.marketplace.application.legacy.product.dto.command.LegacyUpdatePriceCommand;
import com.ryuqq.marketplace.application.legacy.product.dto.command.LegacyUpdateStockCommand;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("LegacyProductCommandApiMapper 단위 테스트")
class LegacyProductCommandApiMapperTest {

    private LegacyProductCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LegacyProductCommandApiMapper();
    }

    @Nested
    @DisplayName("toPriceCommand - 가격 수정 요청 변환")
    class ToPriceCommandTest {

        @Test
        @DisplayName("LegacyCreatePriceRequest를 LegacyUpdatePriceCommand로 변환한다")
        void toPriceCommand_ConvertsRequest_ReturnsCommand() {
            // given
            long productGroupId = LegacyProductApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            LegacyCreatePriceRequest request = LegacyProductApiFixtures.priceRequest();

            // when
            LegacyUpdatePriceCommand command = mapper.toPriceCommand(productGroupId, request);

            // then
            assertThat(command.productGroupId()).isEqualTo(productGroupId);
            assertThat(command.regularPrice())
                    .isEqualTo(LegacyProductApiFixtures.DEFAULT_REGULAR_PRICE);
            assertThat(command.currentPrice())
                    .isEqualTo(LegacyProductApiFixtures.DEFAULT_CURRENT_PRICE);
        }

        @Test
        @DisplayName("다양한 productGroupId에 대해 올바르게 변환된다")
        void toPriceCommand_DifferentProductGroupId_ReturnsCorrectCommand() {
            // given
            long productGroupId = 999L;
            LegacyCreatePriceRequest request =
                    LegacyProductApiFixtures.priceRequest(60000L, 50000L);

            // when
            LegacyUpdatePriceCommand command = mapper.toPriceCommand(productGroupId, request);

            // then
            assertThat(command.productGroupId()).isEqualTo(999L);
            assertThat(command.regularPrice()).isEqualTo(60000L);
            assertThat(command.currentPrice()).isEqualTo(50000L);
        }
    }

    @Nested
    @DisplayName("toLegacyUpdateStockCommand - 재고 수정 요청 변환")
    class ToLegacyUpdateStockCommandTest {

        @Test
        @DisplayName("LegacyUpdateProductStockRequest 목록을 LegacyUpdateStockCommand로 변환한다")
        void toLegacyUpdateStockCommand_ConvertsRequests_ReturnsCommand() {
            // given
            long productGroupId = LegacyProductApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            List<LegacyUpdateProductStockRequest> requests =
                    LegacyProductApiFixtures.stockRequests();

            // when
            LegacyUpdateStockCommand command =
                    mapper.toLegacyUpdateStockCommand(productGroupId, requests);

            // then
            assertThat(command.productGroupId()).isEqualTo(productGroupId);
            assertThat(command.stockEntries()).hasSize(2);
            assertThat(command.stockEntries().get(0).productId())
                    .isEqualTo(LegacyProductApiFixtures.DEFAULT_PRODUCT_ID_1);
            assertThat(command.stockEntries().get(0).stockQuantity()).isEqualTo(80);
            assertThat(command.stockEntries().get(1).productId())
                    .isEqualTo(LegacyProductApiFixtures.DEFAULT_PRODUCT_ID_2);
            assertThat(command.stockEntries().get(1).stockQuantity()).isEqualTo(40);
        }

        @Test
        @DisplayName("null 요청 목록은 빈 entries를 반환한다")
        void toLegacyUpdateStockCommand_NullRequests_ReturnsEmptyEntries() {
            // given
            long productGroupId = LegacyProductApiFixtures.DEFAULT_PRODUCT_GROUP_ID;

            // when
            LegacyUpdateStockCommand command =
                    mapper.toLegacyUpdateStockCommand(productGroupId, null);

            // then
            assertThat(command.productGroupId()).isEqualTo(productGroupId);
            assertThat(command.stockEntries()).isEmpty();
        }

        @Test
        @DisplayName("빈 요청 목록은 빈 entries를 반환한다")
        void toLegacyUpdateStockCommand_EmptyRequests_ReturnsEmptyEntries() {
            // given
            long productGroupId = LegacyProductApiFixtures.DEFAULT_PRODUCT_GROUP_ID;

            // when
            LegacyUpdateStockCommand command =
                    mapper.toLegacyUpdateStockCommand(productGroupId, List.of());

            // then
            assertThat(command.stockEntries()).isEmpty();
        }
    }

    @Nested
    @DisplayName("toProductFetchResponses - 상품 조회 결과 응답 변환")
    class ToProductFetchResponsesTest {

        @Test
        @DisplayName("LegacyProductGroupDetailResult를 Set<LegacyProductFetchResponse>로 변환한다")
        void toProductFetchResponses_ConvertsResult_ReturnsResponses() {
            // given
            LegacyProductGroupDetailResult result =
                    LegacyProductGroupApiFixtures.productGroupDetailResult();
            Map<Long, Long> emptyMap = Map.of();

            // when
            Set<LegacyProductFetchResponse> responses =
                    mapper.toProductFetchResponses(result, emptyMap);

            // then
            assertThat(responses).hasSize(2);
        }

        @Test
        @DisplayName("internalToExternalMap으로 productId가 올바르게 매핑된다")
        void toProductFetchResponses_WithIdMapping_MapsProductIdCorrectly() {
            // given
            LegacyProductGroupDetailResult result =
                    LegacyProductGroupApiFixtures.productGroupDetailResult();
            long internalId = 2001L;
            long externalId = 9001L;
            Map<Long, Long> idMap = Map.of(internalId, externalId);

            // when
            Set<LegacyProductFetchResponse> responses =
                    mapper.toProductFetchResponses(result, idMap);

            // then
            assertThat(responses).anyMatch(r -> r.productId() == externalId);
        }

        @Test
        @DisplayName("products가 null이면 빈 Set을 반환한다")
        void toProductFetchResponses_NullProducts_ReturnsEmptySet() {
            // given
            LegacyProductGroupDetailResult resultWithNullProducts =
                    new LegacyProductGroupDetailResult(
                            100L,
                            "이름",
                            1L,
                            "셀러",
                            10L,
                            "브랜드",
                            1000L,
                            "경로",
                            "OPTION_ONE",
                            "SETOF",
                            50000L,
                            45000L,
                            45000L,
                            5000L,
                            10,
                            10,
                            false,
                            true,
                            "NEW",
                            "대한민국",
                            "CASUAL",
                            "admin",
                            "admin",
                            java.time.LocalDateTime.now(),
                            java.time.LocalDateTime.now(),
                            null,
                            List.of(),
                            null,
                            null,
                            null);
            Map<Long, Long> emptyMap = Map.of();

            // when
            Set<LegacyProductFetchResponse> responses =
                    mapper.toProductFetchResponses(resultWithNullProducts, emptyMap);

            // then
            assertThat(responses).isEmpty();
        }

        @Test
        @DisplayName("옵션 문자열이 올바르게 구성된다")
        void toProductFetchResponses_BuildsOptionString_Correctly() {
            // given
            LegacyProductGroupDetailResult result =
                    LegacyProductGroupApiFixtures.productGroupDetailResult();
            Map<Long, Long> emptyMap = Map.of();

            // when
            Set<LegacyProductFetchResponse> responses =
                    mapper.toProductFetchResponses(result, emptyMap);

            // then
            assertThat(responses).allMatch(r -> r.option() != null);
        }
    }
}
