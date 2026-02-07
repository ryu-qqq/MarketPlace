package com.ryuqq.marketplace.bootstrap.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.authhub.sdk.filter.GatewayAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 설정.
 *
 * <p>Gateway-first 아키텍처:
 *
 * <ul>
 *   <li>JWT 검증은 API Gateway에서 수행
 *   <li>이 서비스는 Gateway가 전달하는 X-User-* 헤더만 파싱
 *   <li>CORS/CSRF 처리도 Gateway 레벨에서 수행
 * </ul>
 *
 * <p>GatewayAuthenticationFilter (AuthHub SDK 제공):
 *
 * <ul>
 *   <li>X-User-Id, X-User-Roles, X-User-Permissions 등 헤더 파싱
 *   <li>UserContext를 ThreadLocal에 저장
 * </ul>
 *
 * <p>GatewaySecurityBridgeFilter:
 *
 * <ul>
 *   <li>UserContextHolder → SecurityContextHolder 브릿지
 *   <li>Spring Security URL 기반 접근 제어와 AuthHub SDK 메서드 기반 접근 제어 연동
 * </ul>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public GatewayAuthenticationFilter gatewayAuthenticationFilter() {
        return new GatewayAuthenticationFilter();
    }

    @Bean
    public GatewaySecurityBridgeFilter gatewaySecurityBridgeFilter() {
        return new GatewaySecurityBridgeFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            GatewayAuthenticationFilter gatewayAuthenticationFilter,
            GatewaySecurityBridgeFilter gatewaySecurityBridgeFilter,
            ObjectMapper objectMapper)
            throws Exception {
        http
                // Gateway 뒤에서 동작하므로 CSRF/CORS 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)

                // Stateless (세션 미사용)
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 폼 로그인 / HTTP Basic 비활성화
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // Security 예외 처리 (RFC 7807 ProblemDetail)
                .exceptionHandling(
                        ex ->
                                ex.authenticationEntryPoint(
                                                (request, response, authException) ->
                                                        writeSecurityProblemDetail(
                                                                response,
                                                                objectMapper,
                                                                HttpStatus.UNAUTHORIZED,
                                                                "Unauthorized",
                                                                "인증이 필요합니다",
                                                                "AUTHENTICATION_REQUIRED",
                                                                request.getRequestURI()))
                                        .accessDeniedHandler(
                                                (request, response, accessDeniedException) ->
                                                        writeSecurityProblemDetail(
                                                                response,
                                                                objectMapper,
                                                                HttpStatus.FORBIDDEN,
                                                                "Forbidden",
                                                                "접근 권한이 없습니다",
                                                                "ACCESS_DENIED",
                                                                request.getRequestURI())))

                // URL 기반 접근 제어
                .authorizeHttpRequests(
                        auth ->
                                auth
                                        // 인증 API (로그인)은 누구나 접근 가능
                                        .requestMatchers(
                                                HttpMethod.POST,
                                                "/api/v1/market/auth/login",
                                                "/api/v1/market/auth/refresh")
                                        .permitAll()

                                        // Actuator
                                        .requestMatchers("/actuator/**")
                                        .permitAll()

                                        // API 문서
                                        .requestMatchers(
                                                "/api/v1/market/api-docs/**",
                                                "/api/v1/market/swagger/**",
                                                "/api/v1/market/docs/**",
                                                "/swagger-ui/**",
                                                "/v3/api-docs/**")
                                        .permitAll()

                                        // 나머지는 인증 필요
                                        .anyRequest()
                                        .authenticated())

                // GatewayAuthenticationFilter: X-User-* 헤더 → UserContext (ThreadLocal)
                .addFilterBefore(
                        gatewayAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // GatewaySecurityBridgeFilter: UserContext → SecurityContext (Spring Security)
                .addFilterAfter(gatewaySecurityBridgeFilter, GatewayAuthenticationFilter.class);

        return http.build();
    }

    private void writeSecurityProblemDetail(
            HttpServletResponse response,
            ObjectMapper objectMapper,
            HttpStatus status,
            String title,
            String detail,
            String errorCode,
            String requestUri)
            throws IOException {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(title);
        pd.setType(URI.create("about:blank"));
        pd.setProperty("timestamp", Instant.now().toString());
        pd.setProperty("code", errorCode);
        pd.setInstance(URI.create(requestUri));

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.addHeader("x-error-code", errorCode);
        objectMapper.writeValue(response.getOutputStream(), pd);
    }
}
