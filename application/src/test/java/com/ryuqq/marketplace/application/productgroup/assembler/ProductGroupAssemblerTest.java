package com.ryuqq.marketplace.application.productgroup.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailBundle;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeQueryResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupListBundle;
import com.ryuqq.marketplace.application.productgroup.dto.response.ProductGroupPageResult;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ProductGroupAssembler 단위 테스트")
class ProductGroupAssemblerTest {

    private ProductGroupAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new ProductGroupAssembler();
    }

    @Nested
    @DisplayName("toPageResult() - 목록 번들 → PageResult 조립")
    class ToPageResultTest {

        @Test
        @DisplayName("빈 번들로 빈 PageResult를 생성한다")
        void toPageResult_EmptyBundle_ReturnsEmptyPageResult() {
            // given
            ProductGroupListBundle bundle = new ProductGroupListBundle(List.of(), List.of(), 0L);
            int page = 0;
            int size = 20;

            // when
            ProductGroupPageResult result = sut.toPageResult(bundle, page, size);

            // then
            assertThat(result).isNotNull();
            assertThat(result.isEmpty()).isTrue();
            assertThat(result.size()).isZero();
        }

        @Test
        @DisplayName("totalElements가 0인 빈 번들은 empty PageResult를 반환한다")
        void toPageResult_ZeroTotalElements_ReturnsEmptyPageResult() {
            // given
            ProductGroupListBundle bundle = new ProductGroupListBundle(List.of(), List.of(), 0L);

            // when
            ProductGroupPageResult result = sut.toPageResult(bundle, 0, 20);

            // then
            assertThat(result.pageMeta().totalElements()).isZero();
        }
    }

    @Nested
    @DisplayName("toDetailResult() - 상세 번들 → DetailCompositeResult 조립")
    class ToDetailResultTest {

        @Test
        @DisplayName("NONE 옵션 타입의 ProductGroup으로 DetailCompositeResult를 생성한다")
        void toDetailResult_NoneOptionGroup_ReturnsResult() {
            // given
            Long productGroupId = 1L;
            Instant now = Instant.now();
            ProductGroup group = ProductGroupFixtures.activeProductGroup();
            ProductGroupDetailBundle bundle = createDetailBundle(group, productGroupId, now);

            // when
            ProductGroupDetailCompositeResult result = sut.toDetailResult(bundle);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(productGroupId);
            assertThat(result.sellerId()).isEqualTo(ProductGroupFixtures.DEFAULT_SELLER_ID);
            assertThat(result.productGroupName())
                    .isEqualTo(ProductGroupFixtures.DEFAULT_PRODUCT_GROUP_NAME);
        }

        @Test
        @DisplayName("이미지 없는 ProductGroup으로 상세 조회 시 이미지 목록이 비어있다")
        void toDetailResult_GroupWithImages_MapsImages() {
            // given
            Long productGroupId = 1L;
            Instant now = Instant.now();
            ProductGroup group = ProductGroupFixtures.activeProductGroup();
            ProductGroupDetailBundle bundle = createDetailBundle(group, productGroupId, now);

            // when
            ProductGroupDetailCompositeResult result = sut.toDetailResult(bundle);

            // then
            assertThat(result).isNotNull();
            assertThat(result.images()).isNotNull();
        }

        @Test
        @DisplayName("description이 없을 때 description 필드가 null이다")
        void toDetailResult_WithoutDescription_DescriptionIsNull() {
            // given
            Long productGroupId = 1L;
            Instant now = Instant.now();
            ProductGroup group = ProductGroupFixtures.activeProductGroup();
            ProductGroupDetailBundle bundle =
                    new ProductGroupDetailBundle(
                            createQueryResult(productGroupId, now),
                            group,
                            List.of(),
                            Optional.empty(),
                            Optional.empty());

            // when
            ProductGroupDetailCompositeResult result = sut.toDetailResult(bundle);

            // then
            assertThat(result.description()).isNull();
        }

        @Test
        @DisplayName("notice가 없을 때 productNotice 필드가 null이다")
        void toDetailResult_WithoutNotice_NoticeIsNull() {
            // given
            Long productGroupId = 1L;
            Instant now = Instant.now();
            ProductGroup group = ProductGroupFixtures.activeProductGroup();
            ProductGroupDetailBundle bundle =
                    new ProductGroupDetailBundle(
                            createQueryResult(productGroupId, now),
                            group,
                            List.of(),
                            Optional.empty(),
                            Optional.empty());

            // when
            ProductGroupDetailCompositeResult result = sut.toDetailResult(bundle);

            // then
            assertThat(result.productNotice()).isNull();
        }

        @Test
        @DisplayName("단일 옵션 ProductGroup으로 DetailCompositeResult를 생성한다")
        void toDetailResult_SingleOptionGroup_ReturnsResultWithOptionMatrix() {
            // given
            Long productGroupId = 1L;
            Instant now = Instant.now();
            ProductGroup group = ProductGroupFixtures.newProductGroupWithSingleOption();
            ProductGroupDetailBundle bundle = createDetailBundle(group, productGroupId, now);

            // when
            ProductGroupDetailCompositeResult result = sut.toDetailResult(bundle);

            // then
            assertThat(result).isNotNull();
            assertThat(result.optionProductMatrix()).isNotNull();
            assertThat(result.optionProductMatrix().optionGroups()).isNotEmpty();
        }
    }

    private ProductGroupDetailBundle createDetailBundle(
            ProductGroup group, Long productGroupId, Instant now) {
        return new ProductGroupDetailBundle(
                createQueryResult(productGroupId, now),
                group,
                List.of(),
                Optional.empty(),
                Optional.empty());
    }

    private ProductGroupDetailCompositeQueryResult createQueryResult(
            Long productGroupId, Instant now) {
        return new ProductGroupDetailCompositeQueryResult(
                productGroupId,
                ProductGroupFixtures.DEFAULT_SELLER_ID,
                "테스트 셀러",
                ProductGroupFixtures.DEFAULT_BRAND_ID,
                "테스트 브랜드",
                ProductGroupFixtures.DEFAULT_CATEGORY_ID,
                "테스트 카테고리",
                "카테고리 > 테스트 카테고리",
                "1/200",
                ProductGroupFixtures.DEFAULT_PRODUCT_GROUP_NAME,
                "NONE",
                "ACTIVE",
                now,
                now,
                null,
                null);
    }
}
