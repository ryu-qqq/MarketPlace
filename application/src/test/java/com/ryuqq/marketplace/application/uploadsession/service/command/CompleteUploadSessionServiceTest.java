package com.ryuqq.marketplace.application.uploadsession.service.command;

import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

import com.ryuqq.marketplace.application.common.manager.FileStorageManager;
import com.ryuqq.marketplace.application.uploadsession.UploadSessionCommandFixtures;
import com.ryuqq.marketplace.application.uploadsession.dto.command.CompleteUploadSessionCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CompleteUploadSessionService 단위 테스트.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("CompleteUploadSessionService 단위 테스트")
class CompleteUploadSessionServiceTest {

    @InjectMocks private CompleteUploadSessionService sut;

    @Mock private FileStorageManager fileStorageManager;

    @Nested
    @DisplayName("execute() - 업로드 세션 완료 처리")
    class ExecuteTest {

        @Test
        @DisplayName("커맨드의 sessionId, fileSize, etag로 FileStorageManager에 완료 처리를 위임한다")
        void execute_ValidCommand_DelegatesToFileStorageManager() {
            // given
            CompleteUploadSessionCommand command =
                    UploadSessionCommandFixtures.completeUploadSessionCommand();
            willDoNothing()
                    .given(fileStorageManager)
                    .completeUploadSession(command.sessionId(), command.fileSize(), command.etag());

            // when
            sut.execute(command);

            // then
            then(fileStorageManager)
                    .should()
                    .completeUploadSession(command.sessionId(), command.fileSize(), command.etag());
        }

        @Test
        @DisplayName("etag가 null인 경우에도 FileStorageManager에 완료 처리를 위임한다")
        void execute_CommandWithNullEtag_DelegatesToFileStorageManager() {
            // given
            CompleteUploadSessionCommand command =
                    UploadSessionCommandFixtures.completeUploadSessionCommandWithoutEtag();
            willDoNothing()
                    .given(fileStorageManager)
                    .completeUploadSession(command.sessionId(), command.fileSize(), null);

            // when
            sut.execute(command);

            // then
            then(fileStorageManager)
                    .should()
                    .completeUploadSession(command.sessionId(), command.fileSize(), null);
        }

        @Test
        @DisplayName("커스텀 sessionId와 fileSize로 완료 처리를 수행한다")
        void execute_CustomSessionIdAndFileSize_DelegatesToFileStorageManager() {
            // given
            String customSessionId = "session-custom-xyz-999";
            long customFileSize = 5242880L;
            String customEtag = "\"abc123def456\"";
            CompleteUploadSessionCommand command =
                    UploadSessionCommandFixtures.completeUploadSessionCommand(
                            customSessionId, customFileSize, customEtag);
            willDoNothing()
                    .given(fileStorageManager)
                    .completeUploadSession(customSessionId, customFileSize, customEtag);

            // when
            sut.execute(command);

            // then
            then(fileStorageManager)
                    .should()
                    .completeUploadSession(customSessionId, customFileSize, customEtag);
        }
    }
}
