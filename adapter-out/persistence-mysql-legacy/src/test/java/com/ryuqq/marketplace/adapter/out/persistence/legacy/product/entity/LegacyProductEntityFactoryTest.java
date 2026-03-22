package com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.productdelivery.entity.LegacyProductDeliveryEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.entity.LegacyDescriptionImageEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.entity.LegacyProductGroupDetailDescriptionEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroup.entity.LegacyProductGroupEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupimage.entity.LegacyProductGroupImageEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productnotice.entity.LegacyProductNoticeEntity;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * LegacyProductEntityFactoryTest - 레거시 상품 관련 엔티티 팩토리 메서드 단위 테스트.
 *
 * <p>엔티티들의 팩토리 메서드와 getter 검증을 위한 테스트 클래스입니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("레거시 상품 엔티티 팩토리 단위 테스트")
class LegacyProductEntityFactoryTest {

    @Nested
    @DisplayName("LegacyProductGroupEntity 테스트")
    class LegacyProductGroupEntityTest {

        @Test
        @DisplayName("create 메서드로 상품그룹 엔티티를 생성합니다")
        void create_WithValidFields_CreatesEntity() {
            // when
            LegacyProductGroupEntity entity =
                    LegacyProductGroupEntity.create(
                            null,
                            "테스트 상품그룹",
                            1L,
                            2L,
                            3L,
                            "SINGLE",
                            "SYSTEM",
                            10000L,
                            9000L,
                            "N",
                            "Y",
                            "NEW",
                            "DOMESTIC",
                            "STYLE001");

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getProductGroupName()).isEqualTo("테스트 상품그룹");
            assertThat(entity.getSellerId()).isEqualTo(1L);
            assertThat(entity.getBrandId()).isEqualTo(2L);
            assertThat(entity.getCategoryId()).isEqualTo(3L);
            assertThat(entity.getOptionType()).isEqualTo("SINGLE");
            assertThat(entity.getManagementType()).isEqualTo("SYSTEM");
            assertThat(entity.getRegularPrice()).isEqualTo(10000L);
            assertThat(entity.getCurrentPrice()).isEqualTo(9000L);
            assertThat(entity.getSalePrice()).isEqualTo(9000L);
            assertThat(entity.getDirectDiscountRate()).isEqualTo(0);
            assertThat(entity.getDirectDiscountPrice()).isEqualTo(0L);
            assertThat(entity.getDiscountRate()).isEqualTo(0);
            assertThat(entity.getSoldOutYn()).isEqualTo("N");
            assertThat(entity.getDisplayYn()).isEqualTo("Y");
            assertThat(entity.getProductCondition()).isEqualTo("NEW");
            assertThat(entity.getOrigin()).isEqualTo("DOMESTIC");
            assertThat(entity.getStyleCode()).isEqualTo("STYLE001");
            assertThat(entity.getDeleteYn()).isEqualTo("N");
        }

        @Test
        @DisplayName("ID를 포함한 엔티티 생성이 가능합니다")
        void create_WithId_CreatesEntityWithId() {
            // when
            LegacyProductGroupEntity entity =
                    LegacyProductGroupEntity.create(
                            100L,
                            "테스트 상품그룹",
                            1L,
                            2L,
                            3L,
                            "MULTI",
                            "MANUAL",
                            15000L,
                            12000L,
                            "Y",
                            "N",
                            "USED",
                            "IMPORT",
                            "STYLE002");

            // then
            assertThat(entity.getId()).isEqualTo(100L);
            assertThat(entity.getOptionType()).isEqualTo("MULTI");
            assertThat(entity.getSoldOutYn()).isEqualTo("Y");
            assertThat(entity.getDisplayYn()).isEqualTo("N");
        }
    }

    @Nested
    @DisplayName("LegacyProductEntity 테스트")
    class LegacyProductEntityTest {

        @Test
        @DisplayName("ID 없이 상품 엔티티를 생성합니다")
        void create_WithoutId_CreatesEntity() {
            // when
            LegacyProductEntity entity = LegacyProductEntity.create(10L, "N", "Y", 0);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getProductGroupId()).isEqualTo(10L);
            assertThat(entity.getSoldOutYn()).isEqualTo("N");
            assertThat(entity.getDisplayYn()).isEqualTo("Y");
            assertThat(entity.getStockQuantity()).isEqualTo(0);
            assertThat(entity.getDeleteYn()).isEqualTo("N");
        }

        @Test
        @DisplayName("ID를 포함하여 상품 엔티티를 생성합니다")
        void create_WithId_CreatesEntityWithId() {
            // when
            LegacyProductEntity entity = LegacyProductEntity.create(1L, 10L, "Y", "N", 5, "Y");

            // then
            assertThat(entity.getId()).isEqualTo(1L);
            assertThat(entity.getProductGroupId()).isEqualTo(10L);
            assertThat(entity.getSoldOutYn()).isEqualTo("Y");
            assertThat(entity.getDisplayYn()).isEqualTo("N");
            assertThat(entity.getStockQuantity()).isEqualTo(5);
            assertThat(entity.getDeleteYn()).isEqualTo("Y");
        }
    }

    @Nested
    @DisplayName("LegacyProductStockEntity 테스트")
    class LegacyProductStockEntityTest {

        @Test
        @DisplayName("기본 재고 엔티티를 생성합니다")
        void create_Basic_CreatesEntity() {
            // when
            LegacyProductStockEntity entity = LegacyProductStockEntity.create(1L, 50);

            // then
            assertThat(entity.getProductId()).isEqualTo(1L);
            assertThat(entity.getStockQuantity()).isEqualTo(50);
            assertThat(entity.getDeleteYn()).isEqualTo("N");
        }

        @Test
        @DisplayName("deleteYn을 포함하여 재고 엔티티를 생성합니다")
        void create_WithDeleteYn_CreatesEntity() {
            // when
            LegacyProductStockEntity entity = LegacyProductStockEntity.create(2L, 0, "Y");

            // then
            assertThat(entity.getProductId()).isEqualTo(2L);
            assertThat(entity.getStockQuantity()).isEqualTo(0);
            assertThat(entity.getDeleteYn()).isEqualTo("Y");
        }
    }

    @Nested
    @DisplayName("LegacyProductOptionEntity 테스트")
    class LegacyProductOptionEntityTest {

        @Test
        @DisplayName("ID 없이 상품 옵션 엔티티를 생성합니다")
        void create_WithoutId_CreatesEntity() {
            // when
            LegacyProductOptionEntity entity = LegacyProductOptionEntity.create(1L, 100L, 200L, 0L);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getProductId()).isEqualTo(1L);
            assertThat(entity.getOptionGroupId()).isEqualTo(100L);
            assertThat(entity.getOptionDetailId()).isEqualTo(200L);
            assertThat(entity.getAdditionalPrice()).isEqualTo(0L);
            assertThat(entity.getDeleteYn()).isEqualTo("N");
        }

        @Test
        @DisplayName("ID를 포함하여 상품 옵션 엔티티를 생성합니다")
        void create_WithId_CreatesEntityWithId() {
            // when
            LegacyProductOptionEntity entity =
                    LegacyProductOptionEntity.create(10L, 1L, 100L, 200L, 500L, "N");

            // then
            assertThat(entity.getId()).isEqualTo(10L);
            assertThat(entity.getProductId()).isEqualTo(1L);
            assertThat(entity.getOptionGroupId()).isEqualTo(100L);
            assertThat(entity.getOptionDetailId()).isEqualTo(200L);
            assertThat(entity.getAdditionalPrice()).isEqualTo(500L);
            assertThat(entity.getDeleteYn()).isEqualTo("N");
        }
    }

    @Nested
    @DisplayName("LegacyProductNoticeEntity 테스트")
    class LegacyProductNoticeEntityTest {

        @Test
        @DisplayName("고시정보 엔티티를 생성합니다")
        void create_WithValidFields_CreatesEntity() {
            // when
            LegacyProductNoticeEntity entity =
                    LegacyProductNoticeEntity.create(
                            1L,
                            "면100%",
                            "빨강",
                            "M",
                            "나이키",
                            "한국",
                            "세탁기 가능",
                            "2025-01",
                            "KC인증",
                            "02-1234-5678");

            // then
            assertThat(entity.getProductGroupId()).isEqualTo(1L);
            assertThat(entity.getMaterial()).isEqualTo("면100%");
            assertThat(entity.getColor()).isEqualTo("빨강");
            assertThat(entity.getSize()).isEqualTo("M");
            assertThat(entity.getMaker()).isEqualTo("나이키");
            assertThat(entity.getOrigin()).isEqualTo("한국");
            assertThat(entity.getWashingMethod()).isEqualTo("세탁기 가능");
            assertThat(entity.getYearMonthDay()).isEqualTo("2025-01");
            assertThat(entity.getAssuranceStandard()).isEqualTo("KC인증");
            assertThat(entity.getAsPhone()).isEqualTo("02-1234-5678");
            assertThat(entity.getDeleteYn()).isEqualTo("N");
        }
    }

    @Nested
    @DisplayName("LegacyProductDeliveryEntity 테스트")
    class LegacyProductDeliveryEntityTest {

        @Test
        @DisplayName("배송정보 엔티티를 생성합니다")
        void create_WithValidFields_CreatesEntity() {
            // when
            LegacyProductDeliveryEntity entity =
                    LegacyProductDeliveryEntity.create(
                            1L, "NATIONWIDE", 3000L, 3, "택배", "CJ", 3000, "서울시 강남구");

            // then
            assertThat(entity.getProductGroupId()).isEqualTo(1L);
            assertThat(entity.getDeliveryArea()).isEqualTo("NATIONWIDE");
            assertThat(entity.getDeliveryFee()).isEqualTo(3000);
            assertThat(entity.getDeliveryPeriodAverage()).isEqualTo(3);
            assertThat(entity.getReturnMethodDomestic()).isEqualTo("택배");
            assertThat(entity.getReturnCourierDomestic()).isEqualTo("CJ");
            assertThat(entity.getReturnChargeDomestic()).isEqualTo(3000);
            assertThat(entity.getReturnExchangeAreaDomestic()).isEqualTo("서울시 강남구");
            assertThat(entity.getDeleteYn()).isEqualTo("N");
        }
    }

    @Nested
    @DisplayName("LegacyProductGroupDetailDescriptionEntity 테스트")
    class LegacyProductGroupDetailDescriptionEntityTest {

        @Test
        @DisplayName("기본 상세설명 엔티티를 생성합니다")
        void create_Basic_CreatesEntity() {
            // when
            LegacyProductGroupDetailDescriptionEntity entity =
                    LegacyProductGroupDetailDescriptionEntity.create(1L, "<p>상세 설명</p>");

            // then
            assertThat(entity.getProductGroupId()).isEqualTo(1L);
            assertThat(entity.getImageUrl()).isEqualTo("<p>상세 설명</p>");
            assertThat(entity.getDeleteYn()).isEqualTo("N");
        }

        @Test
        @DisplayName("전체 필드를 포함한 상세설명 엔티티를 생성합니다")
        void createFull_WithAllFields_CreatesEntity() {
            // when
            LegacyProductGroupDetailDescriptionEntity entity =
                    LegacyProductGroupDetailDescriptionEntity.createFull(
                            1L, "<p>상세 설명</p>", "https://cdn.example.com/path", "PUBLISHED");

            // then
            assertThat(entity.getProductGroupId()).isEqualTo(1L);
            assertThat(entity.getContent()).isEqualTo("<p>상세 설명</p>");
            assertThat(entity.getCdnPath()).isEqualTo("https://cdn.example.com/path");
            assertThat(entity.getPublishStatus()).isEqualTo("PUBLISHED");
        }
    }

    @Nested
    @DisplayName("LegacyProductGroupImageEntity 테스트")
    class LegacyProductGroupImageEntityTest {

        @Test
        @DisplayName("상품그룹 이미지 엔티티를 생성합니다")
        void create_WithValidFields_CreatesEntity() {
            // when
            LegacyProductGroupImageEntity entity =
                    LegacyProductGroupImageEntity.create(
                            null,
                            1L,
                            "MAIN",
                            "https://cdn.example.com/image.jpg",
                            "https://origin.example.com/image.jpg",
                            1L,
                            "N");

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getProductGroupId()).isEqualTo(1L);
            assertThat(entity.getProductGroupImageType()).isEqualTo("MAIN");
            assertThat(entity.getImageUrl()).isEqualTo("https://cdn.example.com/image.jpg");
            assertThat(entity.getOriginUrl()).isEqualTo("https://origin.example.com/image.jpg");
            assertThat(entity.getDisplayOrder()).isEqualTo(1L);
            assertThat(entity.getDeleteYn()).isEqualTo("N");
        }

        @Test
        @DisplayName("ID를 포함한 상품그룹 이미지 엔티티를 생성합니다")
        void create_WithId_CreatesEntityWithId() {
            // when
            LegacyProductGroupImageEntity entity =
                    LegacyProductGroupImageEntity.create(
                            99L,
                            1L,
                            "DETAIL",
                            "https://cdn.example.com/detail.jpg",
                            "https://origin.example.com/detail.jpg",
                            2L,
                            "N");

            // then
            assertThat(entity.getId()).isEqualTo(99L);
            assertThat(entity.getProductGroupImageType()).isEqualTo("DETAIL");
        }
    }

    @Nested
    @DisplayName("LegacyDescriptionImageEntity 테스트")
    class LegacyDescriptionImageEntityTest {

        @Test
        @DisplayName("상세설명 이미지 엔티티를 생성합니다")
        void create_WithValidFields_CreatesEntity() {
            // when
            LegacyDescriptionImageEntity entity =
                    LegacyDescriptionImageEntity.create(
                            null,
                            1L,
                            "https://origin.example.com/img.jpg",
                            "https://cdn.example.com/img.jpg",
                            1,
                            false,
                            null);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getProductGroupId()).isEqualTo(1L);
            assertThat(entity.getOriginUrl()).isEqualTo("https://origin.example.com/img.jpg");
            assertThat(entity.getUploadedUrl()).isEqualTo("https://cdn.example.com/img.jpg");
            assertThat(entity.getSortOrder()).isEqualTo(1);
            assertThat(entity.isDeleted()).isFalse();
            assertThat(entity.getDeletedAt()).isNull();
        }

        @Test
        @DisplayName("삭제된 상태의 상세설명 이미지 엔티티를 생성합니다")
        void create_WithDeletedState_CreatesDeletedEntity() {
            // given
            Instant deletedAt = Instant.parse("2025-01-01T00:00:00Z");

            // when
            LegacyDescriptionImageEntity entity =
                    LegacyDescriptionImageEntity.create(
                            10L,
                            1L,
                            "https://origin.example.com/img.jpg",
                            null,
                            1,
                            true,
                            deletedAt);

            // then
            assertThat(entity.getId()).isEqualTo(10L);
            assertThat(entity.isDeleted()).isTrue();
            assertThat(entity.getDeletedAt()).isEqualTo(deletedAt);
            assertThat(entity.getUploadedUrl()).isNull();
        }
    }
}
