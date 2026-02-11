package com.ryuqq.marketplace.application.canonicaloption.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.canonicaloption.dto.response.CanonicalOptionGroupPageResult;
import com.ryuqq.marketplace.application.canonicaloption.dto.response.CanonicalOptionGroupResult;
import com.ryuqq.marketplace.application.canonicaloption.dto.response.CanonicalOptionValueResult;
import com.ryuqq.marketplace.domain.canonicaloption.CanonicalOptionFixtures;
import com.ryuqq.marketplace.domain.canonicaloption.aggregate.CanonicalOptionGroup;
import com.ryuqq.marketplace.domain.canonicaloption.aggregate.CanonicalOptionValue;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CanonicalOptionGroupAssembler 단위 테스트")
class CanonicalOptionGroupAssemblerTest {

    private final CanonicalOptionGroupAssembler sut = new CanonicalOptionGroupAssembler();

    @Nested
    @DisplayName("toResult() - Domain + Values → Result 변환")
    class ToResultTest {

        @Test
        @DisplayName("CanonicalOptionGroup과 Values를 Result로 변환한다")
        void toResult_ReturnsCanonicalOptionGroupResult() {
            // given
            CanonicalOptionGroup group = CanonicalOptionFixtures.activeCanonicalOptionGroup();
            List<CanonicalOptionValue> values = CanonicalOptionFixtures.canonicalOptionValues();

            // when
            CanonicalOptionGroupResult result = sut.toResult(group, values);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(group.idValue());
            assertThat(result.code()).isEqualTo(group.codeValue());
            assertThat(result.nameKo()).isEqualTo(group.nameKo());
            assertThat(result.nameEn()).isEqualTo(group.nameEn());
            assertThat(result.active()).isEqualTo(group.isActive());
            assertThat(result.values()).hasSize(values.size());
        }

        @Test
        @DisplayName("비활성 상태의 CanonicalOptionGroup을 변환한다")
        void toResult_InactiveGroup_ReturnsInactiveResult() {
            // given
            CanonicalOptionGroup group = CanonicalOptionFixtures.inactiveCanonicalOptionGroup();
            List<CanonicalOptionValue> values = List.of();

            // when
            CanonicalOptionGroupResult result = sut.toResult(group, values);

            // then
            assertThat(result.active()).isFalse();
            assertThat(result.values()).isEmpty();
        }

        @Test
        @DisplayName("Values가 빈 목록이어도 정상 변환된다")
        void toResult_EmptyValues_ReturnsResultWithEmptyValues() {
            // given
            CanonicalOptionGroup group = CanonicalOptionFixtures.activeCanonicalOptionGroup();
            List<CanonicalOptionValue> values = List.of();

            // when
            CanonicalOptionGroupResult result = sut.toResult(group, values);

            // then
            assertThat(result.values()).isEmpty();
        }

        @Test
        @DisplayName("Values의 sortOrder가 정확히 변환된다")
        void toResult_ValuesWithSortOrder_CorrectlyConverted() {
            // given
            CanonicalOptionGroup group = CanonicalOptionFixtures.activeCanonicalOptionGroup();
            List<CanonicalOptionValue> values =
                    List.of(
                            CanonicalOptionFixtures.canonicalOptionValue(1L, 1),
                            CanonicalOptionFixtures.canonicalOptionValue(2L, 2),
                            CanonicalOptionFixtures.canonicalOptionValue(3L, 3));

            // when
            CanonicalOptionGroupResult result = sut.toResult(group, values);

            // then
            List<CanonicalOptionValueResult> valueResults = result.values();
            assertThat(valueResults.get(0).sortOrder()).isEqualTo(1);
            assertThat(valueResults.get(1).sortOrder()).isEqualTo(2);
            assertThat(valueResults.get(2).sortOrder()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("toPageResult() - 페이지 결과 생성")
    class ToPageResultTest {

        @Test
        @DisplayName("Result 목록으로 PageResult를 생성한다")
        void toPageResult_ReturnsPageResult() {
            // given
            CanonicalOptionGroupResult result1 =
                    sut.toResult(
                            CanonicalOptionFixtures.activeCanonicalOptionGroup(1L), List.of());
            CanonicalOptionGroupResult result2 =
                    sut.toResult(
                            CanonicalOptionFixtures.activeCanonicalOptionGroup(2L), List.of());
            List<CanonicalOptionGroupResult> results = List.of(result1, result2);
            int page = 0;
            int size = 20;
            long totalElements = 2L;

            // when
            CanonicalOptionGroupPageResult pageResult =
                    sut.toPageResult(results, page, size, totalElements);

            // then
            assertThat(pageResult).isNotNull();
            assertThat(pageResult.results()).hasSize(2);
            assertThat(pageResult.pageMeta().totalElements()).isEqualTo(totalElements);
        }

        @Test
        @DisplayName("빈 Result 목록으로 빈 PageResult를 생성한다")
        void toPageResult_EmptyResults_ReturnsEmptyPageResult() {
            // given
            List<CanonicalOptionGroupResult> results = List.of();
            int page = 0;
            int size = 20;
            long totalElements = 0L;

            // when
            CanonicalOptionGroupPageResult pageResult =
                    sut.toPageResult(results, page, size, totalElements);

            // then
            assertThat(pageResult.results()).isEmpty();
            assertThat(pageResult.pageMeta().totalElements()).isZero();
        }

        @Test
        @DisplayName("여러 페이지가 있는 경우 PageResult를 생성한다")
        void toPageResult_MultiplePages_ReturnsCorrectPageResult() {
            // given
            List<CanonicalOptionGroupResult> results =
                    List.of(
                            sut.toResult(
                                    CanonicalOptionFixtures.activeCanonicalOptionGroup(1L),
                                    List.of()),
                            sut.toResult(
                                    CanonicalOptionFixtures.activeCanonicalOptionGroup(2L),
                                    List.of()));
            int page = 1;
            int size = 2;
            long totalElements = 10L;

            // when
            CanonicalOptionGroupPageResult pageResult =
                    sut.toPageResult(results, page, size, totalElements);

            // then
            assertThat(pageResult.results()).hasSize(2);
            assertThat(pageResult.pageMeta().totalElements()).isEqualTo(totalElements);
        }
    }
}
