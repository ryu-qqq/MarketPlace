package com.ryuqq.marketplace.application.commoncode.factory;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.application.commoncode.CommonCodeQueryFixtures;
import com.ryuqq.marketplace.application.commoncode.dto.query.CommonCodeSearchParams;
import com.ryuqq.marketplace.domain.commoncode.query.CommonCodeSearchCriteria;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CommonCodeQueryFactory 단위 테스트")
class CommonCodeQueryFactoryTest {

    private final CommonCodeQueryFactory sut = new CommonCodeQueryFactory(new CommonVoFactory());

    @Nested
    @DisplayName("createCriteria() - SearchParams → SearchCriteria 변환")
    class CreateCriteriaTest {

        @Test
        @DisplayName("CommonCodeSearchParams로부터 CommonCodeSearchCriteria를 생성한다")
        void createCriteria_CreatesCriteria() {
            // given
            String commonCodeTypeCode = "PAYMENT_METHOD";
            CommonCodeSearchParams params =
                    CommonCodeQueryFixtures.searchParams(commonCodeTypeCode);

            // when
            CommonCodeSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.commonCodeTypeCode()).isEqualTo(commonCodeTypeCode);
            assertThat(result.queryContext()).isNotNull();
        }

        @Test
        @DisplayName("필터 조건이 포함된 Criteria를 생성한다")
        void createCriteria_WithFilters_CreatesCriteriaWithFilters() {
            // given
            String commonCodeTypeCode = "PAYMENT_METHOD";
            Boolean active = true;
            CommonCodeSearchParams params =
                    CommonCodeQueryFixtures.searchParams(commonCodeTypeCode, active);

            // when
            CommonCodeSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result.active()).isEqualTo(active);
            assertThat(result.commonCodeTypeCode()).isEqualTo(commonCodeTypeCode);
        }

        @Test
        @DisplayName("페이징 정보가 포함된 Criteria를 생성한다")
        void createCriteria_WithPaging_CreatesCriteriaWithPaging() {
            // given
            String commonCodeTypeCode = "PAYMENT_METHOD";
            int page = 2;
            int size = 10;
            CommonCodeSearchParams params =
                    CommonCodeQueryFixtures.searchParams(commonCodeTypeCode, page, size);

            // when
            CommonCodeSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result.queryContext().page()).isEqualTo(page);
            assertThat(result.queryContext().size()).isEqualTo(size);
        }
    }
}
