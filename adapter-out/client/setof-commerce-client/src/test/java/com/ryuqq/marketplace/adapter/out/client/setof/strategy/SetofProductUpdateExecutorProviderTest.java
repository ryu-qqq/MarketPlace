package com.ryuqq.marketplace.adapter.out.client.setof.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.outboundsync.vo.ChangedArea;
import java.util.EnumSet;
import java.util.Set;
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
@DisplayName("SetofProductUpdateExecutorProvider 단위 테스트")
class SetofProductUpdateExecutorProviderTest {

    @InjectMocks private SetofProductUpdateExecutorProvider sut;

    @Mock private SetofFullProductUpdateExecutor fullExecutor;
    @Mock private SetofPartialProductUpdateExecutor partialExecutor;

    @Nested
    @DisplayName("resolve()")
    class ResolveTest {

        @Test
        @DisplayName("changedAreas가 null이면 전체 수정 실행기 반환")
        void nullChangedAreasReturnsFull() {
            SetofProductUpdateExecutor result = sut.resolve(null);

            assertThat(result).isSameAs(fullExecutor);
        }

        @Test
        @DisplayName("changedAreas가 비어있으면 전체 수정 실행기 반환")
        void emptyChangedAreasReturnsFull() {
            SetofProductUpdateExecutor result = sut.resolve(Set.of());

            assertThat(result).isSameAs(fullExecutor);
        }

        @Test
        @DisplayName("changedAreas가 1개면 부분 수정 실행기 반환")
        void singleAreaReturnsPartial() {
            SetofProductUpdateExecutor result = sut.resolve(Set.of(ChangedArea.PRICE));

            assertThat(result).isSameAs(partialExecutor);
        }

        @Test
        @DisplayName("changedAreas가 3개면 부분 수정 실행기 반환")
        void threeAreasReturnsPartial() {
            Set<ChangedArea> areas =
                    EnumSet.of(ChangedArea.PRICE, ChangedArea.STOCK, ChangedArea.IMAGE);

            SetofProductUpdateExecutor result = sut.resolve(areas);

            assertThat(result).isSameAs(partialExecutor);
        }

        @Test
        @DisplayName("changedAreas가 threshold(4) 이상이면 전체 수정 실행기 반환")
        void atThresholdReturnsFull() {
            Set<ChangedArea> areas =
                    EnumSet.of(
                            ChangedArea.PRICE,
                            ChangedArea.STOCK,
                            ChangedArea.IMAGE,
                            ChangedArea.DESCRIPTION);

            SetofProductUpdateExecutor result = sut.resolve(areas);

            assertThat(result).isSameAs(fullExecutor);
        }

        @Test
        @DisplayName("changedAreas가 threshold 초과면 전체 수정 실행기 반환")
        void aboveThresholdReturnsFull() {
            Set<ChangedArea> areas =
                    EnumSet.of(
                            ChangedArea.PRICE,
                            ChangedArea.STOCK,
                            ChangedArea.IMAGE,
                            ChangedArea.DESCRIPTION,
                            ChangedArea.BASIC_INFO);

            SetofProductUpdateExecutor result = sut.resolve(areas);

            assertThat(result).isSameAs(fullExecutor);
        }
    }
}
