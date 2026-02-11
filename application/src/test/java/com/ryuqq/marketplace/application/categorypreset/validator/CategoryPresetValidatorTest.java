package com.ryuqq.marketplace.application.categorypreset.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import com.ryuqq.marketplace.application.category.manager.CategoryReadManager;
import com.ryuqq.marketplace.application.categorypreset.manager.CategoryPresetReadManager;
import com.ryuqq.marketplace.application.saleschannelcategory.manager.SalesChannelCategoryReadManager;
import com.ryuqq.marketplace.application.shop.manager.ShopReadManager;
import com.ryuqq.marketplace.domain.category.aggregate.Category;
import com.ryuqq.marketplace.domain.category.id.CategoryId;
import com.ryuqq.marketplace.domain.categorypreset.CategoryPresetFixtures;
import com.ryuqq.marketplace.domain.categorypreset.aggregate.CategoryPreset;
import com.ryuqq.marketplace.domain.categorypreset.exception.CategoryPresetChannelMismatchException;
import com.ryuqq.marketplace.domain.categorypreset.exception.CategoryPresetInternalCategoryNotFoundException;
import com.ryuqq.marketplace.domain.categorypreset.exception.CategoryPresetNotFoundException;
import com.ryuqq.marketplace.domain.categorypreset.exception.CategoryPresetSalesChannelCategoryNotFoundException;
import com.ryuqq.marketplace.domain.categorypreset.id.CategoryPresetId;
import com.ryuqq.marketplace.domain.saleschannelcategory.aggregate.SalesChannelCategory;
import com.ryuqq.marketplace.domain.saleschannelcategory.id.SalesChannelCategoryId;
import com.ryuqq.marketplace.domain.shop.ShopFixtures;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import com.ryuqq.marketplace.domain.shop.id.ShopId;
import java.util.List;
import java.util.Optional;
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
@DisplayName("CategoryPresetValidator 단위 테스트")
class CategoryPresetValidatorTest {

    @InjectMocks private CategoryPresetValidator sut;

    @Mock private CategoryPresetReadManager readManager;
    @Mock private ShopReadManager shopReadManager;
    @Mock private SalesChannelCategoryReadManager salesChannelCategoryReadManager;
    @Mock private CategoryReadManager categoryReadManager;

    @Nested
    @DisplayName("findExistingOrThrow() - 존재 검증")
    class FindExistingOrThrowTest {

        @Test
        @DisplayName("존재하는 CategoryPreset을 반환한다")
        void findExistingOrThrow_Exists_ReturnsCategoryPreset() {
            // given
            CategoryPresetId id = CategoryPresetId.of(1L);
            CategoryPreset expected = CategoryPresetFixtures.activeCategoryPreset();

            given(readManager.getById(id)).willReturn(expected);

            // when
            CategoryPreset result = sut.findExistingOrThrow(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(readManager).should().getById(id);
        }

        @Test
        @DisplayName("존재하지 않으면 예외가 발생한다")
        void findExistingOrThrow_NotExists_ThrowsException() {
            // given
            CategoryPresetId id = CategoryPresetId.of(999L);

            given(readManager.getById(id))
                    .willThrow(new CategoryPresetNotFoundException(id.value()));

            // when & then
            assertThatThrownBy(() -> sut.findExistingOrThrow(id))
                    .isInstanceOf(CategoryPresetNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("resolveSalesChannelCategoryId() - 판매채널 카테고리 ID 조회 및 검증")
    class ResolveSalesChannelCategoryIdTest {

        @Test
        @DisplayName("카테고리 코드로 판매채널 카테고리 ID를 조회한다")
        void resolveSalesChannelCategoryId_ValidCode_ReturnsId() {
            // given
            Long shopId = 1L;
            String categoryCode = "TEST_CODE";
            Long salesChannelId = 1L;
            Long expectedCategoryId = 200L;
            Shop shop = mock(Shop.class);
            SalesChannelCategory salesChannelCategory = mock(SalesChannelCategory.class);

            given(shop.salesChannelId()).willReturn(salesChannelId);
            given(salesChannelCategory.salesChannelId()).willReturn(salesChannelId);
            given(shopReadManager.getById(ShopId.of(shopId))).willReturn(shop);
            given(readManager.findSalesChannelCategoryIdByCode(salesChannelId, categoryCode))
                    .willReturn(Optional.of(expectedCategoryId));
            given(salesChannelCategoryReadManager.getById(
                            SalesChannelCategoryId.of(expectedCategoryId)))
                    .willReturn(salesChannelCategory);

            // when
            Long result = sut.resolveSalesChannelCategoryId(shopId, categoryCode);

            // then
            assertThat(result).isEqualTo(expectedCategoryId);
        }

        @Test
        @DisplayName("카테고리 코드를 찾을 수 없으면 예외가 발생한다")
        void resolveSalesChannelCategoryId_CodeNotFound_ThrowsException() {
            // given
            Long shopId = 1L;
            String categoryCode = "INVALID_CODE";
            Long salesChannelId = 1L;
            Shop shop = mock(Shop.class);

            given(shop.salesChannelId()).willReturn(salesChannelId);
            given(shopReadManager.getById(ShopId.of(shopId))).willReturn(shop);
            given(readManager.findSalesChannelCategoryIdByCode(salesChannelId, categoryCode))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.resolveSalesChannelCategoryId(shopId, categoryCode))
                    .isInstanceOf(CategoryPresetSalesChannelCategoryNotFoundException.class);
        }

        @Test
        @DisplayName("판매채널이 일치하지 않으면 예외가 발생한다")
        void resolveSalesChannelCategoryId_ChannelMismatch_ThrowsException() {
            // given
            Long shopId = 1L;
            String categoryCode = "TEST_CODE";
            Long shopSalesChannelId = 1L;
            Long categorySalesChannelId = 2L;
            Long salesChannelCategoryId = 200L;
            Shop shop = mock(Shop.class);
            SalesChannelCategory salesChannelCategory = mock(SalesChannelCategory.class);

            given(shop.salesChannelId()).willReturn(shopSalesChannelId);
            given(salesChannelCategory.salesChannelId()).willReturn(categorySalesChannelId);
            given(shopReadManager.getById(ShopId.of(shopId))).willReturn(shop);
            given(readManager.findSalesChannelCategoryIdByCode(
                            shopSalesChannelId, categoryCode))
                    .willReturn(Optional.of(salesChannelCategoryId));
            given(salesChannelCategoryReadManager.getById(
                            SalesChannelCategoryId.of(salesChannelCategoryId)))
                    .willReturn(salesChannelCategory);

            // when & then
            assertThatThrownBy(() -> sut.resolveSalesChannelCategoryId(shopId, categoryCode))
                    .isInstanceOf(CategoryPresetChannelMismatchException.class);
        }
    }

    @Nested
    @DisplayName("validateSameChannel() - 판매채널 일치 검증")
    class ValidateSameChannelTest {

        @Test
        @DisplayName("Shop과 SalesChannelCategory의 판매채널이 일치하면 예외가 발생하지 않는다")
        void validateSameChannel_SameChannel_DoesNotThrow() {
            // given
            Long shopId = 1L;
            Long salesChannelCategoryId = 200L;
            Long salesChannelId = 1L;
            Shop shop = mock(Shop.class);
            SalesChannelCategory salesChannelCategory = mock(SalesChannelCategory.class);

            given(shop.salesChannelId()).willReturn(salesChannelId);
            given(salesChannelCategory.salesChannelId()).willReturn(salesChannelId);
            given(shopReadManager.getById(ShopId.of(shopId))).willReturn(shop);
            given(salesChannelCategoryReadManager.getById(
                            SalesChannelCategoryId.of(salesChannelCategoryId)))
                    .willReturn(salesChannelCategory);

            // when & then
            sut.validateSameChannel(shopId, salesChannelCategoryId);

            then(shopReadManager).should().getById(ShopId.of(shopId));
            then(salesChannelCategoryReadManager)
                    .should()
                    .getById(SalesChannelCategoryId.of(salesChannelCategoryId));
        }

        @Test
        @DisplayName("판매채널이 일치하지 않으면 예외가 발생한다")
        void validateSameChannel_DifferentChannel_ThrowsException() {
            // given
            Long shopId = 1L;
            Long salesChannelCategoryId = 200L;
            Long shopSalesChannelId = 1L;
            Long categorySalesChannelId = 2L;
            Shop shop = mock(Shop.class);
            SalesChannelCategory salesChannelCategory = mock(SalesChannelCategory.class);

            given(shop.salesChannelId()).willReturn(shopSalesChannelId);
            given(salesChannelCategory.salesChannelId()).willReturn(categorySalesChannelId);
            given(shopReadManager.getById(ShopId.of(shopId))).willReturn(shop);
            given(salesChannelCategoryReadManager.getById(
                            SalesChannelCategoryId.of(salesChannelCategoryId)))
                    .willReturn(salesChannelCategory);

            // when & then
            assertThatThrownBy(() -> sut.validateSameChannel(shopId, salesChannelCategoryId))
                    .isInstanceOf(CategoryPresetChannelMismatchException.class);
        }
    }

    @Nested
    @DisplayName("validateInternalCategoriesExist() - 내부 카테고리 존재 검증")
    class ValidateInternalCategoriesExistTest {

        @Test
        @DisplayName("모든 내부 카테고리가 존재하면 예외가 발생하지 않는다")
        void validateInternalCategoriesExist_AllExist_DoesNotThrow() {
            // given
            List<Long> internalCategoryIds = List.of(1L, 2L, 3L);
            List<Category> foundCategories =
                    List.of(mock(Category.class), mock(Category.class), mock(Category.class));

            given(categoryReadManager.findAllByIds(internalCategoryIds)).willReturn(foundCategories);

            // when & then
            sut.validateInternalCategoriesExist(internalCategoryIds);

            then(categoryReadManager).should().findAllByIds(internalCategoryIds);
        }

        private Category createMockCategory(Long id) {
            Category category = mock(Category.class);
            given(category.idValue()).willReturn(id);
            return category;
        }

        @Test
        @DisplayName("존재하지 않는 카테고리가 있으면 예외가 발생한다")
        void validateInternalCategoriesExist_SomeMissing_ThrowsException() {
            // given
            List<Long> internalCategoryIds = List.of(1L, 2L, 999L);
            List<Category> foundCategories =
                    List.of(createMockCategory(1L), createMockCategory(2L));

            given(categoryReadManager.findAllByIds(internalCategoryIds)).willReturn(foundCategories);

            // when & then
            assertThatThrownBy(() -> sut.validateInternalCategoriesExist(internalCategoryIds))
                    .isInstanceOf(CategoryPresetInternalCategoryNotFoundException.class);
        }

        @Test
        @DisplayName("내부 카테고리 ID가 null이면 검증을 건너뛴다")
        void validateInternalCategoriesExist_Null_DoesNothing() {
            // when & then
            sut.validateInternalCategoriesExist(null);

            then(categoryReadManager).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("내부 카테고리 ID가 비어있으면 검증을 건너뛴다")
        void validateInternalCategoriesExist_Empty_DoesNothing() {
            // when & then
            sut.validateInternalCategoriesExist(List.of());

            then(categoryReadManager).shouldHaveNoInteractions();
        }
    }
}
