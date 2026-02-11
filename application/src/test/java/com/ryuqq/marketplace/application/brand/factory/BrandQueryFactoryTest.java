package com.ryuqq.marketplace.application.brand.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.brand.BrandQueryFixtures;
import com.ryuqq.marketplace.application.brand.dto.query.BrandSearchParams;
import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.domain.brand.query.BrandSearchCriteria;
import com.ryuqq.marketplace.domain.brand.query.BrandSearchField;
import com.ryuqq.marketplace.domain.brand.query.BrandSortKey;
import com.ryuqq.marketplace.domain.brand.vo.BrandStatus;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import java.util.List;
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
@DisplayName("BrandQueryFactory 단위 테스트")
class BrandQueryFactoryTest {

    @InjectMocks private BrandQueryFactory sut;

    @Mock private CommonVoFactory commonVoFactory;

    @Nested
    @DisplayName("createCriteria() - 검색 조건 생성")
    class CreateCriteriaTest {

        @Test
        @DisplayName("검색 파라미터를 도메인 검색 조건으로 변환한다")
        void createCriteria_ValidParams_ReturnsCriteria() {
            // given
            BrandSearchParams params = BrandQueryFixtures.searchParams();
            SortDirection sortDirection = SortDirection.DESC;
            PageRequest pageRequest = PageRequest.of(0, 20);
            QueryContext<BrandSortKey> queryContext =
                    QueryContext.of(BrandSortKey.CREATED_AT, sortDirection, pageRequest, false);

            given(commonVoFactory.parseSortDirection(params.sortDirection()))
                    .willReturn(sortDirection);
            given(commonVoFactory.createPageRequest(params.page(), params.size()))
                    .willReturn(pageRequest);
            given(commonVoFactory.createQueryContext(
                            BrandSortKey.CREATED_AT, sortDirection, pageRequest, false))
                    .willReturn(queryContext);

            // when
            BrandSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.queryContext()).isEqualTo(queryContext);
            then(commonVoFactory).should().parseSortDirection(params.sortDirection());
            then(commonVoFactory).should().createPageRequest(params.page(), params.size());
        }

        @Test
        @DisplayName("상태 필터가 있는 검색 파라미터를 변환한다")
        void createCriteria_WithStatuses_ReturnsCriteria() {
            // given
            List<String> statuses = List.of("ACTIVE", "INACTIVE");
            BrandSearchParams params = BrandQueryFixtures.searchParams(statuses);
            SortDirection sortDirection = SortDirection.DESC;
            PageRequest pageRequest = PageRequest.of(0, 20);
            QueryContext<BrandSortKey> queryContext =
                    QueryContext.of(BrandSortKey.CREATED_AT, sortDirection, pageRequest, false);

            given(commonVoFactory.parseSortDirection(params.sortDirection()))
                    .willReturn(sortDirection);
            given(commonVoFactory.createPageRequest(params.page(), params.size()))
                    .willReturn(pageRequest);
            given(commonVoFactory.createQueryContext(
                            BrandSortKey.CREATED_AT, sortDirection, pageRequest, false))
                    .willReturn(queryContext);

            // when
            BrandSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.statuses()).hasSize(2);
            assertThat(result.statuses()).contains(BrandStatus.ACTIVE, BrandStatus.INACTIVE);
        }

        @Test
        @DisplayName("검색 필드와 검색어가 있는 파라미터를 변환한다")
        void createCriteria_WithSearchField_ReturnsCriteria() {
            // given
            BrandSearchParams params = BrandQueryFixtures.searchParams("nameKo", "테스트");
            SortDirection sortDirection = SortDirection.DESC;
            PageRequest pageRequest = PageRequest.of(0, 20);
            QueryContext<BrandSortKey> queryContext =
                    QueryContext.of(BrandSortKey.CREATED_AT, sortDirection, pageRequest, false);

            given(commonVoFactory.parseSortDirection(params.sortDirection()))
                    .willReturn(sortDirection);
            given(commonVoFactory.createPageRequest(params.page(), params.size()))
                    .willReturn(pageRequest);
            given(commonVoFactory.createQueryContext(
                            BrandSortKey.CREATED_AT, sortDirection, pageRequest, false))
                    .willReturn(queryContext);

            // when
            BrandSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.searchField()).isEqualTo(BrandSearchField.NAME_KO);
            assertThat(result.searchWord()).isEqualTo("테스트");
        }

        @Test
        @DisplayName("정렬 키가 없으면 기본 정렬 키를 사용한다")
        void createCriteria_NoSortKey_UsesDefaultKey() {
            // given
            BrandSearchParams params = BrandQueryFixtures.searchParams();
            SortDirection sortDirection = SortDirection.DESC;
            PageRequest pageRequest = PageRequest.of(0, 20);
            QueryContext<BrandSortKey> queryContext =
                    QueryContext.of(BrandSortKey.defaultKey(), sortDirection, pageRequest, false);

            given(commonVoFactory.parseSortDirection(params.sortDirection()))
                    .willReturn(sortDirection);
            given(commonVoFactory.createPageRequest(params.page(), params.size()))
                    .willReturn(pageRequest);
            given(commonVoFactory.createQueryContext(
                            BrandSortKey.defaultKey(), sortDirection, pageRequest, false))
                    .willReturn(queryContext);

            // when
            BrandSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.queryContext().sortKey()).isEqualTo(BrandSortKey.defaultKey());
        }
    }
}
