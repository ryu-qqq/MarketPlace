package com.ryuqq.marketplace.application.productgroupdescription.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.imageupload.manager.ImageUploadOutboxCommandManager;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.RegisterProductGroupDescriptionCommand;
import com.ryuqq.marketplace.application.productgroupdescription.dto.response.DescriptionPersistResult;
import com.ryuqq.marketplace.application.productgroupdescription.factory.ProductGroupDescriptionCommandFactory;
import com.ryuqq.marketplace.application.productgroupdescription.manager.ProductGroupDescriptionReadManager;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
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
@DisplayName("DescriptionCommandCoordinator 단위 테스트")
class DescriptionCommandCoordinatorTest {

    @InjectMocks private DescriptionCommandCoordinator sut;

    @Mock private DescriptionCommandFacade descriptionCommandFacade;
    @Mock private ProductGroupDescriptionCommandFactory descriptionCommandFactory;
    @Mock private ProductGroupDescriptionReadManager descriptionReadManager;
    @Mock private ImageUploadOutboxCommandManager outboxCommandManager;

    @Nested
    @DisplayName("register() - Description 등록 조율")
    class RegisterTest {

        @Test
        @DisplayName("등록 Command로 Description을 생성하고 저장 후 ID를 반환한다")
        void register_ValidCommand_ReturnsDescriptionId() {
            // given
            RegisterProductGroupDescriptionCommand command =
                    new RegisterProductGroupDescriptionCommand(1L, "<p>상세설명</p>");
            ProductGroupDescription description =
                    ProductGroupFixtures.defaultProductGroupDescription();
            DescriptionPersistResult persistResult = new DescriptionPersistResult(1L, List.of());

            given(descriptionCommandFactory.create(command)).willReturn(description);
            given(descriptionCommandFacade.persist(description)).willReturn(persistResult);
            given(descriptionCommandFactory.createDescriptionImageOutboxes(any(), any(), any()))
                    .willReturn(List.of());

            // when
            Long result = sut.register(command);

            // then
            assertThat(result).isEqualTo(1L);
            then(descriptionCommandFactory).should().create(command);
        }

        @Test
        @DisplayName("이미지가 없는 Description은 즉시 PUBLISH_READY로 마킹된다")
        void register_DescriptionWithNoImages_MarksPublishReady() {
            // given
            RegisterProductGroupDescriptionCommand command =
                    new RegisterProductGroupDescriptionCommand(1L, "<p>이미지 없는 상세설명</p>");
            ProductGroupDescription description =
                    ProductGroupFixtures.defaultProductGroupDescription();
            DescriptionPersistResult persistResult = new DescriptionPersistResult(1L, List.of());

            given(descriptionCommandFactory.create(command)).willReturn(description);
            given(descriptionCommandFacade.persist(description)).willReturn(persistResult);
            given(descriptionCommandFactory.createDescriptionImageOutboxes(any(), any(), any()))
                    .willReturn(List.of());

            // when
            sut.register(command);

            // then
            then(descriptionCommandFacade).should().persist(description);
        }
    }

    @Nested
    @DisplayName("persist() - Description 저장 조율")
    class PersistTest {

        @Test
        @DisplayName("Description을 저장하고 descriptionId를 반환한다")
        void persist_ValidDescription_ReturnsDescriptionId() {
            // given
            ProductGroupDescription description =
                    ProductGroupFixtures.defaultProductGroupDescription();
            DescriptionPersistResult persistResult = new DescriptionPersistResult(5L, List.of());

            given(descriptionCommandFacade.persist(description)).willReturn(persistResult);
            given(descriptionCommandFactory.createDescriptionImageOutboxes(any(), any(), any()))
                    .willReturn(List.of());

            // when
            Long result = sut.persist(description);

            // then
            assertThat(result).isEqualTo(5L);
            then(descriptionCommandFacade).should().persist(description);
        }

        @Test
        @DisplayName("아웃박스가 있으면 persistAll을 호출한다")
        void persist_WithOutboxes_PersistsOutboxes() {
            // given
            ProductGroupDescription description =
                    ProductGroupFixtures.defaultProductGroupDescription();
            DescriptionPersistResult persistResult = new DescriptionPersistResult(1L, List.of());

            given(descriptionCommandFacade.persist(description)).willReturn(persistResult);
            given(descriptionCommandFactory.createDescriptionImageOutboxes(any(), any(), any()))
                    .willReturn(List.of());

            // when
            sut.persist(description);

            // then
            then(outboxCommandManager).should().persistAll(anyList());
        }
    }
}
