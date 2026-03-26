package com.ryuqq.marketplace.application.outboundproductimage.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.ryuqq.marketplace.application.outboundproductimage.ResolvedExternalImageFixtures;
import com.ryuqq.marketplace.application.outboundproductimage.dto.ResolvedExternalImages;
import com.ryuqq.marketplace.application.outboundproductimage.manager.OutboundProductImageCommandManager;
import com.ryuqq.marketplace.application.outboundproductimage.manager.OutboundProductImageReadManager;
import com.ryuqq.marketplace.application.outboundproductimage.manager.SalesChannelImageClientManager;
import com.ryuqq.marketplace.domain.outboundproductimage.OutboundProductImageFixtures;
import com.ryuqq.marketplace.domain.outboundproductimage.vo.OutboundProductImages;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;
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
@DisplayName("OutboundImageSyncCoordinator 단위 테스트")
class OutboundImageSyncCoordinatorTest {

    @InjectMocks private OutboundImageSyncCoordinator sut;

    @Mock private OutboundProductImageReadManager readManager;
    @Mock private OutboundProductImageCommandManager commandManager;
    @Mock private SalesChannelImageClientManager imageClientManager;

    @Nested
    @DisplayName("syncImages() - 캐시 없음, 전부 새 이미지")
    class SyncImages_AllNew {

        @Test
        @DisplayName("캐시가 없고 현재 이미지가 있으면 전부 업로드하고 persist한다")
        void syncImages_NoCacheWithCurrentImages_UploadsAndPersistsAll() {
            // given
            Long outboundProductId = OutboundProductImageFixtures.DEFAULT_OUTBOUND_PRODUCT_ID;
            String channelCode = ResolvedExternalImageFixtures.DEFAULT_CHANNEL_CODE;
            List<ProductGroupImage> currentImages =
                    List.of(
                            ProductGroupFixtures.thumbnailImage(),
                            ProductGroupFixtures.detailImage(1));

            OutboundProductImages emptyCache = OutboundProductImageFixtures.emptyImages();
            given(readManager.findByOutboundProductId(outboundProductId)).willReturn(emptyCache);

            List<String> externalUrls =
                    List.of(
                            OutboundProductImageFixtures.DEFAULT_EXTERNAL_URL,
                            "https://cdn.naver.com/detail1.jpg");
            given(imageClientManager.uploadImages(eq(channelCode), anyList()))
                    .willReturn(externalUrls);

            // when
            ResolvedExternalImages result =
                    sut.syncImages(outboundProductId, channelCode, currentImages);

            // then
            assertThat(result).isNotNull();
            assertThat(result.isEmpty()).isFalse();
            then(imageClientManager).should().uploadImages(eq(channelCode), anyList());
            then(commandManager).should().persistAll(anyList());
        }

        @Test
        @DisplayName("캐시가 없고 현재 이미지가 없으면 업로드와 persist를 호출하지 않는다")
        void syncImages_NoCacheAndNoCurrentImages_DoesNotUploadOrPersist() {
            // given
            Long outboundProductId = OutboundProductImageFixtures.DEFAULT_OUTBOUND_PRODUCT_ID;
            String channelCode = ResolvedExternalImageFixtures.DEFAULT_CHANNEL_CODE;
            List<ProductGroupImage> currentImages = List.of();

            OutboundProductImages emptyCache = OutboundProductImageFixtures.emptyImages();
            given(readManager.findByOutboundProductId(outboundProductId)).willReturn(emptyCache);

            // when
            ResolvedExternalImages result =
                    sut.syncImages(outboundProductId, channelCode, currentImages);

            // then
            assertThat(result.isEmpty()).isTrue();
            then(imageClientManager).shouldHaveNoInteractions();
            then(commandManager).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("syncImages() - 캐시와 동일한 이미지 (hasNoChanges)")
    class SyncImages_NoChanges {

        @Test
        @DisplayName("캐시와 현재 이미지가 동일하면 업로드와 persist를 호출하지 않는다")
        void syncImages_SameAsCache_DoesNotUploadOrPersist() {
            // given
            Long outboundProductId = OutboundProductImageFixtures.DEFAULT_OUTBOUND_PRODUCT_ID;
            String channelCode = ResolvedExternalImageFixtures.DEFAULT_CHANNEL_CODE;

            // 캐시된 썸네일 이미지의 originUrl과 동일한 이미지 (uploadedUrl 사용)
            List<ProductGroupImage> currentImages =
                    List.of(
                            buildUploadedThumbnailImage(
                                    OutboundProductImageFixtures.DEFAULT_ORIGIN_URL));

            OutboundProductImages cachedImages = OutboundProductImageFixtures.thumbnailOnlyImages();
            given(readManager.findByOutboundProductId(outboundProductId)).willReturn(cachedImages);

            // when
            ResolvedExternalImages result =
                    sut.syncImages(outboundProductId, channelCode, currentImages);

            // then
            then(imageClientManager).shouldHaveNoInteractions();
            then(commandManager).should(never()).persistAll(anyList());
        }

        @Test
        @DisplayName("변경이 없으면 캐시된 external URL이 반환된다")
        void syncImages_NoChanges_ReturnsCachedExternalUrls() {
            // given
            Long outboundProductId = OutboundProductImageFixtures.DEFAULT_OUTBOUND_PRODUCT_ID;
            String channelCode = ResolvedExternalImageFixtures.DEFAULT_CHANNEL_CODE;

            List<ProductGroupImage> currentImages =
                    List.of(
                            buildUploadedThumbnailImage(
                                    OutboundProductImageFixtures.DEFAULT_ORIGIN_URL));

            OutboundProductImages cachedImages = OutboundProductImageFixtures.thumbnailOnlyImages();
            given(readManager.findByOutboundProductId(outboundProductId)).willReturn(cachedImages);

            // when
            ResolvedExternalImages result =
                    sut.syncImages(outboundProductId, channelCode, currentImages);

            // then
            assertThat(result.thumbnailUrl())
                    .isEqualTo(OutboundProductImageFixtures.DEFAULT_EXTERNAL_URL);
        }
    }

    @Nested
    @DisplayName("syncImages() - 일부 변경")
    class SyncImages_PartialChanges {

        @Test
        @DisplayName("added 이미지만 있으면 업로드 후 persist하고 removed persist는 없다")
        void syncImages_OnlyAdded_UploadsAndPersistsAddedOnly() {
            // given
            Long outboundProductId = OutboundProductImageFixtures.DEFAULT_OUTBOUND_PRODUCT_ID;
            String channelCode = ResolvedExternalImageFixtures.DEFAULT_CHANNEL_CODE;

            // 캐시에 없는 새 이미지
            List<ProductGroupImage> currentImages = List.of(ProductGroupFixtures.thumbnailImage());

            OutboundProductImages emptyCache = OutboundProductImageFixtures.emptyImages();
            given(readManager.findByOutboundProductId(outboundProductId)).willReturn(emptyCache);

            List<String> externalUrls = List.of(OutboundProductImageFixtures.DEFAULT_EXTERNAL_URL);
            given(imageClientManager.uploadImages(eq(channelCode), anyList()))
                    .willReturn(externalUrls);

            // when
            sut.syncImages(outboundProductId, channelCode, currentImages);

            // then
            then(imageClientManager).should().uploadImages(eq(channelCode), anyList());
            then(commandManager).should().persistAll(anyList());
        }

        @Test
        @DisplayName("removed 이미지만 있으면 soft delete persist하고 업로드는 없다")
        void syncImages_OnlyRemoved_PersistsRemovedWithoutUpload() {
            // given
            Long outboundProductId = OutboundProductImageFixtures.DEFAULT_OUTBOUND_PRODUCT_ID;
            String channelCode = ResolvedExternalImageFixtures.DEFAULT_CHANNEL_CODE;

            // 현재 이미지 없음 (캐시에는 있었으나 삭제된 경우)
            List<ProductGroupImage> currentImages = List.of();

            OutboundProductImages cachedImages = OutboundProductImageFixtures.thumbnailOnlyImages();
            given(readManager.findByOutboundProductId(outboundProductId)).willReturn(cachedImages);

            // when
            sut.syncImages(outboundProductId, channelCode, currentImages);

            // then
            then(imageClientManager).shouldHaveNoInteractions();
            then(commandManager).should().persistAll(anyList());
        }
    }

    @Nested
    @DisplayName("syncImages() - added 이미지 externalUrl 세팅 확인")
    class SyncImages_ExternalUrlAssignment {

        @Test
        @DisplayName("업로드 후 added 이미지에 externalUrl이 세팅되어 결과에 포함된다")
        void syncImages_AfterUpload_AddedImagesHaveExternalUrlAssigned() {
            // given
            Long outboundProductId = OutboundProductImageFixtures.DEFAULT_OUTBOUND_PRODUCT_ID;
            String channelCode = ResolvedExternalImageFixtures.DEFAULT_CHANNEL_CODE;
            List<ProductGroupImage> currentImages = List.of(ProductGroupFixtures.thumbnailImage());

            OutboundProductImages emptyCache = OutboundProductImageFixtures.emptyImages();
            given(readManager.findByOutboundProductId(outboundProductId)).willReturn(emptyCache);

            String expectedExternalUrl = OutboundProductImageFixtures.DEFAULT_EXTERNAL_URL;
            given(imageClientManager.uploadImages(eq(channelCode), anyList()))
                    .willReturn(List.of(expectedExternalUrl));

            // when
            ResolvedExternalImages result =
                    sut.syncImages(outboundProductId, channelCode, currentImages);

            // then
            assertThat(result.isEmpty()).isFalse();
            assertThat(result.thumbnailUrl()).isEqualTo(expectedExternalUrl);
        }

        @Test
        @DisplayName("업로드된 external URL 순서가 원본 이미지 순서와 동일하게 매핑된다")
        void syncImages_ExternalUrlsMappedInOrder() {
            // given
            Long outboundProductId = OutboundProductImageFixtures.DEFAULT_OUTBOUND_PRODUCT_ID;
            String channelCode = ResolvedExternalImageFixtures.DEFAULT_CHANNEL_CODE;
            List<ProductGroupImage> currentImages =
                    List.of(
                            ProductGroupFixtures.thumbnailImage(),
                            ProductGroupFixtures.detailImage(1));

            OutboundProductImages emptyCache = OutboundProductImageFixtures.emptyImages();
            given(readManager.findByOutboundProductId(outboundProductId)).willReturn(emptyCache);

            String thumbnailExternalUrl = OutboundProductImageFixtures.DEFAULT_EXTERNAL_URL;
            String detailExternalUrl = "https://cdn.naver.com/detail1.jpg";
            given(imageClientManager.uploadImages(eq(channelCode), anyList()))
                    .willReturn(List.of(thumbnailExternalUrl, detailExternalUrl));

            // when
            ResolvedExternalImages result =
                    sut.syncImages(outboundProductId, channelCode, currentImages);

            // then
            assertThat(result.thumbnailUrl()).isEqualTo(thumbnailExternalUrl);
            assertThat(result.detailUrls()).containsExactly(detailExternalUrl);
        }
    }

    @Nested
    @DisplayName("syncImages() - 반환값 확인")
    class SyncImages_ReturnValue {

        @Test
        @DisplayName("thumbnailUrl과 detailUrls가 포함된 ResolvedExternalImages를 반환한다")
        void syncImages_ReturnsResolvedImagesWithThumbnailAndDetails() {
            // given
            Long outboundProductId = OutboundProductImageFixtures.DEFAULT_OUTBOUND_PRODUCT_ID;
            String channelCode = ResolvedExternalImageFixtures.DEFAULT_CHANNEL_CODE;
            List<ProductGroupImage> currentImages =
                    List.of(
                            ProductGroupFixtures.thumbnailImage(),
                            ProductGroupFixtures.detailImage(1),
                            ProductGroupFixtures.detailImage(2));

            OutboundProductImages emptyCache = OutboundProductImageFixtures.emptyImages();
            given(readManager.findByOutboundProductId(outboundProductId)).willReturn(emptyCache);

            given(imageClientManager.uploadImages(eq(channelCode), anyList()))
                    .willReturn(
                            List.of(
                                    "https://cdn.naver.com/thumb.jpg",
                                    "https://cdn.naver.com/detail1.jpg",
                                    "https://cdn.naver.com/detail2.jpg"));

            // when
            ResolvedExternalImages result =
                    sut.syncImages(outboundProductId, channelCode, currentImages);

            // then
            assertThat(result.thumbnailUrl()).isEqualTo("https://cdn.naver.com/thumb.jpg");
            assertThat(result.detailUrls()).hasSize(2);
            assertThat(result.detailUrls())
                    .contains(
                            "https://cdn.naver.com/detail1.jpg",
                            "https://cdn.naver.com/detail2.jpg");
        }

        @Test
        @DisplayName("retained 이미지들은 기존 externalUrl을 유지하며 결과에 포함된다")
        void syncImages_RetainedImages_IncludedInResultWithExistingExternalUrls() {
            // given
            Long outboundProductId = OutboundProductImageFixtures.DEFAULT_OUTBOUND_PRODUCT_ID;
            String channelCode = ResolvedExternalImageFixtures.DEFAULT_CHANNEL_CODE;

            // 캐시에 있는 이미지와 동일한 이미지
            List<ProductGroupImage> currentImages =
                    List.of(
                            buildUploadedThumbnailImage(
                                    OutboundProductImageFixtures.DEFAULT_ORIGIN_URL));

            OutboundProductImages cachedImages = OutboundProductImageFixtures.thumbnailOnlyImages();
            given(readManager.findByOutboundProductId(outboundProductId)).willReturn(cachedImages);

            // when
            ResolvedExternalImages result =
                    sut.syncImages(outboundProductId, channelCode, currentImages);

            // then
            assertThat(result.thumbnailUrl())
                    .isEqualTo(OutboundProductImageFixtures.DEFAULT_EXTERNAL_URL);
        }
    }

    // ===== 헬퍼 메서드 =====

    /**
     * 특정 URL로 uploadedUrl이 세팅된 썸네일 ProductGroupImage를 생성합니다. OutboundProductImages의 diff 비교는
     * uploadedUrl(우선) 또는 originUrl을 사용합니다.
     */
    private ProductGroupImage buildUploadedThumbnailImage(String uploadedUrl) {
        ProductGroupImage image = ProductGroupFixtures.thumbnailImage();
        image.updateUploadedUrl(ImageUrl.of(uploadedUrl));
        return image;
    }
}
