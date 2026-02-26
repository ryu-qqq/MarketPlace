package com.ryuqq.marketplace.adapter.out.persistence.legacy.commoncode.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.commoncode.LegacyCommonCodeEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.commoncode.entity.LegacyCommonCodeEntity;
import com.ryuqq.marketplace.domain.legacy.commoncode.aggregate.LegacyCommonCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * LegacyCommonCodeEntityMapperTest - 레거시 공통 코드 Mapper 단위 테스트.
 *
 * <p>toDomain(Entity) 메서드를 검증합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("LegacyCommonCodeEntityMapper 단위 테스트")
class LegacyCommonCodeEntityMapperTest {

    private LegacyCommonCodeEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LegacyCommonCodeEntityMapper();
    }

    // ========================================================================
    // 1. toDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain 변환 테스트")
    class ToDomainTest {

        @Test
        @DisplayName("활성 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithActiveEntity_ReturnsValidDomain() {
            // given
            LegacyCommonCodeEntity entity = LegacyCommonCodeEntityFixtures.activeEntity();

            // when
            LegacyCommonCode domain = mapper.toDomain(entity);

            // then
            assertThat(domain.id()).isEqualTo(entity.getId());
            assertThat(domain.codeGroupId()).isEqualTo(entity.getCodeGroupId());
            assertThat(domain.codeDetail()).isEqualTo(entity.getCodeDetail());
            assertThat(domain.codeDetailDisplayName()).isEqualTo(entity.getCodeDetailDisplayName());
            assertThat(domain.displayOrder()).isEqualTo(entity.getDisplayOrder());
        }

        @Test
        @DisplayName("ID가 있는 Entity를 Domain으로 변환하면 ID가 보존됩니다")
        void toDomain_WithIdEntity_PreservesId() {
            // given
            Long expectedId = 42L;
            LegacyCommonCodeEntity entity = LegacyCommonCodeEntityFixtures.activeEntity(expectedId);

            // when
            LegacyCommonCode domain = mapper.toDomain(entity);

            // then
            assertThat(domain.id()).isEqualTo(expectedId);
        }

        @Test
        @DisplayName("코드 그룹 ID를 지정한 Entity를 Domain으로 변환합니다")
        void toDomain_WithCustomGroupId_PreservesGroupId() {
            // given
            Long expectedGroupId = 999L;
            LegacyCommonCodeEntity entity =
                    LegacyCommonCodeEntityFixtures.activeEntity(1L, expectedGroupId);

            // when
            LegacyCommonCode domain = mapper.toDomain(entity);

            // then
            assertThat(domain.codeGroupId()).isEqualTo(expectedGroupId);
        }

        @Test
        @DisplayName("커스텀 코드 상세를 가진 Entity를 Domain으로 변환합니다")
        void toDomain_WithCustomCodeDetail_PreservesCodeDetail() {
            // given
            String expectedDetail = "DEBIT_CARD";
            String expectedDisplayName = "체크카드";
            LegacyCommonCodeEntity entity =
                    LegacyCommonCodeEntityFixtures.activeEntityWithCodeDetail(
                            expectedDetail, expectedDisplayName);

            // when
            LegacyCommonCode domain = mapper.toDomain(entity);

            // then
            assertThat(domain.codeDetail()).isEqualTo(expectedDetail);
            assertThat(domain.codeDetailDisplayName()).isEqualTo(expectedDisplayName);
        }

        @Test
        @DisplayName("표시 순서를 지정한 Entity를 Domain으로 변환합니다")
        void toDomain_WithDisplayOrder_PreservesDisplayOrder() {
            // given
            Integer expectedOrder = 5;
            LegacyCommonCodeEntity entity =
                    LegacyCommonCodeEntityFixtures.entityWithDisplayOrder(expectedOrder);

            // when
            LegacyCommonCode domain = mapper.toDomain(entity);

            // then
            assertThat(domain.displayOrder()).isEqualTo(expectedOrder);
        }

        @Test
        @DisplayName("삭제 상태 Entity도 Domain으로 변환됩니다")
        void toDomain_WithDeletedEntity_ReturnsValidDomain() {
            // given
            LegacyCommonCodeEntity entity = LegacyCommonCodeEntityFixtures.deletedEntity();

            // when
            LegacyCommonCode domain = mapper.toDomain(entity);

            // then
            assertThat(domain.id()).isEqualTo(entity.getId());
            assertThat(domain.codeDetail()).isEqualTo(entity.getCodeDetail());
        }
    }
}
