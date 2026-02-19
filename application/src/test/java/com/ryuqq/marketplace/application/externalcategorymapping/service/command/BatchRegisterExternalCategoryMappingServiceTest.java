package com.ryuqq.marketplace.application.externalcategorymapping.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

import com.ryuqq.marketplace.application.externalcategorymapping.ExternalCategoryMappingCommandFixtures;
import com.ryuqq.marketplace.application.externalcategorymapping.dto.command.BatchRegisterExternalCategoryMappingCommand;
import com.ryuqq.marketplace.application.externalcategorymapping.factory.ExternalCategoryMappingCommandFactory;
import com.ryuqq.marketplace.application.externalcategorymapping.manager.ExternalCategoryMappingCommandManager;
import com.ryuqq.marketplace.application.externalcategorymapping.validator.ExternalCategoryMappingValidator;
import com.ryuqq.marketplace.domain.externalcategorymapping.ExternalCategoryMappingFixtures;
import com.ryuqq.marketplace.domain.externalcategorymapping.aggregate.ExternalCategoryMapping;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("BatchRegisterExternalCategoryMappingService 단위 테스트")
class BatchRegisterExternalCategoryMappingServiceTest {

    @InjectMocks private BatchRegisterExternalCategoryMappingService sut;

    @Mock private ExternalCategoryMappingValidator validator;
    @Mock private ExternalCategoryMappingCommandFactory commandFactory;
    @Mock private ExternalCategoryMappingCommandManager commandManager;

    @Nested
    @DisplayName("execute() - 외부 카테고리 매핑 일괄 등록")
    class ExecuteTest {

        @Test
        @DisplayName("배치 커맨드로 외부 카테고리 매핑들을 일괄 등록하고 ID 목록을 반환한다")
        void execute_ValidBatchCommand_ReturnsMappingIds() {
            // given
            BatchRegisterExternalCategoryMappingCommand command =
                    ExternalCategoryMappingCommandFixtures.batchRegisterCommand();
            List<ExternalCategoryMapping> mappings =
                    List.of(
                            ExternalCategoryMappingFixtures.newMapping(1L, "CAT_SHOES_001", 100L),
                            ExternalCategoryMappingFixtures.newMapping(1L, "CAT_BAG_001", 200L),
                            ExternalCategoryMappingFixtures.newMapping(
                                    1L, "CAT_CLOTHES_001", 300L));
            List<Long> expectedIds = List.of(1L, 2L, 3L);

            willDoNothing()
                    .given(validator)
                    .validateNotDuplicateBulk(
                            ArgumentMatchers.eq(command.externalSourceId()),
                            ArgumentMatchers.anyList());
            given(commandFactory.createAll(command)).willReturn(mappings);
            given(commandManager.persistAll(mappings)).willReturn(expectedIds);

            // when
            List<Long> result = sut.execute(command);

            // then
            assertThat(result).hasSize(3);
            assertThat(result).isEqualTo(expectedIds);
            then(commandFactory).should().createAll(command);
            then(commandManager).should().persistAll(mappings);
        }
    }
}
