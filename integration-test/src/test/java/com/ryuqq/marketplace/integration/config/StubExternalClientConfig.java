package com.ryuqq.marketplace.integration.config;

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
import com.ryuqq.marketplace.application.common.port.out.IdGeneratorPort;
import com.ryuqq.marketplace.application.common.port.out.client.FileStorageClient;
import com.ryuqq.marketplace.application.imagetransform.dto.response.ImageTransformResponse;
import com.ryuqq.marketplace.application.imagetransform.port.out.client.ImageTransformClient;
import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderPayload;
import com.ryuqq.marketplace.application.inboundorder.port.out.client.SalesChannelOrderClient;
import com.ryuqq.marketplace.application.legacy.productgroup.port.in.query.ResolveLegacyProductGroupSellerIdUseCase;
import com.ryuqq.marketplace.application.legacyauth.dto.result.LegacySellerAuthResult;
import com.ryuqq.marketplace.application.legacyauth.dto.result.LegacyTokenResult;
import com.ryuqq.marketplace.application.legacyauth.manager.LegacySellerAuthCompositeReadManager;
import com.ryuqq.marketplace.application.legacyauth.manager.LegacyTokenCacheReadManager;
import com.ryuqq.marketplace.application.legacyauth.manager.LegacyTokenManager;
import com.ryuqq.marketplace.application.legacyauth.port.out.LegacySellerAuthCompositeQueryPort;
import com.ryuqq.marketplace.application.legacyauth.port.out.LegacyTokenCacheCommandPort;
import com.ryuqq.marketplace.application.legacyauth.port.out.LegacyTokenCacheQueryPort;
import com.ryuqq.marketplace.application.legacyauth.port.out.LegacyTokenClient;
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
                    com.ryuqq.marketplace.application.productgroup.dto.composite
                                    .ProductGroupDetailBundle
                            bundle,
                    Long externalCategoryId,
                    Long externalBrandId,
                    com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel
                            channel,
                    com.ryuqq.marketplace.domain.shop.aggregate.Shop shop) {
                return "stub-external-product-" + bundle.group().idValue();
            }

            @Override
            public void updateProduct(
                    com.ryuqq.marketplace.application.productgroup.dto.composite
                                    .ProductGroupDetailBundle
                            bundle,
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

    /**
     * OutboundSyncOutboxCommandPort를 실제 DB 어댑터로 등록합니다.
     *
     * <p>E2E 테스트에서 retrySyncHistory 등 실제 DB 쓰기가 필요한 커맨드 API를 검증하기 위해 실제 어댑터를 사용합니다.
     */
    @Bean
    @Primary
    public OutboundSyncOutboxCommandPort stubOutboundSyncOutboxCommandPort(
            OutboundSyncOutboxCommandAdapter realAdapter) {
        return realAdapter;
    }

    /**
     * OutboundSyncOutboxQueryPort를 실제 DB 어댑터로 등록합니다.
     *
     * <p>E2E 테스트에서 retrySyncHistory 등 실제 DB 조회가 필요한 API를 검증하기 위해 실제 어댑터를 사용합니다.
     * OmsProductQueryE2ETest의 sync-history 조회도 실제 DB 결과를 반환합니다.
     */
    @Bean
    @Primary
    public OutboundSyncOutboxQueryPort stubOutboundSyncOutboxQueryPort(
            OutboundSyncOutboxQueryAdapter realAdapter) {
        return realAdapter;
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
                    java.util.Collection<Long> legacyProductGroupIds) {
                return Collections.emptyList();
            }
        };
    }

    /**
     * legacyconversion 패키지는 ComponentScan에서 legacy.* 패턴으로 제외되므로, ExternalProductInitializer가 의존하는
     * Manager를 직접 등록합니다.
     */
    @Bean
    @Primary
    public LegacyProductIdMappingReadManager stubLegacyProductIdMappingReadManager(
            LegacyProductIdMappingQueryPort queryPort) {
        return new LegacyProductIdMappingReadManager(queryPort);
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
            public java.util.List<
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

    // ===== LegacyAuth Stubs =====

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
        };
    }

    /**
     * LegacyTokenManager는 @ConditionalOnBean(LegacyTokenClient.class)로 선언되어 있으나 TestConfiguration
     * 로딩 순서 문제로 자동 생성이 안 될 수 있습니다. 명시적으로 등록하여 SecurityConfig의 legacyJwtAuthenticationFilter 의존성을
     * 충족합니다.
     */
    @Bean
    @Primary
    public LegacyTokenManager stubLegacyTokenManager(LegacyTokenClient legacyTokenClient) {
        return new LegacyTokenManager(legacyTokenClient);
    }

    @Bean
    @Primary
    public LegacySellerAuthCompositeQueryPort stubLegacySellerAuthCompositeQueryPort() {
        return email ->
                java.util.Optional.of(
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
        return email -> java.util.Optional.empty();
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

    // ===== Naver Commerce Adapter Stubs (ConditionalOnProperty 회피) =====
    // NaverCancelClaimSyncStrategy / NaverRefundClaimSyncStrategy 가 @Component로 선언되어
    // ConditionalOnProperty 없이 항상 등록되려 하므로, 의존 Adapter stub을 제공합니다.

    @Bean
    @Primary
    public com.ryuqq.marketplace.adapter.out.client.naver.adapter.NaverCommerceCancelClientAdapter
            stubNaverCommerceCancelClientAdapter() {
        return new com.ryuqq.marketplace.adapter.out.client.naver.adapter
                .NaverCommerceCancelClientAdapter(null, null) {
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
                .NaverCommerceReturnClientAdapter(null, null) {
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

            @Override
            public com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverClaimResponse
                    rejectReturn(
                            String productOrderId,
                            com.ryuqq.marketplace.adapter.out.client.naver.dto.order
                                            .NaverReturnRejectRequest
                                    request) {
                return null;
            }

            @Override
            public void holdbackReturn(String productOrderId) {
                // stub: no-op
            }

            @Override
            public void releaseReturnHoldback(String productOrderId) {
                // stub: no-op
            }
        };
    }

    // ===== InboundOrder 외부 채널 클라이언트 Stub =====

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
                    Instant fromTime,
                    Instant toTime) {
                return Collections.emptyList();
            }
        };
    }
}
