package com.ryuqq.marketplace.application.productgroupdescription.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.productgroupdescription.dto.response.DescriptionPublishStatusResult;
import com.ryuqq.marketplace.application.productgroupdescription.port.out.query.DescriptionCompositeQueryPort;
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
@DisplayName("DescriptionCompositeReadManager 단위 테스트")
class DescriptionCompositeReadManagerTest {

    @InjectMocks private DescriptionCompositeReadManager sut;

    @Mock private DescriptionCompositeQueryPort compositeQueryPort;

    @Nested
    @DisplayName("getPublishStatus() - 퍼블리시 상태 조회")
    class GetPublishStatusTest {

        @Test
        @DisplayName("productGroupId로 퍼블리시 상태를 조회한다")
        void getPublishStatus_ValidId_ReturnsResult() {
            // given
            Long productGroupId = 1L;
            DescriptionPublishStatusResult expected =
                    DescriptionPublishStatusResult.empty(productGroupId);
            given(compositeQueryPort.findPublishStatus(productGroupId)).willReturn(expected);

            // when
            DescriptionPublishStatusResult result = sut.getPublishStatus(productGroupId);

            // then
            assertThat(result).isEqualTo(expected);
        }
    }
}
