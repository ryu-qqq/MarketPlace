package com.ryuqq.marketplace.adapter.out.client.naver.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverCommerceBrand;
import com.ryuqq.marketplace.application.saleschannelbrand.dto.response.ExternalBrandResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("NaverCommerceBrandMapper 단위 테스트")
class NaverCommerceBrandMapperTest {

    private final NaverCommerceBrandMapper sut = new NaverCommerceBrandMapper();

    @Test
    @DisplayName("NaverCommerceBrand를 ExternalBrandResult로 변환한다")
    void toExternalBrandResult() {
        NaverCommerceBrand brand = new NaverCommerceBrand(12345L, "구찌");

        ExternalBrandResult result = sut.toExternalBrandResult(brand);

        assertThat(result.externalBrandCode()).isEqualTo("12345");
        assertThat(result.externalBrandName()).isEqualTo("구찌");
    }

    @Test
    @DisplayName("브랜드 ID가 0이면 문자열 '0'으로 변환된다")
    void zeroIdConvertedToString() {
        NaverCommerceBrand brand = new NaverCommerceBrand(0L, "미분류");

        ExternalBrandResult result = sut.toExternalBrandResult(brand);

        assertThat(result.externalBrandCode()).isEqualTo("0");
    }
}
