package com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.PersistenceMysqlLegacyTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.LegacyCompositeProductTestHelper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.dto.LegacyProductGroupBasicQueryDto;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.dto.LegacyProductGroupImageQueryDto;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/**
 * LegacyProductGroupDetailQueryDslRepositoryTest - 상품그룹 상세 QueryDSL Repository 통합 테스트.
 *
 * <p>7개 테이블 JOIN 기본 정보 조회 + 이미지 조회를 검증합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("integration")
@DataJpaTest
@ContextConfiguration(classes = PersistenceMysqlLegacyTestApplication.class)
@TestPropertySource(
        properties = {
            "spring.flyway.enabled=false",
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.jpa.show-sql=false",
            "persistence.legacy.enabled=false"
        })
@DisplayName("LegacyProductGroupDetailQueryDslRepository 통합 테스트")
class LegacyProductGroupDetailQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private LegacyProductGroupDetailQueryDslRepository repository;
    private LegacyCompositeProductTestHelper helper;

    @BeforeEach
    void setUp() {
        repository = new LegacyProductGroupDetailQueryDslRepository(
                new JPAQueryFactory(entityManager));
        helper = new LegacyCompositeProductTestHelper(entityManager);
    }

    // ========================================================================
    // 1. fetchBasicInfo 테스트
    // ========================================================================

    @Nested
    @DisplayName("fetchBasicInfo 메서드 테스트")
    class FetchBasicInfoTest {

        @Test
        @DisplayName("7개 테이블 JOIN으로 기본 정보를 조회합니다")
        void fetchBasicInfo_WithFullData_ReturnsBasicDto() {
            // given
            long pgId = helper.setupFullProductGroupData();

            // when
            Optional<LegacyProductGroupBasicQueryDto> result =
                    repository.fetchBasicInfo(pgId);

            // then
            assertThat(result).isPresent();
            LegacyProductGroupBasicQueryDto dto = result.get();
            assertThat(dto.productGroupId()).isEqualTo(pgId);
            assertThat(dto.productGroupName()).isEqualTo("테스트 상품그룹");
            assertThat(dto.sellerId()).isEqualTo(10L);
            assertThat(dto.brandId()).isEqualTo(20L);
            assertThat(dto.categoryId()).isEqualTo(30L);
            // seller join
            assertThat(dto.sellerName()).isEqualTo("테스트 셀러");
            // brand join
            assertThat(dto.brandName()).isEqualTo("나이키");
            // category join
            assertThat(dto.categoryPath()).isEqualTo("패션>의류>상의");
            // delivery join
            assertThat(dto.deliveryArea()).isEqualTo("NATIONWIDE");
            assertThat(dto.deliveryFee()).isEqualTo(3000);
            // description join
            assertThat(dto.detailDescription()).isEqualTo("<p>상품 상세 설명</p>");
            // notice join
            assertThat(dto.material()).isEqualTo("면100%");
            assertThat(dto.color()).isEqualTo("블랙");
        }

        @Test
        @DisplayName("배송/고시/설명이 없는 상품그룹도 LEFT JOIN으로 조회됩니다")
        void fetchBasicInfo_WithMinimalData_ReturnsNullForOptionalJoins() {
            // given
            helper.insertSeller(14L, "셀러4");
            helper.insertBrand(24L, "브랜드4");
            helper.insertCategory(34L, "카테고리4", "400");

            long pgId = helper.persistProductGroup(14L, 24L, 34L);
            helper.flushAndClear();

            // when
            Optional<LegacyProductGroupBasicQueryDto> result =
                    repository.fetchBasicInfo(pgId);

            // then
            assertThat(result).isPresent();
            LegacyProductGroupBasicQueryDto dto = result.get();
            assertThat(dto.productGroupId()).isEqualTo(pgId);
            assertThat(dto.sellerName()).isEqualTo("셀러4");
            assertThat(dto.brandName()).isEqualTo("브랜드4");
            // LEFT JOIN 결과 null
            assertThat(dto.deliveryArea()).isNull();
            assertThat(dto.detailDescription()).isNull();
            assertThat(dto.material()).isNull();
        }

        @Test
        @DisplayName("존재하지 않는 상품그룹 ID로 조회 시 빈 Optional을 반환합니다")
        void fetchBasicInfo_WithNonExistentId_ReturnsEmpty() {
            // when
            Optional<LegacyProductGroupBasicQueryDto> result =
                    repository.fetchBasicInfo(99999L);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("삭제된 상품그룹(deleteYn='Y')은 조회되지 않습니다")
        void fetchBasicInfo_WithDeletedProductGroup_ReturnsEmpty() {
            // given
            helper.insertSeller(15L, "셀러5");
            helper.insertBrand(25L, "브랜드5");
            helper.insertCategory(35L, "카테고리5", "500");

            // 삭제된 상품그룹을 Native SQL로 삽입
            entityManager
                    .createNativeQuery(
                            "INSERT INTO product_group (product_group_name, seller_id, brand_id, category_id, "
                                    + "option_type, management_type, regular_price, current_price, sale_price, "
                                    + "sold_out_yn, display_yn, delete_yn) "
                                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
                    .setParameter(1, "삭제된 상품그룹")
                    .setParameter(2, 15L)
                    .setParameter(3, 25L)
                    .setParameter(4, 35L)
                    .setParameter(5, "SINGLE")
                    .setParameter(6, "SYSTEM")
                    .setParameter(7, 50000L)
                    .setParameter(8, 45000L)
                    .setParameter(9, 45000L)
                    .setParameter(10, "N")
                    .setParameter(11, "Y")
                    .setParameter(12, "Y")
                    .executeUpdate();

            Long deletedId =
                    ((Number)
                                    entityManager
                                            .createNativeQuery(
                                                    "SELECT MAX(product_group_id) FROM product_group")
                                            .getSingleResult())
                            .longValue();
            helper.flushAndClear();

            // when
            Optional<LegacyProductGroupBasicQueryDto> result =
                    repository.fetchBasicInfo(deletedId);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("directDiscountPrice가 null인 경우 0으로 대체됩니다")
        void fetchBasicInfo_WithNullDiscountPrice_ReturnsZero() {
            // given
            long pgId = helper.setupFullProductGroupData();

            // when
            Optional<LegacyProductGroupBasicQueryDto> result =
                    repository.fetchBasicInfo(pgId);

            // then
            assertThat(result).isPresent();
            // create 메서드에서 directDiscountPrice=0, directDiscountRate=0, discountRate=0 설정
            assertThat(result.get().directDiscountPrice()).isZero();
            assertThat(result.get().directDiscountRate()).isZero();
            assertThat(result.get().discountRate()).isZero();
        }
    }

    // ========================================================================
    // 2. fetchImages 테스트
    // ========================================================================

    @Nested
    @DisplayName("fetchImages 메서드 테스트")
    class FetchImagesTest {

        @Test
        @DisplayName("상품그룹의 이미지 목록을 조회합니다")
        void fetchImages_WithExistingImages_ReturnsImageList() {
            // given
            long pgId = helper.setupFullProductGroupData();

            // when
            List<LegacyProductGroupImageQueryDto> images = repository.fetchImages(pgId);

            // then
            assertThat(images).hasSize(2);
            assertThat(images)
                    .extracting(LegacyProductGroupImageQueryDto::imageType)
                    .containsExactlyInAnyOrder("MAIN", "DETAIL");
        }

        @Test
        @DisplayName("이미지가 없는 상품그룹은 빈 목록을 반환합니다")
        void fetchImages_WithNoImages_ReturnsEmptyList() {
            // given
            helper.insertSeller(16L, "셀러6");
            helper.insertBrand(26L, "브랜드6");
            helper.insertCategory(36L, "카테고리6", "600");

            long pgId = helper.persistProductGroup(16L, 26L, 36L);
            helper.flushAndClear();

            // when
            List<LegacyProductGroupImageQueryDto> images = repository.fetchImages(pgId);

            // then
            assertThat(images).isEmpty();
        }

        @Test
        @DisplayName("삭제된 이미지(deleteYn='Y')는 조회에서 제외됩니다")
        void fetchImages_WithDeletedImage_ExcludesDeleted() {
            // given
            helper.insertSeller(17L, "셀러7");
            helper.insertBrand(27L, "브랜드7");
            helper.insertCategory(37L, "카테고리7", "700");

            long pgId = helper.persistProductGroup(17L, 27L, 37L);
            helper.persistImage(pgId, "MAIN", "https://cdn.example.com/main.jpg");

            // 삭제된 이미지
            entityManager
                    .createNativeQuery(
                            "INSERT INTO product_group_image (product_group_id, product_group_image_type, image_url, delete_yn) VALUES (?, ?, ?, ?)")
                    .setParameter(1, pgId)
                    .setParameter(2, "DETAIL")
                    .setParameter(3, "https://cdn.example.com/deleted.jpg")
                    .setParameter(4, "Y")
                    .executeUpdate();
            helper.flushAndClear();

            // when
            List<LegacyProductGroupImageQueryDto> images = repository.fetchImages(pgId);

            // then
            assertThat(images).hasSize(1);
            assertThat(images.get(0).imageType()).isEqualTo("MAIN");
        }

        @Test
        @DisplayName("존재하지 않는 상품그룹 ID로 조회 시 빈 목록을 반환합니다")
        void fetchImages_WithNonExistentId_ReturnsEmptyList() {
            // when
            List<LegacyProductGroupImageQueryDto> images =
                    repository.fetchImages(99999L);

            // then
            assertThat(images).isEmpty();
        }
    }
}
