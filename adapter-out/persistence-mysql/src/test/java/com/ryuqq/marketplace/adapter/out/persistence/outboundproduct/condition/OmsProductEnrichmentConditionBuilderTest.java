package com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.condition;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.BooleanExpression;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * OmsProductEnrichmentConditionBuilderTest - OMS 상품 enrichment 조건 빌더 단위 테스트.
 *
 * <p>PER-CND-002: 각 조건은 BooleanExpression 반환.
 */
@Tag("unit")
@DisplayName("OmsProductEnrichmentConditionBuilder 단위 테스트")
class OmsProductEnrichmentConditionBuilderTest {

    private OmsProductEnrichmentConditionBuilder conditionBuilder;

    @BeforeEach
    void setUp() {
        conditionBuilder = new OmsProductEnrichmentConditionBuilder();
    }

    // ========================================================================
    // 1. imageProductGroupIdIn 테스트
    // ========================================================================

    @Nested
    @DisplayName("imageProductGroupIdIn 메서드 테스트")
    class ImageProductGroupIdInTest {

        @Test
        @DisplayName("유효한 ID 목록 입력 시 BooleanExpression을 반환합니다")
        void imageProductGroupIdIn_WithValidIds_ReturnsBooleanExpression() {
            // given
            List<Long> pgIds = List.of(100L, 200L, 300L);

            // when
            BooleanExpression result = conditionBuilder.imageProductGroupIdIn(pgIds);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("단일 ID 입력 시 BooleanExpression을 반환합니다")
        void imageProductGroupIdIn_WithSingleId_ReturnsBooleanExpression() {
            // given
            List<Long> pgIds = List.of(100L);

            // when
            BooleanExpression result = conditionBuilder.imageProductGroupIdIn(pgIds);

            // then
            assertThat(result).isNotNull();
        }
    }

    // ========================================================================
    // 2. imageThumbnailType 테스트
    // ========================================================================

    @Nested
    @DisplayName("imageThumbnailType 메서드 테스트")
    class ImageThumbnailTypeTest {

        @Test
        @DisplayName("THUMBNAIL 타입 조건 BooleanExpression을 반환합니다")
        void imageThumbnailType_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.imageThumbnailType();

            // then
            assertThat(result).isNotNull();
        }
    }

    // ========================================================================
    // 3. imageNotDeleted 테스트
    // ========================================================================

    @Nested
    @DisplayName("imageNotDeleted 메서드 테스트")
    class ImageNotDeletedTest {

        @Test
        @DisplayName("이미지 삭제 제외 조건 BooleanExpression을 반환합니다")
        void imageNotDeleted_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.imageNotDeleted();

            // then
            assertThat(result).isNotNull();
        }
    }

    // ========================================================================
    // 4. productProductGroupIdIn 테스트
    // ========================================================================

    @Nested
    @DisplayName("productProductGroupIdIn 메서드 테스트")
    class ProductProductGroupIdInTest {

        @Test
        @DisplayName("유효한 ID 목록 입력 시 BooleanExpression을 반환합니다")
        void productProductGroupIdIn_WithValidIds_ReturnsBooleanExpression() {
            // given
            List<Long> pgIds = List.of(100L, 200L);

            // when
            BooleanExpression result = conditionBuilder.productProductGroupIdIn(pgIds);

            // then
            assertThat(result).isNotNull();
        }
    }

    // ========================================================================
    // 5. syncProductGroupIdIn 테스트
    // ========================================================================

    @Nested
    @DisplayName("syncProductGroupIdIn 메서드 테스트")
    class SyncProductGroupIdInTest {

        @Test
        @DisplayName("유효한 ID 목록 입력 시 BooleanExpression을 반환합니다")
        void syncProductGroupIdIn_WithValidIds_ReturnsBooleanExpression() {
            // given
            List<Long> pgIds = List.of(100L, 200L, 300L);

            // when
            BooleanExpression result = conditionBuilder.syncProductGroupIdIn(pgIds);

            // then
            assertThat(result).isNotNull();
        }
    }
}
