package com.ryuqq.marketplace.domain.productgroup.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.brand.id.BrandId;
import com.ryuqq.marketplace.domain.category.id.CategoryId;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.exception.ProductGroupInvalidOptionStructureException;
import com.ryuqq.marketplace.domain.productgroup.exception.ProductGroupInvalidStatusTransitionException;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.*;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ProductGroup Aggregate 단위 테스트")
class ProductGroupTest {

    @Nested
    @DisplayName("forNew 팩토리 메서드 테스트")
    class ForNewTest {

        @Test
        @DisplayName("필수 필드로 새 ProductGroup을 DRAFT 상태로 생성한다")
        void createNewProductGroupWithRequiredFields() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            BrandId brandId = BrandId.of(100L);
            CategoryId categoryId = CategoryId.of(200L);
            ShippingPolicyId shippingPolicyId = ShippingPolicyId.of(1L);
            RefundPolicyId refundPolicyId = RefundPolicyId.of(1L);
            ProductGroupName name = ProductGroupFixtures.defaultProductGroupName();
            OptionType optionType = OptionType.NONE;
            ProductGroupImages images =
                    ProductGroupImages.of(List.of(ProductGroupFixtures.thumbnailImage()));
            Instant now = CommonVoFixtures.now();

            // when
            ProductGroup productGroup =
                    ProductGroup.forNew(
                            sellerId,
                            brandId,
                            categoryId,
                            shippingPolicyId,
                            refundPolicyId,
                            name,
                            optionType,
                            images,
                            SellerOptionGroups.of(List.of()),
                            now);

            // then
            assertThat(productGroup).isNotNull();
            assertThat(productGroup.id().isNew()).isTrue();
            assertThat(productGroup.sellerId()).isEqualTo(sellerId);
            assertThat(productGroup.brandId()).isEqualTo(brandId);
            assertThat(productGroup.categoryId()).isEqualTo(categoryId);
            assertThat(productGroup.productGroupName()).isEqualTo(name);
            assertThat(productGroup.optionType()).isEqualTo(optionType);
            assertThat(productGroup.status()).isEqualTo(ProductGroupStatus.DRAFT);
            assertThat(productGroup.images()).hasSize(1);
            assertThat(productGroup.createdAt()).isEqualTo(now);
            assertThat(productGroup.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("SINGLE 옵션 타입은 1개의 옵션 그룹이 필요하다")
        void createProductGroupWithSingleOption() {
            // given & when
            ProductGroup productGroup = ProductGroupFixtures.newProductGroupWithSingleOption();

            // then
            assertThat(productGroup.optionType()).isEqualTo(OptionType.SINGLE);
            assertThat(productGroup.sellerOptionGroups()).hasSize(1);
        }

        @Test
        @DisplayName("COMBINATION 옵션 타입은 2개의 옵션 그룹이 필요하다")
        void createProductGroupWithCombinationOption() {
            // given & when
            ProductGroup productGroup = ProductGroupFixtures.newProductGroupWithCombinationOption();

            // then
            assertThat(productGroup.optionType()).isEqualTo(OptionType.COMBINATION);
            assertThat(productGroup.sellerOptionGroups()).hasSize(2);
        }

        @Test
        @DisplayName("SINGLE 옵션 타입에 옵션 그룹이 0개면 예외가 발생한다")
        void throwExceptionWhenSingleOptionWithNoGroup() {
            // given & when & then
            assertThatThrownBy(
                            () ->
                                    ProductGroup.forNew(
                                            CommonVoFixtures.defaultSellerId(),
                                            BrandId.of(100L),
                                            CategoryId.of(200L),
                                            ShippingPolicyId.of(1L),
                                            RefundPolicyId.of(1L),
                                            ProductGroupFixtures.defaultProductGroupName(),
                                            OptionType.SINGLE,
                                            ProductGroupImages.of(
                                                    List.of(ProductGroupFixtures.thumbnailImage())),
                                            SellerOptionGroups.of(List.of()),
                                            CommonVoFixtures.now()))
                    .isInstanceOf(ProductGroupInvalidOptionStructureException.class);
        }

        @Test
        @DisplayName("NONE 옵션 타입에 옵션 그룹이 있으면 예외가 발생한다")
        void throwExceptionWhenNoneOptionWithGroups() {
            // given & when & then
            assertThatThrownBy(
                            () ->
                                    ProductGroup.forNew(
                                            CommonVoFixtures.defaultSellerId(),
                                            BrandId.of(100L),
                                            CategoryId.of(200L),
                                            ShippingPolicyId.of(1L),
                                            RefundPolicyId.of(1L),
                                            ProductGroupFixtures.defaultProductGroupName(),
                                            OptionType.NONE,
                                            ProductGroupImages.of(
                                                    List.of(ProductGroupFixtures.thumbnailImage())),
                                            SellerOptionGroups.of(
                                                    List.of(
                                                            ProductGroupFixtures
                                                                    .defaultSellerOptionGroup())),
                                            CommonVoFixtures.now()))
                    .isInstanceOf(ProductGroupInvalidOptionStructureException.class);
        }
    }

    @Nested
    @DisplayName("reconstitute 팩토리 메서드 테스트")
    class ReconstituteTest {

        @Test
        @DisplayName("영속성에서 ACTIVE 상태의 ProductGroup을 복원한다")
        void reconstituteActiveProductGroup() {
            // given
            ProductGroupId id = ProductGroupFixtures.defaultProductGroupId();
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            ProductGroupStatus status = ProductGroupStatus.ACTIVE;

            // when
            ProductGroup productGroup = ProductGroupFixtures.activeProductGroup();

            // then
            assertThat(productGroup.id()).isEqualTo(id);
            assertThat(productGroup.sellerId()).isEqualTo(sellerId);
            assertThat(productGroup.status()).isEqualTo(status);
            assertThat(productGroup.status().isActive()).isTrue();
        }

        @Test
        @DisplayName("영속성에서 DRAFT 상태의 ProductGroup을 복원한다")
        void reconstituteDraftProductGroup() {
            // when
            ProductGroup productGroup = ProductGroupFixtures.draftProductGroup(1L);

            // then
            assertThat(productGroup.status()).isEqualTo(ProductGroupStatus.DRAFT);
            assertThat(productGroup.status().isActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("상태 변경 메서드 테스트")
    class StateChangeTest {

        @Test
        @DisplayName("DRAFT 상태의 ProductGroup을 활성화한다")
        void activateDraftProductGroup() {
            // given
            ProductGroup productGroup = ProductGroupFixtures.draftProductGroup(1L);
            Instant now = CommonVoFixtures.now();

            // when
            productGroup.activate(now);

            // then
            assertThat(productGroup.status()).isEqualTo(ProductGroupStatus.ACTIVE);
            assertThat(productGroup.status().isActive()).isTrue();
            assertThat(productGroup.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("INACTIVE 상태의 ProductGroup을 활성화한다")
        void activateInactiveProductGroup() {
            // given
            ProductGroup productGroup = ProductGroupFixtures.inactiveProductGroup();
            Instant now = CommonVoFixtures.now();

            // when
            productGroup.activate(now);

            // then
            assertThat(productGroup.status()).isEqualTo(ProductGroupStatus.ACTIVE);
        }

        @Test
        @DisplayName("SOLDOUT 상태의 ProductGroup을 활성화한다")
        void activateSoldoutProductGroup() {
            // given
            ProductGroup productGroup = ProductGroupFixtures.soldoutProductGroup();
            Instant now = CommonVoFixtures.now();

            // when
            productGroup.activate(now);

            // then
            assertThat(productGroup.status()).isEqualTo(ProductGroupStatus.ACTIVE);
        }

        @Test
        @DisplayName("DELETED 상태에서는 활성화할 수 없다")
        void cannotActivateDeletedProductGroup() {
            // given
            ProductGroup productGroup = ProductGroupFixtures.deletedProductGroup();
            Instant now = CommonVoFixtures.now();

            // when & then
            assertThatThrownBy(() -> productGroup.activate(now))
                    .isInstanceOf(ProductGroupInvalidStatusTransitionException.class);
        }

        @Test
        @DisplayName("ACTIVE 상태의 ProductGroup을 비활성화한다")
        void deactivateActiveProductGroup() {
            // given
            ProductGroup productGroup = ProductGroupFixtures.activeProductGroup();
            Instant now = CommonVoFixtures.now();

            // when
            productGroup.deactivate(now);

            // then
            assertThat(productGroup.status()).isEqualTo(ProductGroupStatus.INACTIVE);
            assertThat(productGroup.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("ACTIVE가 아닌 상태에서는 비활성화할 수 없다")
        void cannotDeactivateNonActiveProductGroup() {
            // given
            ProductGroup productGroup = ProductGroupFixtures.draftProductGroup(1L);
            Instant now = CommonVoFixtures.now();

            // when & then
            assertThatThrownBy(() -> productGroup.deactivate(now))
                    .isInstanceOf(ProductGroupInvalidStatusTransitionException.class);
        }

        @Test
        @DisplayName("ACTIVE 상태의 ProductGroup을 품절 처리한다")
        void markSoldOutActiveProductGroup() {
            // given
            ProductGroup productGroup = ProductGroupFixtures.activeProductGroup();
            Instant now = CommonVoFixtures.now();

            // when
            productGroup.markSoldOut(now);

            // then
            assertThat(productGroup.status()).isEqualTo(ProductGroupStatus.SOLDOUT);
            assertThat(productGroup.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("ACTIVE가 아닌 상태에서는 품절 처리할 수 없다")
        void cannotMarkSoldOutNonActiveProductGroup() {
            // given
            ProductGroup productGroup = ProductGroupFixtures.draftProductGroup(1L);
            Instant now = CommonVoFixtures.now();

            // when & then
            assertThatThrownBy(() -> productGroup.markSoldOut(now))
                    .isInstanceOf(ProductGroupInvalidStatusTransitionException.class);
        }

        @Test
        @DisplayName("DRAFT 상태의 ProductGroup을 삭제한다")
        void deleteDraftProductGroup() {
            // given
            ProductGroup productGroup = ProductGroupFixtures.draftProductGroup(1L);
            Instant now = CommonVoFixtures.now();

            // when
            productGroup.delete(now);

            // then
            assertThat(productGroup.status()).isEqualTo(ProductGroupStatus.DELETED);
            assertThat(productGroup.status().isDeleted()).isTrue();
            assertThat(productGroup.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("ACTIVE 상태의 ProductGroup을 삭제한다")
        void deleteActiveProductGroup() {
            // given
            ProductGroup productGroup = ProductGroupFixtures.activeProductGroup();
            Instant now = CommonVoFixtures.now();

            // when
            productGroup.delete(now);

            // then
            assertThat(productGroup.status()).isEqualTo(ProductGroupStatus.DELETED);
        }

        @Test
        @DisplayName("이미 DELETED 상태이면 삭제할 수 없다")
        void cannotDeleteAlreadyDeletedProductGroup() {
            // given
            ProductGroup productGroup = ProductGroupFixtures.deletedProductGroup();
            Instant now = CommonVoFixtures.now();

            // when & then
            assertThatThrownBy(() -> productGroup.delete(now))
                    .isInstanceOf(ProductGroupInvalidStatusTransitionException.class);
        }
    }

    @Nested
    @DisplayName("정보 수정 메서드 테스트")
    class UpdateTest {

        @Test
        @DisplayName("기본 정보를 수정한다")
        void updateBasicInfo() {
            // given
            ProductGroup productGroup = ProductGroupFixtures.activeProductGroup();
            ProductGroupName newName = ProductGroupFixtures.productGroupName("수정된 상품명");
            BrandId newBrandId = BrandId.of(999L);
            CategoryId newCategoryId = CategoryId.of(888L);
            ShippingPolicyId newShippingPolicyId = ShippingPolicyId.of(2L);
            RefundPolicyId newRefundPolicyId = RefundPolicyId.of(2L);
            Instant now = CommonVoFixtures.now();

            // when
            productGroup.updateBasicInfo(
                    newName,
                    newBrandId,
                    newCategoryId,
                    newShippingPolicyId,
                    newRefundPolicyId,
                    now);

            // then
            assertThat(productGroup.productGroupName()).isEqualTo(newName);
            assertThat(productGroup.brandId()).isEqualTo(newBrandId);
            assertThat(productGroup.categoryId()).isEqualTo(newCategoryId);
            assertThat(productGroup.shippingPolicyId()).isEqualTo(newShippingPolicyId);
            assertThat(productGroup.refundPolicyId()).isEqualTo(newRefundPolicyId);
            assertThat(productGroup.updatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("이미지 관리 메서드 테스트")
    class ImageManagementTest {

        @Test
        @DisplayName("이미지 전체를 교체한다")
        void replaceImages() {
            // given
            ProductGroup productGroup = ProductGroupFixtures.newProductGroup();
            ProductGroupImages newImages =
                    ProductGroupImages.of(
                            List.of(
                                    ProductGroupFixtures.thumbnailImage(),
                                    ProductGroupFixtures.detailImage(1),
                                    ProductGroupFixtures.detailImage(2)));

            // when
            productGroup.replaceImages(newImages);

            // then
            assertThat(productGroup.images()).hasSize(3);
        }

        @Test
        @DisplayName("이미지가 있는 ProductGroup의 이미지 목록을 조회한다")
        void imagesReturnsImageList() {
            // given
            ProductGroup productGroup = ProductGroupFixtures.activeProductGroup();

            // when & then
            assertThat(productGroup.images()).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("옵션 관리 메서드 테스트")
    class OptionManagementTest {

        @Test
        @DisplayName("셀러 옵션 그룹 전체를 교체하고 검증한다")
        void replaceSellerOptionGroups() {
            // given
            ProductGroup productGroup = ProductGroupFixtures.newProductGroupWithSingleOption();
            SellerOptionGroup newGroup = ProductGroupFixtures.defaultSellerOptionGroup();

            // when
            productGroup.replaceSellerOptionGroups(SellerOptionGroups.of(List.of(newGroup)));

            // then
            assertThat(productGroup.sellerOptionGroups()).hasSize(1);
        }

        @Test
        @DisplayName("옵션 타입과 맞지 않는 옵션 그룹으로 교체하면 예외가 발생한다")
        void throwExceptionWhenReplaceWithInvalidOptionStructure() {
            // given
            ProductGroup productGroup = ProductGroupFixtures.newProductGroupWithSingleOption();

            // when & then
            assertThatThrownBy(
                            () ->
                                    productGroup.replaceSellerOptionGroups(
                                            SellerOptionGroups.of(List.of())))
                    .isInstanceOf(ProductGroupInvalidOptionStructureException.class);
        }

        @Test
        @DisplayName("모든 옵션이 캐노니컬에 매핑되었는지 확인한다")
        void isFullyMappedToCanonical() {
            // given
            ProductGroup productGroup = ProductGroupFixtures.fullyMappedProductGroup();

            // when & then
            assertThat(productGroup.isFullyMappedToCanonical()).isTrue();
        }

        @Test
        @DisplayName("옵션이 캐노니컬에 매핑되지 않았으면 false를 반환한다")
        void isNotFullyMappedToCanonical() {
            // given
            ProductGroup productGroup = ProductGroupFixtures.newProductGroupWithSingleOption();

            // when & then
            assertThat(productGroup.isFullyMappedToCanonical()).isFalse();
        }

        @Test
        @DisplayName("옵션이 없으면 fully mapped로 간주한다")
        void emptyOptionIsFullyMapped() {
            // given
            ProductGroup productGroup = ProductGroupFixtures.newProductGroup();

            // when & then
            assertThat(productGroup.isFullyMappedToCanonical()).isTrue();
        }

        @Test
        @DisplayName("총 옵션 값 수를 반환한다")
        void totalOptionValueCount() {
            // given
            ProductGroup productGroup = ProductGroupFixtures.newProductGroupWithCombinationOption();

            // when
            int count = productGroup.totalOptionValueCount();

            // then
            assertThat(count).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("Getter 메서드 테스트")
    class GetterTest {

        @Test
        @DisplayName("idValue()는 ID의 값을 반환한다")
        void idValueReturnsIdValue() {
            // given
            ProductGroup productGroup = ProductGroupFixtures.activeProductGroup();

            // when & then
            assertThat(productGroup.idValue()).isEqualTo(1L);
        }

        @Test
        @DisplayName("sellerIdValue()는 SellerId의 값을 반환한다")
        void sellerIdValueReturnsValue() {
            // given
            ProductGroup productGroup = ProductGroupFixtures.activeProductGroup();

            // when & then
            assertThat(productGroup.sellerIdValue())
                    .isEqualTo(ProductGroupFixtures.DEFAULT_SELLER_ID);
        }

        @Test
        @DisplayName("brandIdValue()는 BrandId의 값을 반환한다")
        void brandIdValueReturnsValue() {
            // given
            ProductGroup productGroup = ProductGroupFixtures.activeProductGroup();

            // when & then
            assertThat(productGroup.brandIdValue())
                    .isEqualTo(ProductGroupFixtures.DEFAULT_BRAND_ID);
        }

        @Test
        @DisplayName("productGroupNameValue()는 상품 그룹명 문자열을 반환한다")
        void productGroupNameValueReturnsValue() {
            // given
            ProductGroup productGroup = ProductGroupFixtures.activeProductGroup();

            // when & then
            assertThat(productGroup.productGroupNameValue())
                    .isEqualTo(ProductGroupFixtures.DEFAULT_PRODUCT_GROUP_NAME);
        }
    }
}
