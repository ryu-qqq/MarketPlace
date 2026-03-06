package com.ryuqq.marketplace.application.outboundproduct.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.outboundproduct.OmsProductQueryFixtures;
import com.ryuqq.marketplace.application.outboundproduct.dto.query.OmsPartnerSearchParams;
import com.ryuqq.marketplace.application.seller.dto.query.SellerSearchParams;
import com.ryuqq.marketplace.application.seller.factory.SellerQueryFactory;
import com.ryuqq.marketplace.domain.seller.query.SellerSearchCriteria;
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
@DisplayName("OmsPartnerQueryFactory 단위 테스트")
class OmsPartnerQueryFactoryTest {

    private OmsPartnerQueryFactory sut;

    @Mock private SellerQueryFactory sellerQueryFactory;

    @BeforeEach
    void setUp() {
        sut = new OmsPartnerQueryFactory(sellerQueryFactory);
    }

    @Nested
    @DisplayName("createCriteria() - SellerSearchCriteria 생성")
    class CreateCriteriaTest {

        @Test
        @DisplayName("OmsPartnerSearchParams로 SellerSearchCriteria를 생성한다")
        void createCriteria_ValidParams_DelegatesToSellerQueryFactory() {
            // given
            OmsPartnerSearchParams params = OmsProductQueryFixtures.omsPartnerSearchParams();
            SellerSearchCriteria expected = SellerSearchCriteria.defaultCriteria();

            given(
                            sellerQueryFactory.createCriteria(
                                    org.mockito.ArgumentMatchers.any(SellerSearchParams.class)))
                    .willReturn(expected);

            // when
            SellerSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result).isEqualTo(expected);
            then(sellerQueryFactory)
                    .should()
                    .createCriteria(org.mockito.ArgumentMatchers.any(SellerSearchParams.class));
        }

        @Test
        @DisplayName("키워드가 있으면 SELLER_NAME 검색 필드로 변환하여 SellerQueryFactory에 전달한다")
        void createCriteria_WithKeyword_SetsSellerNameSearchField() {
            // given
            String keyword = "테스트 파트너";
            OmsPartnerSearchParams params = OmsProductQueryFixtures.omsPartnerSearchParams(keyword);
            SellerSearchCriteria expected = SellerSearchCriteria.defaultCriteria();

            ArgumentCaptor<SellerSearchParams> captor =
                    ArgumentCaptor.forClass(SellerSearchParams.class);
            given(sellerQueryFactory.createCriteria(captor.capture())).willReturn(expected);

            // when
            sut.createCriteria(params);

            // then
            SellerSearchParams capturedParams = captor.getValue();
            assertThat(capturedParams.searchField()).isEqualTo("SELLER_NAME");
            assertThat(capturedParams.searchWord()).isEqualTo(keyword);
        }

        @Test
        @DisplayName("키워드가 null이면 검색 필드도 null로 SellerQueryFactory에 전달한다")
        void createCriteria_NullKeyword_SetsNullSearchField() {
            // given
            OmsPartnerSearchParams params = OmsProductQueryFixtures.omsPartnerSearchParams();
            SellerSearchCriteria expected = SellerSearchCriteria.defaultCriteria();

            ArgumentCaptor<SellerSearchParams> captor =
                    ArgumentCaptor.forClass(SellerSearchParams.class);
            given(sellerQueryFactory.createCriteria(captor.capture())).willReturn(expected);

            // when
            sut.createCriteria(params);

            // then
            SellerSearchParams capturedParams = captor.getValue();
            assertThat(capturedParams.searchField()).isNull();
            assertThat(capturedParams.searchWord()).isNull();
        }

        @Test
        @DisplayName("키워드가 빈 문자열이면 검색 필드도 null로 처리한다")
        void createCriteria_BlankKeyword_SetsNullSearchField() {
            // given
            OmsPartnerSearchParams params = OmsProductQueryFixtures.omsPartnerSearchParams("  ");
            SellerSearchCriteria expected = SellerSearchCriteria.defaultCriteria();

            ArgumentCaptor<SellerSearchParams> captor =
                    ArgumentCaptor.forClass(SellerSearchParams.class);
            given(sellerQueryFactory.createCriteria(captor.capture())).willReturn(expected);

            // when
            sut.createCriteria(params);

            // then
            SellerSearchParams capturedParams = captor.getValue();
            assertThat(capturedParams.searchField()).isNull();
        }

        @Test
        @DisplayName("공통 검색 파라미터를 SellerSearchParams에 그대로 전달한다")
        void createCriteria_PassesCommonSearchParamsToSellerQueryFactory() {
            // given
            OmsPartnerSearchParams params = OmsProductQueryFixtures.omsPartnerSearchParams(0, 20);
            SellerSearchCriteria expected = SellerSearchCriteria.defaultCriteria();

            ArgumentCaptor<SellerSearchParams> captor =
                    ArgumentCaptor.forClass(SellerSearchParams.class);
            given(sellerQueryFactory.createCriteria(captor.capture())).willReturn(expected);

            // when
            sut.createCriteria(params);

            // then
            SellerSearchParams capturedParams = captor.getValue();
            assertThat(capturedParams.page()).isEqualTo(0);
            assertThat(capturedParams.size()).isEqualTo(20);
        }
    }
}
