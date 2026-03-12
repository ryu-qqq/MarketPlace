package com.ryuqq.marketplace.domain.outboundproductimage.vo;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.common.vo.DeletionStatus;
import com.ryuqq.marketplace.domain.outboundproductimage.OutboundProductImageFixtures;
import com.ryuqq.marketplace.domain.outboundproductimage.aggregate.OutboundProductImage;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageType;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;
import com.ryuqq.marketplace.domain.productgroupimage.id.ProductGroupImageId;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("OutboundProductImages VO 단위 테스트")
class OutboundProductImagesTest {

    // ===== 테스트용 ProductGroupImage 헬퍼 메서드 =====

    /** imageKey 비교용: originUrl 기준 썸네일 ProductGroupImage (uploadedUrl 없음). */
    private ProductGroupImage pgThumbnail(String originUrl) {
        return ProductGroupImage.reconstitute(
                ProductGroupImageId.of(10L),
                ProductGroupFixtures.defaultProductGroupId(),
                ImageUrl.of(originUrl),
                null,
                ImageType.THUMBNAIL,
                0,
                DeletionStatus.active());
    }

    /** imageKey 비교용: uploadedUrl 기준 썸네일 ProductGroupImage. */
    private ProductGroupImage pgThumbnailWithUploadedUrl(String originUrl, String uploadedUrl) {
        return ProductGroupImage.reconstitute(
                ProductGroupImageId.of(10L),
                ProductGroupFixtures.defaultProductGroupId(),
                ImageUrl.of(originUrl),
                ImageUrl.of(uploadedUrl),
                ImageType.THUMBNAIL,
                0,
                DeletionStatus.active());
    }

    /** imageKey 비교용: originUrl 기준 상세 ProductGroupImage. */
    private ProductGroupImage pgDetail(Long id, String originUrl, int sortOrder) {
        return ProductGroupImage.reconstitute(
                ProductGroupImageId.of(id),
                ProductGroupFixtures.defaultProductGroupId(),
                ImageUrl.of(originUrl),
                null,
                ImageType.DETAIL,
                sortOrder,
                DeletionStatus.active());
    }

    /** imageKey 비교용: uploadedUrl 기준 상세 ProductGroupImage. */
    private ProductGroupImage pgDetailWithUploadedUrl(
            Long id, String originUrl, String uploadedUrl, int sortOrder) {
        return ProductGroupImage.reconstitute(
                ProductGroupImageId.of(id),
                ProductGroupFixtures.defaultProductGroupId(),
                ImageUrl.of(originUrl),
                ImageUrl.of(uploadedUrl),
                ImageType.DETAIL,
                sortOrder,
                DeletionStatus.active());
    }

    @Nested
    @DisplayName("of / empty 팩토리 메서드 테스트")
    class FactoryTest {

        @Test
        @DisplayName("이미지 목록으로 생성한다")
        void createWithImages() {
            // given
            OutboundProductImage image = OutboundProductImageFixtures.activeThumbnailImage();

            // when
            OutboundProductImages images = OutboundProductImages.of(List.of(image));

            // then
            assertThat(images.isEmpty()).isFalse();
            assertThat(images.toList()).hasSize(1);
        }

        @Test
        @DisplayName("empty()로 빈 컬렉션을 생성한다")
        void createEmpty() {
            // when
            OutboundProductImages images = OutboundProductImages.empty();

            // then
            assertThat(images.isEmpty()).isTrue();
            assertThat(images.toList()).isEmpty();
        }
    }

    @Nested
    @DisplayName("diff 메서드 테스트 - 변경 없음")
    class DiffNoChangeTest {

        @Test
        @DisplayName("완전 동일한 이미지는 hasNoChanges가 true이고 retained에 포함된다")
        void identicalImagesProduceNoChanges() {
            // given
            String originUrl = OutboundProductImageFixtures.DEFAULT_ORIGIN_URL;
            OutboundProductImage cached = OutboundProductImage.reconstitute(
                    OutboundProductImageFixtures.defaultOutboundProductImageId(),
                    OutboundProductImageFixtures.DEFAULT_OUTBOUND_PRODUCT_ID,
                    OutboundProductImageFixtures.DEFAULT_PRODUCT_GROUP_IMAGE_ID,
                    originUrl,
                    OutboundProductImageFixtures.DEFAULT_EXTERNAL_URL,
                    ImageType.THUMBNAIL,
                    0,
                    DeletionStatus.active());

            OutboundProductImages images = OutboundProductImages.of(List.of(cached));
            ProductGroupImage current = pgThumbnail(originUrl);
            Instant now = CommonVoFixtures.now();

            // when
            OutboundProductImageDiff diff = images.diff(List.of(current),
                    OutboundProductImageFixtures.DEFAULT_OUTBOUND_PRODUCT_ID, now);

            // then
            assertThat(diff.hasNoChanges()).isTrue();
            assertThat(diff.retained()).hasSize(1);
            assertThat(diff.added()).isEmpty();
            assertThat(diff.removed()).isEmpty();
        }

        @Test
        @DisplayName("uploadedUrl이 있는 경우 uploadedUrl 기준으로 비교하여 변경 없음을 반환한다")
        void imagesMatchByUploadedUrl() {
            // given - 캐시에는 uploadedUrl이 originUrl로 저장되어 있음
            String uploadedUrl = "https://s3.example.com/uploaded.jpg";
            OutboundProductImage cached = OutboundProductImage.reconstitute(
                    OutboundProductImageFixtures.defaultOutboundProductImageId(),
                    OutboundProductImageFixtures.DEFAULT_OUTBOUND_PRODUCT_ID,
                    OutboundProductImageFixtures.DEFAULT_PRODUCT_GROUP_IMAGE_ID,
                    uploadedUrl,
                    OutboundProductImageFixtures.DEFAULT_EXTERNAL_URL,
                    ImageType.THUMBNAIL,
                    0,
                    DeletionStatus.active());

            OutboundProductImages images = OutboundProductImages.of(List.of(cached));

            // current는 originUrl은 다르지만 uploadedUrl이 캐시의 originUrl과 같음
            ProductGroupImage current = pgThumbnailWithUploadedUrl(
                    "https://original.example.com/image.jpg", uploadedUrl);
            Instant now = CommonVoFixtures.now();

            // when
            OutboundProductImageDiff diff = images.diff(List.of(current),
                    OutboundProductImageFixtures.DEFAULT_OUTBOUND_PRODUCT_ID, now);

            // then
            assertThat(diff.hasNoChanges()).isTrue();
            assertThat(diff.retained()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("diff 메서드 테스트 - 추가")
    class DiffAddedTest {

        @Test
        @DisplayName("빈 캐시에서 현재 이미지가 있으면 모두 added에 포함된다")
        void emptyCache_AllCurrentImagesAreAdded() {
            // given
            OutboundProductImages emptyImages = OutboundProductImages.empty();
            ProductGroupImage current = pgThumbnail(OutboundProductImageFixtures.DEFAULT_ORIGIN_URL);
            Instant now = CommonVoFixtures.now();

            // when
            OutboundProductImageDiff diff = emptyImages.diff(List.of(current),
                    OutboundProductImageFixtures.DEFAULT_OUTBOUND_PRODUCT_ID, now);

            // then
            assertThat(diff.added()).hasSize(1);
            assertThat(diff.removed()).isEmpty();
            assertThat(diff.retained()).isEmpty();
            assertThat(diff.hasNoChanges()).isFalse();
        }

        @Test
        @DisplayName("새로운 이미지가 추가되면 added에 포함된다")
        void newImageIsIncludedInAdded() {
            // given
            String existingUrl = "https://s3.example.com/existing.jpg";
            String newUrl = "https://s3.example.com/new.jpg";

            OutboundProductImage cached = OutboundProductImage.reconstitute(
                    OutboundProductImageFixtures.defaultOutboundProductImageId(),
                    OutboundProductImageFixtures.DEFAULT_OUTBOUND_PRODUCT_ID,
                    10L,
                    existingUrl,
                    OutboundProductImageFixtures.DEFAULT_EXTERNAL_URL,
                    ImageType.THUMBNAIL,
                    0,
                    DeletionStatus.active());

            OutboundProductImages images = OutboundProductImages.of(List.of(cached));

            ProductGroupImage existing = pgThumbnail(existingUrl);
            ProductGroupImage newImage = pgDetail(20L, newUrl, 1);
            Instant now = CommonVoFixtures.now();

            // when
            OutboundProductImageDiff diff = images.diff(List.of(existing, newImage),
                    OutboundProductImageFixtures.DEFAULT_OUTBOUND_PRODUCT_ID, now);

            // then
            assertThat(diff.added()).hasSize(1);
            assertThat(diff.added().get(0).originUrl()).isEqualTo(newUrl);
            assertThat(diff.retained()).hasSize(1);
            assertThat(diff.removed()).isEmpty();
        }

        @Test
        @DisplayName("added 이미지는 forNew로 생성되어 ID가 null이다")
        void addedImageIsCreatedWithForNew() {
            // given
            OutboundProductImages emptyImages = OutboundProductImages.empty();
            ProductGroupImage current = pgThumbnail(OutboundProductImageFixtures.DEFAULT_ORIGIN_URL);
            Instant now = CommonVoFixtures.now();

            // when
            OutboundProductImageDiff diff = emptyImages.diff(List.of(current),
                    OutboundProductImageFixtures.DEFAULT_OUTBOUND_PRODUCT_ID, now);

            // then
            assertThat(diff.added()).hasSize(1);
            assertThat(diff.added().get(0).id().isNew()).isTrue();
            assertThat(diff.added().get(0).outboundProductId())
                    .isEqualTo(OutboundProductImageFixtures.DEFAULT_OUTBOUND_PRODUCT_ID);
        }
    }

    @Nested
    @DisplayName("diff 메서드 테스트 - 삭제")
    class DiffRemovedTest {

        @Test
        @DisplayName("캐시에 있지만 현재 목록에 없으면 removed에 포함된다")
        void imageMissingFromCurrentIsIncludedInRemoved() {
            // given
            String url = OutboundProductImageFixtures.DEFAULT_ORIGIN_URL;
            OutboundProductImage cached = OutboundProductImage.reconstitute(
                    OutboundProductImageFixtures.defaultOutboundProductImageId(),
                    OutboundProductImageFixtures.DEFAULT_OUTBOUND_PRODUCT_ID,
                    OutboundProductImageFixtures.DEFAULT_PRODUCT_GROUP_IMAGE_ID,
                    url,
                    OutboundProductImageFixtures.DEFAULT_EXTERNAL_URL,
                    ImageType.THUMBNAIL,
                    0,
                    DeletionStatus.active());

            OutboundProductImages images = OutboundProductImages.of(List.of(cached));
            Instant now = CommonVoFixtures.now();

            // when - 현재 이미지 목록이 비어있음
            OutboundProductImageDiff diff = images.diff(List.of(),
                    OutboundProductImageFixtures.DEFAULT_OUTBOUND_PRODUCT_ID, now);

            // then
            assertThat(diff.removed()).hasSize(1);
            assertThat(diff.added()).isEmpty();
            assertThat(diff.retained()).isEmpty();
            assertThat(diff.hasNoChanges()).isFalse();
        }

        @Test
        @DisplayName("removed에 포함된 이미지는 soft delete 처리된다")
        void removedImageIsSoftDeleted() {
            // given
            String url = OutboundProductImageFixtures.DEFAULT_ORIGIN_URL;
            OutboundProductImage cached = OutboundProductImage.reconstitute(
                    OutboundProductImageFixtures.defaultOutboundProductImageId(),
                    OutboundProductImageFixtures.DEFAULT_OUTBOUND_PRODUCT_ID,
                    OutboundProductImageFixtures.DEFAULT_PRODUCT_GROUP_IMAGE_ID,
                    url,
                    OutboundProductImageFixtures.DEFAULT_EXTERNAL_URL,
                    ImageType.THUMBNAIL,
                    0,
                    DeletionStatus.active());

            OutboundProductImages images = OutboundProductImages.of(List.of(cached));
            Instant now = CommonVoFixtures.now();

            // when
            OutboundProductImageDiff diff = images.diff(List.of(),
                    OutboundProductImageFixtures.DEFAULT_OUTBOUND_PRODUCT_ID, now);

            // then
            assertThat(diff.removed()).hasSize(1);
            assertThat(diff.removed().get(0).isDeleted()).isTrue();
        }

        @Test
        @DisplayName("이미 삭제된 이미지는 diff 비교에서 제외된다")
        void alreadyDeletedImagesAreExcludedFromDiff() {
            // given
            String url = OutboundProductImageFixtures.DEFAULT_ORIGIN_URL;

            // 이미 삭제된 이미지
            OutboundProductImage deletedCached = OutboundProductImage.reconstitute(
                    OutboundProductImageFixtures.defaultOutboundProductImageId(),
                    OutboundProductImageFixtures.DEFAULT_OUTBOUND_PRODUCT_ID,
                    OutboundProductImageFixtures.DEFAULT_PRODUCT_GROUP_IMAGE_ID,
                    url,
                    OutboundProductImageFixtures.DEFAULT_EXTERNAL_URL,
                    ImageType.THUMBNAIL,
                    0,
                    DeletionStatus.deletedAt(CommonVoFixtures.yesterday()));

            OutboundProductImages images = OutboundProductImages.of(List.of(deletedCached));
            ProductGroupImage current = pgThumbnail(url);
            Instant now = CommonVoFixtures.now();

            // when - 삭제된 캐시 이미지와 현재 이미지가 같은 URL이더라도
            OutboundProductImageDiff diff = images.diff(List.of(current),
                    OutboundProductImageFixtures.DEFAULT_OUTBOUND_PRODUCT_ID, now);

            // then - 삭제된 이미지는 매칭되지 않으므로 새로 added됨
            assertThat(diff.added()).hasSize(1);
            assertThat(diff.removed()).isEmpty();
        }
    }

    @Nested
    @DisplayName("diff 메서드 테스트 - 혼합 시나리오")
    class DiffMixedTest {

        @Test
        @DisplayName("추가, 삭제, 유지가 동시에 발생하는 혼합 시나리오")
        void mixedScenarioWithAddedRemovedAndRetained() {
            // given
            String retainedUrl = "https://s3.example.com/retained.jpg";
            String removedUrl = "https://s3.example.com/removed.jpg";
            String addedUrl = "https://s3.example.com/added.jpg";

            OutboundProductImage retainedCached = OutboundProductImage.reconstitute(
                    OutboundProductImageFixtures.outboundProductImageId(1L),
                    OutboundProductImageFixtures.DEFAULT_OUTBOUND_PRODUCT_ID,
                    10L,
                    retainedUrl,
                    "https://cdn.naver.com/retained.jpg",
                    ImageType.THUMBNAIL,
                    0,
                    DeletionStatus.active());

            OutboundProductImage removedCached = OutboundProductImage.reconstitute(
                    OutboundProductImageFixtures.outboundProductImageId(2L),
                    OutboundProductImageFixtures.DEFAULT_OUTBOUND_PRODUCT_ID,
                    20L,
                    removedUrl,
                    "https://cdn.naver.com/removed.jpg",
                    ImageType.DETAIL,
                    1,
                    DeletionStatus.active());

            OutboundProductImages images = OutboundProductImages.of(List.of(retainedCached, removedCached));

            ProductGroupImage retainedCurrent = pgThumbnail(retainedUrl);
            ProductGroupImage addedCurrent = pgDetail(30L, addedUrl, 1);
            Instant now = CommonVoFixtures.now();

            // when
            OutboundProductImageDiff diff = images.diff(List.of(retainedCurrent, addedCurrent),
                    OutboundProductImageFixtures.DEFAULT_OUTBOUND_PRODUCT_ID, now);

            // then
            assertThat(diff.retained()).hasSize(1);
            assertThat(diff.retained().get(0).originUrl()).isEqualTo(retainedUrl);

            assertThat(diff.added()).hasSize(1);
            assertThat(diff.added().get(0).originUrl()).isEqualTo(addedUrl);

            assertThat(diff.removed()).hasSize(1);
            assertThat(diff.removed().get(0).originUrl()).isEqualTo(removedUrl);
            assertThat(diff.removed().get(0).isDeleted()).isTrue();

            assertThat(diff.hasNoChanges()).isFalse();
        }

        @Test
        @DisplayName("retained 이미지의 sortOrder는 현재 이미지 기준으로 갱신된다")
        void retainedImageSortOrderIsUpdatedFromCurrent() {
            // given
            String url = OutboundProductImageFixtures.DEFAULT_ORIGIN_URL;
            OutboundProductImage cached = OutboundProductImage.reconstitute(
                    OutboundProductImageFixtures.defaultOutboundProductImageId(),
                    OutboundProductImageFixtures.DEFAULT_OUTBOUND_PRODUCT_ID,
                    OutboundProductImageFixtures.DEFAULT_PRODUCT_GROUP_IMAGE_ID,
                    url,
                    OutboundProductImageFixtures.DEFAULT_EXTERNAL_URL,
                    ImageType.THUMBNAIL,
                    0,
                    DeletionStatus.active());

            OutboundProductImages images = OutboundProductImages.of(List.of(cached));

            // 현재 이미지의 sortOrder가 변경됨
            ProductGroupImage current = ProductGroupImage.reconstitute(
                    ProductGroupImageId.of(OutboundProductImageFixtures.DEFAULT_PRODUCT_GROUP_IMAGE_ID),
                    ProductGroupFixtures.defaultProductGroupId(),
                    ImageUrl.of(url),
                    null,
                    ImageType.THUMBNAIL,
                    5,
                    DeletionStatus.active());

            Instant now = CommonVoFixtures.now();

            // when
            OutboundProductImageDiff diff = images.diff(List.of(current),
                    OutboundProductImageFixtures.DEFAULT_OUTBOUND_PRODUCT_ID, now);

            // then
            assertThat(diff.retained()).hasSize(1);
            assertThat(diff.retained().get(0).sortOrder()).isEqualTo(5);
            assertThat(diff.hasNoChanges()).isTrue();
        }
    }

    @Nested
    @DisplayName("activeImages 메서드 테스트")
    class ActiveImagesTest {

        @Test
        @DisplayName("삭제된 이미지를 제외한 활성 이미지만 반환한다")
        void returnsOnlyActiveImages() {
            // given
            OutboundProductImage active = OutboundProductImageFixtures.activeThumbnailImage();
            OutboundProductImage deleted = OutboundProductImageFixtures.deletedThumbnailImage();
            OutboundProductImages images = OutboundProductImages.of(List.of(active, deleted));

            // when
            List<OutboundProductImage> activeImages = images.activeImages();

            // then
            assertThat(activeImages).hasSize(1);
            assertThat(activeImages.get(0).isDeleted()).isFalse();
        }

        @Test
        @DisplayName("모든 이미지가 삭제된 경우 빈 리스트를 반환한다")
        void returnsEmptyWhenAllDeleted() {
            // given
            OutboundProductImage deleted = OutboundProductImageFixtures.deletedThumbnailImage();
            OutboundProductImages images = OutboundProductImages.of(List.of(deleted));

            // when
            List<OutboundProductImage> activeImages = images.activeImages();

            // then
            assertThat(activeImages).isEmpty();
        }
    }

    @Nested
    @DisplayName("thumbnailExternalUrl 메서드 테스트")
    class ThumbnailExternalUrlTest {

        @Test
        @DisplayName("썸네일 이미지의 externalUrl을 반환한다")
        void returnsThumbnailExternalUrl() {
            // given
            OutboundProductImages images = OutboundProductImageFixtures.thumbnailOnlyImages();

            // when
            String url = images.thumbnailExternalUrl();

            // then
            assertThat(url).isEqualTo(OutboundProductImageFixtures.DEFAULT_EXTERNAL_URL);
        }

        @Test
        @DisplayName("썸네일 이미지가 없으면 null을 반환한다")
        void returnsNullWhenNoThumbnail() {
            // given
            OutboundProductImage detailOnly = OutboundProductImageFixtures.activeDetailImage(2L, 1);
            OutboundProductImages images = OutboundProductImages.of(List.of(detailOnly));

            // when
            String url = images.thumbnailExternalUrl();

            // then
            assertThat(url).isNull();
        }

        @Test
        @DisplayName("썸네일이 삭제된 경우 null을 반환한다")
        void returnsNullWhenThumbnailIsDeleted() {
            // given
            OutboundProductImage deleted = OutboundProductImageFixtures.deletedThumbnailImage();
            OutboundProductImages images = OutboundProductImages.of(List.of(deleted));

            // when
            String url = images.thumbnailExternalUrl();

            // then
            assertThat(url).isNull();
        }

        @Test
        @DisplayName("빈 컬렉션에서는 null을 반환한다")
        void returnsNullForEmptyCollection() {
            // given
            OutboundProductImages images = OutboundProductImages.empty();

            // when
            String url = images.thumbnailExternalUrl();

            // then
            assertThat(url).isNull();
        }
    }

    @Nested
    @DisplayName("detailExternalUrls 메서드 테스트")
    class DetailExternalUrlsTest {

        @Test
        @DisplayName("상세 이미지의 externalUrl 목록을 sortOrder 오름차순으로 반환한다")
        void returnsDetailExternalUrlsSortedBySortOrder() {
            // given
            OutboundProductImages images = OutboundProductImageFixtures.fullImages();

            // when
            List<String> urls = images.detailExternalUrls();

            // then
            assertThat(urls).hasSize(2);
            assertThat(urls.get(0)).isEqualTo("https://cdn.naver.com/detail1.jpg");
            assertThat(urls.get(1)).isEqualTo("https://cdn.naver.com/detail2.jpg");
        }

        @Test
        @DisplayName("상세 이미지가 없으면 빈 리스트를 반환한다")
        void returnsEmptyWhenNoDetailImages() {
            // given
            OutboundProductImages images = OutboundProductImageFixtures.thumbnailOnlyImages();

            // when
            List<String> urls = images.detailExternalUrls();

            // then
            assertThat(urls).isEmpty();
        }

        @Test
        @DisplayName("externalUrl이 없는 상세 이미지는 제외된다")
        void excludesDetailImagesWithoutExternalUrl() {
            // given
            OutboundProductImage detailWithoutExternalUrl = OutboundProductImage.reconstitute(
                    OutboundProductImageFixtures.outboundProductImageId(2L),
                    OutboundProductImageFixtures.DEFAULT_OUTBOUND_PRODUCT_ID,
                    20L,
                    "https://s3.example.com/detail.jpg",
                    null,
                    ImageType.DETAIL,
                    1,
                    DeletionStatus.active());

            OutboundProductImages images = OutboundProductImages.of(List.of(detailWithoutExternalUrl));

            // when
            List<String> urls = images.detailExternalUrls();

            // then
            assertThat(urls).isEmpty();
        }

        @Test
        @DisplayName("삭제된 상세 이미지는 제외된다")
        void excludesDeletedDetailImages() {
            // given
            OutboundProductImage deletedDetail = OutboundProductImage.reconstitute(
                    OutboundProductImageFixtures.outboundProductImageId(2L),
                    OutboundProductImageFixtures.DEFAULT_OUTBOUND_PRODUCT_ID,
                    20L,
                    "https://s3.example.com/detail.jpg",
                    "https://cdn.naver.com/detail.jpg",
                    ImageType.DETAIL,
                    1,
                    DeletionStatus.deletedAt(CommonVoFixtures.yesterday()));

            OutboundProductImages images = OutboundProductImages.of(List.of(deletedDetail));

            // when
            List<String> urls = images.detailExternalUrls();

            // then
            assertThat(urls).isEmpty();
        }
    }

    @Nested
    @DisplayName("toList / isEmpty 메서드 테스트")
    class CollectionTest {

        @Test
        @DisplayName("toList()는 불변 리스트를 반환한다")
        void toListReturnsUnmodifiableList() {
            // given
            OutboundProductImages images = OutboundProductImageFixtures.thumbnailOnlyImages();

            // when & then
            assertThatThrownBy(
                    () -> images.toList().add(OutboundProductImageFixtures.newDetailImage(1)))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("이미지가 있으면 isEmpty()는 false다")
        void isEmptyReturnsFalseWhenHasImages() {
            // given
            OutboundProductImages images = OutboundProductImageFixtures.thumbnailOnlyImages();

            // when & then
            assertThat(images.isEmpty()).isFalse();
        }

        @Test
        @DisplayName("empty()로 생성하면 isEmpty()는 true다")
        void isEmptyReturnsTrueForEmpty() {
            // given
            OutboundProductImages images = OutboundProductImages.empty();

            // when & then
            assertThat(images.isEmpty()).isTrue();
        }
    }
}
