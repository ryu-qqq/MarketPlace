package com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.dto.LegacyProductGroupBasicQueryDto;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.dto.LegacyProductGroupImageQueryDto;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.mapper.LegacyProductGroupCompositeMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.repository.LegacyProductGroupDetailQueryDslRepository;
import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductGroupCompositeResult;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * LegacyProductGroupCompositionQueryAdapterTest - 레거시 상품그룹 Composition Query Adapter 단위 테스트.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyProductGroupCompositionQueryAdapter 단위 테스트")
class LegacyProductGroupCompositionQueryAdapterTest {

    @Mock private LegacyProductGroupDetailQueryDslRepository detailQueryDslRepository;
    @Mock private LegacyProductGroupCompositeMapper mapper;

    @InjectMocks private LegacyProductGroupCompositionQueryAdapter queryAdapter;

    private LegacyProductGroupBasicQueryDto buildBasicDto(long productGroupId) {
        return new LegacyProductGroupBasicQueryDto(
                productGroupId,
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
                "NATIONWIDE",
                3000,
                3,
                "택배",
                "CJ",
                3000,
                "서울시 강남구",
                "<p>상품 상세 설명</p>",
                "면100%",
                "빨강",
                "M",
                "나이키",
                "한국",
                "세탁기 가능",
                "2025-01",
                "KC인증",
                "02-1234-5678");
    }

    @Nested
    @DisplayName("findCompositeById 메서드 테스트")
    class FindCompositeByIdTest {

        @Test
        @DisplayName("존재하는 상품그룹 ID로 조회 시 Composite 결과를 반환합니다")
        void findCompositeById_WithExistingId_ReturnsCompositeResult() {
            // given
            long productGroupId = 1L;
            LegacyProductGroupBasicQueryDto basicDto = buildBasicDto(productGroupId);
            List<LegacyProductGroupImageQueryDto> images =
                    List.of(
                            new LegacyProductGroupImageQueryDto(
                                    "MAIN", "https://cdn.example.com/main.jpg"));

            LegacyProductGroupCompositeResult expectedResult =
                    new LegacyProductGroupCompositeResult(
                            productGroupId,
                            "테스트 상품그룹",
                            10L,
                            "테스트 셀러",
                            20L,
                            "나이키",
                            30L,
                            "패션>의류>상의",
                            "SINGLE",
                            "SYSTEM",
                            10000L,
                            9000L,
                            8000L,
                            500L,
                            5,
                            10,
                            false,
                            true,
                            "NEW",
                            "DOMESTIC",
                            "STYLE001",
                            "admin",
                            "admin",
                            LocalDateTime.of(2025, 1, 1, 0, 0),
                            LocalDateTime.of(2025, 6, 1, 0, 0),
                            List.of(
                                    new LegacyProductGroupCompositeResult.ImageInfo(
                                            "MAIN", "https://cdn.example.com/main.jpg")),
                            "<p>상품 상세 설명</p>",
                            null,
                            null);

            given(detailQueryDslRepository.fetchBasicInfo(productGroupId))
                    .willReturn(Optional.of(basicDto));
            given(detailQueryDslRepository.fetchImages(productGroupId)).willReturn(images);
            given(mapper.toCompositeResult(basicDto, images)).willReturn(expectedResult);

            // when
            Optional<LegacyProductGroupCompositeResult> result =
                    queryAdapter.findCompositeById(productGroupId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().productGroupId()).isEqualTo(productGroupId);
            assertThat(result.get().productGroupName()).isEqualTo("테스트 상품그룹");
            then(detailQueryDslRepository).should().fetchBasicInfo(productGroupId);
            then(detailQueryDslRepository).should().fetchImages(productGroupId);
            then(mapper).should().toCompositeResult(basicDto, images);
        }

        @Test
        @DisplayName("존재하지 않는 상품그룹 ID로 조회 시 빈 Optional을 반환합니다")
        void findCompositeById_WithNonExistingId_ReturnsEmpty() {
            // given
            long productGroupId = 99L;
            given(detailQueryDslRepository.fetchBasicInfo(productGroupId))
                    .willReturn(Optional.empty());

            // when
            Optional<LegacyProductGroupCompositeResult> result =
                    queryAdapter.findCompositeById(productGroupId);

            // then
            assertThat(result).isEmpty();
            then(detailQueryDslRepository).should().fetchBasicInfo(productGroupId);
            then(detailQueryDslRepository).should(never()).fetchImages(productGroupId);
            then(mapper).shouldHaveNoInteractions();
        }
    }
}
