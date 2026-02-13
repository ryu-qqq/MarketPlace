package com.ryuqq.marketplace.adapter.in.rest.brandpreset.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.brandpreset.BrandPresetApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.brandpreset.dto.response.BrandPresetDetailApiResponse;
import com.ryuqq.marketplace.application.brandpreset.dto.response.BrandPresetDetailResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("BrandPresetQueryApiMapper 단위 테스트")
class BrandPresetQueryApiMapperTest {

    private BrandPresetQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new BrandPresetQueryApiMapper();
    }

    @Nested
    @DisplayName("toDetailResponse() - 상세 조회 응답 변환")
    class ToDetailResponseTest {

        @Test
        @DisplayName("BrandPresetDetailResult를 BrandPresetDetailApiResponse로 변환한다")
        void toDetailResponse_ConvertsResultToApiResponse() {
            // given
            BrandPresetDetailResult result = BrandPresetApiFixtures.brandPresetDetailResult(1L);

            // when
            BrandPresetDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.id()).isEqualTo(result.id());
            assertThat(response.shopId()).isEqualTo(result.shopId());
            assertThat(response.shopName()).isEqualTo(result.shopName());
            assertThat(response.salesChannelId()).isEqualTo(result.salesChannelId());
            assertThat(response.salesChannelName()).isEqualTo(result.salesChannelName());
            assertThat(response.accountId()).isEqualTo(result.accountId());
            assertThat(response.presetName()).isEqualTo(result.presetName());
            assertThat(response.mappingBrand().brandCode())
                    .isEqualTo(result.mappingBrand().brandCode());
            assertThat(response.mappingBrand().brandName())
                    .isEqualTo(result.mappingBrand().brandName());
            assertThat(response.internalBrands()).hasSize(result.internalBrands().size());
            assertThat(response.createdAt()).isNotNull();
            assertThat(response.updatedAt()).isNotNull();
        }
    }
}
