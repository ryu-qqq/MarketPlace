package com.ryuqq.marketplace.integration.config;

import com.ryuqq.authhub.sdk.auth.TokenResolver;
import com.ryuqq.marketplace.application.auth.dto.response.LoginResult;
import com.ryuqq.marketplace.application.auth.dto.response.MyInfoResult;
import com.ryuqq.marketplace.application.auth.dto.response.RefreshResult;
import com.ryuqq.marketplace.application.auth.port.out.client.AuthClient;
import com.ryuqq.marketplace.application.common.dto.command.ExternalDownloadRequest;
import com.ryuqq.marketplace.application.common.dto.command.PresignedUploadUrlRequest;
import com.ryuqq.marketplace.application.common.dto.response.ExternalDownloadResponse;
import com.ryuqq.marketplace.application.common.dto.response.PresignedUrlResponse;
import com.ryuqq.marketplace.application.common.port.out.IdGeneratorPort;
import com.ryuqq.marketplace.application.common.port.out.client.FileStorageClient;
import com.ryuqq.marketplace.application.imagetransform.dto.response.ImageTransformResponse;
import com.ryuqq.marketplace.application.imagetransform.port.out.client.ImageTransformClient;
import com.ryuqq.marketplace.application.legacy.productgroup.port.in.query.ResolveLegacyProductGroupSellerIdUseCase;
import com.ryuqq.marketplace.application.legacyconversion.port.out.command.LegacyConversionOutboxCommandPort;
import com.ryuqq.marketplace.application.legacyconversion.port.out.command.LegacyProductIdMappingCommandPort;
import com.ryuqq.marketplace.application.legacyconversion.port.out.query.LegacyConversionOutboxQueryPort;
import com.ryuqq.marketplace.application.legacyconversion.port.out.query.LegacyProductGroupIdScanPort;
import com.ryuqq.marketplace.application.legacyconversion.port.out.query.LegacyProductIdMappingQueryPort;
import com.ryuqq.marketplace.application.outboundsync.port.out.client.OutboundSyncPublishClient;
import com.ryuqq.marketplace.application.outboundsync.port.out.command.OutboundSyncOutboxCommandPort;
import com.ryuqq.marketplace.application.outboundsync.port.out.query.OutboundSyncOutboxQueryPort;
import com.ryuqq.marketplace.application.productintelligence.port.out.client.AggregationPublishClient;
import com.ryuqq.marketplace.application.productintelligence.port.out.client.DescriptionAnalysisAiClient;
import com.ryuqq.marketplace.application.productintelligence.port.out.client.DescriptionAnalysisPublishClient;
import com.ryuqq.marketplace.application.productintelligence.port.out.client.NoticeAnalysisAiClient;
import com.ryuqq.marketplace.application.productintelligence.port.out.client.NoticeAnalysisPublishClient;
import com.ryuqq.marketplace.application.productintelligence.port.out.client.OptionAnalysisAiClient;
import com.ryuqq.marketplace.application.productintelligence.port.out.client.OptionAnalysisPublishClient;
import com.ryuqq.marketplace.application.seller.port.out.client.IdentityClient;
import com.ryuqq.marketplace.application.selleradmin.port.out.client.SellerAdminEmailClient;
import com.ryuqq.marketplace.application.selleradmin.port.out.client.SellerAdminIdentityClient;
import com.ryuqq.marketplace.application.selleradmin.port.out.command.SellerAdminAuthOutboxCommandPort;
import com.ryuqq.marketplace.application.selleradmin.port.out.query.SellerAdminAuthOutboxQueryPort;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyConversionOutbox;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyProductIdMapping;
import com.ryuqq.marketplace.domain.outboundsync.aggregate.OutboundSyncOutbox;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productintelligence.vo.ExtractedAttribute;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdminAuthOutbox;
import com.ryuqq.marketplace.domain.selleradmin.id.SellerAdminId;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 통합 테스트용 외부 서비스 및 미구현 포트 Stub 설정.
 *
 * <p>AuthHub, SES 등 외부 서비스 클라이언트와 아직 Adapter가 구현되지 않은 포트 인터페이스를 Stub으로 대체합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@TestConfiguration
@EnableAsync
public class StubExternalClientConfig {

    // ===== SDK 인프라 Stubs =====

    @Bean
    @Primary
    public TokenResolver stubTokenResolver() {
        return () -> Optional.of("stub-service-token");
    }

    // ===== 외부 서비스 클라이언트 Stubs =====

    @Bean
    @Primary
    public AuthClient stubAuthClient() {
        return new AuthClient() {
            private static final String VALID_IDENTIFIER = "admin@example.com";
            private static final String VALID_PASSWORD = "password123!";
            private static final String USER_ID = "test-user-001";
            private final AtomicLong tokenCounter = new AtomicLong(1);

            @Override
            public LoginResult login(String identifier, String password) {
                if (!VALID_IDENTIFIER.equals(identifier) || !VALID_PASSWORD.equals(password)) {
                    return LoginResult.failure("INVALID_CREDENTIALS", "Invalid credentials");
                }
                String token = "stub-access-token-" + tokenCounter.getAndIncrement();
                String refreshToken = "stub-refresh-token-" + tokenCounter.getAndIncrement();
                return LoginResult.success(USER_ID, token, refreshToken, 3600L, "Bearer");
            }

            @Override
            public void logout(String userId) {
                // stub: no-op
            }

            @Override
            public RefreshResult refresh(String refreshToken) {
                String newAccessToken = "stub-access-token-" + tokenCounter.getAndIncrement();
                String newRefreshToken = "stub-refresh-token-" + tokenCounter.getAndIncrement();
                return RefreshResult.success(newAccessToken, newRefreshToken, 3600L, "Bearer");
            }

            @Override
            public MyInfoResult getMyInfo(String accessToken) {
                return new MyInfoResult(
                        USER_ID,
                        VALID_IDENTIFIER,
                        "Test Admin",
                        "tenant-001",
                        "Test Tenant",
                        "org-admin-001",
                        "Test Organization",
                        List.of(new MyInfoResult.RoleInfo("role-001", "ROLE_SUPER_ADMIN")),
                        List.of("*:*"),
                        null,
                        null,
                        null);
            }
        };
    }

    @Bean
    @Primary
    public IdentityClient stubIdentityClient() {
        return outbox ->
                com.ryuqq.marketplace.application.seller.dto.response
                        .SellerIdentityProvisioningResult.success(
                        "stub-tenant-id", "stub-organization-id");
    }

    @Bean
    @Primary
    public SellerAdminIdentityClient stubSellerAdminIdentityClient() {
        return new SellerAdminIdentityClient() {
            @Override
            public com.ryuqq.marketplace.application.selleradmin.dto.response
                            .SellerAdminIdentityProvisioningResult
                    provisionSellerAdminIdentity(SellerAdminAuthOutbox outbox) {
                return null;
            }

            @Override
            public void resetSellerAdminPassword(String authUserId) {
                // stub: no-op
            }

            @Override
            public void changeSellerAdminPassword(String authUserId, String newPassword) {
                // stub: no-op
            }
        };
    }

    @Bean
    @Primary
    public SellerAdminEmailClient stubSellerAdminEmailClient() {
        return outbox -> null;
    }

    @Bean
    @Primary
    public ImageTransformClient stubImageTransformClient() {
        return new ImageTransformClient() {
            @Override
            public ImageTransformResponse createTransformRequest(
                    String uploadedUrl, ImageVariantType variantType) {
                return ImageTransformResponse.pending("stub-transform-" + UUID.randomUUID());
            }

            @Override
            public ImageTransformResponse getTransformRequest(String transformRequestId) {
                return ImageTransformResponse.completed(
                        transformRequestId,
                        "stub-asset-id",
                        "https://cdn.stub.com/variant.webp",
                        300,
                        300);
            }
        };
    }

    @Bean
    @Primary
    public DescriptionAnalysisAiClient stubDescriptionAnalysisAiClient() {
        return (ProductGroupDescription description, List<ExtractedAttribute> previousResults) ->
                Collections.emptyList();
    }

    @Bean
    @Primary
    public NoticeAnalysisAiClient stubNoticeAnalysisAiClient() {
        return (ProductNotice productNotice,
                List<com.ryuqq.marketplace.domain.productintelligence.vo.NoticeSuggestion>
                        previousResults) -> Collections.emptyList();
    }

    @Bean
    @Primary
    public OptionAnalysisAiClient stubOptionAnalysisAiClient() {
        return (com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup productGroup,
                List<com.ryuqq.marketplace.domain.canonicaloption.aggregate.CanonicalOptionGroup>
                        canonicalOptionGroups,
                List<com.ryuqq.marketplace.domain.productintelligence.vo.OptionMappingSuggestion>
                        previousResults) -> Collections.emptyList();
    }

    @Bean
    @Primary
    public DescriptionAnalysisPublishClient stubDescriptionAnalysisPublishClient() {
        return new DescriptionAnalysisPublishClient() {
            @Override
            public String queueName() {
                return "stub-description-analysis-queue";
            }

            @Override
            public String publish(String messageBody) {
                return "stub-message-id";
            }
        };
    }

    @Bean
    @Primary
    public OptionAnalysisPublishClient stubOptionAnalysisPublishClient() {
        return new OptionAnalysisPublishClient() {
            @Override
            public String queueName() {
                return "stub-option-analysis-queue";
            }

            @Override
            public String publish(String messageBody) {
                return "stub-message-id";
            }
        };
    }

    @Bean
    @Primary
    public NoticeAnalysisPublishClient stubNoticeAnalysisPublishClient() {
        return new NoticeAnalysisPublishClient() {
            @Override
            public String queueName() {
                return "stub-notice-analysis-queue";
            }

            @Override
            public String publish(String messageBody) {
                return "stub-message-id";
            }
        };
    }

    @Bean
    @Primary
    public AggregationPublishClient stubAggregationPublishClient() {
        return new AggregationPublishClient() {
            @Override
            public String queueName() {
                return "stub-aggregation-queue";
            }

            @Override
            public String publish(String messageBody) {
                return "stub-message-id";
            }
        };
    }

    @Bean
    @Primary
    public OutboundSyncPublishClient stubOutboundSyncPublishClient() {
        return messageBody -> "stub-message-id";
    }

    @Bean
    @Primary
    public OutboundSyncOutboxCommandPort stubOutboundSyncOutboxCommandPort() {
        final AtomicLong sequence = new AtomicLong(1);
        return new OutboundSyncOutboxCommandPort() {
            @Override
            public Long persist(OutboundSyncOutbox outbox) {
                return sequence.getAndIncrement();
            }

            @Override
            public void persistAll(List<OutboundSyncOutbox> outboxes) {
                // stub: no-op
            }
        };
    }

    @Bean
    @Primary
    public OutboundSyncOutboxQueryPort stubOutboundSyncOutboxQueryPort() {
        return new OutboundSyncOutboxQueryPort() {
            @Override
            public List<OutboundSyncOutbox> findPendingByProductGroupId(
                    ProductGroupId productGroupId) {
                return Collections.emptyList();
            }

            @Override
            public List<OutboundSyncOutbox> findPendingOutboxes(Instant beforeTime, int batchSize) {
                return Collections.emptyList();
            }

            @Override
            public List<OutboundSyncOutbox> findProcessingTimeoutOutboxes(
                    Instant timeoutBefore, int batchSize) {
                return Collections.emptyList();
            }

            @Override
            public OutboundSyncOutbox getById(Long outboxId) {
                throw new IllegalArgumentException("Stub: OutboundSyncOutbox not found");
            }
        };
    }

    // ===== LegacyConversion 포트 Stubs =====

    @Bean
    @Primary
    public LegacyConversionOutboxCommandPort stubLegacyConversionOutboxCommandPort() {
        final AtomicLong sequence = new AtomicLong(1);
        return outbox -> sequence.getAndIncrement();
    }

    @Bean
    @Primary
    public LegacyConversionOutboxQueryPort stubLegacyConversionOutboxQueryPort() {
        return new LegacyConversionOutboxQueryPort() {
            @Override
            public List<LegacyConversionOutbox> findPendingOutboxes(Instant beforeTime, int limit) {
                return Collections.emptyList();
            }

            @Override
            public List<LegacyConversionOutbox> findProcessingTimeoutOutboxes(
                    Instant timeoutThreshold, int limit) {
                return Collections.emptyList();
            }

            @Override
            public boolean existsPendingByLegacyProductGroupId(long legacyProductGroupId) {
                return false;
            }

            @Override
            public Set<Long> findExistingLegacyProductGroupIds(
                    Collection<Long> legacyProductGroupIds) {
                return Collections.emptySet();
            }

            @Override
            public long countDistinctLegacyProductGroupIds() {
                return 0L;
            }
        };
    }

    @Bean
    @Primary
    public LegacyProductGroupIdScanPort stubLegacyProductGroupIdScanPort() {
        return (afterId, limit) -> Collections.emptyList();
    }

    @Bean
    @Primary
    public LegacyProductIdMappingCommandPort stubLegacyProductIdMappingCommandPort() {
        final AtomicLong sequence = new AtomicLong(1);
        return new LegacyProductIdMappingCommandPort() {
            @Override
            public Long persist(LegacyProductIdMapping mapping) {
                return sequence.getAndIncrement();
            }

            @Override
            public void persistAll(List<LegacyProductIdMapping> mappings) {
                // stub: no-op
            }
        };
    }

    @Bean
    @Primary
    public LegacyProductIdMappingQueryPort stubLegacyProductIdMappingQueryPort() {
        return new LegacyProductIdMappingQueryPort() {
            @Override
            public Optional<LegacyProductIdMapping> findByLegacyProductId(long legacyProductId) {
                return Optional.empty();
            }

            @Override
            public Optional<LegacyProductIdMapping> findByInternalProductId(
                    long internalProductId) {
                return Optional.empty();
            }

            @Override
            public List<LegacyProductIdMapping> findByLegacyProductGroupId(
                    long legacyProductGroupId) {
                return Collections.emptyList();
            }
        };
    }

    // ===== 레거시 모듈 Stubs (persistence-mysql-legacy 제외 시 필요) =====

    @Bean
    @Primary
    public ResolveLegacyProductGroupSellerIdUseCase stubResolveLegacyProductGroupSellerIdUseCase() {
        return productGroupId -> Optional.empty();
    }

    // ===== 미구현 포트 Stubs =====

    @Bean
    @Primary
    public FileStorageClient stubFileStorageClient() {
        return new FileStorageClient() {
            @Override
            public PresignedUrlResponse generateUploadUrl(PresignedUploadUrlRequest request) {
                return new PresignedUrlResponse(
                        "stub-session-id",
                        "https://stub-presigned-url.example.com",
                        "stub-file-key",
                        Instant.now().plusSeconds(3600),
                        "https://stub-access-url.example.com");
            }

            @Override
            public String generateDownloadUrl(String fileAssetId, int expirationMinutes) {
                return "https://stub-download-url.example.com/" + fileAssetId;
            }

            @Override
            public void deleteFile(String fileAssetId) {
                // stub: no-op
            }

            @Override
            public void deleteFiles(List<String> fileAssetIds) {
                // stub: no-op
            }

            @Override
            public ExternalDownloadResponse downloadFromExternalUrl(
                    ExternalDownloadRequest request) {
                return ExternalDownloadResponse.success(
                        request.sourceUrl(),
                        "https://stub-cdn.example.com/" + request.filename(),
                        "stub-asset-" + request.filename());
            }

            @Override
            public List<ExternalDownloadResponse> downloadFromExternalUrls(
                    List<ExternalDownloadRequest> requests) {
                return requests.stream().map(this::downloadFromExternalUrl).toList();
            }

            @Override
            public void completeUploadSession(String sessionId, long fileSize, String etag) {
                // stub: no-op
            }

            @Override
            public String uploadHtmlContent(String htmlContent, String category, String filename) {
                return "https://stub-cdn.example.com/" + category + "/" + filename;
            }
        };
    }

    @Bean
    @Primary
    public IdGeneratorPort stubIdGeneratorPort() {
        return () -> UUID.randomUUID().toString();
    }

    @Bean
    @Primary
    public SellerAdminAuthOutboxCommandPort stubSellerAdminAuthOutboxCommandPort() {
        final AtomicLong sequence = new AtomicLong(1);
        return outbox -> sequence.getAndIncrement();
    }

    @Bean
    @Primary
    public SellerAdminAuthOutboxQueryPort stubSellerAdminAuthOutboxQueryPort() {
        return new SellerAdminAuthOutboxQueryPort() {
            @Override
            public Optional<SellerAdminAuthOutbox> findPendingBySellerAdminId(
                    SellerAdminId sellerAdminId) {
                return Optional.empty();
            }

            @Override
            public List<SellerAdminAuthOutbox> findPendingOutboxesForRetry(
                    Instant beforeTime, int limit) {
                return Collections.emptyList();
            }

            @Override
            public List<SellerAdminAuthOutbox> findProcessingTimeoutOutboxes(
                    Instant timeoutThreshold, int limit) {
                return Collections.emptyList();
            }
        };
    }
}
