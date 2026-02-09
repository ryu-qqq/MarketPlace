package com.ryuqq.marketplace.adapter.in.rest.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 스펙 설정.
 *
 * <p>Gateway 뒤에서 동작하므로, Swagger UI의 "Try it out" 기능이 올바른 URL로 요청하도록 서버 URL을 명시합니다. Gateway-first
 * 아키텍처에서 전달되는 X-User-* 헤더를 SecurityScheme으로 등록하여 Swagger UI에서 인증 헤더를 입력할 수 있게 합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Configuration
public class OpenApiConfig {

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USER_ROLES = "X-User-Roles";
    private static final String HEADER_USER_PERMISSIONS = "X-User-Permissions";
    private static final String HEADER_ORGANIZATION_ID = "X-Organization-Id";
    private static final String HEADER_AUTHENTICATED = "X-Authenticated";

    @Bean
    public OpenAPI openAPI(@Value("${api.server.url:}") String serverUrl) {
        OpenAPI openAPI =
                new OpenAPI()
                        .info(
                                new Info()
                                        .title("SETOF Commerce Admin API")
                                        .version("v1")
                                        .description("SETOF Commerce Admin API 문서"))
                        .components(securityComponents())
                        .security(
                                List.of(
                                        new SecurityRequirement()
                                                .addList(HEADER_USER_ID)
                                                .addList(HEADER_USER_ROLES)
                                                .addList(HEADER_USER_PERMISSIONS)
                                                .addList(HEADER_ORGANIZATION_ID)
                                                .addList(HEADER_AUTHENTICATED)));

        if (serverUrl != null && !serverUrl.isBlank()) {
            openAPI.servers(List.of(new Server().url(serverUrl).description("API Server")));
        }

        return openAPI;
    }

    private Components securityComponents() {
        return new Components()
                .addSecuritySchemes(
                        HEADER_USER_ID,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name(HEADER_USER_ID)
                                .description("사용자 ID (필수)"))
                .addSecuritySchemes(
                        HEADER_USER_ROLES,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name(HEADER_USER_ROLES)
                                .description("사용자 역할 (예: ROLE_SUPER_ADMIN)"))
                .addSecuritySchemes(
                        HEADER_USER_PERMISSIONS,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name(HEADER_USER_PERMISSIONS)
                                .description("사용자 권한 (예: *:*, seller:read)"))
                .addSecuritySchemes(
                        HEADER_ORGANIZATION_ID,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name(HEADER_ORGANIZATION_ID)
                                .description("조직 ID"))
                .addSecuritySchemes(
                        HEADER_AUTHENTICATED,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name(HEADER_AUTHENTICATED)
                                .description("인증 여부"));
    }
}
