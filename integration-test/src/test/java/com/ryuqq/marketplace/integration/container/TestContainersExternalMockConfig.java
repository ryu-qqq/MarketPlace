package com.ryuqq.marketplace.integration.container;

import com.ryuqq.authhub.sdk.auth.TokenResolver;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.adapter.OutboundSyncOutboxCommandAdapter;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.adapter.OutboundSyncOutboxQueryAdapter;
import com.ryuqq.marketplace.application.auth.dto.response.LoginResult;
import com.ryuqq.marketplace.application.auth.dto.response.MyInfoResult;
import com.ryuqq.marketplace.application.auth.dto.response.RefreshResult;
import com.ryuqq.marketplace.application.auth.port.out.client.AuthClient;
import com.ryuqq.marketplace.application.brandmapping.port.out.query.BrandMappingQueryPort;
import com.ryuqq.marketplace.application.categorymapping.port.out.query.CategoryMappingQueryPort;
import com.ryuqq.marketplace.application.claimsync.port.out.client.SalesChannelClaimClient;
import com.ryuqq.marketplace.application.common.dto.command.ExternalDownloadRequest;
import com.ryuqq.marketplace.application.common.dto.command.PresignedUploadUrlRequest;
import com.ryuqq.marketplace.application.common.dto.response.ExternalDownloadResponse;
import com.ryuqq.marketplace.application.common.dto.response.ExternalDownloadStatusResponse;
import com.ryuqq.marketplace.application.common.dto.response.PresignedUrlResponse;
import com.ryuqq.marketplace.application.common.dto.result.OutboxSyncResult;
import com.ryuqq.marketplace.application.common.port.out.IdGeneratorPort;
import com.ryuqq.marketplace.application.common.port.out.client.FileStorageClient;
import com.ryuqq.marketplace.application.imagetransform.dto.response.ImageTransformResponse;
import com.ryuqq.marketplace.application.imagetransform.port.out.client.ImageTransformClient;
import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderPayload;
import com.ryuqq.marketplace.application.inboundorder.port.out.client.SalesChannelOrderClient;
import com.ryuqq.marketplace.application.legacy.auth.dto.result.LegacySellerAuthResult;
import com.ryuqq.marketplace.application.legacy.auth.dto.result.LegacyTokenResult;
import com.ryuqq.marketplace.application.legacy.auth.manager.LegacySellerAuthCompositeReadManager;
import com.ryuqq.marketplace.application.legacy.auth.manager.LegacyTokenCacheReadManager;
import com.ryuqq.marketplace.application.legacy.auth.manager.LegacyTokenManager;
import com.ryuqq.marketplace.application.legacy.auth.port.in.LegacyLoginUseCase;
import com.ryuqq.marketplace.application.legacy.auth.port.out.LegacySellerAuthCompositeQueryPort;
import com.ryuqq.marketplace.application.legacy.auth.port.out.LegacyTokenCacheCommandPort;
import com.ryuqq.marketplace.application.legacy.auth.port.out.LegacyTokenCacheQueryPort;
import com.ryuqq.marketplace.application.legacy.auth.port.out.LegacyTokenClient;
import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderUpdateResult;
import com.ryuqq.marketplace.application.legacy.order.port.in.command.LegacyOrderUpdateUseCase;
import com.ryuqq.marketplace.application.legacy.productgroup.port.in.query.ResolveLegacyProductGroupSellerIdUseCase;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyProductIdMappingReadManager;
import com.ryuqq.marketplace.application.legacyconversion.port.out.command.LegacyConversionOutboxCommandPort;
import com.ryuqq.marketplace.application.legacyconversion.port.out.command.LegacyProductIdMappingCommandPort;
import com.ryuqq.marketplace.application.legacyconversion.port.out.query.LegacyConversionOutboxQueryPort;
import com.ryuqq.marketplace.application.legacyconversion.port.out.query.LegacyProductGroupIdScanPort;
import com.ryuqq.marketplace.application.legacyconversion.port.out.query.LegacyProductIdMappingQueryPort;
import com.ryuqq.marketplace.application.order.port.out.query.OrderQueryPort;
import com.ryuqq.marketplace.application.outboundsync.port.out.client.OutboundSyncPublishClient;
import com.ryuqq.marketplace.application.outboundsync.port.out.client.SalesChannelProductClient;
import com.ryuqq.marketplace.application.outboundsync.port.out.command.OutboundSyncOutboxCommandPort;
import com.ryuqq.marketplace.application.outboundsync.port.out.query.OutboundSyncOutboxQueryPort;
import com.ryuqq.marketplace.application.productintelligence.port.out.client.AggregationPublishClient;
import com.ryuqq.marketplace.application.productintelligence.port.out.client.DescriptionAnalysisAiClient;
import com.ryuqq.marketplace.application.productintelligence.port.out.client.DescriptionAnalysisPublishClient;
import com.ryuqq.marketplace.application.productintelligence.port.out.client.NoticeAnalysisAiClient;
import com.ryuqq.marketplace.application.productintelligence.port.out.client.NoticeAnalysisPublishClient;
import com.ryuqq.marketplace.application.productintelligence.port.out.client.OptionAnalysisAiClient;
import com.ryuqq.marketplace.application.productintelligence.port.out.client.OptionAnalysisPublishClient;
import com.ryuqq.marketplace.application.qna.port.out.client.QnaAnswerSyncStrategy;
import com.ryuqq.marketplace.application.qna.port.out.client.QnaOutboxPublishClient;
import com.ryuqq.marketplace.application.seller.port.out.client.IdentityClient;
import com.ryuqq.marketplace.application.selleradmin.port.out.client.SellerAdminEmailClient;
import com.ryuqq.marketplace.application.selleradmin.port.out.client.SellerAdminIdentityClient;
import com.ryuqq.marketplace.application.selleradmin.port.out.command.SellerAdminAuthOutboxCommandPort;
import com.ryuqq.marketplace.application.selleradmin.port.out.query.SellerAdminAuthOutboxQueryPort;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyConversionOutbox;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyProductIdMapping;
import com.ryuqq.marketplace.domain.order.aggregate.Order;
import com.ryuqq.marketplace.domain.order.id.OrderId;
import com.ryuqq.marketplace.domain.order.query.OrderSearchCriteria;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
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
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Testcontainers 통합 테스트용 외부 서비스 Mock 설정.
 *
 * <p>MySQL + Redis는 실제 Testcontainers를 사용하므로, Redis/Redisson Mock은 제외합니다. 외부 API 클라이언트 (SQS,
 * 네이버/셀릭, PG사 등)만 Mock으로 대체합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@TestConfiguration
@EnableAsync
public class TestContainersExternalMockConfig {

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
                    String uploadedUrl,
                    ImageVariantType variantType,
                    String fileAssetId,
                    String callbackUrl) {
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

            @Override
            public String resolveAssetCdnUrl(String assetId) {
                return "https://cdn.stub.com/" + assetId + ".webp";
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
    public SalesChannelProductClient stubSalesChannelProductClient() {
        return new SalesChannelProductClient() {
            @Override
            public String channelCode() {
                return "STUB";
            }

            @Override
            public String registerProduct(
                    com.ryuqq.marketplace.application.productgroup.dto.response.ProductGroupSyncData
                            syncData,
                    Long externalCategoryId,
                    Long externalBrandId,
                    com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel
                            channel,
                    com.ryuqq.marketplace.domain.shop.aggregate.Shop shop) {
                return "stub-external-product-" + syncData.queryResult().id();
            }

            @Override
            public void updateProduct(
                    com.ryuqq.marketplace.application.productgroup.dto.response.ProductGroupSyncData
                            syncData,
                    Long externalCategoryId,
                    Long externalBrandId,
                    String externalProductId,
                    com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel
                            channel,
                    java.util.Set<com.ryuqq.marketplace.domain.outboundsync.vo.ChangedArea>
                            changedAreas) {
                // stub: no-op
            }

            @Override
            public void deleteProduct(
                    String externalProductId,
                    com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel
                            channel) {
                // stub: no-op
            }
        };
    }

    @Bean
    @Primary
    public CategoryMappingQueryPort stubCategoryMappingQueryPort() {
        return new CategoryMappingQueryPort() {
            @Override
            public Optional<Long> findSalesChannelCategoryId(
                    Long salesChannelId, Long internalCategoryId) {
                return Optional.of(internalCategoryId);
            }

            @Override
            public Optional<String> findExternalCategoryCode(
                    Long salesChannelId, Long internalCategoryId) {
                return Optional.of(String.valueOf(internalCategoryId));
            }
        };
    }

    @Bean
    @Primary
    public BrandMappingQueryPort stubBrandMappingQueryPort() {
        return new BrandMappingQueryPort() {
            @Override
            public Optional<Long> findSalesChannelBrandId(
                    Long salesChannelId, Long internalBrandId) {
                return Optional.of(internalBrandId);
            }

            @Override
            public Optional<String> findExternalBrandCode(
                    Long salesChannelId, Long internalBrandId) {
                return Optional.of(String.valueOf(internalBrandId));
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
    public OutboundSyncOutboxCommandPort stubOutboundSyncOutboxCommandPort(
            OutboundSyncOutboxCommandAdapter realAdapter) {
        return realAdapter;
    }

    @Bean
    @Primary
    public OutboundSyncOutboxQueryPort stubOutboundSyncOutboxQueryPort(
            OutboundSyncOutboxQueryAdapter realAdapter) {
        return realAdapter;
    }

    // ===== QnA 외부 클라이언트 Stubs =====

    @Bean
    @Primary
    public QnaOutboxPublishClient stubQnaOutboxPublishClient() {
        return messageBody -> {
            // stub: no-op
        };
    }

    @Bean
    @Primary
    public QnaAnswerSyncStrategy stubQnaAnswerSyncStrategy() {
        return outbox -> OutboxSyncResult.success();
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
            public List<LegacyProductIdMapping> findByInternalProductGroupId(
                    long internalProductGroupId) {
                return Collections.emptyList();
            }

            @Override
            public List<LegacyProductIdMapping> findByLegacyProductGroupId(
                    long legacyProductGroupId) {
                return Collections.emptyList();
            }

            @Override
            public List<LegacyProductIdMapping> findByLegacyProductGroupIds(
                    Collection<Long> legacyProductGroupIds) {
                return Collections.emptyList();
            }
        };
    }

    @Bean
    @Primary
    public LegacyProductIdMappingReadManager stubLegacyProductIdMappingReadManager(
            LegacyProductIdMappingQueryPort queryPort) {
        return new LegacyProductIdMappingReadManager(queryPort);
    }

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

            @Override
            public String createDownloadTask(ExternalDownloadRequest request) {
                return "stub-download-task-" + request.filename();
            }

            @Override
            public ExternalDownloadStatusResponse getDownloadTaskStatus(String downloadTaskId) {
                return ExternalDownloadStatusResponse.completed(
                        downloadTaskId,
                        "https://stub-cdn.example.com/" + downloadTaskId,
                        "stub-asset-" + downloadTaskId);
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
            public SellerAdminAuthOutbox getById(Long outboxId) {
                throw new IllegalStateException(
                        "Stub: SellerAdminAuthOutbox를 찾을 수 없습니다. outboxId=" + outboxId);
            }

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

    // ===== Order 포트 Stubs =====

    @Bean
    @Primary
    public OrderQueryPort stubOrderQueryPort() {
        return new OrderQueryPort() {
            @Override
            public Optional<Order> findById(OrderId id) {
                return Optional.empty();
            }

            @Override
            public Optional<Order> findByOrderNumber(String orderNumber) {
                return Optional.empty();
            }

            @Override
            public boolean existsByExternalOrderNo(long salesChannelId, String externalOrderNo) {
                return false;
            }

            @Override
            public List<Order> findByCriteria(OrderSearchCriteria criteria) {
                return Collections.emptyList();
            }

            @Override
            public long countByCriteria(OrderSearchCriteria criteria) {
                return 0L;
            }
        };
    }

    // ===== ClaimSync 외부 채널 클라이언트 Stub =====

    @Bean
    @Primary
    public SalesChannelClaimClient stubSalesChannelClaimClient() {
        return new SalesChannelClaimClient() {
            @Override
            public boolean supports(String channelCode) {
                return true;
            }

            @Override
            public List<
                            com.ryuqq.marketplace.application.claimsync.dto.external
                                    .ExternalClaimPayload>
                    fetchClaimChanges(
                            long salesChannelId,
                            long shopId,
                            com.ryuqq.marketplace.domain.shop.vo.ShopCredentials credentials,
                            java.time.Instant fromTime,
                            java.time.Instant toTime) {
                return Collections.emptyList();
            }
        };
    }

    // ===== 주문 폴링 Stubs =====

    @Bean
    @Primary
    public SalesChannelOrderClient stubSalesChannelOrderClient() {
        return new SalesChannelOrderClient() {
            @Override
            public boolean supports(String channelCode) {
                return true;
            }

            @Override
            public List<ExternalOrderPayload> fetchNewOrders(
                    long salesChannelId,
                    long shopId,
                    com.ryuqq.marketplace.domain.shop.vo.ShopCredentials credentials,
                    java.time.Instant fromTime,
                    java.time.Instant toTime) {
                return Collections.emptyList();
            }
        };
    }

    // ===== LegacyAuth Stubs =====

    @Bean
    @Primary
    public com.ryuqq.marketplace.application.legacy.auth.port.out.LegacyPasswordEncoder
            stubLegacyPasswordEncoder() {
        return (rawPassword, encodedPassword) ->
                rawPassword != null && rawPassword.equals(encodedPassword);
    }

    @Bean
    @Primary
    public LegacyTokenClient stubLegacyTokenClient() {
        return new LegacyTokenClient() {
            @Override
            public LegacyTokenResult generateToken(String email, long sellerId, String roleType) {
                return new LegacyTokenResult(
                        "stub-legacy-access-token", "stub-legacy-refresh-token", email, 3600L);
            }

            @Override
            public String extractSubject(String token) {
                return "stub@example.com";
            }

            @Override
            public boolean isValid(String token) {
                return true;
            }

            @Override
            public boolean isExpired(String token) {
                return false;
            }

            @Override
            public long extractSellerId(String token) {
                return 10L;
            }

            @Override
            public String extractRole(String token) {
                return "MASTER";
            }
        };
    }

    @Bean
    @Primary
    public LegacyTokenManager stubLegacyTokenManager(LegacyTokenClient legacyTokenClient) {
        return new LegacyTokenManager(legacyTokenClient);
    }

    @Bean
    @Primary
    public LegacySellerAuthCompositeQueryPort stubLegacySellerAuthCompositeQueryPort() {
        return email ->
                Optional.of(
                        new LegacySellerAuthResult(10L, email, "stub-hash", "MASTER", "APPROVED"));
    }

    @Bean
    @Primary
    public LegacySellerAuthCompositeReadManager stubLegacySellerAuthCompositeReadManager(
            LegacySellerAuthCompositeQueryPort queryPort) {
        return new LegacySellerAuthCompositeReadManager(queryPort);
    }

    @Bean
    @Primary
    public LegacyTokenCacheQueryPort stubLegacyTokenCacheQueryPort() {
        return email -> Optional.empty();
    }

    @Bean
    @Primary
    public LegacyTokenCacheCommandPort stubLegacyTokenCacheCommandPort() {
        return (email, refreshToken, expiresInSeconds) -> {
            // stub: no-op
        };
    }

    @Bean
    @Primary
    public LegacyTokenCacheReadManager stubLegacyTokenCacheReadManager(
            LegacyTokenCacheQueryPort cacheQueryPort) {
        return new LegacyTokenCacheReadManager(cacheQueryPort);
    }

    @Bean
    @Primary
    public LegacyLoginUseCase stubLegacyLoginUseCase() {
        return command -> "stub-legacy-token";
    }

    @Bean
    @Primary
    public LegacyOrderUpdateUseCase stubLegacyOrderUpdateUseCase() {
        return command ->
                new LegacyOrderUpdateResult(
                        command.orderId(), 0L, "UNKNOWN", "UNKNOWN", "stub", "stub");
    }

    @Bean
    @Primary
    public com.ryuqq.marketplace.application.legacy.order.port.in.query.LegacyOrderQueryUseCase
            stubLegacyOrderQueryUseCase() {
        return Mockito.mock(
                com.ryuqq.marketplace.application.legacy.order.port.in.query.LegacyOrderQueryUseCase
                        .class);
    }

    @Bean
    @Primary
    public com.ryuqq.marketplace.application.legacy.order.port.in.query.LegacyOrderListQueryUseCase
            stubLegacyOrderListQueryUseCase() {
        return Mockito.mock(
                com.ryuqq.marketplace.application.legacy.order.port.in.query
                        .LegacyOrderListQueryUseCase.class);
    }

    @Bean
    @Primary
    public com.ryuqq.marketplace.application.legacy.seller.port.in.LegacyGetCurrentSellerUseCase
            stubLegacyGetCurrentSellerUseCase() {
        return Mockito.mock(
                com.ryuqq.marketplace.application.legacy.seller.port.in
                        .LegacyGetCurrentSellerUseCase.class);
    }

    @Bean
    @Primary
    public com.ryuqq.marketplace.application.legacy.productgroup.port.in.command
                    .LegacyProductGroupFullRegisterUseCase
            stubLegacyProductGroupFullRegisterUseCase() {
        return Mockito.mock(
                com.ryuqq.marketplace.application.legacy.productgroup.port.in.command
                        .LegacyProductGroupFullRegisterUseCase.class);
    }

    @Bean
    @Primary
    public com.ryuqq.marketplace.application.legacy.productgroup.port.in.command
                    .LegacyProductGroupFullUpdateUseCase
            stubLegacyProductGroupFullUpdateUseCase() {
        return Mockito.mock(
                com.ryuqq.marketplace.application.legacy.productgroup.port.in.command
                        .LegacyProductGroupFullUpdateUseCase.class);
    }

    @Bean
    @Primary
    public com.ryuqq.marketplace.application.legacy.productgroup.port.in.command
                    .LegacyProductMarkOutOfStockUseCase
            stubLegacyProductMarkOutOfStockUseCase() {
        return Mockito.mock(
                com.ryuqq.marketplace.application.legacy.productgroup.port.in.command
                        .LegacyProductMarkOutOfStockUseCase.class);
    }

    @Bean
    @Primary
    public com.ryuqq.marketplace.application.legacy.productgroup.port.in.command
                    .LegacyProductUpdateDisplayStatusUseCase
            stubLegacyProductUpdateDisplayStatusUseCase() {
        return Mockito.mock(
                com.ryuqq.marketplace.application.legacy.productgroup.port.in.command
                        .LegacyProductUpdateDisplayStatusUseCase.class);
    }

    @Bean
    @Primary
    public com.ryuqq.marketplace.application.legacy.productcontext.port.in.query
                    .ResolveLegacyProductContextUseCase
            stubResolveLegacyProductContextUseCase() {
        return Mockito.mock(
                com.ryuqq.marketplace.application.legacy.productcontext.port.in.query
                        .ResolveLegacyProductContextUseCase.class);
    }

    @Bean
    @Primary
    public com.ryuqq.marketplace.application.legacy.productgroup.port.in.command
                    .LegacyProductUpdatePriceUseCase
            stubLegacyProductUpdatePriceUseCase() {
        return Mockito.mock(
                com.ryuqq.marketplace.application.legacy.productgroup.port.in.command
                        .LegacyProductUpdatePriceUseCase.class);
    }

    @Bean
    @Primary
    public com.ryuqq.marketplace.application.legacy.product.port.in.command
                    .LegacyProductUpdateOptionsUseCase
            stubLegacyProductUpdateOptionsUseCase() {
        return Mockito.mock(
                com.ryuqq.marketplace.application.legacy.product.port.in.command
                        .LegacyProductUpdateOptionsUseCase.class);
    }

    @Bean
    @Primary
    public com.ryuqq.marketplace.application.legacy.product.port.in.command
                    .LegacyProductUpdateStockUseCase
            stubLegacyProductUpdateStockUseCase() {
        return Mockito.mock(
                com.ryuqq.marketplace.application.legacy.product.port.in.command
                        .LegacyProductUpdateStockUseCase.class);
    }

    @Bean
    @Primary
    public com.ryuqq.marketplace.application.legacy.productgroup.port.in.query
                    .LegacyProductQueryUseCase
            stubLegacyProductQueryUseCase() {
        return Mockito.mock(
                com.ryuqq.marketplace.application.legacy.productgroup.port.in.query
                        .LegacyProductQueryUseCase.class);
    }

    @Bean
    @Primary
    public com.ryuqq.marketplace.application.legacy.productgroupdescription.port.in.command
                    .LegacyProductUpdateDescriptionUseCase
            stubLegacyProductUpdateDescriptionUseCase() {
        return Mockito.mock(
                com.ryuqq.marketplace.application.legacy.productgroupdescription.port.in.command
                        .LegacyProductUpdateDescriptionUseCase.class);
    }

    @Bean
    @Primary
    public com.ryuqq.marketplace.application.legacy.productgroupimage.port.in.command
                    .LegacyProductUpdateImagesUseCase
            stubLegacyProductUpdateImagesUseCase() {
        return Mockito.mock(
                com.ryuqq.marketplace.application.legacy.productgroupimage.port.in.command
                        .LegacyProductUpdateImagesUseCase.class);
    }

    @Bean
    @Primary
    public com.ryuqq.marketplace.application.legacy.productnotice.port.in.command
                    .LegacyProductUpdateNoticeUseCase
            stubLegacyProductUpdateNoticeUseCase() {
        return Mockito.mock(
                com.ryuqq.marketplace.application.legacy.productnotice.port.in.command
                        .LegacyProductUpdateNoticeUseCase.class);
    }

    @Bean
    @Primary
    public com.ryuqq.marketplace.application.legacy.shipment.port.in
                    .LegacyGetShipmentCompanyCodesUseCase
            stubLegacyGetShipmentCompanyCodesUseCase() {
        return Mockito.mock(
                com.ryuqq.marketplace.application.legacy.shipment.port.in
                        .LegacyGetShipmentCompanyCodesUseCase.class);
    }

    @Bean
    @Primary
    public com.ryuqq.marketplace.application.legacy.session.port.in.command
                    .LegacyGetPresignedUrlUseCase
            stubLegacyGetPresignedUrlUseCase() {
        return Mockito.mock(
                com.ryuqq.marketplace.application.legacy.session.port.in.command
                        .LegacyGetPresignedUrlUseCase.class);
    }

    @Bean
    @Primary
    public com.ryuqq.marketplace.application.legacy.productnotice.port.in.query
                    .LegacyResolveNoticeFieldsUseCase
            stubLegacyResolveNoticeFieldsUseCase() {
        return Mockito.mock(
                com.ryuqq.marketplace.application.legacy.productnotice.port.in.query
                        .LegacyResolveNoticeFieldsUseCase.class);
    }

    @Bean
    @Primary
    public com.ryuqq.marketplace.application.legacy.productcontext.resolver
                    .LegacyNoticeCategoryResolver
            stubLegacyNoticeCategoryResolver() {
        return Mockito.mock(
                com.ryuqq.marketplace.application.legacy.productcontext.resolver
                        .LegacyNoticeCategoryResolver.class);
    }

    // ===== ImageVariantSync Stubs =====

    @Bean
    @Primary
    public com.ryuqq.marketplace.application.imagevariantsync.port.out.client.ImageVariantSyncClient
            stubImageVariantSyncClient() {
        return (sourceImageId, sourceType, variants) -> {
            // stub: no-op
        };
    }

    // ===== 이미지 인프라 Stubs =====

    @Bean
    @Primary
    public com.ryuqq.marketplace.application.common.port.out.InternalImageUrlChecker
            stubInternalImageUrlChecker() {
        return url -> false;
    }

    // ===== Naver Commerce Adapter Stubs =====

    @Bean
    @Primary
    public com.ryuqq.marketplace.adapter.out.client.naver.adapter.NaverCommerceCancelClientAdapter
            stubNaverCommerceCancelClientAdapter() {
        return new com.ryuqq.marketplace.adapter.out.client.naver.adapter
                .NaverCommerceCancelClientAdapter(null) {
            @Override
            public com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverClaimResponse
                    requestCancel(
                            String productOrderId,
                            com.ryuqq.marketplace.adapter.out.client.naver.dto.order
                                            .NaverCancelRequest
                                    request) {
                return null;
            }

            @Override
            public com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverClaimResponse
                    approveCancel(String productOrderId) {
                return null;
            }
        };
    }

    @Bean
    @Primary
    public com.ryuqq.marketplace.adapter.out.client.naver.adapter.NaverCommerceReturnClientAdapter
            stubNaverCommerceReturnClientAdapter() {
        return new com.ryuqq.marketplace.adapter.out.client.naver.adapter
                .NaverCommerceReturnClientAdapter(null) {
            @Override
            public com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverClaimResponse
                    requestReturn(
                            String productOrderId,
                            com.ryuqq.marketplace.adapter.out.client.naver.dto.order
                                            .NaverReturnRequest
                                    request) {
                return null;
            }

            @Override
            public com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverClaimResponse
                    approveReturn(String productOrderId) {
                return null;
            }
        };
    }
}
