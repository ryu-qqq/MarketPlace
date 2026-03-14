package com.ryuqq.marketplace.adapter.out.client.setof.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupUpdateRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.mapper.SetofCommerceProductMapper;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailBundle;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeQueryResult;
import com.ryuqq.marketplace.domain.outboundsync.vo.ChangedArea;
import com.ryuqq.marketplace.domain.product.ProductFixtures;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.sellersaleschannel.SellerSalesChannelFixtures;
import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SetofFullProductUpdateExecutor 단위 테스트")
class SetofFullProductUpdateExecutorTest {

    @InjectMocks private SetofFullProductUpdateExecutor sut;

    @Mock private RestClient restClient;
    @Mock private SetofCommerceProductMapper mapper;

    @Nested
    @DisplayName("supports()")
    class SupportsTest {

        @Test
        @DisplayName("changedAreas가 null이면 true 반환")
        void nullReturnTrue() {
            assertThat(sut.supports(null)).isTrue();
        }

        @Test
        @DisplayName("changedAreas가 비어있으면 true 반환")
        void emptyReturnTrue() {
            assertThat(sut.supports(Set.of())).isTrue();
        }

        @Test
        @DisplayName("changedAreas가 비어있지 않으면 false 반환")
        void nonEmptyReturnFalse() {
            assertThat(sut.supports(EnumSet.of(ChangedArea.PRICE))).isFalse();
        }
    }

    @Nested
    @DisplayName("execute()")
    class ExecuteTest {

        @Test
        @DisplayName("전체 수정 요청을 PUT으로 호출한다")
        void executeSendsFullUpdateRequest() {
            // given
            var bundle = createBundle();
            Long externalCategoryId = 500L;
            Long externalBrandId = 600L;
            String externalProductId = "12345";

            var updateRequest =
                    new SetofProductGroupUpdateRequest(
                            "테스트", null, null, null, null, null, 0, 0, null, null, null, null,
                            null);
            given(mapper.toUpdateRequest(bundle, externalCategoryId, externalBrandId))
                    .willReturn(updateRequest);

            var requestHeadersUriSpec = mock(RestClient.RequestBodyUriSpec.class);
            var requestBodySpec = mock(RestClient.RequestBodySpec.class);
            var responseSpec = mock(RestClient.ResponseSpec.class);

            given(restClient.put()).willReturn(requestHeadersUriSpec);
            given(
                            requestHeadersUriSpec.uri(
                                    eq("/api/v2/admin/product-groups/{productGroupId}"),
                                    eq(externalProductId)))
                    .willReturn(requestBodySpec);
            given(requestBodySpec.contentType(MediaType.APPLICATION_JSON))
                    .willReturn(requestBodySpec);
            given(requestBodySpec.body(updateRequest)).willReturn(requestBodySpec);
            given(requestBodySpec.retrieve()).willReturn(responseSpec);
            given(responseSpec.toBodilessEntity()).willReturn(null);

            // when
            sut.execute(
                    bundle,
                    externalCategoryId,
                    externalBrandId,
                    externalProductId,
                    SellerSalesChannelFixtures.connectedSellerSalesChannel(),
                    null);

            // then
            verify(mapper).toUpdateRequest(bundle, externalCategoryId, externalBrandId);
            verify(restClient).put();
        }
    }

    private ProductGroupDetailBundle createBundle() {
        var queryResult =
                new ProductGroupDetailCompositeQueryResult(
                        1L,
                        1L,
                        "테스트셀러",
                        100L,
                        "테스트브랜드",
                        200L,
                        "테스트카테고리",
                        "상의 > 긴팔",
                        "1/200",
                        "테스트 상품 그룹",
                        "NONE",
                        "ACTIVE",
                        Instant.now(),
                        Instant.now(),
                        null,
                        null);
        return new ProductGroupDetailBundle(
                queryResult,
                ProductGroupFixtures.activeProductGroup(),
                List.of(ProductFixtures.activeProduct()),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Map.of());
    }
}
