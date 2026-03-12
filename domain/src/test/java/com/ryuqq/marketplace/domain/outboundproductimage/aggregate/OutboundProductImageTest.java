package com.ryuqq.marketplace.domain.outboundproductimage.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.common.vo.DeletionStatus;
import com.ryuqq.marketplace.domain.outboundproductimage.OutboundProductImageFixtures;
import com.ryuqq.marketplace.domain.outboundproductimage.id.OutboundProductImageId;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageType;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("OutboundProductImage Aggregate 단위 테스트")
class OutboundProductImageTest {

    @Nested
    @DisplayName("forNew 팩토리 메서드 테스트")
    class ForNewTest {

        @Test
        @DisplayName("필수 필드로 새 OutboundProductImage를 생성한다")
        void createNewOutboundProductImageWithRequiredFields() {
            // given
            Long outboundProductId = OutboundProductImageFixtures.DEFAULT_OUTBOUND_PRODUCT_ID;
            Long productGroupImageId = OutboundProductImageFixtures.DEFAULT_PRODUCT_GROUP_IMAGE_ID;
            String originUrl = OutboundProductImageFixtures.DEFAULT_ORIGIN_URL;
            ImageType imageType = ImageType.THUMBNAIL;
            int sortOrder = 0;

            // when
            OutboundProductImage image = OutboundProductImage.forNew(
                    outboundProductId, productGroupImageId, originUrl, imageType, sortOrder);

            // then
            assertThat(image).isNotNull();
            assertThat(image.id().isNew()).isTrue();
            assertThat(image.outboundProductId()).isEqualTo(outboundProductId);
            assertThat(image.productGroupImageId()).isEqualTo(productGroupImageId);
            assertThat(image.originUrl()).isEqualTo(originUrl);
            assertThat(image.externalUrl()).isNull();
            assertThat(image.imageType()).isEqualTo(imageType);
            assertThat(image.sortOrder()).isEqualTo(sortOrder);
            assertThat(image.isDeleted()).isFalse();
        }

        @Test
        @DisplayName("새로 생성된 이미지의 externalUrl은 null이다")
        void newImageHasNullExternalUrl() {
            // given & when
            OutboundProductImage image = OutboundProductImageFixtures.newThumbnailImage();

            // then
            assertThat(image.externalUrl()).isNull();
            assertThat(image.hasExternalUrl()).isFalse();
        }

        @Test
        @DisplayName("새로 생성된 이미지는 삭제되지 않은 상태다")
        void newImageIsNotDeleted() {
            // given & when
            OutboundProductImage image = OutboundProductImageFixtures.newThumbnailImage();

            // then
            assertThat(image.isDeleted()).isFalse();
        }

        @Test
        @DisplayName("THUMBNAIL 타입으로 생성하면 isThumbnail이 true다")
        void createThumbnailTypeImage() {
            // given & when
            OutboundProductImage image = OutboundProductImageFixtures.newThumbnailImage();

            // then
            assertThat(image.imageType()).isEqualTo(ImageType.THUMBNAIL);
            assertThat(image.isThumbnail()).isTrue();
        }

        @Test
        @DisplayName("DETAIL 타입으로 생성하면 isThumbnail이 false다")
        void createDetailTypeImage() {
            // given & when
            OutboundProductImage image = OutboundProductImageFixtures.newDetailImage(1);

            // then
            assertThat(image.imageType()).isEqualTo(ImageType.DETAIL);
            assertThat(image.isThumbnail()).isFalse();
        }
    }

    @Nested
    @DisplayName("reconstitute 팩토리 메서드 테스트")
    class ReconstituteTest {

        @Test
        @DisplayName("영속성에서 활성 이미지를 복원한다")
        void reconstituteActiveImage() {
            // given
            OutboundProductImageId id = OutboundProductImageFixtures.defaultOutboundProductImageId();
            Long outboundProductId = OutboundProductImageFixtures.DEFAULT_OUTBOUND_PRODUCT_ID;
            Long productGroupImageId = OutboundProductImageFixtures.DEFAULT_PRODUCT_GROUP_IMAGE_ID;
            String originUrl = OutboundProductImageFixtures.DEFAULT_ORIGIN_URL;
            String externalUrl = OutboundProductImageFixtures.DEFAULT_EXTERNAL_URL;
            ImageType imageType = ImageType.THUMBNAIL;
            int sortOrder = 0;
            DeletionStatus deletionStatus = DeletionStatus.active();

            // when
            OutboundProductImage image = OutboundProductImage.reconstitute(
                    id, outboundProductId, productGroupImageId,
                    originUrl, externalUrl, imageType, sortOrder, deletionStatus);

            // then
            assertThat(image.id()).isEqualTo(id);
            assertThat(image.idValue()).isEqualTo(id.value());
            assertThat(image.outboundProductId()).isEqualTo(outboundProductId);
            assertThat(image.productGroupImageId()).isEqualTo(productGroupImageId);
            assertThat(image.originUrl()).isEqualTo(originUrl);
            assertThat(image.externalUrl()).isEqualTo(externalUrl);
            assertThat(image.imageType()).isEqualTo(imageType);
            assertThat(image.sortOrder()).isEqualTo(sortOrder);
            assertThat(image.isDeleted()).isFalse();
            assertThat(image.hasExternalUrl()).isTrue();
        }

        @Test
        @DisplayName("영속성에서 삭제된 이미지를 복원한다")
        void reconstituteDeletedImage() {
            // given
            Instant deletedAt = CommonVoFixtures.yesterday();

            // when
            OutboundProductImage image = OutboundProductImage.reconstitute(
                    OutboundProductImageFixtures.defaultOutboundProductImageId(),
                    OutboundProductImageFixtures.DEFAULT_OUTBOUND_PRODUCT_ID,
                    OutboundProductImageFixtures.DEFAULT_PRODUCT_GROUP_IMAGE_ID,
                    OutboundProductImageFixtures.DEFAULT_ORIGIN_URL,
                    OutboundProductImageFixtures.DEFAULT_EXTERNAL_URL,
                    ImageType.THUMBNAIL,
                    0,
                    DeletionStatus.deletedAt(deletedAt));

            // then
            assertThat(image.isDeleted()).isTrue();
            assertThat(image.deletionStatus().deletedAt()).isEqualTo(deletedAt);
        }

        @Test
        @DisplayName("externalUrl이 null인 이미지를 복원한다")
        void reconstituteImageWithNullExternalUrl() {
            // given & when
            OutboundProductImage image = OutboundProductImage.reconstitute(
                    OutboundProductImageFixtures.defaultOutboundProductImageId(),
                    OutboundProductImageFixtures.DEFAULT_OUTBOUND_PRODUCT_ID,
                    OutboundProductImageFixtures.DEFAULT_PRODUCT_GROUP_IMAGE_ID,
                    OutboundProductImageFixtures.DEFAULT_ORIGIN_URL,
                    null,
                    ImageType.THUMBNAIL,
                    0,
                    DeletionStatus.active());

            // then
            assertThat(image.externalUrl()).isNull();
            assertThat(image.hasExternalUrl()).isFalse();
        }
    }

    @Nested
    @DisplayName("assignExternalUrl 메서드 테스트")
    class AssignExternalUrlTest {

        @Test
        @DisplayName("외부 채널 업로드 완료 후 externalUrl을 설정한다")
        void assignExternalUrl() {
            // given
            OutboundProductImage image = OutboundProductImageFixtures.newThumbnailImage();
            String externalUrl = OutboundProductImageFixtures.DEFAULT_EXTERNAL_URL;

            // when
            image.assignExternalUrl(externalUrl);

            // then
            assertThat(image.externalUrl()).isEqualTo(externalUrl);
            assertThat(image.hasExternalUrl()).isTrue();
        }

        @Test
        @DisplayName("externalUrl을 재설정하면 덮어쓴다")
        void reassignExternalUrl() {
            // given
            OutboundProductImage image = OutboundProductImageFixtures.thumbnailImageWithExternalUrl();
            String newUrl = "https://cdn.naver.com/new-image.jpg";

            // when
            image.assignExternalUrl(newUrl);

            // then
            assertThat(image.externalUrl()).isEqualTo(newUrl);
        }
    }

    @Nested
    @DisplayName("delete 메서드 테스트")
    class DeleteTest {

        @Test
        @DisplayName("활성 이미지를 soft delete 처리한다")
        void deleteActiveImage() {
            // given
            OutboundProductImage image = OutboundProductImageFixtures.activeThumbnailImage();
            Instant now = CommonVoFixtures.now();

            // when
            image.delete(now);

            // then
            assertThat(image.isDeleted()).isTrue();
            assertThat(image.deletionStatus().isDeleted()).isTrue();
            assertThat(image.deletionStatus().deletedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("신규 이미지를 soft delete 처리한다")
        void deleteNewImage() {
            // given
            OutboundProductImage image = OutboundProductImageFixtures.newThumbnailImage();
            Instant now = CommonVoFixtures.now();

            // when
            image.delete(now);

            // then
            assertThat(image.isDeleted()).isTrue();
        }
    }

    @Nested
    @DisplayName("imageKey 메서드 테스트")
    class ImageKeyTest {

        @Test
        @DisplayName("imageKey는 originUrl과 imageType을 구분자로 연결한다")
        void imageKeyContainsOriginUrlAndImageType() {
            // given
            OutboundProductImage image = OutboundProductImageFixtures.newThumbnailImage();

            // when
            String key = image.imageKey();

            // then
            assertThat(key).isEqualTo(
                    OutboundProductImageFixtures.DEFAULT_ORIGIN_URL + "::" + ImageType.THUMBNAIL.name());
        }

        @Test
        @DisplayName("DETAIL 이미지의 imageKey는 DETAIL을 포함한다")
        void imageKeyForDetailImage() {
            // given
            OutboundProductImage image = OutboundProductImageFixtures.newDetailImage(1);

            // when
            String key = image.imageKey();

            // then
            assertThat(key).contains("::" + ImageType.DETAIL.name());
        }

        @Test
        @DisplayName("같은 originUrl과 imageType이면 imageKey가 같다")
        void sameOriginUrlAndTypeProduceSameKey() {
            // given
            OutboundProductImage image1 = OutboundProductImage.forNew(
                    1L, 10L, "https://s3.example.com/same.jpg", ImageType.THUMBNAIL, 0);
            OutboundProductImage image2 = OutboundProductImage.forNew(
                    2L, 20L, "https://s3.example.com/same.jpg", ImageType.THUMBNAIL, 1);

            // when & then
            assertThat(image1.imageKey()).isEqualTo(image2.imageKey());
        }

        @Test
        @DisplayName("같은 URL이지만 imageType이 다르면 imageKey가 다르다")
        void sameUrlButDifferentTypeProduceDifferentKey() {
            // given
            OutboundProductImage thumbnail = OutboundProductImage.forNew(
                    1L, 10L, "https://s3.example.com/same.jpg", ImageType.THUMBNAIL, 0);
            OutboundProductImage detail = OutboundProductImage.forNew(
                    1L, 10L, "https://s3.example.com/same.jpg", ImageType.DETAIL, 1);

            // when & then
            assertThat(thumbnail.imageKey()).isNotEqualTo(detail.imageKey());
        }
    }

    @Nested
    @DisplayName("isThumbnail / hasExternalUrl 메서드 테스트")
    class PredicateTest {

        @Test
        @DisplayName("THUMBNAIL 타입이면 isThumbnail이 true다")
        void isThumbnailReturnsTrueForThumbnailType() {
            // given
            OutboundProductImage image = OutboundProductImageFixtures.newThumbnailImage();

            // when & then
            assertThat(image.isThumbnail()).isTrue();
        }

        @Test
        @DisplayName("DETAIL 타입이면 isThumbnail이 false다")
        void isThumbnailReturnsFalseForDetailType() {
            // given
            OutboundProductImage image = OutboundProductImageFixtures.newDetailImage(1);

            // when & then
            assertThat(image.isThumbnail()).isFalse();
        }

        @Test
        @DisplayName("externalUrl이 있으면 hasExternalUrl이 true다")
        void hasExternalUrlReturnsTrueWhenExternalUrlExists() {
            // given
            OutboundProductImage image = OutboundProductImageFixtures.activeThumbnailImage();

            // when & then
            assertThat(image.hasExternalUrl()).isTrue();
        }

        @Test
        @DisplayName("externalUrl이 없으면 hasExternalUrl이 false다")
        void hasExternalUrlReturnsFalseWhenExternalUrlIsNull() {
            // given
            OutboundProductImage image = OutboundProductImageFixtures.newThumbnailImage();

            // when & then
            assertThat(image.hasExternalUrl()).isFalse();
        }
    }

    @Nested
    @DisplayName("Getter 메서드 테스트")
    class GetterTest {

        @Test
        @DisplayName("idValue()는 ID의 Long 값을 반환한다")
        void idValueReturnsLongValue() {
            // given
            OutboundProductImage image = OutboundProductImageFixtures.activeThumbnailImage();

            // when & then
            assertThat(image.idValue()).isEqualTo(OutboundProductImageFixtures.DEFAULT_ID);
        }

        @Test
        @DisplayName("updateSortOrder()는 정렬 순서를 변경한다")
        void updateSortOrderChangesValue() {
            // given
            OutboundProductImage image = OutboundProductImageFixtures.newThumbnailImage();

            // when
            image.updateSortOrder(5);

            // then
            assertThat(image.sortOrder()).isEqualTo(5);
        }
    }
}
