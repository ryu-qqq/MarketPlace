package com.ryuqq.marketplace.adapter.out.persistence.legacy.commoncode.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.commoncode.LegacyCommonCodeEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.commoncode.entity.LegacyCommonCodeEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.commoncode.mapper.LegacyCommonCodeEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.commoncode.repository.LegacyCommonCodeQueryDslRepository;
import com.ryuqq.marketplace.domain.legacy.commoncode.aggregate.LegacyCommonCode;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * LegacyCommonCodeQueryAdapterTest - 레거시 공통 코드 Query Adapter 단위 테스트.
 *
 * <p>QueryAdapter는 QueryDslRepository에 위임하고 Mapper로 Domain 변환을 검증합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyCommonCodeQueryAdapter 단위 테스트")
class LegacyCommonCodeQueryAdapterTest {

    @Mock private LegacyCommonCodeQueryDslRepository queryDslRepository;

    @Mock private LegacyCommonCodeEntityMapper mapper;

    @InjectMocks private LegacyCommonCodeQueryAdapter queryAdapter;

    // ========================================================================
    // 1. findByCodeGroupId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByCodeGroupId 메서드 테스트")
    class FindByCodeGroupIdTest {

        @Test
        @DisplayName("코드 그룹 ID로 공통 코드 목록을 조회합니다")
        void findByCodeGroupId_WithValidGroupId_ReturnsCommonCodes() {
            // given
            Long codeGroupId = 100L;

            LegacyCommonCodeEntity entity1 =
                    LegacyCommonCodeEntityFixtures.activeEntity(1L, codeGroupId);
            LegacyCommonCodeEntity entity2 =
                    LegacyCommonCodeEntityFixtures.activeEntity(2L, codeGroupId);
            List<LegacyCommonCodeEntity> entities = List.of(entity1, entity2);

            LegacyCommonCode domain1 =
                    LegacyCommonCode.reconstitute(1L, codeGroupId, "CODE1", "코드1", 1);
            LegacyCommonCode domain2 =
                    LegacyCommonCode.reconstitute(2L, codeGroupId, "CODE2", "코드2", 2);

            given(queryDslRepository.findByCodeGroupId(codeGroupId)).willReturn(entities);
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<LegacyCommonCode> result = queryAdapter.findByCodeGroupId(codeGroupId);

            // then
            assertThat(result).hasSize(2);
            then(queryDslRepository).should().findByCodeGroupId(codeGroupId);
            then(mapper).should().toDomain(entity1);
            then(mapper).should().toDomain(entity2);
        }

        @Test
        @DisplayName("해당 그룹 ID의 공통 코드가 없으면 빈 목록을 반환합니다")
        void findByCodeGroupId_WithNonExistentGroupId_ReturnsEmptyList() {
            // given
            Long codeGroupId = 999L;

            given(queryDslRepository.findByCodeGroupId(codeGroupId))
                    .willReturn(Collections.emptyList());

            // when
            List<LegacyCommonCode> result = queryAdapter.findByCodeGroupId(codeGroupId);

            // then
            assertThat(result).isEmpty();
            then(queryDslRepository).should().findByCodeGroupId(codeGroupId);
        }

        @Test
        @DisplayName("단일 Entity가 있을 때 단일 Domain을 반환합니다")
        void findByCodeGroupId_WithSingleEntity_ReturnsSingleDomain() {
            // given
            Long codeGroupId = 100L;

            LegacyCommonCodeEntity entity =
                    LegacyCommonCodeEntityFixtures.activeEntity(1L, codeGroupId);
            LegacyCommonCode domain =
                    LegacyCommonCode.reconstitute(1L, codeGroupId, "CREDIT_CARD", "신용카드", 1);

            given(queryDslRepository.findByCodeGroupId(codeGroupId)).willReturn(List.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            List<LegacyCommonCode> result = queryAdapter.findByCodeGroupId(codeGroupId);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().codeGroupId()).isEqualTo(codeGroupId);
        }
    }
}
