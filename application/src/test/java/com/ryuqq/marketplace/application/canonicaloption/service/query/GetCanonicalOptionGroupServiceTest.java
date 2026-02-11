package com.ryuqq.marketplace.application.canonicaloption.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.canonicaloption.dto.response.CanonicalOptionGroupResult;
import com.ryuqq.marketplace.application.canonicaloption.internal.CanonicalOptionGroupReadFacade;
import java.time.Instant;
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
@DisplayName("GetCanonicalOptionGroupService 단위 테스트")
class GetCanonicalOptionGroupServiceTest {

    @InjectMocks private GetCanonicalOptionGroupService sut;

    @Mock private CanonicalOptionGroupReadFacade readFacade;

    @Nested
    @DisplayName("execute() - 캐노니컬 옵션 그룹 단건 조회")
    class ExecuteTest {

        @Test
        @DisplayName("ID로 캐노니컬 옵션 그룹을 조회한다")
        void execute_ValidId_ReturnsCanonicalOptionGroupResult() {
            // given
            Long groupId = 1L;
            CanonicalOptionGroupResult expected =
                    new CanonicalOptionGroupResult(
                            groupId, "COLOR", "색상", "Color", true, List.of(), Instant.now());

            given(readFacade.getById(groupId)).willReturn(expected);

            // when
            CanonicalOptionGroupResult result = sut.execute(groupId);

            // then
            assertThat(result).isEqualTo(expected);
            assertThat(result.id()).isEqualTo(groupId);
            then(readFacade).should().getById(groupId);
        }

        @Test
        @DisplayName("값이 포함된 그룹을 조회한다")
        void execute_GroupWithValues_ReturnsGroupWithValues() {
            // given
            Long groupId = 1L;
            CanonicalOptionGroupResult expected =
                    new CanonicalOptionGroupResult(
                            groupId,
                            "COLOR",
                            "색상",
                            "Color",
                            true,
                            List.of(
                                    new com.ryuqq.marketplace.application.canonicaloption.dto
                                            .response.CanonicalOptionValueResult(
                                            1L, "RED", "빨강", "Red", 1)),
                            Instant.now());

            given(readFacade.getById(groupId)).willReturn(expected);

            // when
            CanonicalOptionGroupResult result = sut.execute(groupId);

            // then
            assertThat(result.values()).isNotEmpty();
            assertThat(result.values()).hasSize(1);
        }
    }
}
