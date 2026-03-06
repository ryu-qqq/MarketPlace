package com.ryuqq.marketplace.bootstrap.config;

import com.ryuqq.authhub.sdk.context.UserContext;
import com.ryuqq.authhub.sdk.context.UserContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * UserContextHolder → SecurityContextHolder 브릿지 필터.
 *
 * <p>GatewayAuthenticationFilter가 X-User-* 헤더를 파싱하여 UserContextHolder에 저장한 후, 이 필터가 Spring
 * Security의 SecurityContextHolder에 Authentication을 설정합니다.
 *
 * <p>이를 통해 Spring Security의 URL 기반 접근 제어({@code anyRequest().authenticated()})와 AuthHub SDK의 메서드 기반
 * 접근 제어({@code @PreAuthorize("@access.*")})가 모두 정상 동작합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public class GatewaySecurityBridgeFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Authentication existing = SecurityContextHolder.getContext().getAuthentication();
        if (existing != null
                && existing.isAuthenticated()
                && !(existing instanceof AnonymousAuthenticationToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        UserContext context = UserContextHolder.getContext();

        if (context.isAuthenticated()) {
            List<GrantedAuthority> authorities = new ArrayList<>();
            for (String role : context.getRoles()) {
                authorities.add(new SimpleGrantedAuthority(role));
            }
            for (String permission : context.getPermissions()) {
                authorities.add(new SimpleGrantedAuthority(permission));
            }

            PreAuthenticatedAuthenticationToken authentication =
                    new PreAuthenticatedAuthenticationToken(context.getUserId(), null, authorities);
            authentication.setDetails(context);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
