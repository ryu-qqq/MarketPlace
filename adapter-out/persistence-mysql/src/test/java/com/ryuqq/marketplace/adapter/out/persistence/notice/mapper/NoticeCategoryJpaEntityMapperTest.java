package com.ryuqq.marketplace.adapter.out.persistence.notice.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.adapter.out.persistence.notice.NoticeCategoryJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.notice.entity.NoticeCategoryJpaEntity;
import com.ryuqq.marketplace.domain.category.vo.CategoryGroup;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * NoticeCategoryJpaEntityMapperTest - 공지사항 카테고리 Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@DisplayName("NoticeCategoryJpaEntityMapper 단위 테스트")
class NoticeCategoryJpaEntityMapperTest {

    private NoticeCategoryJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new NoticeCategoryJpaEntityMapper();
    }

    // ========================================================================
    // 1. toDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain 메서드 테스트")
    class ToDomainTest {

        @Test
        @DisplayName("활성 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithActiveEntity_ConvertsCorrectly() {
            // given
            NoticeCategoryJpaEntity entity = NoticeCategoryJpaEntityFixtures.activeEntity(1L);

            // when
            NoticeCategory domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.codeValue()).isEqualTo(entity.getCode());
            assertThat(domain.nameKo()).isEqualTo(entity.getNameKo());
            assertThat(domain.nameEn()).isEqualTo(entity.getNameEn());
            assertThat(domain.targetCategoryGroup().name())
                    .isEqualTo(entity.getTargetCategoryGroup());
            assertThat(domain.isActive()).isTrue();
        }

        @Test
        @DisplayName("비활성 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithInactiveEntity_ConvertsCorrectly() {
            // given
            NoticeCategoryJpaEntity entity = NoticeCategoryJpaEntityFixtures.inactiveEntity();

            // when
            NoticeCategory domain = mapper.toDomain(entity);

            // then
            assertThat(domain.isActive()).isFalse();
        }

        @Test
        @DisplayName("영어 이름이 없는 Entity를 Domain으로 변환합니다")
        void toDomain_WithoutEnglishName_ConvertsCorrectly() {
            // given
            NoticeCategoryJpaEntity entity =
                    NoticeCategoryJpaEntityFixtures.entityWithoutEnglishName();

            // when
            NoticeCategory domain = mapper.toDomain(entity);

            // then
            assertThat(domain.nameEn()).isNull();
            assertThat(domain.nameKo()).isEqualTo(entity.getNameKo());
        }

        @Test
        @DisplayName("CLOTHING 카테고리 그룹을 올바르게 변환합니다")
        void toDomain_WithClothingGroup_ConvertsCorrectly() {
            // given
            NoticeCategoryJpaEntity entity =
                    NoticeCategoryJpaEntityFixtures.activeEntityWithCode("CLOTHING", "CLOTHING");

            // when
            NoticeCategory domain = mapper.toDomain(entity);

            // then
            assertThat(domain.targetCategoryGroup()).isEqualTo(CategoryGroup.CLOTHING);
        }

        @Test
        @DisplayName("DIGITAL 카테고리 그룹을 올바르게 변환합니다")
        void toDomain_WithDigitalGroup_ConvertsCorrectly() {
            // given
            NoticeCategoryJpaEntity entity = NoticeCategoryJpaEntityFixtures.electronicsEntity();

            // when
            NoticeCategory domain = mapper.toDomain(entity);

            // then
            assertThat(domain.targetCategoryGroup()).isEqualTo(CategoryGroup.DIGITAL);
        }

        @Test
        @DisplayName("FURNITURE 카테고리 그룹을 올바르게 변환합니다")
        void toDomain_WithFurnitureGroup_ConvertsCorrectly() {
            // given
            NoticeCategoryJpaEntity entity = NoticeCategoryJpaEntityFixtures.furnitureEntity();

            // when
            NoticeCategory domain = mapper.toDomain(entity);

            // then
            assertThat(domain.targetCategoryGroup()).isEqualTo(CategoryGroup.FURNITURE);
        }

        @Test
        @DisplayName("ID가 null인 Entity는 변환 시 예외를 발생시킵니다")
        void toDomain_WithNullId_ThrowsException() {
            // given
            NoticeCategoryJpaEntity entity = NoticeCategoryJpaEntityFixtures.newEntity();

            // when & then
            assertThatThrownBy(() -> mapper.toDomain(entity))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("영속화된 엔티티의 ID는 null일 수 없습니다");
        }

        @Test
        @DisplayName("생성일시와 수정일시가 올바르게 변환됩니다")
        void toDomain_PreservesTimestamps() {
            // given
            NoticeCategoryJpaEntity entity = NoticeCategoryJpaEntityFixtures.activeEntity(1L);

            // when
            NoticeCategory domain = mapper.toDomain(entity);

            // then
            assertThat(domain.createdAt()).isEqualTo(entity.getCreatedAt());
            assertThat(domain.updatedAt()).isEqualTo(entity.getUpdatedAt());
        }

        @Test
        @DisplayName("필드 리스트가 비어있는 상태로 변환됩니다")
        void toDomain_HasEmptyFieldsList() {
            // given
            NoticeCategoryJpaEntity entity = NoticeCategoryJpaEntityFixtures.activeEntity(1L);

            // when
            NoticeCategory domain = mapper.toDomain(entity);

            // then
            assertThat(domain.fields()).isEmpty();
        }
    }

    // ========================================================================
    // 2. 엣지 케이스 테스트
    // ========================================================================

    @Nested
    @DisplayName("엣지 케이스 테스트")
    class EdgeCaseTest {

        @Test
        @DisplayName("긴 이름을 가진 Entity를 변환합니다")
        void toDomain_WithLongName_ConvertsCorrectly() {
            // given
            String longNameKo = "매우 긴 카테고리 이름".repeat(5);
            String longNameEn = "Very Long Category Name".repeat(3);
            NoticeCategoryJpaEntity entity =
                    NoticeCategoryJpaEntityFixtures.activeEntityWithName(longNameKo, longNameEn);

            // when
            NoticeCategory domain = mapper.toDomain(entity);

            // then
            assertThat(domain.nameKo()).isEqualTo(longNameKo);
            assertThat(domain.nameEn()).isEqualTo(longNameEn);
        }

        @Test
        @DisplayName("특수문자가 포함된 코드를 변환합니다")
        void toDomain_WithSpecialCharactersInCode_ConvertsCorrectly() {
            // given
            String codeWithSpecialChars = "CODE_WITH-SPECIAL.CHARS";
            NoticeCategoryJpaEntity entity =
                    NoticeCategoryJpaEntityFixtures.activeEntityWithCode(
                            codeWithSpecialChars, "CLOTHING");

            // when
            NoticeCategory domain = mapper.toDomain(entity);

            // then
            assertThat(domain.codeValue()).isEqualTo(codeWithSpecialChars);
        }
    }
}
