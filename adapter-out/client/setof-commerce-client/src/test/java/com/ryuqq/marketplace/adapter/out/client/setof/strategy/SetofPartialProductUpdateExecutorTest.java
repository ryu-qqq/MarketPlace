package com.ryuqq.marketplace.adapter.out.client.setof.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.ryuqq.marketplace.adapter.out.client.setof.adapter.SetofCommerceBasicInfoAdapter;
import com.ryuqq.marketplace.adapter.out.client.setof.adapter.SetofCommerceDescriptionAdapter;
import com.ryuqq.marketplace.adapter.out.client.setof.adapter.SetofCommerceImageAdapter;
import com.ryuqq.marketplace.adapter.out.client.setof.adapter.SetofCommerceNoticeAdapter;
import com.ryuqq.marketplace.adapter.out.client.setof.adapter.SetofCommerceProductAdapter;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofDescriptionRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofImagesRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofNoticeRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupBasicInfoUpdateRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductsUpdateRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.mapper.SetofCommerceProductMapper;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailBundle;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeQueryResult;
import com.ryuqq.marketplace.domain.outboundsync.vo.ChangedArea;
import com.ryuqq.marketplace.domain.product.ProductFixtures;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productnotice.ProductNoticeFixtures;
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

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SetofPartialProductUpdateExecutor 단위 테스트")
class SetofPartialProductUpdateExecutorTest {

    @InjectMocks private SetofPartialProductUpdateExecutor sut;

    @Mock private SetofCommerceBasicInfoAdapter basicInfoAdapter;
    @Mock private SetofCommerceProductAdapter productAdapter;
    @Mock private SetofCommerceImageAdapter imageAdapter;
    @Mock private SetofCommerceDescriptionAdapter descriptionAdapter;
    @Mock private SetofCommerceNoticeAdapter noticeAdapter;
    @Mock private SetofCommerceProductMapper mapper;

    @Nested
    @DisplayName("supports()")
    class SupportsTest {

        @Test
        @DisplayName("changedAreas가 null이면 false 반환")
        void nullReturnFalse() {
            assertThat(sut.supports(null)).isFalse();
        }

        @Test
        @DisplayName("changedAreas가 비어있으면 false 반환")
        void emptyReturnFalse() {
            assertThat(sut.supports(Set.of())).isFalse();
        }

        @Test
        @DisplayName("changedAreas가 비어있지 않으면 true 반환")
        void nonEmptyReturnTrue() {
            assertThat(sut.supports(EnumSet.of(ChangedArea.PRICE))).isTrue();
        }

        @Test
        @DisplayName("changedAreas에 여러 영역이 있어도 true 반환")
        void multipleAreasReturnTrue() {
            assertThat(
                            sut.supports(
                                    EnumSet.of(
                                            ChangedArea.PRICE,
                                            ChangedArea.STOCK,
                                            ChangedArea.IMAGE)))
                    .isTrue();
        }
    }

    @Nested
    @DisplayName("execute()")
    class ExecuteTest {

        @Test
        @DisplayName("BASIC_INFO 변경 시 basicInfoAdapter만 호출한다")
        void basicInfoOnly() {
            var bundle = createBundle();
            var request =
                    new SetofProductGroupBasicInfoUpdateRequest("테스트", 600L, 500L, null, null);
            given(mapper.toBasicInfoUpdateRequest(bundle, 500L, 600L)).willReturn(request);

            sut.execute(
                    bundle,
                    500L,
                    600L,
                    "12345",
                    SellerSalesChannelFixtures.connectedSellerSalesChannel(),
                    EnumSet.of(ChangedArea.BASIC_INFO),
                    null);

            verify(basicInfoAdapter).updateBasicInfo("12345", request);
            verify(productAdapter, never()).updateProducts(any(), any());
            verify(imageAdapter, never()).updateImages(any(), any());
            verify(descriptionAdapter, never()).updateDescription(any(), any());
            verify(noticeAdapter, never()).updateNotice(any(), any());
        }

        @Test
        @DisplayName("PRICE 변경 시 productAdapter를 호출한다")
        void priceChangedCallsProductAdapter() {
            var bundle = createBundle();
            var request = new SetofProductsUpdateRequest(List.of(), List.of());
            given(
                            mapper.toProductsUpdateRequest(
                                    bundle.products(), bundle.group().sellerOptionGroups(), null))
                    .willReturn(request);

            sut.execute(
                    bundle,
                    500L,
                    600L,
                    "12345",
                    SellerSalesChannelFixtures.connectedSellerSalesChannel(),
                    EnumSet.of(ChangedArea.PRICE),
                    null);

            verify(productAdapter).updateProducts(12345L, request);
        }

        @Test
        @DisplayName("STOCK 변경 시 productAdapter를 호출한다")
        void stockChangedCallsProductAdapter() {
            var bundle = createBundle();
            var request = new SetofProductsUpdateRequest(List.of(), List.of());
            given(
                            mapper.toProductsUpdateRequest(
                                    bundle.products(), bundle.group().sellerOptionGroups(), null))
                    .willReturn(request);

            sut.execute(
                    bundle,
                    500L,
                    600L,
                    "12345",
                    SellerSalesChannelFixtures.connectedSellerSalesChannel(),
                    EnumSet.of(ChangedArea.STOCK),
                    null);

            verify(productAdapter).updateProducts(12345L, request);
        }

        @Test
        @DisplayName("OPTION 변경 시 productAdapter를 호출한다")
        void optionChangedCallsProductAdapter() {
            var bundle = createBundle();
            var request = new SetofProductsUpdateRequest(List.of(), List.of());
            given(
                            mapper.toProductsUpdateRequest(
                                    bundle.products(), bundle.group().sellerOptionGroups(), null))
                    .willReturn(request);

            sut.execute(
                    bundle,
                    500L,
                    600L,
                    "12345",
                    SellerSalesChannelFixtures.connectedSellerSalesChannel(),
                    EnumSet.of(ChangedArea.OPTION),
                    null);

            verify(productAdapter).updateProducts(12345L, request);
        }

        @Test
        @DisplayName("IMAGE 변경 시 imageAdapter를 호출한다")
        void imageChangedCallsImageAdapter() {
            var bundle = createBundle();
            var request = new SetofImagesRequest(List.of());
            given(mapper.toImagesRequest(bundle.group().images())).willReturn(request);

            sut.execute(
                    bundle,
                    500L,
                    600L,
                    "12345",
                    SellerSalesChannelFixtures.connectedSellerSalesChannel(),
                    EnumSet.of(ChangedArea.IMAGE),
                    null);

            verify(imageAdapter).updateImages(12345L, request);
        }

        @Test
        @DisplayName("DESCRIPTION 변경 시 descriptionAdapter를 호출한다")
        void descriptionChangedCallsDescriptionAdapter() {
            var bundle = createBundleWithDescription();
            var request = new SetofDescriptionRequest("<p>상세설명</p>", null);
            given(mapper.toDescriptionRequest(bundle)).willReturn(request);

            sut.execute(
                    bundle,
                    500L,
                    600L,
                    "12345",
                    SellerSalesChannelFixtures.connectedSellerSalesChannel(),
                    EnumSet.of(ChangedArea.DESCRIPTION),
                    null);

            verify(descriptionAdapter).updateDescription(12345L, request);
        }

        @Test
        @DisplayName("DESCRIPTION 변경이지만 description이 null이면 호출하지 않는다")
        void descriptionNullSkipsAdapter() {
            var bundle = createBundle();
            given(mapper.toDescriptionRequest(bundle)).willReturn(null);

            sut.execute(
                    bundle,
                    500L,
                    600L,
                    "12345",
                    SellerSalesChannelFixtures.connectedSellerSalesChannel(),
                    EnumSet.of(ChangedArea.DESCRIPTION),
                    null);

            verify(descriptionAdapter, never()).updateDescription(any(), any());
        }

        @Test
        @DisplayName("NOTICE 변경 시 noticeAdapter를 호출한다")
        void noticeChangedCallsNoticeAdapter() {
            var notice = ProductNoticeFixtures.newProductNotice();
            var bundle = createBundleWithNotice(notice);
            var request = new SetofNoticeRequest(List.of());
            given(mapper.toNoticeRequest(notice, null)).willReturn(request);

            sut.execute(
                    bundle,
                    500L,
                    600L,
                    "12345",
                    SellerSalesChannelFixtures.connectedSellerSalesChannel(),
                    EnumSet.of(ChangedArea.NOTICE),
                    null);

            verify(noticeAdapter).updateNotice(12345L, request);
        }

        @Test
        @DisplayName("NOTICE 변경이지만 notice가 없으면 호출하지 않는다")
        void noticeEmptySkipsAdapter() {
            var bundle = createBundle();

            sut.execute(
                    bundle,
                    500L,
                    600L,
                    "12345",
                    SellerSalesChannelFixtures.connectedSellerSalesChannel(),
                    EnumSet.of(ChangedArea.NOTICE),
                    null);

            verify(noticeAdapter, never()).updateNotice(any(), any());
        }

        @Test
        @DisplayName("여러 영역 변경 시 해당하는 adapter를 모두 호출한다")
        void multipleAreasCallMultipleAdapters() {
            var bundle = createBundle();
            var basicInfoRequest =
                    new SetofProductGroupBasicInfoUpdateRequest("테스트", 600L, 500L, null, null);
            var productsRequest = new SetofProductsUpdateRequest(List.of(), List.of());
            var imagesRequest = new SetofImagesRequest(List.of());

            given(mapper.toBasicInfoUpdateRequest(bundle, 500L, 600L)).willReturn(basicInfoRequest);
            given(
                            mapper.toProductsUpdateRequest(
                                    bundle.products(), bundle.group().sellerOptionGroups(), null))
                    .willReturn(productsRequest);
            given(mapper.toImagesRequest(bundle.group().images())).willReturn(imagesRequest);

            sut.execute(
                    bundle,
                    500L,
                    600L,
                    "12345",
                    SellerSalesChannelFixtures.connectedSellerSalesChannel(),
                    EnumSet.of(ChangedArea.BASIC_INFO, ChangedArea.PRICE, ChangedArea.IMAGE),
                    null);

            verify(basicInfoAdapter).updateBasicInfo("12345", basicInfoRequest);
            verify(productAdapter).updateProducts(12345L, productsRequest);
            verify(imageAdapter).updateImages(12345L, imagesRequest);
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

    private ProductGroupDetailBundle createBundleWithDescription() {
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
                Optional.of(ProductGroupFixtures.defaultProductGroupDescription()),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Map.of());
    }

    private ProductGroupDetailBundle createBundleWithNotice(
            com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice notice) {
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
                Optional.of(notice),
                Optional.empty(),
                Optional.empty(),
                Map.of());
    }
}
