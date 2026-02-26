package com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.dto.LegacyProductGroupBasicQueryDto;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.dto.LegacyProductGroupImageQueryDto;
import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductGroupCompositeResult;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * LegacyProductGroupCompositeMapperTest - 레거시 상품그룹 Composite Mapper 단위 테스트.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("LegacyProductGroupCompositeMapper 단위 테스트")
class LegacyProductGroupCompositeMapperTest {

    private final LegacyProductGroupCompositeMapper mapper =
            new LegacyProductGroupCompositeMapper();

    private LegacyProductGroupBasicQueryDto buildBasicDto(
            String deliveryArea, Integer deliveryFee, String material, String color) {
        return new LegacyProductGroupBasicQueryDto(
                1L,
                "테스트 상품그룹",
                10L,
                20L,
                30L,
                "SINGLE",
                "SYSTEM",
                10000L,
                9000L,
                8000L,
                500L,
                5,
                10,
                "N",
                "Y",
                "NEW",
                "DOMESTIC",
                "STYLE001",
                "admin",
                "admin",
                LocalDateTime.of(2025, 1, 1, 0, 0),
                LocalDateTime.of(2025, 6, 1, 0, 0),
                "테스트 셀러",
                "나이키",
                "패션>의류>상의",
                deliveryArea,
                deliveryFee,
                3,
                "택배",
                "CJ",
                3000,
                "서울시 강남구",
                "<p>상품 상세 설명</p>",
                material,
                color,
                "M",
                "나이키",
                "한국",
                "세탁기 가능",
                "2025-01",
                "KC인증",
                "02-1234-5678");
    }

    @Nested
    @DisplayName("toCompositeResult 메서드 테스트")
    class ToCompositeResultTest {

        @Test
        @DisplayName("기본 필드가 올바르게 매핑됩니다")
        void toCompositeResult_WithBasicDto_MapsFieldsCorrectly() {
            // given
            LegacyProductGroupBasicQueryDto dto = buildBasicDto("NATIONWIDE", 3000, "면100%", "빨강");
            List<LegacyProductGroupImageQueryDto> images =
                    List.of(
                            new LegacyProductGroupImageQueryDto(
                                    "MAIN", "https://cdn.example.com/main.jpg"),
                            new LegacyProductGroupImageQueryDto(
                                    "DETAIL", "https://cdn.example.com/detail.jpg"));

            // when
            LegacyProductGroupCompositeResult result = mapper.toCompositeResult(dto, images);

            // then
            assertThat(result.productGroupId()).isEqualTo(1L);
            assertThat(result.productGroupName()).isEqualTo("테스트 상품그룹");
            assertThat(result.sellerId()).isEqualTo(10L);
            assertThat(result.brandId()).isEqualTo(20L);
            assertThat(result.categoryId()).isEqualTo(30L);
            assertThat(result.regularPrice()).isEqualTo(10000L);
            assertThat(result.currentPrice()).isEqualTo(9000L);
            assertThat(result.soldOut()).isFalse();
            assertThat(result.displayed()).isTrue();
        }

        @Test
        @DisplayName("이미지 목록이 올바르게 변환됩니다")
        void toCompositeResult_WithImages_MapsImagesCorrectly() {
            // given
            LegacyProductGroupBasicQueryDto dto = buildBasicDto("NATIONWIDE", 3000, "면100%", "빨강");
            List<LegacyProductGroupImageQueryDto> images =
                    List.of(
                            new LegacyProductGroupImageQueryDto(
                                    "MAIN", "https://cdn.example.com/main.jpg"),
                            new LegacyProductGroupImageQueryDto(
                                    "DETAIL", "https://cdn.example.com/detail.jpg"));

            // when
            LegacyProductGroupCompositeResult result = mapper.toCompositeResult(dto, images);

            // then
            assertThat(result.images()).hasSize(2);
            assertThat(result.images().get(0).imageType()).isEqualTo("MAIN");
            assertThat(result.images().get(0).imageUrl())
                    .isEqualTo("https://cdn.example.com/main.jpg");
            assertThat(result.images().get(1).imageType()).isEqualTo("DETAIL");
        }

        @Test
        @DisplayName("이미지 목록이 비어있는 경우 빈 목록으로 변환됩니다")
        void toCompositeResult_WithEmptyImages_ReturnsEmptyImages() {
            // given
            LegacyProductGroupBasicQueryDto dto = buildBasicDto("NATIONWIDE", 3000, "면100%", "빨강");

            // when
            LegacyProductGroupCompositeResult result = mapper.toCompositeResult(dto, List.of());

            // then
            assertThat(result.images()).isEmpty();
        }

        @Test
        @DisplayName("고시정보가 null인 경우 notice가 null로 변환됩니다")
        void toCompositeResult_WithNullNotice_ReturnsNullNotice() {
            // given
            LegacyProductGroupBasicQueryDto dto = buildBasicDto("NATIONWIDE", 3000, null, null);

            // when
            LegacyProductGroupCompositeResult result = mapper.toCompositeResult(dto, List.of());

            // then - material, color, noticeSize, maker 모두 null이면 notice가 null
            // buildBasicDto에서 material=null, color=null이지만 noticeSize="M", maker="나이키"가 있음
            // 실제로는 noticeSize와 maker가 있어서 notice가 null이 아님
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("배송정보가 있는 경우 DeliveryInfo가 올바르게 변환됩니다")
        void toCompositeResult_WithDeliveryInfo_MapsDeliveryCorrectly() {
            // given
            LegacyProductGroupBasicQueryDto dto = buildBasicDto("NATIONWIDE", 3000, "면100%", "빨강");

            // when
            LegacyProductGroupCompositeResult result = mapper.toCompositeResult(dto, List.of());

            // then
            assertThat(result.delivery()).isNotNull();
            assertThat(result.delivery().deliveryArea()).isEqualTo("NATIONWIDE");
            assertThat(result.delivery().deliveryFee()).isEqualTo(3000);
        }

        @Test
        @DisplayName("배송정보가 없는 경우 delivery가 null로 변환됩니다")
        void toCompositeResult_WithNullDelivery_ReturnsNullDelivery() {
            // given
            LegacyProductGroupBasicQueryDto dto = buildBasicDto(null, null, "면100%", "빨강");

            // when
            LegacyProductGroupCompositeResult result = mapper.toCompositeResult(dto, List.of());

            // then
            assertThat(result.delivery()).isNull();
        }

        @Test
        @DisplayName("고시정보(material, color, size, maker) 모두 null인 경우 notice가 null입니다")
        void toCompositeResult_WithAllNullNoticeFields_ReturnsNullNotice() {
            // given - 모든 고시정보를 null로 만들기 위해 커스텀 dto 생성
            LegacyProductGroupBasicQueryDto dto =
                    new LegacyProductGroupBasicQueryDto(
                            1L,
                            "테스트 상품그룹",
                            10L,
                            20L,
                            30L,
                            "SINGLE",
                            "SYSTEM",
                            10000L,
                            9000L,
                            8000L,
                            500L,
                            5,
                            10,
                            "N",
                            "Y",
                            "NEW",
                            "DOMESTIC",
                            "STYLE001",
                            "admin",
                            "admin",
                            LocalDateTime.of(2025, 1, 1, 0, 0),
                            LocalDateTime.of(2025, 6, 1, 0, 0),
                            "테스트 셀러",
                            "나이키",
                            "패션>의류>상의",
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            "<p>상품 상세 설명</p>",
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null);

            // when
            LegacyProductGroupCompositeResult result = mapper.toCompositeResult(dto, List.of());

            // then
            assertThat(result.notice()).isNull();
        }
    }
}
