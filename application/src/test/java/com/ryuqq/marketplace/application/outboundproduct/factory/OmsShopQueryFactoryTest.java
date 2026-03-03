package com.ryuqq.marketplace.application.outboundproduct.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.outboundproduct.OmsProductQueryFixtures;
import com.ryuqq.marketplace.application.outboundproduct.dto.query.OmsShopSearchParams;
import com.ryuqq.marketplace.application.shop.dto.query.ShopSearchParams;
import com.ryuqq.marketplace.application.shop.factory.ShopQueryFactory;
import com.ryuqq.marketplace.domain.shop.query.ShopSearchCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("OmsShopQueryFactory 단위 테스트")
class OmsShopQueryFactoryTest {

    private OmsShopQueryFactory sut;

    @Mock private ShopQueryFactory shopQueryFactory;

    @BeforeEach
    void setUp() {
        sut = new OmsShopQueryFactory(shopQueryFactory);
    }

    @Nested
    @DisplayName("createCriteria() - ShopSearchCriteria 생성")
    class CreateCriteriaTest {

        @Test
        @DisplayName("OmsShopSearchParams로 ShopSearchCriteria를 생성한다")
        void createCriteria_ValidParams_DelegatesToShopQueryFactory() {
            // given
            OmsShopSearchParams params = OmsProductQueryFixtures.omsShopSearchParams();
            ShopSearchCriteria expected = ShopSearchCriteria.defaultCriteria();

            given(
                            shopQueryFactory.createCriteria(
                                    org.mockito.ArgumentMatchers.any(ShopSearchParams.class)))
                    .willReturn(expected);

            // when
            ShopSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result).isEqualTo(expected);
            then(shopQueryFactory)
                    .should()
                    .createCriteria(org.mockito.ArgumentMatchers.any(ShopSearchParams.class));
        }

        @Test
        @DisplayName("키워드가 있으면 SHOP_NAME 검색 필드로 변환하여 ShopQueryFactory에 전달한다")
        void createCriteria_WithKeyword_SetsShopNameSearchField() {
            // given
            String keyword = "네이버";
            OmsShopSearchParams params = OmsProductQueryFixtures.omsShopSearchParams(keyword);
            ShopSearchCriteria expected = ShopSearchCriteria.defaultCriteria();

            ArgumentCaptor<ShopSearchParams> captor =
                    ArgumentCaptor.forClass(ShopSearchParams.class);
            given(shopQueryFactory.createCriteria(captor.capture())).willReturn(expected);

            // when
            sut.createCriteria(params);

            // then
            ShopSearchParams capturedParams = captor.getValue();
            assertThat(capturedParams.searchField()).isEqualTo("SHOP_NAME");
            assertThat(capturedParams.searchWord()).isEqualTo(keyword);
        }

        @Test
        @DisplayName("키워드가 null이면 검색 필드도 null로 ShopQueryFactory에 전달한다")
        void createCriteria_NullKeyword_SetsNullSearchField() {
            // given
            OmsShopSearchParams params = OmsProductQueryFixtures.omsShopSearchParams();
            ShopSearchCriteria expected = ShopSearchCriteria.defaultCriteria();

            ArgumentCaptor<ShopSearchParams> captor =
                    ArgumentCaptor.forClass(ShopSearchParams.class);
            given(shopQueryFactory.createCriteria(captor.capture())).willReturn(expected);

            // when
            sut.createCriteria(params);

            // then
            ShopSearchParams capturedParams = captor.getValue();
            assertThat(capturedParams.searchField()).isNull();
            assertThat(capturedParams.searchWord()).isNull();
        }

        @Test
        @DisplayName("키워드가 빈 문자열이면 검색 필드도 null로 처리한다")
        void createCriteria_BlankKeyword_SetsNullSearchField() {
            // given
            OmsShopSearchParams params = OmsProductQueryFixtures.omsShopSearchParams("  ");
            ShopSearchCriteria expected = ShopSearchCriteria.defaultCriteria();

            ArgumentCaptor<ShopSearchParams> captor =
                    ArgumentCaptor.forClass(ShopSearchParams.class);
            given(shopQueryFactory.createCriteria(captor.capture())).willReturn(expected);

            // when
            sut.createCriteria(params);

            // then
            ShopSearchParams capturedParams = captor.getValue();
            assertThat(capturedParams.searchField()).isNull();
        }

        @Test
        @DisplayName("공통 검색 파라미터를 ShopSearchParams에 그대로 전달한다")
        void createCriteria_PassesCommonSearchParamsToShopQueryFactory() {
            // given
            OmsShopSearchParams params = OmsProductQueryFixtures.omsShopSearchParams(0, 20);
            ShopSearchCriteria expected = ShopSearchCriteria.defaultCriteria();

            ArgumentCaptor<ShopSearchParams> captor =
                    ArgumentCaptor.forClass(ShopSearchParams.class);
            given(shopQueryFactory.createCriteria(captor.capture())).willReturn(expected);

            // when
            sut.createCriteria(params);

            // then
            ShopSearchParams capturedParams = captor.getValue();
            assertThat(capturedParams.page()).isEqualTo(0);
            assertThat(capturedParams.size()).isEqualTo(20);
        }
    }
}
