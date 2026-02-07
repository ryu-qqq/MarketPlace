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
            Long commonCodeTypeId = 1L;
            CommonCodeSearchParams params = CommonCodeQueryFixtures.searchParams(commonCodeTypeId);

            // when
            CommonCodeSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.commonCodeTypeId().value()).isEqualTo(commonCodeTypeId);
            assertThat(result.queryContext()).isNotNull();
        }

        @Test
        @DisplayName("필터 조건이 포함된 Criteria를 생성한다")
        void createCriteria_WithFilters_CreatesCriteriaWithFilters() {
            // given
            Long commonCodeTypeId = 1L;
            Boolean active = true;
            String code = "CREDIT";
            CommonCodeSearchParams params =
                    CommonCodeQueryFixtures.searchParams(commonCodeTypeId, active, code);

            // when
            CommonCodeSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result.active()).isEqualTo(active);
            assertThat(result.code()).isEqualTo(code);
        }

        @Test
        @DisplayName("페이징 정보가 포함된 Criteria를 생성한다")
        void createCriteria_WithPaging_CreatesCriteriaWithPaging() {
            // given
            Long commonCodeTypeId = 1L;
            int page = 2;
            int size = 10;
            CommonCodeSearchParams params =
                    CommonCodeQueryFixtures.searchParams(commonCodeTypeId, page, size);

            // when
            CommonCodeSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result.queryContext().page()).isEqualTo(page);
            assertThat(result.queryContext().size()).isEqualTo(size);
        }
    }
}
