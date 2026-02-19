package com.ryuqq.marketplace.application.productgroupdescription.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.productgroupdescription.dto.response.DescriptionPublishStatusResult;
import com.ryuqq.marketplace.application.productgroupdescription.manager.DescriptionCompositeReadManager;
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
@DisplayName("GetDescriptionPublishStatusService 단위 테스트")
class GetDescriptionPublishStatusServiceTest {

    @InjectMocks private GetDescriptionPublishStatusService sut;

    @Mock private DescriptionCompositeReadManager compositeReadManager;

    @Nested
    @DisplayName("execute() - 상세설명 퍼블리시 상태 조회")
    class ExecuteTest {

        @Test
        @DisplayName("productGroupId로 퍼블리시 상태를 조회한다")
        void execute_ValidProductGroupId_ReturnsPublishStatus() {
            // given
            Long productGroupId = 1L;
            DescriptionPublishStatusResult expectedResult =
                    DescriptionPublishStatusResult.empty(productGroupId);

            given(compositeReadManager.getPublishStatus(productGroupId)).willReturn(expectedResult);

            // when
            DescriptionPublishStatusResult result = sut.execute(productGroupId);

            // then
            assertThat(result).isEqualTo(expectedResult);
            assertThat(result.productGroupId()).isEqualTo(productGroupId);
            then(compositeReadManager).should().getPublishStatus(productGroupId);
        }
    }
}
