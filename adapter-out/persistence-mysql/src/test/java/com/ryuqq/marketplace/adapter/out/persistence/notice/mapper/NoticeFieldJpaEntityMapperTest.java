package com.ryuqq.marketplace.adapter.out.persistence.notice.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.adapter.out.persistence.notice.NoticeFieldJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.notice.entity.NoticeFieldJpaEntity;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * NoticeFieldJpaEntityMapperTest - 공지사항 필드 Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@DisplayName("NoticeFieldJpaEntityMapper 단위 테스트")
class NoticeFieldJpaEntityMapperTest {

    private NoticeFieldJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new NoticeFieldJpaEntityMapper();
    }

    // ========================================================================
    // 1. toDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain 메서드 테스트")
    class ToDomainTest {

        @Test
        @DisplayName("필수 필드 Entity를 Domain으로 변환합니다")
        void toDomain_WithRequiredField_ConvertsCorrectly() {
            // given
            NoticeFieldJpaEntity entity = NoticeFieldJpaEntityFixtures.requiredFieldEntity(1L);

            // when
            NoticeField domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.fieldCodeValue()).isEqualTo(entity.getFieldCode());
            assertThat(domain.fieldNameValue()).isEqualTo(entity.getFieldName());
            assertThat(domain.isRequired()).isTrue();
            assertThat(domain.sortOrder()).isEqualTo(entity.getSortOrder());
        }

        @Test
        @DisplayName("선택 필드 Entity를 Domain으로 변환합니다")
        void toDomain_WithOptionalField_ConvertsCorrectly() {
            // given
            NoticeFieldJpaEntity entity = NoticeFieldJpaEntityFixtures.optionalFieldEntity();

            // when
            NoticeField domain = mapper.toDomain(entity);

            // then
            assertThat(domain.isRequired()).isFalse();
        }

        @Test
        @DisplayName("정렬 순서가 올바르게 변환됩니다")
        void toDomain_PreservesSortOrder() {
            // given
            int expectedSortOrder = 5;
            NoticeFieldJpaEntity entity =
                    NoticeFieldJpaEntityFixtures.entityWithSortOrder(expectedSortOrder);

            // when
            NoticeField domain = mapper.toDomain(entity);

            // then
            assertThat(domain.sortOrder()).isEqualTo(expectedSortOrder);
        }

        @Test
        @DisplayName("제조사 필드를 올바르게 변환합니다")
        void toDomain_WithManufacturerField_ConvertsCorrectly() {
            // given
            NoticeFieldJpaEntity entity =
                    NoticeFieldJpaEntityFixtures.manufacturerFieldEntity();

            // when
            NoticeField domain = mapper.toDomain(entity);

            // then
            assertThat(domain.fieldCodeValue()).isEqualTo("MANUFACTURER");
            assertThat(domain.fieldNameValue()).isEqualTo("제조사");
            assertThat(domain.isRequired()).isTrue();
        }

        @Test
        @DisplayName("원산지 필드를 올바르게 변환합니다")
        void toDomain_WithOriginField_ConvertsCorrectly() {
            // given
            NoticeFieldJpaEntity entity = NoticeFieldJpaEntityFixtures.originFieldEntity();

            // when
            NoticeField domain = mapper.toDomain(entity);

            // then
            assertThat(domain.fieldCodeValue()).isEqualTo("ORIGIN");
            assertThat(domain.fieldNameValue()).isEqualTo("원산지");
        }

        @Test
        @DisplayName("세탁방법 필드를 올바르게 변환합니다")
        void toDomain_WithWashingMethodField_ConvertsCorrectly() {
            // given
            NoticeFieldJpaEntity entity =
                    NoticeFieldJpaEntityFixtures.washingMethodFieldEntity();

            // when
            NoticeField domain = mapper.toDomain(entity);

            // then
            assertThat(domain.fieldCodeValue()).isEqualTo("WASHING_METHOD");
            assertThat(domain.fieldNameValue()).isEqualTo("세탁방법");
            assertThat(domain.isRequired()).isFalse();
        }

        @Test
        @DisplayName("ID가 null인 Entity는 변환 시 예외를 발생시킵니다")
        void toDomain_WithNullId_ThrowsException() {
            // given
            NoticeFieldJpaEntity entity = NoticeFieldJpaEntityFixtures.newEntity();

            // when & then
            assertThatThrownBy(() -> mapper.toDomain(entity))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("영속화된 엔티티의 ID는 null일 수 없습니다");
        }

        @Test
        @DisplayName("커스텀 필드 코드와 이름을 올바르게 변환합니다")
        void toDomain_WithCustomCodeAndName_ConvertsCorrectly() {
            // given
            String customCode = "CUSTOM_FIELD";
            String customName = "커스텀 필드";
            NoticeFieldJpaEntity entity =
                    NoticeFieldJpaEntityFixtures.entityWithCodeAndName(customCode, customName);

            // when
            NoticeField domain = mapper.toDomain(entity);

            // then
            assertThat(domain.fieldCodeValue()).isEqualTo(customCode);
            assertThat(domain.fieldNameValue()).isEqualTo(customName);
        }
    }

    // ========================================================================
    // 2. 엣지 케이스 테스트
    // ========================================================================

    @Nested
    @DisplayName("엣지 케이스 테스트")
    class EdgeCaseTest {

        @Test
        @DisplayName("정렬 순서가 0인 필드를 변환합니다")
        void toDomain_WithZeroSortOrder_ConvertsCorrectly() {
            // given
            NoticeFieldJpaEntity entity = NoticeFieldJpaEntityFixtures.entityWithSortOrder(0);

            // when
            NoticeField domain = mapper.toDomain(entity);

            // then
            assertThat(domain.sortOrder()).isZero();
        }

        @Test
        @DisplayName("긴 필드 이름을 가진 Entity를 변환합니다")
        void toDomain_WithLongFieldName_ConvertsCorrectly() {
            // given
            String longFieldName = "매우 긴 필드 이름".repeat(10);
            NoticeFieldJpaEntity entity =
                    NoticeFieldJpaEntityFixtures.entityWithCodeAndName("LONG_CODE", longFieldName);

            // when
            NoticeField domain = mapper.toDomain(entity);

            // then
            assertThat(domain.fieldNameValue()).isEqualTo(longFieldName);
        }

        @Test
        @DisplayName("특수문자가 포함된 필드 코드를 변환합니다")
        void toDomain_WithSpecialCharactersInCode_ConvertsCorrectly() {
            // given
            String codeWithSpecialChars = "FIELD_CODE-WITH.SPECIAL_CHARS";
            NoticeFieldJpaEntity entity =
                    NoticeFieldJpaEntityFixtures.entityWithCodeAndName(
                            codeWithSpecialChars, "특수문자 필드");

            // when
            NoticeField domain = mapper.toDomain(entity);

            // then
            assertThat(domain.fieldCodeValue()).isEqualTo(codeWithSpecialChars);
        }

        @Test
        @DisplayName("높은 정렬 순서 값을 가진 필드를 변환합니다")
        void toDomain_WithHighSortOrder_ConvertsCorrectly() {
            // given
            int highSortOrder = 999;
            NoticeFieldJpaEntity entity =
                    NoticeFieldJpaEntityFixtures.entityWithSortOrder(highSortOrder);

            // when
            NoticeField domain = mapper.toDomain(entity);

            // then
            assertThat(domain.sortOrder()).isEqualTo(highSortOrder);
        }
    }
}
