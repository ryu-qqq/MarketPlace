package com.ryuqq.marketplace.application.brandpreset.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import com.ryuqq.marketplace.application.brand.manager.BrandReadManager;
import com.ryuqq.marketplace.application.brandpreset.manager.BrandPresetReadManager;
import com.ryuqq.marketplace.application.shop.manager.ShopReadManager;
import com.ryuqq.marketplace.domain.brand.aggregate.Brand;
import com.ryuqq.marketplace.domain.brandpreset.BrandPresetFixtures;
import com.ryuqq.marketplace.domain.brandpreset.aggregate.BrandPreset;
import com.ryuqq.marketplace.domain.brandpreset.exception.BrandPresetChannelMismatchException;
import com.ryuqq.marketplace.domain.brandpreset.exception.BrandPresetInternalBrandNotFoundException;
import com.ryuqq.marketplace.domain.brandpreset.exception.BrandPresetNotFoundException;
import com.ryuqq.marketplace.domain.brandpreset.exception.BrandPresetSalesChannelBrandNotFoundException;
import com.ryuqq.marketplace.domain.brandpreset.id.BrandPresetId;
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
@DisplayName("BrandPresetValidator 단위 테스트")
class BrandPresetValidatorTest {

    @InjectMocks private BrandPresetValidator sut;

    @Mock private BrandPresetReadManager readManager;
    @Mock private ShopReadManager shopReadManager;
    @Mock private BrandReadManager brandReadManager;

    @Nested
    @DisplayName("findExistingOrThrow() - 존재 검증")
    class FindExistingOrThrowTest {

        @Test
        @DisplayName("존재하는 BrandPreset을 반환한다")
        void findExistingOrThrow_Exists_ReturnsBrandPreset() {
            // given
            BrandPresetId id = BrandPresetId.of(1L);
            BrandPreset expected = BrandPresetFixtures.activeBrandPreset();

            given(readManager.getById(id)).willReturn(expected);

            // when
            BrandPreset result = sut.findExistingOrThrow(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(readManager).should().getById(id);
        }

        @Test
        @DisplayName("존재하지 않으면 예외가 발생한다")
        void findExistingOrThrow_NotExists_ThrowsException() {
            // given
            BrandPresetId id = BrandPresetId.of(999L);

            given(readManager.getById(id))
                    .willThrow(new BrandPresetNotFoundException(id.value()));

            // when & then
            assertThatThrownBy(() -> sut.findExistingOrThrow(id))
                    .isInstanceOf(BrandPresetNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("validateSameChannel() - 판매채널 일치 검증")
    class ValidateSameChannelTest {

        @Test
        @DisplayName("Shop과 SalesChannelBrand의 판매채널이 일치하면 예외가 발생하지 않는다")
        void validateSameChannel_SameChannel_DoesNotThrow() {
            // given
            Long shopId = 1L;
            Long salesChannelBrandId = 100L;
            Long salesChannelId = 1L;
            Shop shop = mock(Shop.class);

            given(shop.salesChannelId()).willReturn(salesChannelId);
            given(shopReadManager.getById(ShopId.of(shopId))).willReturn(shop);
            given(readManager.findSalesChannelIdBySalesChannelBrandId(salesChannelBrandId))
                    .willReturn(Optional.of(salesChannelId));

            // when & then
            sut.validateSameChannel(shopId, salesChannelBrandId);

            then(shopReadManager).should().getById(ShopId.of(shopId));
            then(readManager).should().findSalesChannelIdBySalesChannelBrandId(salesChannelBrandId);
        }

        @Test
        @DisplayName("판매채널 브랜드를 찾을 수 없으면 예외가 발생한다")
        void validateSameChannel_BrandNotFound_ThrowsException() {
            // given
            Long shopId = 1L;
            Long salesChannelBrandId = 999L;
            Shop shop = mock(Shop.class);

            given(shopReadManager.getById(ShopId.of(shopId))).willReturn(shop);
            given(readManager.findSalesChannelIdBySalesChannelBrandId(salesChannelBrandId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.validateSameChannel(shopId, salesChannelBrandId))
                    .isInstanceOf(BrandPresetSalesChannelBrandNotFoundException.class);
        }

        @Test
        @DisplayName("판매채널이 일치하지 않으면 예외가 발생한다")
        void validateSameChannel_DifferentChannel_ThrowsException() {
            // given
            Long shopId = 1L;
            Long salesChannelBrandId = 100L;
            Long shopSalesChannelId = 1L;
            Long brandSalesChannelId = 2L;
            Shop shop = mock(Shop.class);

            given(shop.salesChannelId()).willReturn(shopSalesChannelId);
            given(shopReadManager.getById(ShopId.of(shopId))).willReturn(shop);
            given(readManager.findSalesChannelIdBySalesChannelBrandId(salesChannelBrandId))
                    .willReturn(Optional.of(brandSalesChannelId));

            // when & then
            assertThatThrownBy(() -> sut.validateSameChannel(shopId, salesChannelBrandId))
                    .isInstanceOf(BrandPresetChannelMismatchException.class);
        }
    }

    @Nested
    @DisplayName("validateInternalBrandsExist() - 내부 브랜드 존재 검증")
    class ValidateInternalBrandsExistTest {

        @Test
        @DisplayName("모든 내부 브랜드가 존재하면 예외가 발생하지 않는다")
        void validateInternalBrandsExist_AllExist_DoesNotThrow() {
            // given
            List<Long> internalBrandIds = List.of(10L, 11L, 12L);
            List<Brand> foundBrands =
                    List.of(mock(Brand.class), mock(Brand.class), mock(Brand.class));

            given(brandReadManager.findAllByIds(internalBrandIds)).willReturn(foundBrands);

            // when & then
            sut.validateInternalBrandsExist(internalBrandIds);

            then(brandReadManager).should().findAllByIds(internalBrandIds);
        }

        private Brand createMockBrand(Long id) {
            Brand brand = mock(Brand.class);
            given(brand.idValue()).willReturn(id);
            return brand;
        }

        @Test
        @DisplayName("존재하지 않는 브랜드가 있으면 예외가 발생한다")
        void validateInternalBrandsExist_SomeMissing_ThrowsException() {
            // given
            List<Long> internalBrandIds = List.of(10L, 11L, 999L);
            List<Brand> foundBrands = List.of(createMockBrand(10L), createMockBrand(11L));

            given(brandReadManager.findAllByIds(internalBrandIds)).willReturn(foundBrands);

            // when & then
            assertThatThrownBy(() -> sut.validateInternalBrandsExist(internalBrandIds))
                    .isInstanceOf(BrandPresetInternalBrandNotFoundException.class);
        }

        @Test
        @DisplayName("내부 브랜드 ID가 null이면 검증을 건너뛴다")
        void validateInternalBrandsExist_Null_DoesNothing() {
            // when & then
            sut.validateInternalBrandsExist(null);

            then(brandReadManager).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("내부 브랜드 ID가 비어있으면 검증을 건너뛴다")
        void validateInternalBrandsExist_Empty_DoesNothing() {
            // when & then
            sut.validateInternalBrandsExist(List.of());

            then(brandReadManager).shouldHaveNoInteractions();
        }
    }
}
