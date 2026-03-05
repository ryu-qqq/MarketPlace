package com.ryuqq.marketplace.application.common.fallback;

import com.ryuqq.marketplace.application.outboundseller.dto.response.OutboundSellerSyncResult;
import com.ryuqq.marketplace.application.outboundseller.port.out.client.OutboundRefundPolicySyncClient;
import com.ryuqq.marketplace.application.outboundseller.port.out.client.OutboundSellerAddressSyncClient;
import com.ryuqq.marketplace.application.outboundseller.port.out.client.OutboundSellerSyncClient;
import com.ryuqq.marketplace.application.outboundseller.port.out.client.OutboundShippingPolicySyncClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 외부 셀러 동기화 클라이언트 폴백 설정.
 *
 * <p>setof-commerce-client 모듈이 클래스패스에 없거나 service-token이 설정되지 않은 경우 NoOp 구현체를 제공합니다.
 */
@Configuration
public class OutboundSellerClientFallbackConfig {

    @Bean
    @ConditionalOnMissingBean
    OutboundSellerSyncClient noOpOutboundSellerSyncClient() {
        return new OutboundSellerSyncClient() {
            @Override
            public OutboundSellerSyncResult createSeller(Long sellerId) {
                return OutboundSellerSyncResult.nonRetryableFailure(
                        "NO_CLIENT", "외부 셀러 동기화 클라이언트가 설정되지 않았습니다");
            }

            @Override
            public OutboundSellerSyncResult updateSeller(Long sellerId) {
                return OutboundSellerSyncResult.nonRetryableFailure(
                        "NO_CLIENT", "외부 셀러 동기화 클라이언트가 설정되지 않았습니다");
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean
    OutboundShippingPolicySyncClient noOpOutboundShippingPolicySyncClient() {
        return new OutboundShippingPolicySyncClient() {
            @Override
            public OutboundSellerSyncResult createShippingPolicy(Long sellerId, Long policyId) {
                return OutboundSellerSyncResult.nonRetryableFailure(
                        "NO_CLIENT", "외부 셀러 동기화 클라이언트가 설정되지 않았습니다");
            }

            @Override
            public OutboundSellerSyncResult updateShippingPolicy(Long sellerId, Long policyId) {
                return OutboundSellerSyncResult.nonRetryableFailure(
                        "NO_CLIENT", "외부 셀러 동기화 클라이언트가 설정되지 않았습니다");
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean
    OutboundRefundPolicySyncClient noOpOutboundRefundPolicySyncClient() {
        return new OutboundRefundPolicySyncClient() {
            @Override
            public OutboundSellerSyncResult createRefundPolicy(Long sellerId, Long policyId) {
                return OutboundSellerSyncResult.nonRetryableFailure(
                        "NO_CLIENT", "외부 셀러 동기화 클라이언트가 설정되지 않았습니다");
            }

            @Override
            public OutboundSellerSyncResult updateRefundPolicy(Long sellerId, Long policyId) {
                return OutboundSellerSyncResult.nonRetryableFailure(
                        "NO_CLIENT", "외부 셀러 동기화 클라이언트가 설정되지 않았습니다");
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean
    OutboundSellerAddressSyncClient noOpOutboundSellerAddressSyncClient() {
        return new OutboundSellerAddressSyncClient() {
            @Override
            public OutboundSellerSyncResult createSellerAddress(Long sellerId, Long addressId) {
                return OutboundSellerSyncResult.nonRetryableFailure(
                        "NO_CLIENT", "외부 셀러 동기화 클라이언트가 설정되지 않았습니다");
            }

            @Override
            public OutboundSellerSyncResult updateSellerAddress(Long sellerId, Long addressId) {
                return OutboundSellerSyncResult.nonRetryableFailure(
                        "NO_CLIENT", "외부 셀러 동기화 클라이언트가 설정되지 않았습니다");
            }

            @Override
            public OutboundSellerSyncResult deleteSellerAddress(Long sellerId, Long addressId) {
                return OutboundSellerSyncResult.nonRetryableFailure(
                        "NO_CLIENT", "외부 셀러 동기화 클라이언트가 설정되지 않았습니다");
            }
        };
    }
}
