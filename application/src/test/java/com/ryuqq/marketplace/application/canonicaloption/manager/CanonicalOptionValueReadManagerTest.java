package com.ryuqq.marketplace.application.canonicaloption.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.canonicaloption.port.out.query.CanonicalOptionValueQueryPort;
import com.ryuqq.marketplace.domain.canonicaloption.CanonicalOptionFixtures;
import com.ryuqq.marketplace.domain.canonicaloption.aggregate.CanonicalOptionValue;
import java.util.List;
import java.util.Map;
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
@DisplayName("CanonicalOptionValueReadManager 단위 테스트")
class CanonicalOptionValueReadManagerTest {

    @InjectMocks private CanonicalOptionValueReadManager sut;

    @Mock private CanonicalOptionValueQueryPort queryPort;

    @Nested
    @DisplayName("getByCanonicalOptionGroupId() - 그룹 ID로 값 조회")
    class GetByCanonicalOptionGroupIdTest {

        @Test
        @DisplayName("캐노니컬 옵션 그룹 ID로 값 목록을 조회한다")
        void getByCanonicalOptionGroupId_ReturnsValues() {
            // given
            Long groupId = 1L;
            List<CanonicalOptionValue> expected = CanonicalOptionFixtures.canonicalOptionValues();

            given(queryPort.findByCanonicalOptionGroupId(groupId)).willReturn(expected);

            // when
            List<CanonicalOptionValue> result = sut.getByCanonicalOptionGroupId(groupId);

            // then
            assertThat(result).hasSize(3);
            then(queryPort).should().findByCanonicalOptionGroupId(groupId);
        }

        @Test
        @DisplayName("값이 없으면 빈 목록을 반환한다")
        void getByCanonicalOptionGroupId_Empty_ReturnsEmptyList() {
            // given
            Long groupId = 999L;

            given(queryPort.findByCanonicalOptionGroupId(groupId)).willReturn(List.of());

            // when
            List<CanonicalOptionValue> result = sut.getByCanonicalOptionGroupId(groupId);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getGroupedByCanonicalOptionGroupIds() - 그룹 ID 목록으로 그룹화 조회")
    class GetGroupedByCanonicalOptionGroupIdsTest {

        @Test
        @DisplayName("그룹 ID 목록으로 값들을 그룹화하여 조회한다")
        void getGroupedByCanonicalOptionGroupIds_ReturnsGroupedValues() {
            // given
            List<Long> groupIds = List.of(1L, 2L);
            Map<Long, List<CanonicalOptionValue>> expected =
                    Map.of(
                            1L, List.of(CanonicalOptionFixtures.canonicalOptionValue(1L)),
                            2L, List.of(CanonicalOptionFixtures.canonicalOptionValue(2L)));

            given(queryPort.findGroupedByCanonicalOptionGroupIds(groupIds)).willReturn(expected);

            // when
            Map<Long, List<CanonicalOptionValue>> result =
                    sut.getGroupedByCanonicalOptionGroupIds(groupIds);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(1L)).hasSize(1);
            assertThat(result.get(2L)).hasSize(1);
            then(queryPort).should().findGroupedByCanonicalOptionGroupIds(groupIds);
        }

        @Test
        @DisplayName("빈 그룹 ID 목록이면 빈 맵을 반환한다")
        void getGroupedByCanonicalOptionGroupIds_EmptyIds_ReturnsEmptyMap() {
            // given
            List<Long> groupIds = List.of();

            // when
            Map<Long, List<CanonicalOptionValue>> result =
                    sut.getGroupedByCanonicalOptionGroupIds(groupIds);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("값이 없는 그룹도 빈 목록으로 처리된다")
        void getGroupedByCanonicalOptionGroupIds_SomeGroupsWithoutValues() {
            // given
            List<Long> groupIds = List.of(1L, 2L, 3L);
            Map<Long, List<CanonicalOptionValue>> expected =
                    Map.of(
                            1L, List.of(CanonicalOptionFixtures.canonicalOptionValue(1L)),
                            2L, List.of());

            given(queryPort.findGroupedByCanonicalOptionGroupIds(groupIds)).willReturn(expected);

            // when
            Map<Long, List<CanonicalOptionValue>> result =
                    sut.getGroupedByCanonicalOptionGroupIds(groupIds);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(1L)).hasSize(1);
            assertThat(result.get(2L)).isEmpty();
        }
    }
}
