package com.ryuqq.marketplace.adapter.out.client.naver.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverCommerceCategory;
import com.ryuqq.marketplace.application.saleschannelcategory.dto.response.ExternalCategoryResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("NaverCommerceCategoryMapper 단위 테스트")
class NaverCommerceCategoryMapperTest {

    private final NaverCommerceCategoryMapper sut = new NaverCommerceCategoryMapper();

    @Test
    @DisplayName("NaverCommerceCategory를 ExternalCategoryResult로 변환한다")
    void toExternalCategoryResult() {
        NaverCommerceCategory category =
                new NaverCommerceCategory("패션의류 > 남성의류 > 티셔츠", "50002322", "티셔츠", true);

        ExternalCategoryResult result = sut.toExternalCategoryResult(category);

        assertThat(result.externalCategoryCode()).isEqualTo("50002322");
        assertThat(result.externalCategoryName()).isEqualTo("티셔츠");
        assertThat(result.displayPath()).isEqualTo("패션의류 > 남성의류 > 티셔츠");
        assertThat(result.leaf()).isTrue();
    }

    @Test
    @DisplayName("최하위 카테고리가 아니면 last=false")
    void nonLeafCategory() {
        NaverCommerceCategory category =
                new NaverCommerceCategory("패션의류 > 남성의류", "50000000", "남성의류", false);

        ExternalCategoryResult result = sut.toExternalCategoryResult(category);

        assertThat(result.leaf()).isFalse();
    }
}
