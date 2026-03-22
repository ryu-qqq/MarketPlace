package com.ryuqq.marketplace.application.legacy.commoncode.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.legacy.commoncode.LegacyCommonCodeQueryFixtures;
import com.ryuqq.marketplace.application.legacy.commoncode.port.out.query.LegacyCommonCodeQueryPort;
import com.ryuqq.marketplace.domain.legacy.commoncode.aggregate.LegacyCommonCode;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyCommonCodeReadManager 단위 테스트")
class LegacyCommonCodeReadManagerTest {

    @InjectMocks private LegacyCommonCodeReadManager sut;

    @Mock private LegacyCommonCodeQueryPort queryPort;

    @Nested
    @DisplayName("getByCodeGroupId() - 코드 그룹 ID로 공통 코드 목록 조회")
    class GetByCodeGroupIdTest {

        @Test
        @DisplayName("유효한 코드 그룹 ID로 공통 코드 목록을 반환한다")
        void getByCodeGroupId_ValidId_ReturnsCommonCodeList() {
            // given
            Long codeGroupId = 1L;
            List<LegacyCommonCode> expected =
                    LegacyCommonCodeQueryFixtures.legacyCommonCodeList(codeGroupId);

            given(queryPort.findByCodeGroupId(codeGroupId)).willReturn(expected);

            // when
            List<LegacyCommonCode> result = sut.getByCodeGroupId(codeGroupId);

            // then
            assertThat(result).hasSize(3);
            assertThat(result.get(0).codeGroupId()).isEqualTo(codeGroupId);
            assertThat(result.get(0).codeDetail()).isEqualTo("CODE_A");
            then(queryPort).should().findByCodeGroupId(codeGroupId);
        }

        @Test
        @DisplayName("코드 그룹에 공통 코드가 없으면 빈 목록을 반환한다")
        void getByCodeGroupId_NoCodes_ReturnsEmptyList() {
            // given
            Long codeGroupId = 99L;

            given(queryPort.findByCodeGroupId(codeGroupId))
                    .willReturn(LegacyCommonCodeQueryFixtures.emptyLegacyCommonCodeList());

            // when
            List<LegacyCommonCode> result = sut.getByCodeGroupId(codeGroupId);

            // then
            assertThat(result).isEmpty();
            then(queryPort).should().findByCodeGroupId(codeGroupId);
        }

        @Test
        @DisplayName("코드 그룹 ID를 LegacyCommonCodeQueryPort에 위임하고 상호작용이 정확하다")
        void getByCodeGroupId_DelegatesToQueryPort_WithNoMoreInteractions() {
            // given
            Long codeGroupId = 2L;
            List<LegacyCommonCode> expected =
                    LegacyCommonCodeQueryFixtures.legacyCommonCodeList(codeGroupId);

            given(queryPort.findByCodeGroupId(codeGroupId)).willReturn(expected);

            // when
            sut.getByCodeGroupId(codeGroupId);

            // then
            then(queryPort).should().findByCodeGroupId(codeGroupId);
            then(queryPort).shouldHaveNoMoreInteractions();
        }

        @Test
        @DisplayName("단일 공통 코드가 있는 경우 크기 1인 목록을 반환한다")
        void getByCodeGroupId_SingleCode_ReturnsSingleElementList() {
            // given
            Long codeGroupId = 3L;
            List<LegacyCommonCode> expected =
                    List.of(
                            LegacyCommonCodeQueryFixtures.legacyCommonCode(
                                    10L, codeGroupId, "SINGLE_CODE", "단일 코드 표시명"));

            given(queryPort.findByCodeGroupId(codeGroupId)).willReturn(expected);

            // when
            List<LegacyCommonCode> result = sut.getByCodeGroupId(codeGroupId);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).id()).isEqualTo(10L);
            assertThat(result.get(0).codeDetail()).isEqualTo("SINGLE_CODE");
            assertThat(result.get(0).codeDetailDisplayName()).isEqualTo("단일 코드 표시명");
            then(queryPort).should().findByCodeGroupId(codeGroupId);
        }
    }
}
