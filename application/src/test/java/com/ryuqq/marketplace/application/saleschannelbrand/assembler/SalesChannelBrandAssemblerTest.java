package com.ryuqq.marketplace.application.saleschannelbrand.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.saleschannelbrand.dto.response.SalesChannelBrandPageResult;
import com.ryuqq.marketplace.application.saleschannelbrand.dto.response.SalesChannelBrandResult;
import com.ryuqq.marketplace.domain.saleschannelbrand.SalesChannelBrandFixtures;
import com.ryuqq.marketplace.domain.saleschannelbrand.aggregate.SalesChannelBrand;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SalesChannelBrandAssembler 단위 테스트")
class SalesChannelBrandAssemblerTest {

    private SalesChannelBrandAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new SalesChannelBrandAssembler();
    }

    @Nested
    @DisplayName("toResult() - SalesChannelBrand Domain → SalesChannelBrandResult 변환")
    class ToResultTest {

        @Test
        @DisplayName("활성 SalesChannelBrand를 SalesChannelBrandResult로 변환한다")
        void toResult_ActiveBrand_ReturnsResult() {
            // given
            SalesChannelBrand brand = SalesChannelBrandFixtures.activeSalesChannelBrand();

            // when
            SalesChannelBrandResult result = sut.toResult(brand);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(brand.idValue());
            assertThat(result.salesChannelId()).isEqualTo(brand.salesChannelId());
            assertThat(result.externalBrandCode()).isEqualTo(brand.externalBrandCode());
            assertThat(result.externalBrandName()).isEqualTo(brand.externalBrandName());
            assertThat(result.status()).isEqualTo("ACTIVE");
            assertThat(result.createdAt()).isEqualTo(brand.createdAt());
            assertThat(result.updatedAt()).isEqualTo(brand.updatedAt());
        }

        @Test
        @DisplayName("비활성 SalesChannelBrand를 변환하면 status가 INACTIVE이다")
        void toResult_InactiveBrand_ReturnsInactiveResult() {
            // given
            SalesChannelBrand brand = SalesChannelBrandFixtures.inactiveSalesChannelBrand();

            // when
            SalesChannelBrandResult result = sut.toResult(brand);

            // then
            assertThat(result.status()).isEqualTo("INACTIVE");
        }
    }

    @Nested
    @DisplayName("toResults() - SalesChannelBrand List → SalesChannelBrandResult List 변환")
    class ToResultsTest {

        @Test
        @DisplayName("SalesChannelBrand 목록을 SalesChannelBrandResult 목록으로 변환한다")
        void toResults_ReturnsList() {
            // given
            List<SalesChannelBrand> brands =
                    List.of(
                            SalesChannelBrandFixtures.activeSalesChannelBrand(1L),
                            SalesChannelBrandFixtures.activeSalesChannelBrand(2L),
                            SalesChannelBrandFixtures.inactiveSalesChannelBrand());

            // when
            List<SalesChannelBrandResult> results = sut.toResults(brands);

            // then
            assertThat(results).hasSize(3);
            assertThat(results.get(0).id()).isEqualTo(1L);
            assertThat(results.get(1).id()).isEqualTo(2L);
            assertThat(results.get(2).status()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("빈 목록을 변환하면 빈 결과를 반환한다")
        void toResults_EmptyList_ReturnsEmpty() {
            // given
            List<SalesChannelBrand> brands = List.of();

            // when
            List<SalesChannelBrandResult> results = sut.toResults(brands);

            // then
            assertThat(results).isEmpty();
        }
    }

    @Nested
    @DisplayName("toPageResult() - 페이지 결과 생성")
    class ToPageResultTest {

        @Test
        @DisplayName("SalesChannelBrand 목록으로 PageResult를 생성한다")
        void toPageResult_ReturnsPageResult() {
            // given
            List<SalesChannelBrand> brands =
                    List.of(SalesChannelBrandFixtures.activeSalesChannelBrand());
            int page = 0;
            int size = 20;
            long totalElements = 1L;

            // when
            SalesChannelBrandPageResult result =
                    sut.toPageResult(brands, page, size, totalElements);

            // then
            assertThat(result).isNotNull();
            assertThat(result.results()).hasSize(1);
            assertThat(result.pageMeta().totalElements()).isEqualTo(totalElements);
            assertThat(result.pageMeta().page()).isEqualTo(page);
            assertThat(result.pageMeta().size()).isEqualTo(size);
        }

        @Test
        @DisplayName("빈 목록으로 빈 PageResult를 생성한다")
        void toPageResult_EmptyList_ReturnsEmptyPageResult() {
            // given
            List<SalesChannelBrand> brands = List.of();
            int page = 0;
            int size = 20;
            long totalElements = 0L;

            // when
            SalesChannelBrandPageResult result =
                    sut.toPageResult(brands, page, size, totalElements);

            // then
            assertThat(result.results()).isEmpty();
            assertThat(result.pageMeta().totalElements()).isZero();
        }

        @Test
        @DisplayName("다음 페이지가 있으면 hasNext가 true이다")
        void toPageResult_HasMorePages_HasNextIsTrue() {
            // given
            List<SalesChannelBrand> brands =
                    List.of(
                            SalesChannelBrandFixtures.activeSalesChannelBrand(1L),
                            SalesChannelBrandFixtures.activeSalesChannelBrand(2L));
            int page = 0;
            int size = 2;
            long totalElements = 10L;

            // when
            SalesChannelBrandPageResult result =
                    sut.toPageResult(brands, page, size, totalElements);

            // then
            assertThat(result.pageMeta().hasNext()).isTrue();
        }

        @Test
        @DisplayName("마지막 페이지이면 hasNext가 false이다")
        void toPageResult_LastPage_HasNextIsFalse() {
            // given
            List<SalesChannelBrand> brands =
                    List.of(SalesChannelBrandFixtures.activeSalesChannelBrand(1L));
            int page = 4;
            int size = 2;
            long totalElements = 10L;

            // when
            SalesChannelBrandPageResult result =
                    sut.toPageResult(brands, page, size, totalElements);

            // then
            assertThat(result.pageMeta().hasNext()).isFalse();
        }
    }
}
