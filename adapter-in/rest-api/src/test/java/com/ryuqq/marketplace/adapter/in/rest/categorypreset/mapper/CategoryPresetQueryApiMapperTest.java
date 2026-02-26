package com.ryuqq.marketplace.adapter.in.rest.categorypreset.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.categorypreset.CategoryPresetApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.categorypreset.dto.response.CategoryPresetDetailApiResponse;
import com.ryuqq.marketplace.application.categorypreset.dto.response.CategoryPresetDetailResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CategoryPresetQueryApiMapper 단위 테스트")
class CategoryPresetQueryApiMapperTest {

    private CategoryPresetQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CategoryPresetQueryApiMapper();
    }

    @Nested
    @DisplayName("toDetailResponse() - 상세 조회 응답 변환")
    class ToDetailResponseTest {

        @Test
        @DisplayName("CategoryPresetDetailResult를 CategoryPresetDetailApiResponse로 변환한다")
        void toDetailResponse_ConvertsResultToApiResponse() {
            // given
            CategoryPresetDetailResult result =
                    CategoryPresetApiFixtures.categoryPresetDetailResult(1L);

            // when
            CategoryPresetDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.id()).isEqualTo(result.id());
            assertThat(response.shopId()).isEqualTo(result.shopId());
            assertThat(response.shopName()).isEqualTo(result.shopName());
            assertThat(response.salesChannelId()).isEqualTo(result.salesChannelId());
            assertThat(response.salesChannelName()).isEqualTo(result.salesChannelName());
            assertThat(response.accountId()).isEqualTo(result.accountId());
            assertThat(response.presetName()).isEqualTo(result.presetName());
            assertThat(response.mappingCategory().categoryCode())
                    .isEqualTo(result.mappingCategory().categoryCode());
            assertThat(response.mappingCategory().categoryPath())
                    .isEqualTo(result.mappingCategory().categoryPath());
            assertThat(response.internalCategories()).hasSize(result.internalCategories().size());
            assertThat(response.createdAt()).isNotNull();
            assertThat(response.updatedAt()).isNotNull();
        }
    }
}
